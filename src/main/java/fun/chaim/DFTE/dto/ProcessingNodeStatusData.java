package fun.chaim.DFTE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingNodeStatusData {
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
}
