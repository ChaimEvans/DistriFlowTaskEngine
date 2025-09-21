package fun.chaim.DFTE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import fun.chaim.DFTE.converter.WorkflowDataConverter;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 工作流实体类
 * 用于存储工作流的定义和配置信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "workflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {
    
    /**
     * 工作流ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 工作流名称
     */
    @Column(name = "name", length = 128, nullable = false)
    private String name;
    
    /**
     * 工作流描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 工作流数据，JSON格式
     */
    @Column(name = "data", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private String data;
    
    /**
     * 运行时上锁，上锁不可修改
     */
    @Column(name = "`lock`", nullable = false)
    private Boolean lock = false;
    
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

    public Optional<WorkflowData> getWorkflowData() {
        if (data == null || data.isEmpty()) return Optional.empty();
        return Optional.ofNullable(new WorkflowDataConverter().convertToEntityAttribute(this.data));
    }
}
