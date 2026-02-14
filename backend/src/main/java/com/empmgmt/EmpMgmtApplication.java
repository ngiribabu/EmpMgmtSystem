package com.empmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EmpMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpMgmtApplication.class, args);
    }
}
