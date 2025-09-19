package fun.chaim.DFTE.dto;

import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 队列中的任务信息数据传输对象
 * @author Chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInQueueDto {
    /**
     * 任务UUID
     */
    private UUID uuid;
    
    /**
     * 任务名称
     */
    private String name;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 使用的处理程序ID
     */
    private Integer program;

    /**
     * 处理程序名称
     */
    private String programName;

    /**
     * 传入的参数，JSON格式
     */
    private ObjectNode params;
}
