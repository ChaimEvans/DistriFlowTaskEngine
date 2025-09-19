package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.FileUploadDto;
import fun.chaim.DFTE.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileUploadService {
    
    /**
     * 文件上传目录
     */
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @return 文件上传信息
     */
    public FileUploadDto uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("文件不能为空");
        }
        
        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件上传成功: {} -> {}", originalFilename, filePath);
            
            return new FileUploadDto(
                    "/uploads/" + uniqueFilename,
                    originalFilename,
                    file.getSize()
            );
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new ValidationException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 列出上传文件夹中的所有文件
     * 
     * @return 文件列表
     */
    public List<FileUploadDto> listUploadedFiles() {
        List<FileUploadDto> files = new ArrayList<>();
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return files;
            }
            
            Files.list(uploadPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String filename = path.getFileName().toString();
                            long fileSize = Files.size(path);
                            files.add(new FileUploadDto(
                                    "/uploads/" + filename,
                                    filename,
                                    fileSize
                            ));
                        } catch (IOException e) {
                            log.warn("获取文件信息失败: {}", path, e);
                        }
                    });
            
        } catch (IOException e) {
            log.error("列出文件失败", e);
            throw new ValidationException("列出文件失败: " + e.getMessage());
        }
        
        return files;
    }
}
