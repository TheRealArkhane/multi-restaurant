package com.testcase.waiterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность, представляющая блюдо в меню.
 */
@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    /**
     * ID блюда.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название блюда.
     */
    @Column(name = "dish_name", nullable = false)
    private String name;

    /**
     * Стоимость блюда.
     */
    @Column(name = "dish_cost", nullable = false)
    private Double cost;

    /**
     * Позиции заказов, в которых фигурирует это блюдо. (Фигурирует для явного отображения связи между сущностями)
     */
    @OneToMany
    private List<OrderPosition> orderPositions = new ArrayList<>();


    /**
     * Override метода equals() с учетом особенностей работы Hibernate
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();

        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (!thisEffectiveClass.equals(oEffectiveClass)) {
            return false;
        }

        Menu other = (Menu) o;
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(cost, other.cost);
    }

    /**
     * Override метода hashcode() с учетом полей сущности
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, cost);
    }
}
