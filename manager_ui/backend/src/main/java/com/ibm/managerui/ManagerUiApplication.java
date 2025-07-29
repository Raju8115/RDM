package com.ibm.managerui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ibm.managerui")
public class ManagerUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerUiApplication.class, args);
    }
} 