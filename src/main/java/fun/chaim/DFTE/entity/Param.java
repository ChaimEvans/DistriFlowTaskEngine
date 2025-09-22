package fun.chaim.DFTE.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数实体类
 * 用于存储处理程序或工作流的参数定义
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "param")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Param {
    
    /**
     * 参数ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 隶属的处理程序ID（外键）
     */
    @Column(name = "program")
    private Integer program;
    
    /**
     * 隶属的工作流ID（外键）
     */
    @Column(name = "workflow")
    private Integer workflow;
    
    /**
     * 参数名称
     */
    @Column(name = "name", length = 128, nullable = false)
    private String name;
    
    /**
     * 参数类型
     */
    @Column(name = "type", length = 128, nullable = false)
    private String type = "*";
    
    /**
     * 参数描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 是否是返回值
     */
    @Column(name = "retval", nullable = false)
    private Boolean retval = false;
    
    /**
     * 是否为必须参数
     */
    @Column(name = "`require`", nullable = false)
    private Boolean require = true;
    
    /**
     * 关联的处理程序实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Program programEntity;
    
    /**
     * 关联的工作流实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workflow workflowEntity;
}
