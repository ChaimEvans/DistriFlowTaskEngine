package fun.chaim.DFTE.dto.projection;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface RunningRecordProjections {
    interface RunningRecordInfo {
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

    interface RunningRecordDetail {
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
         * 启动时的工作流数据
         */
        String getWorkflowData();

        /**
         * 启动时工作流输入参数
         */
        ObjectNode getWorkflowInput();

        /**
         * 启动时工作流输出结果
         */
        String getWorkflowOutput();

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
