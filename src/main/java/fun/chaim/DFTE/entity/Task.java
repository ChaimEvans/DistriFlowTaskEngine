package fun.chaim.DFTE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 任务实体类
 * 用于存储任务执行的基本信息和状态
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    /**
     * 任务UUID，主键
     */
    @Id
    @Column(name = "uuid", columnDefinition = "BINARY(16)")
    private UUID uuid;

    /**
     * 父任务
     */
    @Column(name = "parent")
    private UUID parent;
    
    /**
     * 任务名称
     */
    @Column(name = "name", length = 128)
    private String name;
    
    /**
     * 所隶属的运行记录ID（外键）
     */
    @Column(name = "running_record", nullable = false)
    private Integer runningRecord;
    
    /**
     * 隶属的项目ID（外键）
     */
    @Column(name = "project")
    private Integer project;
    
    /**
     * 隶属的工作流ID（外键）
     */
    @Column(name = "workflow")
    private Integer workflow;
    
    /**
     * 对应工作流JSON节点的ID
     */
    @Column(name = "node_id")
    private Integer nodeId;
    
    /**
     * 使用的处理程序ID（外键）
     */
    @Column(name = "program")
    private Integer program;
    
    /**
     * 传入的参数，JSON格式
     */
    @Column(name = "params", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ObjectNode params = JsonNodeFactory.instance.objectNode();
    
    /**
     * 任务状态
     * -2：失败，-1：跳过，0：等待中，1：处理中，2：成功
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;
    
    /**
     * 任务结束时返回的文本型消息
     */
    @Column(name = "retmsg", columnDefinition = "TEXT")
    private String retmsg;
    
    /**
     * 任务成功后返回的JSON数据
     */
    @Column(name = "retdata", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ArrayNode retdata = JsonNodeFactory.instance.arrayNode();

    /**
     * 处理该任务的节点的MAC地址
     */
    @Column(name = "processing_node_mac", length = 17)
    private String processingNodeMac;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * 关联的运行记录实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_record", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RunningRecord runningRecordEntity;
    
    /**
     * 关联的项目实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Project projectEntity;
    
    /**
     * 关联的工作流实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Workflow workflowEntity;
    
    /**
     * 关联的处理程序实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Program programEntity;
}
