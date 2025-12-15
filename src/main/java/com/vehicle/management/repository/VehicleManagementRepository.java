package com.vehicle.management.repository;

import com.vehicle.management.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleManagementRepository extends JpaRepository<Vehicle, UUID> {
    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:plate IS NULL OR v.plate = :plate) AND " +
            "(:brand IS NULL OR v.brand = :brand) AND " +
            "(:year IS NULL OR v.year = :year) AND " +
            "(:color IS NULL OR v.color = :color) AND " +
            "(:price IS NULL OR price = :price) AND " +
            "v.active = true")
    List<Vehicle> findByFilters(
            @Param("plate") String plate,
            @Param("brand") String brand,
            @Param("year") Integer year,
            @Param("color") String color,
            @Param("price") BigDecimal price
    );

    List<Vehicle> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    List<Vehicle> findByActiveTrue();

    Optional<Vehicle> findByPlateAndActiveTrue(String plate);

    Optional<Vehicle> findByIdAndActiveTrue(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.active = false WHERE v.id = :id")
    void deactivateById(UUID id);

    @Query("SELECT v.brand, COUNT(v) FROM Vehicle v WHERE v.active = true GROUP BY v.brand")
    List<Object[]> countVehiclesByBrand();
}