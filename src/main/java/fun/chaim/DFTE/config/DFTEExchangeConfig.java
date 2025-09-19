package fun.chaim.DFTE.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DFTEExchangeConfig {

    // Direct 交换机
    @Bean
    public DirectExchange dfteExchange() {
        return new DirectExchange("DFTE.Exchange", true, false);
    }

    // 三个队列
    @Bean
    public Queue dfteTaskInsideQueue() {
        return new Queue("DFTE.Queue.TaskInside", true);
    }

    @Bean
    public Queue dfteTaskQueue() {
        return new Queue("DFTE.Queue.Task", true);
    }

    @Bean
    public Queue dfteReportQueue() {
        return new Queue("DFTE.Queue.Report", true);
    }

    // 队列绑定到交换机并指定路由键
    @Bean
    public Binding bindTaskInside(DirectExchange dftExchange, Queue dfteTaskInsideQueue) {
        return BindingBuilder.bind(dfteTaskInsideQueue)
                .to(dftExchange)
                .with("task.inside");
    }

    @Bean
    public Binding bindTask(DirectExchange dftExchange, Queue dfteTaskQueue) {
        return BindingBuilder.bind(dfteTaskQueue)
                .to(dftExchange)
                .with("task");
    }

    @Bean
    public Binding bindReport(DirectExchange dftExchange, Queue dfteReportQueue) {
        return BindingBuilder.bind(dfteReportQueue)
                .to(dftExchange)
                .with("report");
    }
}