package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.projection.TaskProjections;
import fun.chaim.DFTE.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 任务控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * 分页查询列出任务数据（排除 params、retdata，联合查询项目名、工作流名、处理程序名）
     * 可选参数 runningRecord、project、workflow、nodeId、program、status
     *
     * @param runningRecord 运行记录 ID，可选
     * @param project 项目 ID，可选
     * @param workflow 工作流 ID，可选
     * @param nodeId 节点 ID，可选
     * @param program 程序 ID，可选
     * @param status 状态，可选
     * @param page 页码（从 0 开始）
     * @param size 每页大小
     * @return 任务信息分页（DTO）
     */
    @GetMapping
    public ApiResponse<List<TaskProjections.TaskInfo>> getTaskInfoPage(
            @RequestParam(required = false) Integer running_record,
            @RequestParam(required = false) Integer project,
            @RequestParam(required = false) Integer workflow,
            @RequestParam(required = false) Integer node_id,
            @RequestParam(required = false) Integer program,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<TaskProjections.TaskInfo> result = taskService.getTaskInfoPage(running_record, project, workflow, node_id, program, status, page, size).getContent();

        return ApiResponse.success(result);
    }
    
    /**
     * 指定uuid查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务详细信息
     */
    @GetMapping("/{uuid}")
    public ApiResponse<TaskProjections.TaskDetail> getTaskDetailByUuid(@PathVariable UUID uuid) {
        TaskProjections.TaskDetail result = taskService.getTaskDetailByUuid(uuid);
        return ApiResponse.success(result);
    }
}
