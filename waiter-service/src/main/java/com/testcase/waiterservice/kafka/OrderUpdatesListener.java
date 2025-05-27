package com.testcase.waiterservice.kafka;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.waiterservice.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Сервисный компонент, который слушает обновления статуса заказа, поступающие через Kafka из сервиса кухни.
 * При получении обновления, вызывает сервис {@link OrderServiceImpl} для обновления статуса заказа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderUpdatesListener {

    private final OrderServiceImpl orderServiceImpl;


    /**
     * Обрабатывает обновления статуса заказа, полученные из Kafka.
     * Этот метод вызывается при получении сообщения из топика {@code kitchen-order-status-updates-topic}.
     *
     * @param statusDTO объект, содержащий данные обновления статуса заказа
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.kitchen-order-status-updates}",
            groupId = "${spring.kafka.consumer.group.kitchen-status-group-id}")
    public void handleOrderStatusUpdate(UpdateOrderStatusDTO statusDTO) {
        log.info("Kafka: Получены данные для изменения статуса заказа: {}", statusDTO);
        orderServiceImpl.updateOrderStatusFromKafkaDTO(statusDTO);
    }
}
