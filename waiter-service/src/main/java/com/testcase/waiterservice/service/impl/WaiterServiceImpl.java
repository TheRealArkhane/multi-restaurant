package com.testcase.waiterservice.service.impl;

import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.entity.Waiter;
import com.testcase.waiterservice.exception.WaiterNotFoundException;
import com.testcase.waiterservice.mapper.WaiterMapper;
import com.testcase.waiterservice.repository.WaiterRepository;
import com.testcase.waiterservice.service.WaiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса, предоставляющего бизнес-логику
 * для получения данных об официантах.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaiterServiceImpl implements WaiterService {

    private final WaiterRepository waiterRepository;
    private final WaiterMapper waiterMapper;


    @Override
    public WaiterDTO getWaiterById(Long id) {
        log.info("Получение официанта по id: {}", id);
        WaiterDTO waiterDTO = waiterMapper.toWaiterDTO(waiterRepository.findById(id)
                .orElseThrow(() -> new WaiterNotFoundException(
                        String.format("Официант с id: %d не найден", id))));
        log.info("Официант с id: {} получен", id);
        return waiterDTO;
    }


    @Override
    public List<WaiterDTO> getAllWaiters() {
        log.info("Получение списка всех официантов");
        List<WaiterDTO> waiters = waiterMapper.toWaiterDTOList(waiterRepository.findAll());
        log.info("Получено {} официантов", waiters.size());
        return waiters;
    }


    @Override
    public Page<WaiterDTO> getWaitersPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Waiter> waitersPage = waiterRepository.findAll(pageable);
        return waitersPage.map(waiterMapper::toWaiterDTO);
    }
}
