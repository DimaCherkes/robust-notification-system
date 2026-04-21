package com.dmytrocherkes.subscriptionservice.model.entity;

import com.dmytrocherkes.subscriptionservice.model.enums.CityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "city_status")
    private CityStatus status;

    @Column(name = "active_subscriptions_count")
    private Integer activeSubscriptionsCount;
}
