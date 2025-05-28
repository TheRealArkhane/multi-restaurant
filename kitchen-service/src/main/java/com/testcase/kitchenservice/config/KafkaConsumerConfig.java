
package com.testcase.kitchenservice.config;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация для Kafka-консюмеров, обрабатывающих сообщения с типами {@link OrderDTO} и {@link UpdateOrderStatusDTO}.
 * Настроены десериализаторы для обработки сообщений в формате JSON.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final String bootstrapAddress;


    /**
     * Конструктор конфигурации Kafka-потребителей.
     *
     * @param bootstrapAddress адрес(а) Kafka-брокеров, полученные из application.yml/properties
     */
    public KafkaConsumerConfig(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }


    /**
     * Фабрика Kafka-консюмеров для десериализации сообщений типа {@link OrderDTO}.
     * Настроен десериализатор для обработки JSON-сообщений, где значением является {@link OrderDTO}.
     *
     * @return ConsumerFactory для {@link OrderDTO}
     */
    @Bean
    public ConsumerFactory<String, OrderDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        Map<String, Object> valueDeserializerProps = new HashMap<>();
        valueDeserializerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        valueDeserializerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderDTO.class);
        JsonDeserializer<OrderDTO> jsonDeserializer = new JsonDeserializer<>(OrderDTO.class);
        jsonDeserializer.configure(valueDeserializerProps, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }


    /**
     * Фабрика контейнеров Kafka-listeners для обработки сообщений типа {@link OrderDTO}.
     * Используется {@link ConsumerFactory} для десериализации сообщений.
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory} для {@link OrderDTO}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDTO> createOrderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    /**
     * Фабрика Kafka-консюмеров для десериализации сообщений типа {@link UpdateOrderStatusDTO}.
     * Настроен десериализатор для обработки JSON-сообщений, где значением является {@link UpdateOrderStatusDTO}.
     *
     * @return ConsumerFactory для {@link UpdateOrderStatusDTO}
     */
    @Bean
    public ConsumerFactory<String, UpdateOrderStatusDTO> updateOrderStatusConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        Map<String, Object> valueDeserializerProps = new HashMap<>();
        valueDeserializerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        valueDeserializerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UpdateOrderStatusDTO.class);
        JsonDeserializer<UpdateOrderStatusDTO> jsonDeserializer = new JsonDeserializer<>(UpdateOrderStatusDTO.class);
        jsonDeserializer.configure(valueDeserializerProps, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }


    /**
     * Фабрика контейнеров Kafka-listeners для обработки сообщений типа {@link UpdateOrderStatusDTO}.
     * Используется {@link ConsumerFactory} для десериализации сообщений.
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory} для {@link UpdateOrderStatusDTO}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusDTO>
    updateOrderStatusKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updateOrderStatusConsumerFactory());
        return factory;
    }
}

