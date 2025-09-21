package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.projection.ParamProjections;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.exception.ValidationException;
import fun.chaim.DFTE.repository.ParamRepository;
import fun.chaim.DFTE.repository.ProgramRepository;
import fun.chaim.DFTE.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 参数服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParamService {
    
    private final ParamRepository paramRepository;
    private final ProgramRepository programRepository;
    private final WorkflowRepository workflowRepository;
    
    /**
     * 为处理程序创建参数
     * 
     * @param programId 处理程序ID
     * @param param 参数信息
     * @return 创建的参数
     */
    @Transactional
    public ParamDto createProgramParam(Integer programId, Param param) {
        // 验证处理程序是否存在
        if (!programRepository.existsById(programId))
            throw new ResourceNotFoundException("处理程序", programId);
        
        // 验证参数名称是否重复
        if (paramRepository.existsByProgramAndName(programId, param.getName())) {
            throw new ValidationException("处理程序参数名称已存在: " + param.getName());
        }
        
        param.setProgram(programId);
        param.setWorkflow(null);
        Param savedParam = paramRepository.save(param);
        log.info("为处理程序 {} 创建参数成功: {}", programId, param.getName());
        
        return ParamDto.fromEntity(savedParam);
    }
    
    /**
     * 为工作流创建参数
     * 
     * @param workflowId 工作流ID
     * @param param 参数信息
     * @return 创建的参数
     */
    @Transactional
    public ParamDto createWorkflowParam(Integer workflowId, Param param) {
        // 验证工作流是否存在
        if (!workflowRepository.existsById(workflowId))
            throw new ResourceNotFoundException("工作流", workflowId);
        
        // 验证参数名称是否重复
        if (paramRepository.existsByWorkflowAndName(workflowId, param.getName())) {
            throw new ValidationException("工作流参数名称已存在: " + param.getName());
        }
        
        param.setProgram(null);
        param.setWorkflow(workflowId);
        Param savedParam = paramRepository.save(param);
        log.info("为工作流 {} 创建参数成功: {}", workflowId, param.getName());
        
        return ParamDto.fromEntity(savedParam);
    }
    
    /**
     * 删除参数
     * 
     * @param paramId 参数ID
     */
    @Transactional
    public void deleteParam(Integer paramId) {
        Param param = paramRepository.findById(paramId)
                .orElseThrow(() -> new ResourceNotFoundException("参数", paramId));
        
        paramRepository.delete(param);
        log.info("删除参数成功: {}", param.getName());
    }
    
    /**
     * 更新参数信息
     * 
     * @param paramId 参数ID
     * @param param 更新信息
     * @return 更新后的参数
     */
    @Transactional
    public ParamDto updateParam(Integer paramId, Param param) {
        Param existingParam = paramRepository.findById(paramId)
                .orElseThrow(() -> new ResourceNotFoundException("参数", paramId));
        
        // 检查名称是否与其他参数冲突
        if (!existingParam.getName().equals(param.getName())) {
            if (existingParam.getProgram() != null) {
                if (paramRepository.existsByProgramAndName(existingParam.getProgram(), param.getName())) {
                    throw new ValidationException("处理程序参数名称已存在: " + param.getName());
                }
            } else if (existingParam.getWorkflow() != null) {
                if (paramRepository.existsByWorkflowAndName(existingParam.getWorkflow(), param.getName())) {
                    throw new ValidationException("工作流参数名称已存在: " + param.getName());
                }
            }
        }
        
        // 更新字段
        existingParam.setName(param.getName());
        existingParam.setType(param.getType());
        existingParam.setDescription(param.getDescription());
        existingParam.setRetval(param.getRetval());
        existingParam.setRequire(param.getRequire());
        
        Param savedParam = paramRepository.save(existingParam);
        log.info("更新参数成功: {}", savedParam.getName());
        
        return ParamDto.fromEntity(savedParam);
    }
    
    /**
     * 根据参数类型、返回值标识和所属类型查询参数信息
     * 
     * @param type 参数类型
     * @param retval 是否是返回值
     * @param isWorkflow 是否是工作流参数
     * @return 参数信息列表
     */
    public List<ParamProjections.ParamInfo> getParamsByTypeAndRetval(String type, Boolean retval, Boolean isWorkflow) {
        return paramRepository.findParamsByTypeAndRetval(type, retval, isWorkflow);
    }
}
