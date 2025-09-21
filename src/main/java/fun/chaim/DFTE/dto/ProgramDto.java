package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import fun.chaim.DFTE.entity.Program;

/**
 * 处理程序数据传输对象
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto {
    
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

    /*
     * 是否内置程序
     */
    private boolean buildin;
    
    /**
     * 上传到服务器中的文件路径
     */
    private String file;
    
    /**
     * 启动命令
     */
    private String cmd;
    
    /**
     * 运行时上锁状态
     */
    private Boolean lock;
    
    /**
     * 输入参数列表
     */
    private List<ParamDto> inputParams;
    
    /**
     * 返回值列表
     */
    private List<ParamDto> outputParams;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体类转换成数据传输对象
     * 
     * @param program 实体类
     * @param inputParams 输入参数列表
     * @param outputParams 返回值列表
     * @return 数据传输对象
     */
    public static ProgramDto fromEntity(Program program, List<ParamDto> inputParams, List<ParamDto> outputParams) {
        return new ProgramDto(
            program.getId(),
            program.getName(),
            program.getTitle(),
            program.getDescription(),
            program.getBuildin(),
            program.getFile(),
            program.getCmd(),
            program.getLock(),
            inputParams,
            outputParams,
            program.getCreatedAt(),
            program.getUpdatedAt()
        );
    }
}
