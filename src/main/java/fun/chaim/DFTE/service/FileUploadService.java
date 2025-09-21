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
import java.util.concurrent.ThreadLocalRandom;

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

            // 原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")); // 去掉扩展名部分
            }

            // 生成唯一文件名（带随机码，确保不重复）
            String uniqueFilename;
            Path filePath;
            do {
                String randomCode = generateRandomCode(6);
                uniqueFilename = String.format("%s-%s%s", originalFilename, randomCode, extension);
                filePath = uploadPath.resolve(uniqueFilename);
            } while (Files.exists(filePath));

            // 保存文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("文件上传成功: {} -> {}", originalFilename, filePath);

            return new FileUploadDto(
                    "/uploads/" + uniqueFilename,
                    originalFilename + extension,
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

                            // originalFilename 还原逻辑
                            String originalFilename = restoreOriginalFilename(filename);

                            files.add(new FileUploadDto(
                                    "/uploads/" + filename,
                                    originalFilename,
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


    /**
     * 生成指定位数的随机字符串（包含数字和大小写字母）
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 从存储文件名中还原 originalFilename
     * 规则： {originalName}-{randomCode}.{ext}
     */
    private String restoreOriginalFilename(String storedFilename) {
        int dotIndex = storedFilename.lastIndexOf(".");
        String extension = dotIndex != -1 ? storedFilename.substring(dotIndex) : "";
        String namePart = dotIndex != -1 ? storedFilename.substring(0, dotIndex) : storedFilename;

        // 找最后一个 `-`，分离出随机码
        int dashIndex = namePart.lastIndexOf("-");
        if (dashIndex != -1) {
            return namePart.substring(0, dashIndex) + extension;
        } else {
            // 没找到分隔符，直接返回原名
            return storedFilename;
        }
    }
}
