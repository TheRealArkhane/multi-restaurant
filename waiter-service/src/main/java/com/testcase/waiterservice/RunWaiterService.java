package com.testcase.waiterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в микросервис официантов.
 * Запускает Spring Boot приложение.
 */
@SpringBootApplication
@SuppressWarnings("uncommentedmain")
public class RunWaiterService {

    /**
     * Основной метод запуска сервиса официантов.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // ...
        SpringApplication.run(RunWaiterService.class, args);
    }
}
