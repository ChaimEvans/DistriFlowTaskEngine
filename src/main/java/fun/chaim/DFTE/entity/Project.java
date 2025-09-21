package fun.chaim.DFTE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;

/**
 * 项目实体类
 * 用于存储项目的基本信息和工作流配置
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    /**
     * 项目ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 项目名称
     */
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    /**
     * 是否已完成
     */
    @Column(name = "finish", nullable = false)
    private Boolean finish = false;
    
    /**
     * 所使用的工作流ID（外键）
     */
    @Column(name = "workflow")
    private Integer workflow;
    
    /**
     * 工作流输入数据，JSON格式
     */
    @Column(name = "workflow_input", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ObjectNode workflowInput = JsonNodeFactory.instance.objectNode();
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * 关联的工作流实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Workflow workflowEntity;
}
