package fun.chaim.DFTE.service.TaskInsideExecutorImplement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fun.chaim.DFTE.service.TaskInsideService.Executor;;

public class DirectPipeline extends Executor {
    static {
        registerSubclass("DirectPipeline", "直接输出输入内容", DirectPipeline.class);
    }

    @Override
    public ArrayNode execute(ObjectNode args) {
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        ret.add(args);
        return ret;
    }
}
