package com.testcase.kitchenservice.kafka;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.kitchenservice.service.impl.KitchenOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KitchenOrderListener {

    private final KitchenOrderServiceImpl kitchenOrderServiceImpl;


    /**
     * Обрабатывает событие создания заказа из топика, заданного в конфиге.
     * Вызывает {@link KitchenOrderServiceImpl#createKitchenOrderFromOrderDTO(OrderDTO)} для создания заказа на кухне.
     *
     * @param orderDTO объект заказа, полученный из Kafka
     */
    @KafkaListener(topics = "${spring.kafka.consumer.topic.order-creation}",
            groupId = "${spring.kafka.consumer.group.order-creation-group-id}",
            containerFactory = "createOrderKafkaListenerContainerFactory")
    public void handleKitchenOrderCreation(OrderDTO orderDTO) {
        log.info("Kafka: Получены данные для создания заказа: {}", orderDTO);
        kitchenOrderServiceImpl.createKitchenOrderFromOrderDTO(orderDTO);
    }

    /**
     * Обрабатывает событие обновления статуса заказа, пришедшее из топика, заданного в конфиге.
     * Вызывает {@link KitchenOrderServiceImpl#updateOrderStatusFromWaiterServiceByKafka(UpdateOrderStatusDTO)}
     * для обновления статуса заказа на кухне.
     *
     * @param statusDTO DTO с информацией об обновлённом статусе заказа
     */
    @KafkaListener(topics = "${spring.kafka.consumer.topic.waiter-order-status-updates}",
            groupId = "${spring.kafka.consumer.group.waiter-status-group-id}",
            containerFactory = "updateOrderStatusKafkaListenerContainerFactory")
    public void handleWaiterOrderStatusUpdate(UpdateOrderStatusDTO statusDTO) {
        log.info("Kafka: Получены данные для изменения статуса заказа: {}", statusDTO);
        kitchenOrderServiceImpl.updateOrderStatusFromWaiterServiceByKafka(statusDTO);
    }
}
