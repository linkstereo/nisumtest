package com.nisum;

import com.nisum.model.Role;
import com.nisum.model.RoleName;
import com.nisum.repository.RoleRepository;
import com.nisum.repository.UserRepository;
import com.nisum.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.SpringVersion;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Log4j2
public class NisumtestApplication {

    public static void main(String[] args) {

        SpringApplication.run(NisumtestApplication.class, args);
        log.info("Spring vesion: " + SpringVersion.getVersion());
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args ->
        {
            roleRepository.save(new Role(RoleName.USER));
        };
    }

}
