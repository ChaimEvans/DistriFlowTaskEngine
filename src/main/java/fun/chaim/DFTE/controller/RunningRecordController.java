package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.RunningRecordInfoDto;
import fun.chaim.DFTE.service.RunningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 运行记录控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/running-records")
@RequiredArgsConstructor
public class RunningRecordController {
    
    private final RunningRecordService runningRecordService;
    
    /**
     * 分页按条件查询列出运行记录数据（排除workflow_data、workflow_input、workflow_output，联合查询工作流名称、项目名称）
     * 
     * @param workflowId 工作流ID（可选）
     * @param projectId 项目ID（可选）
     * @param finish 是否完成（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 运行记录信息分页
     */
    @GetMapping
    public ApiResponse<Page<RunningRecordInfoDto>> getRunningRecordInfoPage(
            @RequestParam(required = false) Integer workflowId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) Boolean finish,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RunningRecordInfoDto> result = runningRecordService.getRunningRecordInfoPage(workflowId, projectId, finish, page, size);
        return ApiResponse.success(result);
    }
}
