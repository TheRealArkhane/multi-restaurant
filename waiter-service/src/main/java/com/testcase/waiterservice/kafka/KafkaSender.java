package com.testcase.waiterservice.kafka;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки сообщений в Kafka, связанных с заказами.
 * Отправляет сообщения для создания заказа и обновления его статуса.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, OrderDTO> orderKafkaTemplate;
    private final KafkaTemplate<String, UpdateOrderStatusDTO> updateOrderStatusKafkaTemplate;

    @Value("${spring.kafka.producer.topic.order-creation}")
    private String orderCreationTopic;

    @Value("${spring.kafka.producer.topic.waiter-order-status-updates}")
    private String waiterOrderStatusTopic;

    /**
     * Отправляет сообщение для создания заказа в Kafka.
     *
     * @param orderDTO DTO заказа для отправки
     */
    public void sendOrderCreation(OrderDTO orderDTO) {

        log.info("Kafka: Отправка нового заказа с id: {} в topic: {}",
                orderDTO.getId(), orderCreationTopic);
        orderKafkaTemplate.send(orderCreationTopic, orderDTO);
        log.info("Новый заказ с id: {} отправлен в topic: {}",
                orderDTO.getId(), orderCreationTopic);
    }

    /**
     * Отправляет сообщение об обновлении статуса заказа в Kafka.
     *
     * @param updateDTO DTO обновления статуса заказа
     */
    public void sendUpdateOrderStatus(UpdateOrderStatusDTO updateDTO) {
        log.info("Kafka: Отправка обновленного статуса заказа с id: {} в topic: {}",
                updateDTO.getId(), waiterOrderStatusTopic);
        updateOrderStatusKafkaTemplate.send(waiterOrderStatusTopic, updateDTO);
        log.info("Kafka: Обновленный статус заказа с id: {} в topic: {} успешно отправлен",
                updateDTO.getId(), waiterOrderStatusTopic);
    }
}
