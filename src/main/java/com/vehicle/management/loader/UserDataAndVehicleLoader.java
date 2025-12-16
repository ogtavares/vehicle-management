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
            User adminUser = new User(
                    "adminUser",
                    passwordEncoder.encode("adminUser123"),
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)
            );
            userRepository.save(adminUser);

            User onlyAdmin = new User(
                    "onlyAdmin",
                    passwordEncoder.encode("admin123"),
                    Set.of(Role.ROLE_ADMIN)
            );
            userRepository.save(onlyAdmin);

            User onlyUser = new User(
                    "onlyUser",
                    passwordEncoder.encode("user123"),
                    Set.of(Role.ROLE_USER)
            );
            userRepository.save(onlyUser);

            User noRole = new User(
                    "noRole",
                    passwordEncoder.encode("nopass"),
                    Set.of()
            );
            userRepository.save(noRole);
        }
    }

    private void loadVehicles() {
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle("AAA-1234", "Toyota", "Red", 2020, new BigDecimal("95000.00"));
            Vehicle v2 = new Vehicle("BBB-5678", "Honda", "Blue", 2019, new BigDecimal("85000.00"));
            Vehicle v3 = new Vehicle("CCC-9012", "Ford", "Black", 2021, new BigDecimal("120000.00"));
            Vehicle v4 = new Vehicle("DDD-3456", "Chevrolet", "White", 2018, new BigDecimal("78000.00"));

            vehicleRepository.save(v1);
            vehicleRepository.save(v2);
            vehicleRepository.save(v3);
            vehicleRepository.save(v4);
        }
    }
}
