package ru.kononov.quotationservice.error.exception;

import lombok.Getter;

import static ru.kononov.quotationservice.error.exception.ExceptionCodeType.*;

public class ApplicationException extends RuntimeException {

    @Getter
    private final String fullErrorCode;
    private final ExceptionCodeType type;

    ApplicationException(String serviceCode, String operationCode, ExceptionCodeType type, String code, String message, Throwable cause) {
        super(message, cause);
        this.fullErrorCode = serviceCode + "-" + operationCode + "-" + type + code;
        this.type = type;
    }

    public boolean isValidationException() {
        return V.equals(type);
    }

    public boolean isInvocation() {
        return I.equals(type);
    }

    public boolean isBusiness() {
        return B.equals(type);
    }

    public boolean isInternal() {
        return Z.equals(type);
    }

    @Override
    public String toString() {
        return fullErrorCode;
    }

}
