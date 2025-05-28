package com.testcase.waiterservice.repository.order;

import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.waiterservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Репозиторий для управления заказами.
 * Предоставляет методы для работы с сущностью {@link Order}.
 * Включает в себя стандартные CRUD операции и специфические запросы для изменения статуса заказа.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Обновляет статус заказа по его идентификатору.
     * Этот метод используется для изменения статуса заказа.
     *
     * @param orderId идентификатор заказа
     * @param status новый статус заказа
     */
    @Transactional
    @Modifying
    @Query("UPDATE Order SET status = :status WHERE id = :orderId")
    void updateOrderStatusById(@Param("orderId") Long orderId, @Param("status") OrderStatus status);
}
