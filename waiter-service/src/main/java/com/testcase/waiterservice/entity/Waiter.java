package com.testcase.waiterservice.entity;

import com.testcase.commondto.waiterservice.Sex;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Сущность, представляющая официанта.
 * Содержит информацию о его имени, дате трудоустройства, поле и обслуживаемых заказах.
 */
@Entity
@Table(name = "waiter_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Waiter {

    /**
     * ID официанта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiter_id")
    private Long id;

    /**
     * Имя официанта.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Дата трудоустройства официанта.
     */
    @Column(name = "employment_date", nullable = false)
    private OffsetDateTime employmentDate;

    /**
     * Пол официанта.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    /**
     * Заказы, обслуживаемые официантом.
     */
    @OneToMany
    private Set<Order> orders = new HashSet<>();

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

        Waiter other = (Waiter) o;
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(employmentDate, other.employmentDate)
                && Objects.equals(sex, other.sex);
    }

    /**
     * Override метода hashcode() с учетом полей сущности
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, employmentDate, sex);
    }
}
