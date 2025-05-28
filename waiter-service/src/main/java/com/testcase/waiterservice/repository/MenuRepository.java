package com.testcase.waiterservice.repository;

import com.testcase.waiterservice.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с меню.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}
