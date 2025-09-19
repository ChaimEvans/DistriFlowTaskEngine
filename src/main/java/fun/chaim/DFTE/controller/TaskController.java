package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.TaskDetailDto;
import fun.chaim.DFTE.dto.TaskInfoDto;
import fun.chaim.DFTE.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
     * 分页查询列出任务数据（排除params、retdata，联合查询项目名、工作流名、处理程序名）
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 任务信息分页
     */
    @GetMapping
    public ApiResponse<Page<TaskInfoDto>> getTaskInfoPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskInfoDto> result = taskService.getTaskInfoPage(page, size);
        return ApiResponse.success(result);
    }
    
    /**
     * 指定uuid查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务详细信息
     */
    @GetMapping("/{uuid}")
    public ApiResponse<TaskDetailDto> getTaskDetailByUuid(@PathVariable UUID uuid) {
        TaskDetailDto result = taskService.getTaskDetailByUuid(uuid);
        return ApiResponse.success(result);
    }
}
