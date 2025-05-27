package com.testcase.waiterservice.service;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.waiterservice.dto.request.OrderCalculateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderFilterDTO;
import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import com.testcase.waiterservice.entity.PaymentType;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.exception.OrderNotFoundException;
import com.testcase.waiterservice.exception.OrderServingException;
import com.testcase.waiterservice.exception.UnsuccessfulPaymentException;
import com.testcase.waiterservice.exception.WaiterNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Интерфейс сервиса для управления заказами.
 * Предоставляет методы для создания заказов, получения информации о заказах,
 * обновления их статуса, а также для обработки связанных с заказами операций.
 */
public interface OrderService {

    /**
     * Возвращает список всех заказов.
     *
     * @return список {@link OrderDTO} всех заказов
     */
    List<OrderDTO> getAllOrders();

    /**
     * Возвращает заказ по ID.
     *
     * @param id ID заказа
     * @return {@link OrderDTO} найденного заказа
     * @throws OrderNotFoundException если заказ с указанным идентификатором не найден
     */
    OrderDTO getOrderById(Long id);

    /**
     * Создаёт новый заказ на основе входящего запроса.
     *
     * @param requestDTO DTO с данными для создания заказа {@link OrderCreateRequestDTO}
     * @return {@link OrderDTO} сохранённого заказа
     * @throws WaiterNotFoundException если официант с указанным ID не найден
     */
    OrderDTO createOrder(OrderCreateRequestDTO requestDTO);

    /**
     * Рассчитывает и обновляет позиции заказа на основе новых данных.
     *
     * @param orderId    идентификатор заказа
     * @param requestDTO DTO с данными для перерасчёта позиций заказа {@link OrderCalculateRequestDTO}
     * @return {@link OrderDTO} обновлённого заказа
     * @throws IllegalArgumentException если количество новой позиции меньше или равно 0
     * @throws MenuPositionNotFoundException если позиция меню с указанным ID не найдена
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    OrderDTO calculateOrder(Long orderId, OrderCalculateRequestDTO requestDTO);

    /**
     * Очищает все позиции в заказе.
     *
     * @param orderId идентификатор заказа
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    void clearOrderPositions(Long orderId);

    /**
     * Отправляет заказ на кухню через Kafka.
     *
     * @param orderId идентификатор заказа
     * @return {@link OrderDTO} отправленного заказа
     * @throws IllegalStateException если статус заказа не соответствует ожидаемому
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    OrderDTO sendOrderToKitchen(Long orderId);

    /**
     * Возвращает статус заказа в виде строки по ID заказа.
     *
     * @param id идентификатор заказа
     * @return строковое представление статуса заказа
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    String getOrderStatusByOrderId(Long id);

    /**
     * Обновляет статус заказа из данных, полученных через Kafka.
     *
     * @param updateDTO данные обновления статуса заказа {@link UpdateOrderStatusDTO}
     */
    void updateOrderStatusFromKafkaDTO(UpdateOrderStatusDTO updateDTO);

    /**
     * Обрабатывает платёж за заказ с симуляцией случайной неудачи оплаты.
     *
     * @param orderId     идентификатор заказа
     * @param paymentType тип оплаты {@link PaymentType}
     * @return {@link PaymentDTO} сохранённого платежа
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     * @throws IllegalStateException  если заказ уже был оплачен
     * @throws UnsuccessfulPaymentException если оплата не удалась
     */
    PaymentDTO payOrder(Long orderId, PaymentType paymentType);

    /**
     * Подаёт заказ, если статус соответствует ожиданию и оплата проведена.
     *
     * @param orderId идентификатор заказа
     * @return {@link OrderDTO} сервированного заказа
     * @throws OrderServingException если заказ не может быть подан
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    OrderDTO serveOrder(Long orderId);

    /**
     * Отменяет заказ и обновляет его статус согласно заданным правилам.
     *
     * @param orderId  идентификатор заказа, который нужно отменить
     * @param newStatus новый статус заказа, на который он должен быть изменён
     * @return {@link OrderDTO} отменённого заказа с обновлённым статусом
     * @throws IllegalStateException если заказ не может быть отменён с текущего статуса
     * @throws OrderNotFoundException если заказ с указанным ID не найден
     */
    OrderDTO cancelOrder(Long orderId, OrderStatus newStatus);

    /**
     * Рассчитывает общую стоимость заказа, суммируя стоимость всех позиций заказа.
     *
     * @param order заказ
     * @return общая стоимость заказа
     */
    Double calculateTotalSum(Order order);

    /**
     * Добавляет позицию в заказ.
     *
     * @param order    заказ
     * @param position позиция для добавления
     */
    void addPosition(Order order, OrderPosition position);

    /**
     * Удаляет позицию из заказа.
     *
     * @param order    заказ
     * @param position позиция для удаления
     */
    void removePosition(Order order, OrderPosition position);

    /**
     * Выполняет фильтрацию заказов по заданным критериям и возвращает постраничный результат.
     * Фильтрация выполняется по следующим параметрам:
     * <ul>
     *     <li><b>Статус</b> заказа ({@link OrderStatus})</li>
     *     <li><b>Дата создания</b> заказа от (включительно)</li>
     *     <li><b>Дата создания</b> заказа до (включительно)</li>
     *     <li><b>ID официанта</b></li>
     *     <li><b>Номер столика</b></li>
     * </ul>
     *
     * Результат возвращается постранично.
     * @param filter объект фильтра {@link OrderFilterDTO}, содержащий параметры фильтрации
     * @param page номер страницы (нумерация начинается с 0)
     * @param size количество элементов на странице
     * @return страница заказов {@link Page} с DTO {@link OrderDTO}, удовлетворяющих фильтрующим условиям
     */
    Page<OrderDTO> getOrdersByFilter(OrderFilterDTO filter, int page, int size);
}
