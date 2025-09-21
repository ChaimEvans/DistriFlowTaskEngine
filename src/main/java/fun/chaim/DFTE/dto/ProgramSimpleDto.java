package fun.chaim.DFTE.dto;

import fun.chaim.DFTE.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理程序简单信息数据传输对象
 * 用于列表展示，不包含敏感信息
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSimpleDto {
    
    /**
     * 程序ID
     */
    private Integer id;
    
    /**
     * 程序标识名称
     */
    private String name;
    
    /**
     * 程序显示名称
     */
    private String title;
    
    /**
     * 程序描述
     */
    private String description;

    /**
     * 是否内置程序
     */
    private boolean buildin;
    
    /**
     * 运行时上锁状态
     */
    private Boolean lock;

    /**
     * 从实体类转换成数据传输对象
     * 
     * @param program 实体类
     * @return 数据传输对象
     */
    public static ProgramSimpleDto fromEntity(Program program) {
        return new ProgramSimpleDto(
            program.getId(),
            program.getName(),
            program.getTitle(),
            program.getDescription(),
            program.getBuildin(),
            program.getLock()
        );
    }
}
