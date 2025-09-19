package fun.chaim.DFTE.controller;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.FileUploadDto;
import fun.chaim.DFTE.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @return 文件上传信息
     */
    @PostMapping("/upload")
    public ApiResponse<FileUploadDto> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadDto result = fileUploadService.uploadFile(file);
        return ApiResponse.success("文件上传成功", result);
    }
    
    /**
     * 列出上传文件夹中的所有文件
     * 
     * @return 文件列表
     */
    @GetMapping("/list")
    public ApiResponse<List<FileUploadDto>> listUploadedFiles() {
        List<FileUploadDto> files = fileUploadService.listUploadedFiles();
        return ApiResponse.success(files);
    }
}
