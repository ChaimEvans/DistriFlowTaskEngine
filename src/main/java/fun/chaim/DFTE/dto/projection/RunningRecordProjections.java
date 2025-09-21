package fun.chaim.DFTE.dto.projection;

import java.time.LocalDateTime;

public interface RunningRecordProjections {
    interface RunningRecordInfoView {
        /**
         * 运行记录ID
         */
        Integer getId();

        /**
         * 对应的工作流ID
         */
        Integer getWorkflow();

        /**
         * 工作流名称
         */
        String getWorkflowName();

        /**
         * 隶属的项目ID
         */
        Integer getProject();

        /**
         * 项目名称
         */
        String getProjectName();

        /**
         * 是否已结束
         */
        Boolean getFinish();

        /**
         * 创建时间
         */
        LocalDateTime getCreatedAt();

        /**
         * 更新时间
         */
        LocalDateTime getUpdatedAt();
    }

}
