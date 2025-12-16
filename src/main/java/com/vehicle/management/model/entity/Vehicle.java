package com.vehicle.management.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicles", uniqueConstraints = {
            @UniqueConstraint(columnNames = "plate")
        }
)
public class Vehicle {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String plate;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, name = "vehicle_year")
    private Integer vehicleYear;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    public Vehicle(String plate, String brand, String color, Integer vehicleYear, BigDecimal price) {
        this.plate = plate;
        this.brand = brand;
        this.color = color;
        this.vehicleYear = vehicleYear;
        this.price = price;
    }

    public void deactivate() {
        this.active = false;
    }
}