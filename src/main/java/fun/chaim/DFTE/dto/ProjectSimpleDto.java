package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目简单信息数据传输对象
 * 用于工作流关联项目列表
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSimpleDto {
    
    /**
     * 项目ID
     */
    private Integer id;
    
    /**
     * 项目名称
     */
    private String name;
}
