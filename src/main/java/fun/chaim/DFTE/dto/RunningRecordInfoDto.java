package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 运行记录信息数据传输对象
 * 包含关联实体名称信息，不包含敏感数据
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunningRecordInfoDto {
    
    /**
     * 运行记录ID
     */
    private Integer id;
    
    /**
     * 对应的工作流ID
     */
    private Integer workflow;
    
    /**
     * 工作流名称
     */
    private String workflowName;
    
    /**
     * 隶属的项目ID
     */
    private Integer project;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 是否已结束
     */
    private Boolean finish;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
