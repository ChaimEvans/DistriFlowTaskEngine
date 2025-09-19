package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.ProjectDto;
import fun.chaim.DFTE.dto.ProjectInfoDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Project;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.exception.ValidationException;
import fun.chaim.DFTE.repository.ParamRepository;
import fun.chaim.DFTE.repository.ProjectRepository;
import fun.chaim.DFTE.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final WorkflowRepository workflowRepository;
    private final ParamRepository paramRepository;
    
    /**
     * 创建项目
     * 
     * @param project 项目信息
     * @return 创建的项目
     */
    @Transactional
    public ProjectDto createProject(Project project) {
        // 验证工作流是否存在
        if(!workflowRepository.existsById(project.getWorkflow()))
            throw new ResourceNotFoundException("工作流", project.getWorkflow());
        
        // 验证必填字段
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new ValidationException("项目名称不能为空");
        }
        
        Project savedProject = projectRepository.save(project);
        log.info("创建项目成功: {}", savedProject.getName());
        
        return convertToDto(savedProject);
    }
    
    /**
     * 根据ID获取项目
     * 
     * @param id 项目ID
     * @return 项目信息
     */
    public ProjectDto getProjectById(Integer id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("项目", id));
        
        return convertToDto(project);
    }
    
    /**
     * 获取所有项目信息（排除workflow_input，联合查询工作流名称）
     * 
     * @return 项目信息列表
     */
    public List<ProjectInfoDto> getAllProjects() {
        List<Object[]> results = projectRepository.findAllProjectInfo();
        return results.stream()
                .map(result -> new ProjectInfoDto(
                        (Integer) result[0],
                        (String) result[1],
                        (Integer) result[2],
                        (String) result[3],
                        (java.time.LocalDateTime) result[4]
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 更新项目
     * 
     * @param id 项目ID
     * @param project 更新信息
     * @return 更新后的项目
     */
    @Transactional
    public ProjectDto updateProject(Integer id, Project project) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("项目", id));
        
        // 如果更新工作流，验证工作流是否存在
        if (project.getWorkflow() != null && !project.getWorkflow().equals(existingProject.getWorkflow())) {
            if(!workflowRepository.existsById(project.getWorkflow()))
                throw new ResourceNotFoundException("工作流", project.getWorkflow());
        }
        
        // 更新字段
        if (project.getName() != null) {
            existingProject.setName(project.getName());
        }
        if (project.getWorkflow() != null) {
            existingProject.setWorkflow(project.getWorkflow());
        }
        if (project.getWorkflowInput() != null) {
            existingProject.setWorkflowInput(project.getWorkflowInput());
        }
        
        Project savedProject = projectRepository.save(existingProject);
        log.info("更新项目成功: {}", savedProject.getName());
        
        return convertToDto(savedProject);
    }

    /**
     * 验证项目
     * @param id 项目ID
     */
    public Project verifyProject(Integer id) {
        // 验证项目
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("项目", id));
        // 验证工作流
        Integer workflowId = project.getWorkflow();
        if (workflowId == null)
            throw new ResourceNotFoundException("项目.工作流", id);
        // 验证项目输入
        JsonNode workflowInput = project.getWorkflowInput();
        for (Param param : paramRepository.findByWorkflow(workflowId)){
            if (workflowInput.get(param.getName()) == null) throw new ValidationException("项目输入与工作流不匹配");
        }
        return project;
    }
    
    /**
     * 转换为DTO
     */
    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setWorkflow(project.getWorkflow());
        dto.setWorkflowInput(project.getWorkflowInput());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }
}
