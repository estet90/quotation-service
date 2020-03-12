package ru.kononov.quotationservice;

import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.module.PropertyModule;

@Log4j2
public class Application {

    public static void main(String[] args) {
        try {
            var server = DaggerApplicationComponent.builder()
                    .propertyModule(new PropertyModule(args))
                    .build()
                    .server();
            server.start();
        } catch (Exception e) {
            log.error("Application.main.thrown", e);
        }
    }

}
