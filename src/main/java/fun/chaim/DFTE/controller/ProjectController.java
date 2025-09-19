package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ProjectDto;
import fun.chaim.DFTE.dto.ProjectInfoDto;
import fun.chaim.DFTE.entity.Project;
import fun.chaim.DFTE.service.ProjectService;
import fun.chaim.DFTE.service.RunningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 项目控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    private final RunningRecordService runningRecordService;
    
    /**
     * 新建项目
     * 
     * @param project 项目信息
     * @return 创建的项目
     */
    @PostMapping
    public ApiResponse<ProjectDto> createProject(@Valid @RequestBody Project project) {
        ProjectDto result = projectService.createProject(project);
        return ApiResponse.success("项目创建成功", result);
    }
    
    /**
     * 指定id查询项目
     * 
     * @param id 项目ID
     * @return 项目信息
     */
    @GetMapping("/{id}")
    public ApiResponse<ProjectDto> getProjectById(@PathVariable Integer id) {
        ProjectDto result = projectService.getProjectById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 列出所有项目信息（排除workflow_input，联合查询工作流名称）
     * 
     * @return 项目信息列表
     */
    @GetMapping
    public ApiResponse<List<ProjectInfoDto>> getAllProjects() {
        List<ProjectInfoDto> result = projectService.getAllProjects();
        return ApiResponse.success(result);
    }
    
    /**
     * 指定id修改项目
     * 
     * @param id 项目ID
     * @param project 更新信息
     * @return 更新后的项目
     */
    @PutMapping("/{id}")
    public ApiResponse<ProjectDto> updateProject(@PathVariable Integer id, @Valid @RequestBody Project project) {
        ProjectDto result = projectService.updateProject(id, project);
        return ApiResponse.success("项目更新成功", result);
    }

    /**
     * 指定id开始项目
     * 
     * @param id 项目ID
     */
    @PostMapping("/{id}/start")
    public ApiResponse<Void> startProject(@PathVariable Integer id) {
        runningRecordService.createAndStartRunningRecord(id);
        return ApiResponse.success();
    }
}
