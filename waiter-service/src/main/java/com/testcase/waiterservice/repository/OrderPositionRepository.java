package com.testcase.waiterservice.repository;

import com.testcase.waiterservice.entity.OrderPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с позициями заказа.
 */
@Repository
public interface OrderPositionRepository extends JpaRepository<OrderPosition, Long> {
}
