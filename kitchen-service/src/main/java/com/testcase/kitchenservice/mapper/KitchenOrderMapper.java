package com.testcase.kitchenservice.mapper;

import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.entity.KitchenOrder;
import com.testcase.kitchenservice.entity.OrderToDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * MyBatis-маршрутизатор для работы с базой данных заказов на кухне.
 * Определяет методы для получения, вставки и обновления заказов и связанных с ними блюд.
 */
@Mapper
@Repository
public interface KitchenOrderMapper {

    /**
     * Получает заказ по его идентификатору.
     *
     * @param id идентификатор заказа
     * @return {@link Optional} с заказом или пустой, если не найден
     */
    Optional<KitchenOrder> getKitchenOrderById(@Param("id") Long id);

    /**
     * Получает список заказов по переданным критериям.
     *
     * @param criteriaDTO DTO с критериями фильтрации
     * @return список заказов кухни
     */
    List<KitchenOrder> getKitchenOrdersByCriteria(@Param("criteriaDTO") CriteriaDTO criteriaDTO);

    /**
     * Обновляет статус заказа.
     *
     * @param order заказ с новым статусом
     * @return количество обновлённых записей
     */
    int updateStatus(@Param("order") KitchenOrder order);

    /**
     * Создает новый заказ на кухне.
     *
     * @param kitchenOrder сущность нового заказа
     */
    void insertKitchenOrder(KitchenOrder kitchenOrder);

    /**
     * Вставляет позицию заказа (блюдо, привязанное к заказу), привязанную к заказу.
     *
     * @param orderToDish сущность позиции заказа
     */
    void insertOrderToDish(OrderToDish orderToDish);


    void batchInsertOrderToDish(@Param("set") Set<OrderToDish> orderToDishes);
}
