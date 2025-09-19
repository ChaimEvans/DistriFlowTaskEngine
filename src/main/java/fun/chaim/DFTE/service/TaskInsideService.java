package fun.chaim.DFTE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.Channel;

import fun.chaim.DFTE.dto.TaskInQueueDto;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.repository.ProgramRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInsideService {

    private final ProgramRepository programRepository;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "DFTE.Queue.TaskInside")
    public void receiveMessage(TaskInQueueDto data, Channel channel, Message message) {
        log.info("收到信息: {}", data);
        Executor executor = null;
        try {
            executor = Executor.createInstance(data.getProgramName());
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        if (executor != null) {
            try {
                ArrayNode result = executor.execute(data.getParams());
                log.info("执行结果: {}", result);
                rabbitTemplate.convertAndSend("DFTE.Exchange", "report", result);
            } catch (Exception e) {
                log.error("执行程序时发送错误: {}", e.getMessage());
            }
        } else {
            log.error("无法运行程序: {}", data.getProgramName());
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("无法确认信息: \n\tDeliveryTag: {}\n\tUUID: {}\n\t原因: {}",
                    message.getMessageProperties().getDeliveryTag(),
                    data.getUuid(),
                    e.getMessage()
                );
        }
    }

    /**
     * 自动初始化数据插入数据库
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        for (String name : Executor.registry.keySet()) {
            if (!programRepository.existsByName(name))
                programRepository.save(new Program(null, name, name, Executor.registry_desc.get(name), true, null, null, true, null));
        }
    }

    public static abstract class Executor {
        private static final Map<String, Class<? extends Executor>> registry = new HashMap<>();
        private static final Map<String, String> registry_desc = new HashMap<>();

        /**
         * 注册派生类
         *
         * @param identifier 派生类标识
         * @param desc       派生类描述
         * @param clazz      派生类类对象
         */
        protected static void registerSubclass(String identifier, String desc, Class<? extends Executor> clazz) {
            registry.put(identifier.toLowerCase(), clazz);
            registry_desc.put(desc, desc);
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
            String key = identifier.toLowerCase();
            Class<? extends Executor> clazz = registry.get(key);

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
        public ArrayNode execute(ObjectNode args) {
            return JsonNodeFactory.instance.arrayNode();
        }
    }
}
