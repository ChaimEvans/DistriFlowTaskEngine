package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数信息数据传输对象
 * 包含父级信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamInfoDto {
    
    /**
     * 参数ID
     */
    private Integer id;
    
    /**
     * 参数名称
     */
    private String name;
    
    /**
     * 参数类型
     */
    private String type;
    
    /**
     * 参数描述
     */
    private String description;
    
    /**
     * 是否为必须参数
     */
    private Boolean require;
    
    /**
     * 父级ID（处理程序ID或工作流ID）
     */
    private Integer parentId;
    
    /**
     * 父级名称（处理程序名称或工作流名称）
     */
    private String parentName;
}
