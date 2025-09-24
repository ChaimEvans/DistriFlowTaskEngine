package fun.chaim.DFTE.dto;

import fun.chaim.DFTE.entity.ProcessingNode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessingNodeStatusDto {
    /**
     * 节点ID (WebSocket连接ID)
     */
    String id;

    /**
     * 节点名称
     */
    String name;

    /**
     * 节点地址
     */
    String address;

    /**
     * 节点MAC
     */
    String mac;

    /**
     * 在线状态
     */
    Boolean online;

    /**
     * CPU名称
     */
    String cpuName;

    /**
     * CPU使用率
     */
    Float cpuUsage;

    /**
     * 内存总大小
     */
    Integer memoryTotal;

    /**
     * 内存剩余
     */
    Integer memoryFree;

    /**
     * GPU名称
     */
    String gpuName;

    /**
     * GPU使用率
     */
    Float gpuUsage;

    /**
     * 显存总大小
     */
    Integer gpuMemoryTotal;

    /**
     * 显存剩余
     */
    Integer gpuMemoryFree;

    /**
     * 正在处理的任务数
     */
    Integer taskProcessing;

    /**
     * 已处理的任务数
     */
    Integer taskProcessed;

    /**
     * 失败任务数
     */
    Integer taskFailed;

    public static ProcessingNodeStatusDto fromEntity(ProcessingNode node) {
        ProcessingNodeStatusData statusData = node.getStatusData();
        return new ProcessingNodeStatusDto(
            node.getId(),
            node.getName(),
            node.getAddresss(),
            node.getMac(),
            node.getOnline(),
            statusData.getCpuName(),
            statusData.getCpuUsage(),
            statusData.getMemoryTotal(),
            statusData.getMemoryFree(),
            statusData.getGpuName(),
            statusData.getGpuUsage(),
            statusData.getGpuMemoryTotal(),
            statusData.getGpuMemoryFree(),
            statusData.getTaskProcessing(),
            statusData.getTaskProcessed(),
            statusData.getTaskFailed()
        );
    }
}
