package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDto {
    
    /**
     * 文件下载路径
     */
    private String downloadPath;
    
    /**
     * 原始文件名
     */
    private String originalFilename;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}
