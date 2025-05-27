package com.testcase.waiterservice.service.impl;

import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.dto.request.PaymentFilterDTO;
import com.testcase.waiterservice.entity.Payment;
import com.testcase.waiterservice.exception.PaymentNotFoundException;
import com.testcase.waiterservice.mapper.PaymentMapper;
import com.testcase.waiterservice.repository.payment.PaymentRepository;
import com.testcase.waiterservice.repository.payment.PaymentSpecification;
import com.testcase.waiterservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для управления оплатами.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDTO getPaymentById(Long orderId) {
        return paymentMapper.toPaymentDTO(paymentRepository.findById(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж с id: " + orderId + " не найден")));
    }

    @Override
    public Page<PaymentDTO> getPayments(PaymentFilterDTO filter, Pageable pageable) {
        Specification<Payment> spec = PaymentSpecification.withFilters(filter);
        return paymentRepository.findAll(spec, pageable)
                .map(paymentMapper::toPaymentDTO);
    }
}
