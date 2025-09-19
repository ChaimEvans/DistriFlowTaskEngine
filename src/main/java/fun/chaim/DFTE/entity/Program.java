package fun.chaim.DFTE.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理程序实体类
 * 用于存储可执行程序的基本信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Entity
@Table(name = "program")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    
    /**
     * 程序ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 程序标识名称，唯一
     */
    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;
    
    /**
     * 程序显示名称
     */
    @Column(name = "title", length = 128)
    private String title;
    
    /**
     * 程序描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 是否是内置函数
     */
    @Column(name = "buildin", nullable = false)
    private Boolean buildin = false;
    
    /**
     * 上传到服务器中的文件路径
     */
    @Column(name = "file", length = 512)
    private String file;
    
    /**
     * 启动命令
     */
    @Column(name = "cmd", length = 512)
    private String cmd;
    
    /**
     * 运行时上锁，上锁不可修改
     */
    @Column(name = "`lock`", nullable = false)
    private Boolean lock = false;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 实体更新前自动更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
