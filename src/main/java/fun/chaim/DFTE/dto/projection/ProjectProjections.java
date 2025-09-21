package fun.chaim.DFTE.dto.projection;

import java.time.LocalDateTime;

/**
 * 项目相关投影接口
 * 
 * @author chaim
 * @since 1.0.0
 */
public interface ProjectProjections {

    /**
     * 项目信息（包含工作流信息）
     */
    interface ProjectInfo {
        Integer getId();

        String getName();

        String getWorkflowName();

        Boolean getFinish();

        LocalDateTime getCreatedAt();
    }
}
