package com.fmd.spring_jpa_demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Spring JPA Demo.
 * <p>
 * Starts the Spring Boot application.
 */
@Slf4j
@SpringBootApplication
public class SpringJpaDemoApplication {

    /**
     * Main method to launch the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Spring JPA Demo Application");
        SpringApplication.run(SpringJpaDemoApplication.class, args);
        log.info("Spring JPA Demo Application started successfully");
    }

}
