package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 运行记录数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunningRecordDto {
    
    /**
     * 运行记录ID
     */
    private Integer id;
    
    /**
     * 对应的工作流ID
     */
    private Integer workflow;
    
    /**
     * 对应的工作流的数据拷贝，JSON格式
     */
    private String workflowData;
    
    /**
     * 工作流输入数据，JSON格式
     */
    private String workflowInput;
    
    /**
     * 工作流输出数据，JSON格式
     */
    private String workflowOutput;
    
    /**
     * 隶属的项目ID
     */
    private Integer project;
    
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
