package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.ProjectSimpleDto;
import fun.chaim.DFTE.dto.WorkflowDto;
import fun.chaim.DFTE.dto.WorkflowSimpleDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Workflow;
import fun.chaim.DFTE.service.ParamService;
import fun.chaim.DFTE.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 工作流控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowService workflowService;
    private final ParamService paramService;
    
    /**
     * 新建工作流
     * 
     * @param workflow 工作流信息
     * @return 创建的工作流
     */
    @PostMapping
    public ApiResponse<WorkflowDto> createWorkflow(@Valid @RequestBody Workflow workflow) {
        WorkflowDto result = workflowService.createWorkflow(workflow);
        return ApiResponse.success("工作流创建成功", result);
    }
    
    /**
     * 指定id克隆工作流
     * 
     * @param id 源工作流ID
     * @param request 克隆请求
     * @return 克隆的工作流
     */
    @PostMapping("/{id}/clone")
    public ApiResponse<WorkflowDto> cloneWorkflow(@PathVariable Integer id, @RequestBody CloneWorkflowRequest request) {
        WorkflowDto result = workflowService.cloneWorkflow(id, request.getName());
        return ApiResponse.success("工作流克隆成功", result);
    }
    
    /**
     * 指定id查询工作流
     * 
     * @param id 工作流ID
     * @return 工作流信息
     */
    @GetMapping("/{id}")
    public ApiResponse<WorkflowDto> getWorkflowById(@PathVariable Integer id) {
        WorkflowDto result = workflowService.getWorkflowById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 列出所有工作流信息（排除data）
     * 
     * @return 工作流列表
     */
    @GetMapping
    public ApiResponse<List<WorkflowSimpleDto>> getAllWorkflows() {
        List<WorkflowSimpleDto> result = workflowService.getAllWorkflows();
        return ApiResponse.success(result);
    }
    
    /**
     * 指定id删除工作流
     * 
     * @param id 工作流ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkflow(@PathVariable Integer id) {
        workflowService.deleteWorkflow(id);
        return ApiResponse.success();
    }
    
    /**
     * 指定id修改工作流（排除lock）
     * 
     * @param id 工作流ID
     * @param workflow 更新信息
     * @return 更新后的工作流
     */
    @PutMapping("/{id}")
    public ApiResponse<WorkflowDto> updateWorkflow(@PathVariable Integer id, @Valid @RequestBody Workflow workflow) {
        WorkflowDto result = workflowService.updateWorkflow(id, workflow);
        return ApiResponse.success("工作流更新成功", result);
    }
    
    /**
     * 指定id解锁工作流
     * 
     * @param id 工作流ID
     * @return 解锁后的工作流
     */
    @PostMapping("/{id}/unlock")
    public ApiResponse<WorkflowDto> unlockWorkflow(@PathVariable Integer id) {
        WorkflowDto result = workflowService.unlockWorkflow(id);
        return ApiResponse.success("工作流解锁成功", result);
    }
    
    /**
     * 指定id列出所有使用该工作流的项目的id、名称
     * 
     * @param id 工作流ID
     * @return 项目列表
     */
    @GetMapping("/{id}/projects")
    public ApiResponse<List<ProjectSimpleDto>> getProjectsByWorkflowId(@PathVariable Integer id) {
        List<ProjectSimpleDto> result = workflowService.getProjectsByWorkflowId(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 为工作流创建参数
     * 
     * @param workflowId 工作流ID
     * @param param 参数信息
     * @return 创建的参数
     */
    @PostMapping("/{workflowId}/params")
    public ApiResponse<ParamDto> createWorkflowParam(@PathVariable Integer workflowId, @Valid @RequestBody Param param) {
        ParamDto result = paramService.createWorkflowParam(workflowId, param);
        return ApiResponse.success("参数创建成功", result);
    }
    
    /**
     * 克隆工作流请求
     */
    public static class CloneWorkflowRequest {
        private String name;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
