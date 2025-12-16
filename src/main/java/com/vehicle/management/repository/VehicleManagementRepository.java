package com.vehicle.management.repository;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.model.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleManagementRepository extends JpaRepository<Vehicle, UUID> {
    @Query("""
        SELECT v FROM Vehicle v WHERE
        (:plate IS NULL OR LOWER(v.plate) = LOWER(:plate)) AND
        (:brand IS NULL OR LOWER(v.brand) = LOWER(:brand)) AND
        (:vehicleYear IS NULL OR v.vehicleYear = :vehicleYear) AND
        (:color IS NULL OR LOWER(v.color) = LOWER(:color)) AND
        (:minPrice IS NULL OR v.price >= :minPrice) AND
        (:maxPrice IS NULL OR v.price <= :maxPrice) AND
        v.active = true
    """)
    Page<Vehicle> findByFilters(
            @Param("plate") String plate,
            @Param("brand") String brand,
            @Param("vehicleYear") Integer vehicleYear,
            @Param("color") String color,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    Page<Vehicle> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Vehicle> findByActiveTrue(Pageable pageable);

    Optional<Vehicle> findByIdAndActiveTrue(UUID id);

    Optional<Vehicle> findByPlateAndActiveTrue(String plate);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.active = false WHERE v.id = :id")
    void deactivateById(UUID id);

    @Query("""
    SELECT new com.vehicle.management.dto.VehicleBrandReportDTO(
        v.brand,
        COUNT(v)
    )
    FROM Vehicle v
    WHERE v.active = true
    GROUP BY v.brand
    """)
    Page<VehicleBrandReportDTO> countVehiclesByBrand(Pageable pageable);
}