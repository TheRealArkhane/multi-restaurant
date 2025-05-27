package com.testcase.kitchenservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурационный класс Kafka, создающий необходимые топики для взаимодействия между микросервисами.
 * Используется в waiter-service и kitchen-service для создания заказов,
 * получения обновлений от кухни и отправки статусов обратно.
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Топик для получения в waiter-service обновлений статусов заказов от кухни.
     * Используется сервисом кухни для отправки обновлённых статусов заказов.
     *
     * @return новый Kafka-топик с именем "kitchen-order-status-updates-topic"
     */
    @Bean
    public NewTopic kitchenOrderStatusUpdatesTopic() {
        return TopicBuilder.name("kitchen-order-status-updates-topic")
                .partitions(1)
                .build();
    }
}
