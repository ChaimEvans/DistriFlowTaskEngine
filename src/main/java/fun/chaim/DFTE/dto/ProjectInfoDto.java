package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目信息数据传输对象
 * 包含工作流名称信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDto {
    
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
     * 工作流名称
     */
    private String workflowName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
