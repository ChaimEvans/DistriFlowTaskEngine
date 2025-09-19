package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.RunningRecordInfoDto;
import fun.chaim.DFTE.entity.Project;
import fun.chaim.DFTE.entity.RunningRecord;
import fun.chaim.DFTE.entity.Workflow;
import fun.chaim.DFTE.entity.WorkflowData;
import fun.chaim.DFTE.entity.WorkflowData.WorkflowNode;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.repository.ProgramRepository;
import fun.chaim.DFTE.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 运行记录服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RunningRecordService {
    
    private final RunningRecordRepository runningRecordRepository;
    private final ProgramRepository programRepository;

    private final ProjectService projectService;
    private final WorkflowService workflowService;
    private final TaskService taskService;

    @Transactional
    public RunningRecordInfoDto createAndStartRunningRecord(Integer projectId) {
        // 验证项目
        Project project = projectService.verifyProject(projectId);
        log.info("项目验证通过: {}/{}", projectId, project.getName());
        // 验证工作流
        Integer workflowId = project.getWorkflow();
        Workflow workflow = workflowService.verifyWokflow(workflowId);
        // 锁定工作流
        workflow.setLock(true);
        // 创建运行记录
        RunningRecord rr = new RunningRecord();
        rr.setProject(projectId);
        rr.setWorkflow(workflow.getId());
        rr.setWorkflowData(workflow.getData());
        ObjectNode workflowInput = project.getWorkflowInput();
        rr.setWorkflowInput(workflowInput);
        rr = runningRecordRepository.save(rr);
        // 添加begin任务
        WorkflowData workflowData = workflow.getWorkflowData().get(); // 前面验证过了，直接get
        WorkflowNode beginNode = workflowData.getBeginNode().get(); // 前面验证过了，直接get
        taskService.createAndStartTask(
            null, 
            String.format("启动: %s(%s)", project.getName(), workflow.getName()),
            projectId,
            workflowId,
            beginNode.getId(),
            programRepository.findByName("DirectPipeline").orElseThrow(() -> new RuntimeException("！！！严重错误：未找到DirectPipeline记录")).getId(),
            workflowInput
        );
        return convertToRunningRecordInfoDto(rr);
    }
    
    /**
     * 分页按条件查询运行记录数据（排除workflow_data、workflow_input、workflow_output，联合查询工作流名称、项目名称）
     * 
     * @param workflowId 工作流ID（可选）
     * @param projectId 项目ID（可选）
     * @param finish 是否完成（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 运行记录信息分页
     */
    public Page<RunningRecordInfoDto> getRunningRecordInfoPage(Integer workflowId, Integer projectId, Boolean finish, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = runningRecordRepository.findRunningRecordInfoPage(workflowId, projectId, finish, pageable);
        
        return results.map(this::convertToRunningRecordInfoDto);
    }
    
    /**
     * 转换为RunningRecordInfoDto
     */
    private RunningRecordInfoDto convertToRunningRecordInfoDto(Object[] result) {
        RunningRecordInfoDto dto = new RunningRecordInfoDto();
        dto.setId((Integer) result[0]);
        dto.setWorkflow((Integer) result[1]);
        dto.setProject((Integer) result[2]);
        dto.setFinish((Boolean) result[3]);
        dto.setCreatedAt((java.time.LocalDateTime) result[4]);
        dto.setUpdatedAt((java.time.LocalDateTime) result[5]);
        dto.setWorkflowName((String) result[6]);
        dto.setProjectName((String) result[7]);
        return dto;
    }

    /**
     * 转换为RunningRecordInfoDto
     */
    private RunningRecordInfoDto convertToRunningRecordInfoDto(RunningRecord rr) {
        Object[] result = runningRecordRepository.findRunningRecordInfoById(rr.getId())
            .orElseThrow(() -> new ResourceNotFoundException("运行记录", rr.getId()));
        return this.convertToRunningRecordInfoDto(result);
    }
}
