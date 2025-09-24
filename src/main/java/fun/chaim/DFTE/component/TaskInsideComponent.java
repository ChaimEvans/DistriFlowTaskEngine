package fun.chaim.DFTE.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fun.chaim.DFTE.dto.TaskInQueueDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.repository.ParamRepository;
import fun.chaim.DFTE.repository.ProgramRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskInsideComponent {

    private final ProgramRepository programRepository;
    private final ParamRepository paramRepository;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "DFTE.Queue.TaskInside")
    public void receiveMessage(TaskInQueueDto data) {
        log.info("收到信息: {}", data);
        rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), 1, null, null));
        Executor executor = null;
        try {
            executor = Executor.createInstance(data.getProgramName());
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), -2, e.getMessage(), null));
            return;
        }
        if (executor != null) {
            try {
                Map.Entry<String, ArrayNode> result = executor.execute(data.getParams());
                log.info("执行结果: {}", result);
                if (result.getKey().isEmpty()) // 有内容标记跳过
                    rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), 2, null, result.getValue()));
                else
                    rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), -1, result.getKey(), result.getValue()));
            } catch (Exception e) {
                log.error("执行程序时发送错误: {}", e.getMessage());
                rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), -2, String.format("执行程序时发送错误: %s", e.getMessage()), null));
                return;
            }
        } else {
            log.error("无法运行程序: {}", data.getProgramName());
            rabbitTemplate.convertAndSend("DFTE.Exchange", "report", makeResponse(data.getUuid().toString(), -2, String.format("无法运行程序: %s", data.getProgramName()), null));
            return;
        }
    }

    private static ObjectNode makeResponse(String uuid, Integer status, String retmsg, ArrayNode retdata) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.set("uuid", JsonNodeFactory.instance.textNode(uuid));
        response.set("status", JsonNodeFactory.instance.numberNode(status));
        response.set("retmsg", JsonNodeFactory.instance.textNode(retmsg));
        response.set("retdata", retdata);
        response.set("timestamp", JsonNodeFactory.instance.numberNode(System.currentTimeMillis()));
        return response;
    }

    /**
     * 自动初始化数据插入数据库
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("Initialize the internal program data. Len: {}", Executor.registry.size());
        for (String name : Executor.registry.keySet()) {
            log.info("注册内部程序: " + name);
            if (!programRepository.existsByName(name)) {
                log.info("插入数据库: " + name);
                Program program_unsave = new Program();
                program_unsave.setName(name);
                program_unsave.setTitle(name);
                program_unsave.setDescription(Executor.registry_desc.get(name));
                program_unsave.setBuildin(true);
                program_unsave.setLock(true);
                Program program = programRepository.save(program_unsave);
                for (Object[] param : Executor.registry_params.get(name)) {
                    Param p = new Param();
                    p.setProgram(program.getId());
                    p.setName((String) param[0]);
                    p.setDescription((String) param[1]);
                    p.setType((String) param[2]);
                    p.setRequire((Boolean) param[3]);
                    p.setRetval(false);
                    paramRepository.save(p);
                }
                for (String[] ret : Executor.registry_returns.get(name)) {
                    Param p = new Param();
                    p.setProgram(program.getId());
                    p.setName(ret[0]);
                    p.setDescription(ret[1]);
                    p.setType(ret[2]);
                    p.setRetval(true);
                    paramRepository.save(p);
                }
            }
        }
    }

    public static abstract class Executor {
        private static final Map<String, Class<? extends Executor>> registry = new HashMap<>();
        private static final Map<String, String> registry_desc = new HashMap<>();
        private static final Map<String, List<Object[]>> registry_params = new HashMap<>(); // 名称、描述、类型、必填
        private static final Map<String, List<String[]>> registry_returns = new HashMap<>(); // 名称、描述、类型

        /**
         * 注册派生类
         *
         * @param identifier 派生类标识
         * @param desc       派生类描述
         * @param clazz      派生类类对象
         */
        protected static void registerSubclass(String identifier, String desc, Class<? extends Executor> clazz, List<Object[]> params, List<String[]> returns) {
            registry.put(identifier, clazz);
            registry_desc.put(identifier, desc);
            registry_params.put(identifier, params == null ? Collections.emptyList() : params);
            registry_returns.put(identifier, returns == null ? Collections.emptyList() : returns);
        }
        
        // 根据文本标识创建实例
        /**
         * 根据文本标识创建实例
         *
         * @param identifier 派生类标识
         * @return 派生类实例
         * @throws ClassNotFoundException 如果未找到派生类
         * @throws InstantiationException 如果实例化失败
         */
        public static Executor createInstance(String identifier)
                throws ClassNotFoundException, InstantiationException {
            Class<? extends Executor> clazz = registry.get(identifier);

            if (clazz == null) {
                throw new ClassNotFoundException("未注册的派生类: " + identifier);
            }

            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new InstantiationException("实例化失败: " + e.getMessage());
            }
        }

        /**
         * 执行任务
         *
         * @param args 任务参数
         * @return 任务结果
         */
        public Map.Entry<String, ArrayNode> execute(ObjectNode args) {
            return Map.entry("", JsonNodeFactory.instance.arrayNode()); // String 有内容则作为msg返回，并且该任务标记为跳过
        }
    }
}
