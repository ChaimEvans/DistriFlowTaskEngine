package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ProjectDto;
import fun.chaim.DFTE.dto.projection.ProjectProjections;
import fun.chaim.DFTE.dto.projection.RunningRecordProjections;
import fun.chaim.DFTE.entity.Project;
import fun.chaim.DFTE.service.ProjectService;
import fun.chaim.DFTE.service.RunningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
     * 获取项目列表（分页，可选筛选 workflowId、finish）
     */
    @GetMapping
    public ApiResponse<List<ProjectProjections.ProjectInfo>> getAllProjects(
            @RequestParam(required = false) Integer workflowId,
            @RequestParam(required = false) Boolean finish,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<ProjectProjections.ProjectInfo> result =
                projectService.getAllProjects(workflowId, finish, page, size).getContent();
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
    public ApiResponse<RunningRecordProjections.RunningRecordInfo> startProject(@PathVariable Integer id) {
        RunningRecordProjections.RunningRecordInfo result = runningRecordService.createAndStartRunningRecord(id);
        return ApiResponse.success("项目开始成功", result);
    }
}
