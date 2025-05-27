package com.testcase.waiterservice.service;

import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.dto.request.PaymentFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Интерфейс сервиса для управления оплатами.
 */
public interface PaymentService {

    /**
     * Получает оплату по её идентификатору (id заказа).
     *
     * @param orderId идентификатор заказа (одновременно id платежа)
     * @return DTO с информацией об оплате
     */
    PaymentDTO getPaymentById(Long orderId);

    /**
     * Получает список платежей с фильтрацией и пагинацией.
     *
     * @param filter фильтры поиска
     * @param pageable параметры пагинации
     * @return страница с DTO оплат
     */
    Page<PaymentDTO> getPayments(PaymentFilterDTO filter, Pageable pageable);
}
