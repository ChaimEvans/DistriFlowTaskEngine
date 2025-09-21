package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.projection.ParamProjections;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.service.ParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 参数控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/params")
@RequiredArgsConstructor
public class ParamController {

    private final ParamService paramService;

    /**
     * 指定参数id删除参数
     * 
     * @param id 参数ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteParam(@PathVariable Integer id) {
        paramService.deleteParam(id);
        return ApiResponse.success();
    }

    /**
     * 指定参数id修改参数信息
     * 
     * @param id    参数ID
     * @param param 更新信息
     * @return 更新后的参数
     */
    @PutMapping("/{id}")
    public ApiResponse<ParamDto> updateParam(@PathVariable Integer id, @Valid @RequestBody Param param) {
        ParamDto result = paramService.updateParam(id, param);
        return ApiResponse.success("参数更新成功", result);
    }

    /**
     * 指定参数type、retval和bool(是program还是workflow)查询信息
     * 
     * @param type       参数类型
     * @param retval     是否是返回值
     * @param isWorkflow 是否是工作流参数
     * @return 参数信息列表
     */
    @GetMapping("/search")
    public ApiResponse<List<ParamProjections.ParamInfo>> searchParams(
            @RequestParam String type,
            @RequestParam Boolean retval,
            @RequestParam(defaultValue = "false", required = false) Boolean isWorkflow) {
        List<ParamProjections.ParamInfo> result = paramService.getParamsByTypeAndRetval(type, retval, isWorkflow);
        return ApiResponse.success(result);
    }
}
