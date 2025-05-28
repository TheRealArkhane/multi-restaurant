package com.testcase.waiterservice.config;

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
     * Топик для отправки новых заказов на кухню.
     * Используется официантом для публикации заказов.
     *
     * @return новый Kafka-топик с именем "order-creation-topic"
     */
    @Bean
    public NewTopic orderCreationTopic() {
        return TopicBuilder.name("order-creation-topic")
                .partitions(1)
                .build();
    }


    /**
     * Топик для отправки из waiter-service обновлённого статуса заказа на кухню.
     *
     * @return новый Kafka-топик с именем "waiter-order-status-updates-topic"
     */
    @Bean
    public NewTopic waiterOrderStatusUpdatesTopic() {
        return TopicBuilder.name("waiter-order-status-updates-topic")
                .partitions(1)
                .build();
    }
}
