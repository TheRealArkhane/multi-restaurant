package com.testcase.waiterservice.repository.payment;

import com.education.waiterservice.dto.request.PaymentFilterDTO;
import com.education.waiterservice.entity.Payment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для фильтрации платежей по различным критериям.
 */
public final class PaymentSpecification {

    private PaymentSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Создаёт спецификацию для фильтрации платежей.
     *
     * @param filter объект с критериями фильтрации
     * @return спецификация для поиска
     */
    public static Specification<Payment> withFilters(PaymentFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getPaymentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentType"), filter.getPaymentType()));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("paymentDate"), filter.getDateFrom()));
            }

            if (filter.getDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("paymentDate"), filter.getDateTo()));
            }

            if (filter.getSumFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("paymentSum"), filter.getSumFrom()));
            }

            if (filter.getSumTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("paymentSum"), filter.getSumTo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
