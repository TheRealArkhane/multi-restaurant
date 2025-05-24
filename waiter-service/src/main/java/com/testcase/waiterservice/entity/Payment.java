package com.testcase.waiterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Сущность, представляющая информацию об оплате заказа.
 */
@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    /**
     * ID заказа, совпадает с ID оплаты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    private Long orderId;

    /**
     * Тип оплаты (наличные или карта).
     */
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    /**
     * Дата и время оплаты.
     */
    @Column(name = "payment_date")
    private OffsetDateTime paymentDate;

    /**
     * Сумма оплаты.
     */
    @Column(name = "payment_sum")
    private Double paymentSum;

    /**
     * Связанный заказ, которому соответствует эта оплата.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "order_no")
    private Order order;

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

        Payment other = (Payment) o;
        return Objects.equals(orderId, other.orderId)
                && Objects.equals(paymentType, other.paymentType)
                && Objects.equals(paymentDate, other.paymentDate)
                && Objects.equals(paymentSum, other.paymentSum)
                && Objects.equals(order, other.order);
    }

    /**
     * Override метода hashcode() с учетом полей сущности
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentType, paymentDate, paymentSum);
    }
}
