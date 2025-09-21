package fun.chaim.DFTE.dto.projection;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TaskProjections {
    /**
     * 列表页任务信息投影（无 params、retdata）
     */
    interface TaskInfo {
        UUID getUuid();

        String getName();

        Integer getRunningRecord();

        Integer getProject();

        Integer getWorkflow();

        Integer getNodeId();

        Integer getProgram();

        Integer getStatus();

        String getRetmsg();

        LocalDateTime getCreatedAt();

        LocalDateTime getUpdatedAt();

        String getProjectName();

        String getWorkflowName();

        String getProgramName();
    }

    /**
     * 详情页任务信息投影（包含 parent、params、retdata）
     */
    interface TaskDetail {
        UUID getUuid();

        String getName();

        UUID getParent();

        String getParentName();

        Integer getRunningRecord();

        Integer getProject();

        Integer getWorkflow();

        Integer getNodeId();

        Integer getProgram();

        ObjectNode getParams();

        Integer getStatus();

        String getRetmsg();

        ArrayNode getRetdata();

        LocalDateTime getCreatedAt();

        LocalDateTime getUpdatedAt();

        String getProjectName();

        String getWorkflowName();

        String getProgramName();
    }
}
