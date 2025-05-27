package com.testcase.waiterservice.config;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурационный класс, настраивающий Kafka-продюсеров для отправки сообщений
 * между микросервисами waiter-service и kitchen-service.
 * Содержит настройки для отправки заказов (OrderDTO) и обновлений статуса заказов (UpdateOrderStatusDTO).
 */
@Configuration
public class KafkaProducerConfig {

    private final String bootstrapAddress;

    /**
     * Конструктор, инициализирующий адрес Kafka-брокеров.
     *
     * @param bootstrapAddress значение из свойства spring.kafka.bootstrap-servers
     */
    public KafkaProducerConfig(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }


    /**
     * Фабрика Kafka-продюсера для отправки сообщений типа {@link OrderDTO}.
     *
     * @return настроенная {@link ProducerFactory}
     */
    @Bean
    public ProducerFactory<String, OrderDTO> orderProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    /**
     * KafkaTemplate для отправки заказов (OrderDTO) на кухню.
     *
     * @return настроенный {@link KafkaTemplate}
     */
    @Bean
    public KafkaTemplate<String, OrderDTO> orderCreationKafkaTemplate() {
        return new KafkaTemplate<>(orderProducerFactory());
    }


    /**
     * Фабрика Kafka-продюсера для отправки сообщений типа {@link UpdateOrderStatusDTO}.
     *
     * @return настроенная {@link ProducerFactory}
     */
    @Bean
    public ProducerFactory<String, UpdateOrderStatusDTO> updateOrderStatusProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    /**
     * KafkaTemplate для отправки обновлений статуса заказов (UpdateOrderStatusDTO).
     *
     * @return настроенный {@link KafkaTemplate}
     */
    @Bean
    public KafkaTemplate<String, UpdateOrderStatusDTO> updateOrderStatusKafkaTemplate() {
        return new KafkaTemplate<>(updateOrderStatusProducerFactory());
    }
}
