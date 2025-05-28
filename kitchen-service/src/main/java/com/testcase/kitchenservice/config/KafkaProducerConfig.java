package com.testcase.kitchenservice.config;

import com.testcase.commondto.UpdateOrderStatusDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka-продюсера для отправки сообщений типа {@link UpdateOrderStatusDTO}.
 * Использует Json-сериализацию значений и строковый ключ.
 */
@Configuration
public class KafkaProducerConfig {

    private final String bootstrapAddress;


    /**
     * Конструктор конфигурации Kafka-продюсера.
     *
     * @param bootstrapAddress адрес(а) Kafka-брокеров, полученные из application.yml/properties
     */
    public KafkaProducerConfig(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }


    /**
     * Фабрика продюсеров Kafka, настраиваемая для сериализации ключей как {@link String}
     * и значений как {@link UpdateOrderStatusDTO} в формате JSON.
     *
     * @return ProducerFactory для Kafka-продюсеров
     */
    @Bean
    public ProducerFactory<String, UpdateOrderStatusDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    /**
     * Бин {@link KafkaTemplate}, используемый для отправки сообщений Kafka с ключом {@link String}
     * и значением {@link UpdateOrderStatusDTO}.
     *
     * @return настроенный KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, UpdateOrderStatusDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
