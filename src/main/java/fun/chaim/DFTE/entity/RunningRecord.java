package fun.chaim.DFTE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;

/**
 * 运行记录实体类
 * 用于存储工作流执行的历史记录
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "running_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunningRecord {
    
    /**
     * 运行记录ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 对应的工作流ID（外键）
     */
    @Column(name = "workflow")
    private Integer workflow;
    
    /**
     * 对应的工作流的数据拷贝，JSON格式（仅事后查看用）
     */
    @Column(name = "workflow_data", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String workflowData;
    
    /**
     * 工作流输入数据，JSON格式
     */
    @Column(name = "workflow_input", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ObjectNode workflowInput = JsonNodeFactory.instance.objectNode();
    
    /**
     * 工作流输出数据，JSON格式
     */
    @Column(name = "workflow_output", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private String workflowOutput;
    
    /**
     * 隶属的项目ID（外键）
     */
    @Column(name = "project")
    private Integer project;
    
    /**
     * 是否已结束
     */
    @Column(name = "finish", nullable = false)
    private Boolean finish = false;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 关联的工作流实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow", insertable = false, updatable = false)
    private Workflow workflowEntity;
    
    /**
     * 关联的项目实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project", insertable = false, updatable = false)
    private Project projectEntity;
    
    /**
     * 实体保存前自动设置时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 实体更新前自动更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
