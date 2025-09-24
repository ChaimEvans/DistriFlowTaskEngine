package fun.chaim.DFTE.component.TaskInsideExecutorImplement;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fun.chaim.DFTE.component.TaskInsideComponent.Executor;

@Component
public class WorkflowBegin extends Executor {
    static {
        registerSubclass("Workflow Begin", "工作流开始节点", DirectPipeline.class, null, null);
    }

    @Override
    public Map.Entry<String, ArrayNode> execute(ObjectNode args) {
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        ret.add(args);
        return Map.entry("", ret);
    }
}
