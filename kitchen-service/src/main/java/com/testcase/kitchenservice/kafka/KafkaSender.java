
package com.testcase.kitchenservice.kafka;

import com.testcase.commondto.UpdateOrderStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, UpdateOrderStatusDTO> kafkaTemplate;

    @Value("${spring.kafka.producer.topic.kitchen-order-status-updates}")
    private String kitchenOrderStatusTopic;


    public void sendOrderStatusUpdate(UpdateOrderStatusDTO statusDTO) {
        log.info("Kafka: Отправка обновленного статуса заказа с id: {} в topic: {}",
                statusDTO.getId(), kitchenOrderStatusTopic);
        kafkaTemplate.send(kitchenOrderStatusTopic, statusDTO);
        log.info("Kafka: Обновленный статус заказа с id: {} в topic: {} успешно отправлен",
                statusDTO.getId(), kitchenOrderStatusTopic);
    }
}
