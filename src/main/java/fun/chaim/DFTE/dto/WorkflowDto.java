package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import fun.chaim.DFTE.entity.Workflow;

/**
 * 工作流数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDto {
    
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
     * 工作流数据，JSON格式
     */
    private String data;
    
    /**
     * 运行时上锁状态
     */
    private Boolean lock;

    /**
     * 输入参数列表
     */
    private List<ParamDto> inputParams;
    
    /**
     * 返回值列表
     */
    private List<ParamDto> outputParams;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体类转换成数据传输对象
     * 
     * @param workflow 工作流实体类
     * @param inputParams 输入参数列表
     * @param outputParams 返回值列表
     * @return 数据传输对象
     */
    public static WorkflowDto fromEntity(Workflow workflow, List<ParamDto> inputParams, List<ParamDto> outputParams) { 
        return new WorkflowDto(
            workflow.getId(),
            workflow.getName(),
            workflow.getDescription(),
            workflow.getData(),
            workflow.getLock(),
            inputParams,
            outputParams,
            workflow.getCreatedAt(),
            workflow.getUpdatedAt()
        );
    }
}
