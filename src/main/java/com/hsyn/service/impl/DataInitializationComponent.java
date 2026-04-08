package com.hsyn.service.impl;

import com.hsyn.domain.UserRole;
import com.hsyn.model.User;
import com.hsyn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        initializeAdminUser();
    }


    private void initializeAdminUser(){
        String adminEmail = "huseynalasgarli@gmail.com";
        String adminPassword = "huseynalasgarli";
        if(userRepository.findByEmail(adminEmail) == null){
            User user = User.builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("huseynalasgarli")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            User admin = userRepository.save(user);
        }
    }
}
