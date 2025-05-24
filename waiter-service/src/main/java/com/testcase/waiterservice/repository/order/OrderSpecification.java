package com.testcase.waiterservice.repository.order;

import com.education.waiterservice.dto.request.OrderFilterDTO;
import com.education.waiterservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


/**
 * Спецификация для фильтрации заказов по различным критериям.
 */
public final class OrderSpecification {

    private OrderSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Создаёт спецификацию для фильтрации заказов.
     *
     * @param filter объект с критериями фильтрации
     * @return спецификация для поиска
     */
    public static Specification<Order> withFilters(OrderFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getCreatedFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createDateTime"), filter.getCreatedFrom()));
            }

            if (filter.getCreatedTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDateTime"), filter.getCreatedTo()));
            }

            if (filter.getWaiterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("waiter").get("id"), filter.getWaiterId()));
            }

            if (filter.getTableNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tableNumber"), filter.getTableNumber()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
