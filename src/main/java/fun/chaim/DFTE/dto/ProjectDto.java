package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 项目数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    
    /**
     * 项目ID
     */
    private Integer id;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 所使用的工作流ID
     */
    private Integer workflow;
    
    /**
     * 工作流输入数据，JSON格式
     */
    private JsonNode workflowInput;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
