package com.testcase.kitchenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в микросервис кухни.
 * Запускает Spring Boot приложение.
 */
@SpringBootApplication
@SuppressWarnings("uncommentedmain")
public class RunKitchenService {

    /**
     * Основной метод запуска сервиса кухни.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(RunKitchenService.class, args);
    }
}
