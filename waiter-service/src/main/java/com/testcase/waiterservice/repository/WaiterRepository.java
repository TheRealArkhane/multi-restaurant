package com.testcase.waiterservice.repository;

import com.testcase.waiterservice.entity.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями официантов.
 */
@Repository
public interface WaiterRepository extends JpaRepository<Waiter, Long> {
}
