package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 任务信息数据传输对象
 * 包含关联实体名称信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDto {
    
    /**
     * 任务UUID
     */
    private UUID uuid;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 所隶属的运行记录ID
     */
    private Integer runningRecord;
    
    /**
     * 隶属的项目ID
     */
    private Integer project;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 隶属的工作流ID
     */
    private Integer workflow;
    
    /**
     * 工作流名称
     */
    private String workflowName;
    
    /**
     * 对应工作流JSON节点的ID
     */
    private Integer nodeId;
    
    /**
     * 使用的处理程序ID
     */
    private Integer program;
    
    /**
     * 处理程序名称
     */
    private String programName;
    
    /**
     * 任务状态
     * -2：失败，-1：跳过，0：等待中，1：处理中，2：成功
     */
    private Integer status;
    
    /**
     * 任务结束时返回的文本型消息
     */
    private String retmsg;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
