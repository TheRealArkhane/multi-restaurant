package com.testcase.waiterservice.service;

import com.testcase.waiterservice.entity.OrderPosition;

/**
 * Интерфейс сервиса для управления позициями в заказах.
 * Предоставляет методы для создания, обновления, получения и удаления позиций заказа.
 */
public interface OrderPositionService {

    /**
     * Создаёт новую позицию заказа.
     * Проверяет корректность количества и наличие меню и заказа, после чего сохраняет позицию в базе.
     *
     * @param menuId идентификатор позиции меню
     * @param quantity количество позиций в заказе
     * @param orderId идентификатор заказа
     * @return {@link OrderPosition} созданной позиции заказа
     */
    OrderPosition createOrderPosition(Long menuId, Integer quantity, Long orderId);

    /**
     * Получает позицию заказа по ID.
     *
     * @param orderPositionId идентификатор позиции заказа
     * @return {@link OrderPosition} найденной позиции заказа
     */
    OrderPosition getOrderPositionById(Long orderPositionId);

    /**
     * Обновляет количество позиции заказа по ID позиции.
     * Если новое количество равно 0, позиция будет удалена.
     *
     * @param orderPositionId идентификатор позиции заказа
     * @param newQuantity новое количество позиций
     */
    void updateOrderPosition(Long orderPositionId, int newQuantity);

    /**
     * Удаляет позицию заказа по ID.
     *
     * @param orderPositionId идентификатор позиции заказа
     */
    void deleteOrderPosition(Long orderPositionId);

    /**
     * Вычисляет стоимость позиции заказа.
     *
     * @param orderPosition позиция заказа
     * @return стоимость (цена блюда × количество)
     */
    Double getTotalCost(OrderPosition orderPosition);
}
