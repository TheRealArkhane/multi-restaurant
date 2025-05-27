package com.testcase.kitchenservice.service.impl;

import com.testcase.kitchenservice.dto.DishBalanceUpdateDTO;
import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.entity.Dish;
import com.testcase.kitchenservice.exception.DishNotFoundException;
import com.testcase.kitchenservice.mapper.DishMapper;
import com.testcase.kitchenservice.mapstruct.mapper.DishMapstructMapper;
import com.testcase.kitchenservice.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Реализация {@link DishService}, предоставляющая методы для работы с блюдами.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private static final String DISH_NOT_FOUND = "Блюдо с id: '%d' не найдено";

    private final DishMapper dishMapper;
    private final DishMapstructMapper dishMapstructMapper;


    @Override
    public List<DishDTO> getAllDishes() {
        log.info("Получение списка всех блюд");
        List<Dish> dishes = dishMapper.getAllDishes();
        log.info("Найдено {} блюд", dishes.size());
        return dishMapstructMapper.toDishDTOList(dishes);
    }


    @Override
    public DishDTO getDishDTOById(Long id) {
        log.info("Получение блюда с id: {}", id);
        DishDTO dishDTO = dishMapstructMapper.toDishDTO(
                dishMapper.getDishById(id)
                        .orElseThrow(() -> new DishNotFoundException(
                                String.format(DISH_NOT_FOUND, id))));
        log.info("Найдено блюдо с id: {}", id);
        return dishDTO;
    }


    @Override
    public Dish getDishById(Long id) {
        log.info("Получение сущности блюда с id: {}", id);
        Dish dish = dishMapper.getDishById(id)
                .orElseThrow(() -> new DishNotFoundException(String.format(DISH_NOT_FOUND, id)));
        log.info("Найдена сущность блюда с id: {}", id);
        return dish;
    }


    @Override
    public List<Dish> getDishesByIds(Set<Long> ids) {
        return dishMapper.getDishesByIds(ids);
    }


    @Override
    public void updateDishBalance(Long id, Integer additionalValue) {
        log.info("Обновление баланса блюда с id: {}", id);
        dishMapper.updateDishBalance(id, additionalValue);
        log.info("Баланс блюда с id: {} успешно обновлён", id);
    }

    @Override
    public void batchUpdateDishBalances(Set<DishBalanceUpdateDTO> dishUpdates) {
        log.info("Производится обновление баланса блюд");
        dishMapper.batchUpdateDishBalances(dishUpdates);
        log.info("Обновление баланса блюд проведено успешно");
    }
}
