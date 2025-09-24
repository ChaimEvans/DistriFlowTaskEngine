package fun.chaim.DFTE.component.TaskInsideExecutorImplement;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fun.chaim.DFTE.component.TaskInsideComponent.Executor;

@Component
public class DirectPipeline extends Executor {
    static {
        registerSubclass(
            "DirectPipeline",
            "直接输出输入内容", 
            DirectPipeline.class,
            List.<Object[]>of(new Object[]{"in", "输入", "*", true}),
            List.<String[]>of(new String[]{"out", "输出", "*"})
        );
    }

    @Override
    public Map.Entry<String, ArrayNode> execute(ObjectNode args) {
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        ret.add(args);
        return Map.entry("", ret);
    }
}
