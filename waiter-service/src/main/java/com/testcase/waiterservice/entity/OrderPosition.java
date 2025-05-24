package com.testcase.waiterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Сущность, представляющая позицию в заказе (конкретное блюдо и его количество).
 */
@Entity
@Table(name = "order_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPosition {

    /**
     * ID позиции заказа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "composition_id")
    private Long id;

    /**
     * Количество заказанных блюд.
     */
    @Column(name = "dish_num", nullable = false)
    private Integer quantity;

    /**
     * Заказ, к которому относится эта позиция.
     */
    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    /**
     * Блюдо, добавленное в заказ.
     */
    @ManyToOne
    @JoinColumn(name = "menu_position_id", nullable = false)
    private Menu menu;


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

        OrderPosition other = (OrderPosition) o;
        return Objects.equals(id, other.id)
                && Objects.equals(quantity, other.quantity)
                && Objects.equals(menu, other.menu);
    }


    /**
     * Override метода hashcode() с учетом полей сущности
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, menu);
    }
}
