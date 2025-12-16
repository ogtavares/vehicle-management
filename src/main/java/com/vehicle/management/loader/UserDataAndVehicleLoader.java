package com.vehicle.management.loader;

import com.vehicle.management.model.entity.User;
import com.vehicle.management.model.entity.Vehicle;
import com.vehicle.management.model.enums.Role;
import com.vehicle.management.repository.UserRepository;
import com.vehicle.management.repository.VehicleManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class UserDataAndVehicleLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleManagementRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadUsers();
        loadVehicles();
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {

            userRepository.save(new User(
                    "adminUser",
                    passwordEncoder.encode("adminUser123"),
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)
            ));

            userRepository.save(new User(
                    "onlyAdmin",
                    passwordEncoder.encode("admin123"),
                    Set.of(Role.ROLE_ADMIN)
            ));

            userRepository.save(new User(
                    "onlyUser",
                    passwordEncoder.encode("user123"),
                    Set.of(Role.ROLE_USER)
            ));

            userRepository.save(new User(
                    "noRole",
                    passwordEncoder.encode("nopass"),
                    Set.of()
            ));
        }
    }

    private void loadVehicles() {
        if (vehicleRepository.count() > 0) {
            return;
        }

        List<Vehicle> vehicles = List.of(
                new Vehicle("AAA1001", "Ford", "Black", 2018, new BigDecimal("55000.00")),
                new Vehicle("AAA1002", "Ford", "White", 2019, new BigDecimal("62000.00")),
                new Vehicle("AAA1003", "Ford", "Red", 2020, new BigDecimal("72000.00")),

                new Vehicle("BBB2001", "Toyota", "Silver", 2019, new BigDecimal("85000.00")),
                new Vehicle("BBB2002", "Toyota", "Black", 2020, new BigDecimal("92000.00")),
                new Vehicle("BBB2003", "Toyota", "White", 2021, new BigDecimal("98000.00")),

                new Vehicle("CCC3001", "Honda", "Blue", 2018, new BigDecimal("78000.00")),
                new Vehicle("CCC3002", "Honda", "Black", 2019, new BigDecimal("83000.00")),
                new Vehicle("CCC3003", "Honda", "White", 2020, new BigDecimal("88000.00")),

                new Vehicle("DDD4001", "Chevrolet", "Gray", 2017, new BigDecimal("60000.00")),
                new Vehicle("DDD4002", "Chevrolet", "Black", 2018, new BigDecimal("65000.00")),
                new Vehicle("DDD4003", "Chevrolet", "White", 2019, new BigDecimal("70000.00")),

                new Vehicle("EEE5001", "Volkswagen", "Blue", 2020, new BigDecimal("90000.00")),
                new Vehicle("EEE5002", "Volkswagen", "Black", 2021, new BigDecimal("95000.00")),
                new Vehicle("EEE5003", "Volkswagen", "White", 2022, new BigDecimal("105000.00")),

                new Vehicle("FFF6001", "BMW", "Black", 2021, new BigDecimal("180000.00")),
                new Vehicle("FFF6002", "BMW", "White", 2022, new BigDecimal("195000.00")),

                new Vehicle("GGG7001", "Audi", "Gray", 2021, new BigDecimal("170000.00")),
                new Vehicle("GGG7002", "Audi", "Black", 2022, new BigDecimal("185000.00"))
        );

        vehicleRepository.saveAll(vehicles);
    }
}