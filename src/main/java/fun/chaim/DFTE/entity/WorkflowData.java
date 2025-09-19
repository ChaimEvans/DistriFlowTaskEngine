package fun.chaim.DFTE.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowData {
    private UUID id;
    private int revision;
    private long lastNodeId;
    private long lastLinkId;
    private List<WorkflowNode> nodes;
    private List<WorkflowLink> links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WorkflowNode {
        private Integer id;
        private String type; // 对应JSON中的type字段
        private Map<String, Object> flags; // 不固定结构
        private Integer order;
        private Integer mode;
        private List<InputPort> inputs;
        private List<OutputPort> outputs;
        private Map<String, Object> properties; // 不固定结构

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InputPort {
            private String name;
            private String type;
            private Integer link; // 可能为null
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OutputPort {
            private String name;
            private String type;
            private List<Integer> links; // 可能为null
        }

        /**
         * 获取当前节点的下一步节点
         * @return 下一步节点
         */
        public Optional<WorkflowNode> getNextNode() {
            for (OutputPort op : this.outputs) {
                if (op.type.equals("-1") && op.links != null) { // 匹配执行引脚
                    WorkflowLink link = linkMap.get(op.links.get(0));
                    return Optional.ofNullable(nodeMap.get(link.toNode));
                }
            }
            return Optional.empty();
        }

        @AllArgsConstructor
        @Getter
        public static class InputDataInfo {
            private String paramName; // 参数名
            private Integer fromNode; // 所连接的节点id
            private String fromSlotName; // 所连接的引脚名
        }

        /**
         * 获取输入数据信息
         * @return 输入数据信息
         */
        public List<InputDataInfo> getInputDataInfo() {
            List<InputDataInfo> inputDataInfo = new ArrayList<>();
            for (InputPort ip : this.inputs) {
                if (ip.type.equals("-1")) continue; // 跳过执行引脚
                if (ip.link != null) {
                    WorkflowLink link = linkMap.get(ip.link); // 获取链接
                    WorkflowNode fromNode = nodeMap.get(link.fromNode); // 获取所连接的节点
                    String slot_name = fromNode.outputs.get(link.fromSlot).name; // 获取所连接的节点输出引脚名称
                    inputDataInfo.add(new InputDataInfo(ip.getName(), link.fromNode, slot_name));
                }
            }
            return inputDataInfo;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowLink {
        private Integer linkId;
        private Integer fromNode;
        private Integer fromSlot;
        private Integer toNode;
        private Integer toSlot;
        private String type;
    }

    @JsonIgnore
    @Getter
    private Optional<WorkflowNode> beginNode;
    @JsonIgnore
    private LinkedHashMap<Integer, WorkflowNode> nodeMap;
    @JsonIgnore
    private LinkedHashMap<Integer, WorkflowLink> linkMap;

    public Optional<WorkflowNode> findNodeById(int id) {
        if (!nodeMap.containsKey(id)) return Optional.empty();
        return Optional.of(nodeMap.get(id));
    }

    /*
     * 整理数据（转换时调用）
     */
    public WorkflowData disposal() {
        for (WorkflowNode node : this.nodes) {
            if (node.getType().equals("workflow_begin")) {
                this.beginNode = Optional.of(node);
                break;
            }
        }
        this.nodeMap = new LinkedHashMap<>();
        this.nodeMap.putAll(this.nodes.stream().collect(Collectors.toMap(WorkflowNode::getId, v -> v)));
        this.linkMap = new LinkedHashMap<>();
        this.linkMap.putAll(this.links.stream().collect(Collectors.toMap(WorkflowLink::getLinkId, v -> v)));
        return this;
    }
}