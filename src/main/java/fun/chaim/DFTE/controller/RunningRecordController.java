package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.projection.RunningRecordProjections;
import fun.chaim.DFTE.service.RunningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
     * 获取运行记录信息（排除workflow_data、workflow_input、workflow_output，联合查询工作流名称、项目名称）
     * 
     * @param id 运行记录ID
     * @return 运行记录信息
     */
    @GetMapping("/{id}")
    public ApiResponse<RunningRecordProjections.RunningRecordInfo> getRunningRecordInfoById(@PathVariable Integer id) {
        RunningRecordProjections.RunningRecordInfo result = runningRecordService.getRunningRecordInfoById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 分页按条件查询列出运行记录数据（排除workflow_data、workflow_input、workflow_output，联合查询工作流名称、项目名称）
     * 
     * @param workflow 工作流ID（可选）
     * @param project 项目ID（可选）
     * @param finish 是否完成（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 运行记录信息分页
     */
    @GetMapping
    public ApiResponse<List<RunningRecordProjections.RunningRecordInfo>> getRunningRecordInfoPage(
            @RequestParam(required = false) Integer workflow,
            @RequestParam(required = false) Integer project,
            @RequestParam(required = false) Boolean finish,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RunningRecordProjections.RunningRecordInfo> result = runningRecordService.getRunningRecordInfoPage(workflow, project, finish, page, size).getContent();
        return ApiResponse.success(result);
    }

    /**
     * 获取运行记录详情
     * 
     * @param id 运行记录ID
     * @return 运行记录详情
     */
    @GetMapping("/{id}/detail")
    public ApiResponse<RunningRecordProjections.RunningRecordDetail> getRunningRecordDetailById(@PathVariable Integer id) {
        RunningRecordProjections.RunningRecordDetail result = runningRecordService.getRunningRecordDetailById(id);
        return ApiResponse.success(result);
    }
}
