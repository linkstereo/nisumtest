package com.nisum;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
@Log4j2
public class NisumtestApplication {

    public static void main(String[] args) {

        SpringApplication.run(NisumtestApplication.class, args);
        log.info("Spring vesion: " + SpringVersion.getVersion());
    }

}
