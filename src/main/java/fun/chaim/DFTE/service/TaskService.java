package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.TaskDetailDto;
import fun.chaim.DFTE.dto.TaskInQueueDto;
import fun.chaim.DFTE.dto.TaskInfoDto;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.entity.Project;
import fun.chaim.DFTE.entity.RunningRecord;
import fun.chaim.DFTE.entity.Task;
import fun.chaim.DFTE.entity.Workflow;
import fun.chaim.DFTE.entity.WorkflowData;
import fun.chaim.DFTE.entity.WorkflowData.WorkflowNode;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.repository.ProgramRepository;
import fun.chaim.DFTE.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProgramRepository programRepository;

    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 分页查询任务数据（排除params、retdata，联合查询项目名、工作流名、处理程序名）
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 任务信息分页
     */
    public Page<TaskInfoDto> getTaskInfoPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = taskRepository.findTaskInfoPage(pageable);
        
        return results.map(this::convertToTaskInfoDto);
    }
    
    /**
     * 根据UUID查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务详细信息
     */
    public TaskDetailDto getTaskDetailByUuid(UUID uuid) {
        Object[] result = taskRepository.findTaskInfoByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("任务", uuid));
        
        return convertToTaskDetailDto(result);
    }

    /**
     * 创建并启动任务
     * @param parentUuid 父任务UUID
     * @param name 任务名称
     * @param projectId 项目ID
     * @param workflowId 工作流ID
     * @param nodeId 节点ID
     * @param programId 处理程序ID
     * @param params 参数
     * @return 任务UUID
     */
    @Transactional
    public Optional<UUID> createAndStartTask(UUID parentUuid, String name, Integer projectId, Integer workflowId, Integer nodeId, Integer programId, ObjectNode params) {
        Optional<Program> program = programRepository.findById(programId);
        if (!program.isPresent()) {
            log.error("未找到处理程序: {}", programId);
            return Optional.empty();
        }
        UUID taskUuid = UUID.randomUUID();
        Task task = new Task();
        task.setUuid(taskUuid);
        task.setParent(parentUuid);
        task.setName(name == null ? String.format("%s(%s)", program.get().getName(), taskUuid) : name);
        task.setProject(projectId);
        task.setWorkflow(workflowId);
        task.setNodeId(nodeId);
        task.setProgram(programId);
        task.setParams(params);
        task = taskRepository.save(task);
        TaskInQueueDto taskInQueueDto = convertToTaskInQueueDto(task);
        log.info("创建任务: {}", taskInQueueDto);
        if (program.get().getBuildin())
            rabbitTemplate.convertAndSend("DFTE.Exchange", "task.inside", taskInQueueDto);
        else
            rabbitTemplate.convertAndSend("DFTE.Exchange", "task", taskInQueueDto);
        return Optional.of(taskUuid);
    }

    /**
     * 启动下一个任务
     * @param task
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UUID> startNextTask(Task task) {
        List<UUID> uuids = new ArrayList<>();
        RunningRecord rr = task.getRunningRecordEntity();
        if (rr == null) {
            log.error("任务未关联运行记录: {}", task.getUuid());
            return uuids;
        }
        Workflow workflow = rr.getWorkflowEntity();
        if (workflow == null) {
            log.error("运行记录未关联工作流: {}/{}", rr.getId());
            return uuids;
        }
        Optional<WorkflowData> wd = workflow.getWorkflowData();
        if (!wd.isPresent()) {
            log.error("工作流无数据: {}/{}", workflow.getId(), workflow.getName());
            return uuids;
        }
        Optional<WorkflowData.WorkflowNode> node = wd.get().findNodeById(task.getNodeId());
        if (!node.isPresent()) {
            log.error("未找到节点: {}/{}", workflow.getName(), task.getNodeId());
            return uuids;
        }
        Optional<WorkflowNode> next = node.get().getNextNode();
        if (!next.isPresent()) {
            log.info("工作流结束: {}/{}", rr.getId(), workflow.getName());
            return uuids;
        }
        Optional<Program> program = programRepository.findByName(next.get().getType());
        if (!program.isPresent()) {
            log.error("未找到处理程序: {}", next.get().getType());
            return uuids;
        }
        List<Object[]> params = new ArrayList<>();
        for (WorkflowData.WorkflowNode.InputDataInfo info : next.get().getInputDataInfo()) {
            Optional<Task> parentTask = taskRepository.findById(task.getParent());
            while (parentTask.isPresent()) {
                if (parentTask.get().getNodeId() != info.getFromNode()) {
                    parentTask = taskRepository.findById(parentTask.get().getParent());
                    continue;
                }
                List<JsonNode> slotData = parentTask.get()
                        .getRetdata()
                        .valueStream()
                        .map(item -> item.get(info.getFromSlotName()))
                        .collect(Collectors.toList());
                params.add(new Object[] { info.getParamName(), slotData });
                break;
            }
        }
        Integer minLen = params.stream()
                .mapToInt(param -> ((List<?>) ((Object[]) param)[1]).size())
                .min()
                .orElse(0);
        for (int i = 0; i < minLen; i++) {
            ObjectNode input = JsonNodeFactory.instance.objectNode();
            for (Object[] param : params) {
                input.set((String)param[0], ((List<JsonNode>)param[1]).get(i));
            }
            Optional<UUID> newTaskUuid = createAndStartTask(task.getUuid(), null, rr.getProject(), workflow.getId(), next.get().getId(), program.get().getId(), input);
            if (newTaskUuid.isPresent()) uuids.add(newTaskUuid.get());
        }
        return uuids;
    }

    /**
     * 任务状态改变消息队列
     * @param message
     */
    @Transactional
    @RabbitListener(queues = "DFTE.Queue.Report")
    public void receiveStatusUpdate(JsonNode data, Channel channel, Message message) {
        log.info("收到任务状态更新: {}", data);
        String uuid_str = data.get("uuid").asText("");
        UUID uuid;
        try {
            uuid = UUID.fromString(uuid_str);
        } catch (IllegalArgumentException e) {
            log.error("UUID格式错误: {}", uuid_str);
            return;
        }
        Optional<Task> taskOptional = taskRepository.findById(uuid);
        if (!taskOptional.isPresent()) {
            log.error("任务不存在: {}", uuid);
            return;
        }
        Task task = taskOptional.get();
        Integer status = task.getStatus();
        if (status != 0 && status != 1) {
            log.error("任务已结束，消息重复: {}", uuid);
            return;
        }
        status = data.get("status").asInt();
        task.setStatus(status);
        task.setRetmsg(data.get("retmsg").asText(""));
        task.setRetdata(data.get("retdata").isArray() ? (ArrayNode) data.get("retdata") : JsonNodeFactory.instance.arrayNode());
        task.setUpdatedAt(java.time.LocalDateTime.now());
        taskRepository.save(task);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("无法确认信息: \n\tDeliveryTag: {}\n\tUUID: {}\n\t原因: {}", message.getMessageProperties().getDeliveryTag(), uuid_str, e.getMessage());
        }
        if (status == 0 || status == 1) return;
        log.info("任务结束：{}", task);
        // 启动下一个任务
        startNextTask(task);
    }
    
    /**
     * 转换为TaskInfoDto
     */
    private TaskInfoDto convertToTaskInfoDto(Object[] result) {
        TaskInfoDto dto = new TaskInfoDto();
        dto.setUuid((UUID) result[0]);
        dto.setName((String) result[1]);
        dto.setRunningRecord((Integer) result[2]);
        dto.setProject((Integer) result[3]);
        dto.setWorkflow((Integer) result[4]);
        dto.setNodeId((Integer) result[5]);
        dto.setProgram((Integer) result[6]);
        dto.setStatus((Integer) result[7]);
        dto.setRetmsg((String) result[8]);
        dto.setCreatedAt((java.time.LocalDateTime) result[9]);
        dto.setUpdatedAt((java.time.LocalDateTime) result[10]);
        dto.setProjectName((String) result[11]);
        dto.setWorkflowName((String) result[12]);
        dto.setProgramName((String) result[13]);
        return dto;
    }
    
    /**
     * 转换为TaskDetailDto
     */
    private TaskDetailDto convertToTaskDetailDto(Object[] result) {
        TaskDetailDto dto = new TaskDetailDto();
        dto.setUuid((UUID) result[0]);
        dto.setName((String) result[1]);
        dto.setParent((UUID) result[2]);
        dto.setParentName((String) result[3]);
        dto.setRunningRecord((Integer) result[4]);
        dto.setProject((Integer) result[5]);
        dto.setWorkflow((Integer) result[6]);
        dto.setNodeId((Integer) result[7]);
        dto.setProgram((Integer) result[8]);
        dto.setParams((String) result[9]);
        dto.setStatus((Integer) result[10]);
        dto.setRetmsg((String) result[11]);
        dto.setRetdata((String) result[12]);
        dto.setCreatedAt((java.time.LocalDateTime) result[13]);
        dto.setUpdatedAt((java.time.LocalDateTime) result[14]);
        dto.setProjectName((String) result[15]);
        dto.setWorkflowName((String) result[16]);
        dto.setProgramName((String) result[17]);
        return dto;
    }

    private TaskInQueueDto convertToTaskInQueueDto(Task task) { 
        TaskInQueueDto dto = new TaskInQueueDto();
        dto.setUuid(task.getUuid());
        dto.setName(task.getName());
        dto.setProjectName(Optional.ofNullable(task.getProjectEntity())
                                    .map(Project::getName)
                                    .orElse("无关联项目"));
        dto.setWorkflowName(Optional.ofNullable(task.getWorkflowEntity())
                                    .map(Workflow::getName)
                                    .orElse("无关联工作流"));
        dto.setProgram(task.getProgram());
        dto.setProgramName(Optional.ofNullable(task.getProgramEntity())
                                    .map(Program::getName)
                                    .orElse("无关联程序"));
        dto.setParams(task.getParams());
        return dto;
    }
}
