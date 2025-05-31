package com.orbyta.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BankingServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(BankingServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Banking Service Application");
        SpringApplication.run(BankingServiceApplication.class, args);
        logger.info("Banking Service Application is running");
    }

    @Bean
    public RestTemplate restTemplate() {
        logger.debug("Creating RestTemplate bean");
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
