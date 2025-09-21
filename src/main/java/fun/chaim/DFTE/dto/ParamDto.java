package fun.chaim.DFTE.dto;

import fun.chaim.DFTE.entity.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamDto {
    
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
     * 是否是返回值
     */
    private Boolean retval;
    
    /**
     * 是否为必须参数
     */
    private Boolean require;

    /**
     * 从实体类转换成数据传输对象
     * 
     * @param param 实体类
     * @return 数据传输对象
     */
    public static ParamDto fromEntity(Param param) {
        return new ParamDto(
            param.getId(),
            param.getName(),
            param.getType(),
            param.getDescription(),
            param.getRetval(),
            param.getRequire()
        );
    }
}
