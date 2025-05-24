package com.testcase.waiterservice.entity;

import com.education.commondto.waiterservice.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Сущность, представляющая заказ, создаваемый официантом.
 */
@Entity
@Table(name = "waiter_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * ID заказа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    private Long id;

    /**
     * Статус заказа.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Дата и время создания заказа.
     */
    @Column(name = "create_dttm", nullable = false)
    private OffsetDateTime createDateTime;

    /**
     * Официант, оформивший заказ.
     */
    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private Waiter waiter;

    /**
     * Номер стола, для которого оформлен заказ.
     */
    @Column(name = "table_no", nullable = false)
    private String tableNumber;

    /**
     * Позиции заказа (список блюд и количество каждого блюда в заказе).
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderPosition> orderPositions = new HashSet<>();


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

        Order other = (Order) o;
        return Objects.equals(id, other.id)
                && Objects.equals(status, other.status)
                && Objects.equals(createDateTime, other.createDateTime)
                && Objects.equals(waiter, other.waiter)
                && Objects.equals(tableNumber, other.tableNumber)
                && Objects.equals(orderPositions, other.orderPositions);
    }


    /**
     * Override метода hashcode() с учетом полей сущности
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, status, createDateTime, waiter, tableNumber, orderPositions);
    }
}
