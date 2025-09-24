package fun.chaim.DFTE.entity;

import fun.chaim.DFTE.dto.ProcessingNodeStatusData;
import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingNode {
    private String id;

    private Session session;

    private String name;

    private String addresss;

    private String mac;

    private Boolean online = true;

    private ProcessingNodeStatusData statusData;
}
