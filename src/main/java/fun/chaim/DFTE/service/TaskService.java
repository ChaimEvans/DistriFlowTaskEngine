package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.TaskInQueueDto;
import fun.chaim.DFTE.dto.WorkflowData;
import fun.chaim.DFTE.dto.WorkflowData.WorkflowNode;
import fun.chaim.DFTE.dto.projection.TaskProjections;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.entity.RunningRecord;
import fun.chaim.DFTE.entity.Task;
import fun.chaim.DFTE.entity.Workflow;
import fun.chaim.DFTE.exception.BusinessException;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.repository.ProgramRepository;
import fun.chaim.DFTE.repository.RunningRecordRepository;
import fun.chaim.DFTE.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.rabbitmq.client.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final RunningRecordRepository runningRecordRepository;

    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 分页查询任务数据（排除 params、retdata，联合查询项目名、工作流名、处理程序名）
     * 可根据 runningRecord、project、workflow、nodeId、program、status 过滤
     *
     * @param runningRecord 运行记录 ID，可为空
     * @param project 项目 ID，可为空
     * @param workflow 工作流 ID，可为空
     * @param nodeId 节点 ID，可为空
     * @param program 程序 ID，可为空
     * @param status 状态，可为空
     * @param page 页码（从 0 开始）
     * @param size 每页大小
     * @return 任务信息分页（DTO）
     */
    public Page<TaskProjections.TaskInfo> getTaskInfoPage(
            Integer runningRecord,
            Integer project,
            Integer workflow,
            Integer nodeId,
            Integer program,
            Integer status,
            int page,
            int size) {
        return taskRepository.findTaskInfoPage(runningRecord, project, workflow, nodeId, program, status, PageRequest.of(page, size));
    }
    
    /**
     * 根据UUID查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务详细信息
     */
    public TaskProjections.TaskDetail getTaskDetailByUuid(UUID uuid) {
        return taskRepository.findTaskInfoByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("任务", uuid));
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
    public Optional<UUID> createAndStartTask(UUID parentUuid, String name, Integer runningRecordId, Integer projectId, Integer workflowId, Integer nodeId, Integer programId, ObjectNode params) {
        Optional<Program> programOpt = programRepository.findById(programId);
        if (!programOpt.isPresent()) {
            log.error("未找到处理程序: {}", programId);
            return Optional.empty();
        }
        Program program = programOpt.get();
        program.setLock(true); // 锁定程序
        programRepository.save(program);
        // 创建任务
        UUID taskUuid = UUID.randomUUID();
        Task task = new Task();
        task.setUuid(taskUuid);
        task.setParent(parentUuid);
        task.setName(name == null ? String.format("%s(%s)", program.getName(), taskUuid) : name);
        task.setRunningRecord(runningRecordId);
        task.setProject(projectId);
        task.setWorkflow(workflowId);
        task.setNodeId(nodeId);
        task.setProgram(programId);
        task.setParams(params);
        task = taskRepository.save(task);
        TaskInQueueDto taskInQueueDto = taskRepository.findTaskInQueueByUuid(taskUuid)
                .orElseThrow(()-> new BusinessException("任务创建失败"));
        log.info("创建任务: {}", taskInQueueDto);
        // 发送信息
        if (program.getBuildin())
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
    // @SuppressWarnings("unchecked")
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
        Optional<WorkflowNode> next = wd.get().getNextNode(node.get());
        if (!next.isPresent()) {
            log.info("工作流结束: {}/{}", rr.getId(), workflow.getName());
            return uuids;
        }
        Optional<Program> program = programRepository.findByName(next.get().getType());
        if (!program.isPresent()) {
            log.error("未找到处理程序: {}", next.get().getType());
            return uuids;
        }
        List<Map.Entry<String, List<JsonNode>>> params = new ArrayList<>();
        for (WorkflowData.WorkflowNode.InputDataInfo info : wd.get().getInputDataInfo(next.get())) {
            Optional<Task> parentTask;
            UUID parentUuid = task.getParent();
            if (parentUuid == null) parentTask = Optional.empty();
            else parentTask = taskRepository.findById(parentUuid);
            while (parentTask.isPresent()) {
                if (parentTask.get().getNodeId() != info.getFromNode()) {
                    parentUuid = task.getParent();
                    if (parentUuid == null) parentTask = Optional.empty();
                    else parentTask = taskRepository.findById(parentUuid);
                    parentTask = taskRepository.findById(parentTask.get().getParent());
                    continue;
                }
                List<JsonNode> slotData = parentTask.get()
                        .getRetdata()
                        .valueStream()
                        .map(item -> item.get(info.getFromSlotName()))
                        .collect(Collectors.toList());
                params.add(Map.entry(info.getParamName(), slotData));
                break;
            }
        }
        Integer minLen = params.stream()
                .mapToInt(param -> ((List<?>) param.getValue()).size())
                .min()
                .orElse(0);
        for (int i = 0; i < minLen; i++) {
            ObjectNode input = JsonNodeFactory.instance.objectNode();
            for (Map.Entry<String, List<JsonNode>> param : params) {
                input.set(param.getKey(), param.getValue().get(i));
            }
            Optional<UUID> newTaskUuid = createAndStartTask(task.getUuid(), null, rr.getId(), rr.getProject(), workflow.getId(), next.get().getId(), program.get().getId(), input);
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
    public void receiveStatusUpdate(JsonNode data) {
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
        task.setProcessingNodeMac(Optional.ofNullable(data.get("mac")).map(JsonNode::asText).orElse(null));
        if (status == 0 || status == 1) { // 未结束
            taskRepository.save(task);
            return;
        }
        // 任务结束
        log.info("当前任务结束：{}", task);
        Program program = task.getProgramEntity();
        if (program != null) { // 解锁程序
            program.setLock(false);
            programRepository.save(program);
        }
        // 启动下一个任务
        List<UUID> uuids = startNextTask(task);
        if (uuids.size() == 0) {
            // 无后续任务，更新运行记录
            RunningRecord rr = task.getRunningRecordEntity();
            if (rr != null) {
                rr.setFinish(true);
                ArrayNode output = rr.getWorkflowOutput();
                output.addAll(task.getRetdata());
                rr.setWorkflowOutput(output);
                runningRecordRepository.save(rr);
            }
        }
    }
}
