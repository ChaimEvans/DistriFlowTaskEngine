package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import fun.chaim.DFTE.entity.Workflow;

/**
 * 工作流简单信息数据传输对象
 * 用于列表展示，不包含敏感数据
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowSimpleDto {
    
    /**
     * 工作流ID
     */
    private Integer id;
    
    /**
     * 工作流名称
     */
    private String name;
    
    /**
     * 工作流描述
     */
    private String description;
    
    /**
     * 运行时上锁状态
     */
    private Boolean lock;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体对象转换为数据传输对象
     * 
     * @param workflow 实体对象
     * @return 数据传输对象
     */
    public static WorkflowSimpleDto fromEntity(Workflow workflow) {
        return new WorkflowSimpleDto(
            workflow.getId(),
            workflow.getName(),
            workflow.getDescription(),
            workflow.getLock(),
            workflow.getCreatedAt(),
            workflow.getUpdatedAt()
        );
    }
}
