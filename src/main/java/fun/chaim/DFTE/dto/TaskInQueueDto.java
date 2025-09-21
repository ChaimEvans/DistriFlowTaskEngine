package fun.chaim.DFTE.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInQueueDto {
    private UUID uuid;
    private Integer program;
    private ObjectNode params;
    private String name;
    private String parentName;
    private String projectName;
    private String workflowName;
    private String programName;
}