package com.vehicle.management.loader;

import com.vehicle.management.model.entity.User;
import com.vehicle.management.repository.UserRepository;
import com.vehicle.management.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {

            //cria usuário com ambos roles ADMIN e USER
            User adminUser = new User(
                    "adminUser",
                    passwordEncoder.encode("adminUser123"),
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)
            );
            userRepository.save(adminUser);

            //cria usuário apenas ADMIN
            User onlyAdmin = new User(
                    "onlyAdmin",
                    passwordEncoder.encode("admin123"),
                    Set.of(Role.ROLE_ADMIN)
            );
            userRepository.save(onlyAdmin);

            //cria usuário apenas USER
            User onlyUser = new User(
                    "onlyUser",
                    passwordEncoder.encode("user123"),
                    Set.of(Role.ROLE_USER)
            );
            userRepository.save(onlyUser);

            //cria usuário sem role
            User noRole = new User(
                    "noRole",
                    passwordEncoder.encode("nopass"),
                    Set.of()
            );
            userRepository.save(noRole);
        }
    }
}
