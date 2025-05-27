package com.testcase.kitchenservice.service;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.exception.InvalidOrderStatusException;
import com.testcase.kitchenservice.exception.KitchenOrderNotFoundException;

import java.util.List;

/**
 * Сервис для управления заказами кухни.
 * KitchenOrderService осуществляет создание заказов на кухне, обновление статуса заказа,
 * поиск заказов по заданным критериям и передачу обновленных данных
 * сервису официантов (waiter-service) при смене статуса заказа.
 */
public interface KitchenOrderService {


    /**
     * Обновляет статус заказа на кухне.
     * Выполняет проверку возможности перехода между статусами,
     * корректирует статус в случае отмены на стадии готовки,
     * возвращает блюда на склад при отмене, если готовка ещё не началась.
     * Отправляет обновлённый статус в Kafka.
     *
     * @param orderId   ID заказа
     * @param newStatus Новый статус заказа
     * @return DTO с обновлённым заказом {@link KitchenOrderDTO}
     * @throws KitchenOrderNotFoundException если заказ не найден
     * @throws InvalidOrderStatusException   если переход в указанный статус невозможен
     */
    KitchenOrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);


    /**
     * Обновляет статус заказа на кухне по сообщению, пришедшему из waiter-service через Kafka.
     * Производит все необходимые проверки и корректировки.
     *
     * @param statusDTO DTO с ID заказа и новым статусом
     * @throws KitchenOrderNotFoundException если заказ не найден
     * @throws InvalidOrderStatusException   если переход в указанный статус невозможен
     */
    void updateOrderStatusFromWaiterServiceByKafka(UpdateOrderStatusDTO statusDTO);


    /**
     * Возвращает список кухонных заказов, соответствующих заданным критериям:
     * - статус заказа (status)
     * - дата и время, от которых был создан заказ включительно (createdFrom)
     * - дата и время, до которых был создан заказ включительно (createdTo)
     * - ID официанта, создавшего заказ (waiterId)
     *
     * @param criteriaDTO объект с критериями для фильтрации заказов
     * @return список DTO {@link KitchenOrderDTO} подходящих заказов
     */
    List<KitchenOrderDTO> getKitchenOrdersByCriteria(CriteriaDTO criteriaDTO);


    /**
     * Получает заказ по его ID и отдает {@link KitchenOrderDTO}.
     *
     * @param id идентификатор кухонного заказа
     * @return DTO с данными заказа {@link KitchenOrderDTO}
     * @throws KitchenOrderNotFoundException если заказ не найден
     */
    KitchenOrderDTO getKitchenOrderById(Long id);


    /**
     * Создаёт новый заказ на кухне на основе DTO заказа официанта.
     * Производит вычитание заказанных блюд из баланса.
     *
     * @param orderDTO DTO заказа от официанта
     */
    void createKitchenOrderFromOrderDTO(OrderDTO orderDTO);


    /**
     * Валидирует, достаточно ли блюд на складе для выполнения заказа.
     *
     * @param validationDTO DTO заказа
     * @return true если всех блюд достаточно
     * @throws IllegalArgumentException если хотя бы одного блюда недостаточно
     */
    Boolean validateOrder(OrderValidationDTO validationDTO);
}

