package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.ProgramDto;
import fun.chaim.DFTE.dto.ProgramSimpleDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.service.ParamService;
import fun.chaim.DFTE.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 处理程序控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {
    
    private final ProgramService programService;
    private final ParamService paramService;
    
    /**
     * 新建处理程序
     * 
     * @param program 处理程序信息
     * @return 创建的处理程序
     */
    @PostMapping
    public ApiResponse<ProgramDto> createProgram(@Valid @RequestBody Program program) {
        ProgramDto result = programService.createProgram(program);
        return ApiResponse.success("处理程序创建成功", result);
    }
    
    /**
     * 指定处理程序id查询处理程序信息（简单版，排除file、cmd）
     * 
     * @param id 处理程序ID
     * @return 处理程序信息
     */
    @GetMapping("/{id}")
    public ApiResponse<ProgramDto> getProgramById(@PathVariable Integer id) {
        ProgramDto result = programService.getProgramById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 指定处理程序id查询处理程序信息（详细版，包含file、cmd）
     * 
     * @param id 处理程序ID
     * @return 处理程序详细信息
     */
    @GetMapping("/{id}/detail")
    public ApiResponse<ProgramDto> getProgramDetailById(@PathVariable Integer id) {
        ProgramDto result = programService.getProgramDetailById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 列出所有处理程序信息（排除file、cmd）
     * 
     * @return 处理程序列表
     */
    @GetMapping
    public ApiResponse<List<ProgramSimpleDto>> getAllPrograms() {
        List<ProgramSimpleDto> result = programService.getAllPrograms();
        return ApiResponse.success(result);
    }
    
    /**
     * 指定处理程序id查询处理程序名称
     * 
     * @param id 处理程序ID
     * @return 处理程序名称
     */
    @GetMapping("/{id}/name")
    public ApiResponse<String> getProgramNameById(@PathVariable Integer id) {
        String result = programService.getProgramNameById(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 指定处理程序id删除处理程序
     * 
     * @param id 处理程序ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProgram(@PathVariable Integer id) {
        programService.deleteProgram(id);
        return ApiResponse.success();
    }
    
    /**
     * 指定处理程序id修改处理程序信息（排除lock）
     * 
     * @param id 处理程序ID
     * @param program 更新信息
     * @return 更新后的处理程序
     */
    @PutMapping("/{id}")
    public ApiResponse<ProgramDto> updateProgram(@PathVariable Integer id, @Valid @RequestBody Program program) {
        ProgramDto result = programService.updateProgram(id, program);
        return ApiResponse.success("处理程序更新成功", result);
    }
    
    /**
     * 为处理程序创建参数
     * 
     * @param programId 处理程序ID
     * @param param 参数信息
     * @return 创建的参数
     */
    @PostMapping("/{programId}/params")
    public ApiResponse<ParamDto> createProgramParam(@PathVariable Integer programId, @Valid @RequestBody Param param) {
        ParamDto result = paramService.createProgramParam(programId, param);
        return ApiResponse.success("参数创建成功", result);
    }
}
