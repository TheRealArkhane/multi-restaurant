package com.testcase.waiterservice.config;

import com.testcase.commondto.UpdateOrderStatusDTO;
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
 * Конфигурационный класс Kafka-консюмера.
 * Используется для настройки компонентов, необходимых для получения сообщений от Kafka-брокера,
 * таких как фабрика потребителей и контейнер listeners.
 * Обрабатывает сообщения с обновлённым статусом заказов, приходящие из kitchen-service.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final String bootstrapAddress;

    /**
     * Конструктор, инициализирующий адрес Kafka-брокеров.
     *
     * @param bootstrapAddress значение из свойства spring.kafka.bootstrap-servers
     */
    public KafkaConsumerConfig(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }


    /**
     * Фабрика Kafka-консюмеров, обрабатывающих сообщения типа {@link UpdateOrderStatusDTO}.
     * Настраивает:
     * - десериализацию ключей как строки;
     * - десериализацию значений через {@link JsonDeserializer} с поддержкой обработки ошибок.
     *
     * @return {@link ConsumerFactory} для использования в KafkaListener
     */
    @Bean
    public ConsumerFactory<String, UpdateOrderStatusDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);

        Map<String, Object> valueDeserializerProps = new HashMap<>();
        valueDeserializerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        valueDeserializerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UpdateOrderStatusDTO.class);
        JsonDeserializer<UpdateOrderStatusDTO> jsonDeserializer = new JsonDeserializer<>(UpdateOrderStatusDTO.class);
        jsonDeserializer.configure(valueDeserializerProps, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }


    /**
     * Фабрика контейнеров для аннотированных методов KafkaListener.
     * Используется для прослушивания Kafka-топиков с сообщениями типа {@link UpdateOrderStatusDTO}.
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory} для конфигурации слушателей
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
