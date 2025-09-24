package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.WorkflowData;
import fun.chaim.DFTE.dto.WorkflowDto;
import fun.chaim.DFTE.dto.WorkflowSimpleDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.entity.Workflow;
import fun.chaim.DFTE.exception.ForbiddenException;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.exception.ValidationException;
import fun.chaim.DFTE.repository.ParamRepository;
import fun.chaim.DFTE.repository.ProgramRepository;
import fun.chaim.DFTE.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {
    
    private final WorkflowRepository workflowRepository;
    private final ProgramRepository programRepository;
    private final ParamRepository paramRepository;

    /**
     * 创建工作流
     * 
     * @param workflow 工作流信息
     * @return 创建的工作流
     */
    @Transactional
    public WorkflowDto createWorkflow(Workflow workflow) {
        // 验证必填字段
        if (workflow.getName() == null || workflow.getName().trim().isEmpty()) {
            throw new ValidationException("工作流名称不能为空");
        }
        
        workflow.setLock(false);
        Workflow savedWorkflow = workflowRepository.save(workflow);
        log.info("创建工作流成功: {}", savedWorkflow.getName());
        
        return WorkflowDto.fromEntity(savedWorkflow, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 根据ID获取工作流
     * 
     * @param id 工作流ID
     * @return 工作流信息
     */
    public WorkflowDto getWorkflowById(Integer id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));

        // 获取参数
        List<Param> params = paramRepository.findByWorkflow(id);
        List<ParamDto> inputParams = params.stream()
                .filter(p -> !p.getRetval())
                .map(ParamDto::fromEntity)
                .collect(Collectors.toList());
        List<ParamDto> outputParams = params.stream()
                .filter(Param::getRetval)
                .map(ParamDto::fromEntity)
                .collect(Collectors.toList());

        return WorkflowDto.fromEntity(workflow, inputParams, outputParams);
    }
    
    /**
     * 克隆工作流
     * 
     * @param id 源工作流ID
     * @param newName 新工作流名称
     * @return 克隆的工作流
     */
    @Transactional
    public WorkflowDto cloneWorkflow(Integer id, String newName) {
        Workflow sourceWorkflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));
        
        if (sourceWorkflow.getLock()) {
            throw new ForbiddenException("工作流已锁定，无法克隆");
        }
        
        // 创建新工作流
        Workflow newWorkflow = new Workflow();
        newWorkflow.setName(newName);
        newWorkflow.setDescription(sourceWorkflow.getDescription());
        newWorkflow.setData(sourceWorkflow.getData());
        newWorkflow.setLock(false);
        
        Workflow savedWorkflow = workflowRepository.save(newWorkflow);
        log.info("克隆工作流成功: {} -> {}", sourceWorkflow.getName(), newName);

        // 克隆参数
        List<Param> params = paramRepository.findByWorkflow(id);
        for (Param param : params) {
            param.setId(null);
            param.setWorkflow(savedWorkflow.getId());
            paramRepository.save(param);
        }
        
        return WorkflowDto.fromEntity(savedWorkflow, null, null);
    }
    
    /**
     * 获取所有工作流列表（简单版，排除data）
     * 
     * @return 工作流列表
     */
    public List<WorkflowSimpleDto> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(WorkflowSimpleDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 删除工作流
     * 
     * @param id 工作流ID
     */
    @Transactional
    public void deleteWorkflow(Integer id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));
        
        if (workflow.getLock()) {
            throw new ForbiddenException("工作流已锁定，无法删除");
        }
        
        workflowRepository.delete(workflow);
        log.info("删除工作流成功: {}", workflow.getName());
    }
    
    /**
     * 更新工作流（排除lock字段）
     * 
     * @param id 工作流ID
     * @param workflow 更新信息
     * @return 更新后的工作流
     */
    @Transactional
    public WorkflowDto updateWorkflow(Integer id, Workflow workflow) {
        Workflow existingWorkflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));
        
        if (existingWorkflow.getLock()) {
            throw new ForbiddenException("工作流已锁定，无法修改");
        }
        
        // 更新字段
        existingWorkflow.setName(workflow.getName());
        existingWorkflow.setDescription(workflow.getDescription());
        existingWorkflow.setData(workflow.getData());
        
        Workflow savedWorkflow = workflowRepository.save(existingWorkflow);
        log.info("更新工作流成功: {}", savedWorkflow.getName());
        
        return getWorkflowById(id);
    }
    
    /**
     * 解锁工作流
     * 
     * @param id 工作流ID
     * @return 更新后的工作流
     */
    @Transactional
    public WorkflowSimpleDto unlockWorkflow(Integer id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));
        
        workflow.setLock(false);
        Workflow savedWorkflow = workflowRepository.save(workflow);
        log.info("解锁工作流成功: {}", savedWorkflow.getName());
        
        return WorkflowSimpleDto.fromEntity(savedWorkflow);
    }

    /**
     * 验证工作流（开始前调用）
     * 
     * @param id 工作流ID
     * @return 验证后的工作流
     */
    public Workflow verifyWokflow(Integer id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作流", id));
        // 验证是否存在开始节点
        WorkflowData workflowData = workflow.getWorkflowData()
                .orElseThrow(() -> new ResourceNotFoundException("工作流.数据", id));
        workflowData.getBeginNode()
                .orElseThrow(() -> new ResourceNotFoundException("工作流.开始节点", id));
        // 验证工作流节点
        for (WorkflowData.WorkflowNode node : workflowData.getNodes()) {
            // 验证程序是否存在
            Program program = programRepository.findByName(node.getType())
                    .orElseThrow(() -> new ResourceNotFoundException("程序", node.getType()));
            for (WorkflowData.WorkflowNode.InputPort inputPort : node.getInputs()) {
                String inputName = inputPort.getName();
                if (inputPort.getType().equals("-1")) continue; // 跳过执行引脚
                if (inputName.equals("Workflow Begin")) continue; // 跳过开始节点引脚
                // 验证参数是否存在
                Param param = paramRepository.findByProgramAndName(program.getId(), inputPort.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("参数", inputPort.getName()));
                // 验证require参数是否连接
                if (param.getRequire() && inputPort.getLink() == null) {
                    throw new ValidationException("参数 " + inputPort.getName() + " 未连接");
                }
            }
        }
        return workflow;
    }
}
