package ru.kononov.quotationservice;

import ru.kononov.quotationservice.builder.ErrorBuilder;
import ru.kononov.quotationservice.controller.ElvlsController;

public class TestCase {

    public static ElvlsController controller() {
        var builder = DaggerApplicationTestComponent.builder()
                .build();
        return new ElvlsController(
                "/quotation-service/api/v1/elvls",
                builder.addElvlOperation(),
                builder.getAllElvlsOperation(),
                builder.getElvlOperation(),
                new ErrorBuilder()
        );
    }

}
