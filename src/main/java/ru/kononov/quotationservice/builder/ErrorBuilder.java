package ru.kononov.quotationservice.builder;

import ru.kononov.quotationservice.error.exception.ApplicationException;
import ru.kononov.quotationservice.model.Error;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ErrorBuilder {

    @Inject
    public ErrorBuilder() {
    }

    public Error build(ApplicationException e) {
        return new Error()
                .code(e.getFullErrorCode())
                .message(e.getMessage());
    }

}
