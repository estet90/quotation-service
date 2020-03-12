package ru.kononov.quotationservice.error.exception;

import lombok.Getter;

import static ru.kononov.quotationservice.error.exception.ExceptionCodeType.*;

/**
 * Исключение-обёртка для исключений, возникающих в процессе работы приложения.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Формируемый код исключения вида "A-B-CD", где
     * </br>A - код приложения,
     * </br>B - код операции,
     * </br>C - тип исключения (см {@link ExceptionCodeType}),
     * </br>D - код исключения
     * </br>Пример: 001-01-I01
     */
    @Getter
    private final String fullErrorCode;
    private final ExceptionCodeType type;

    /**
     * Исключение формируется только с использованием фабричных методов {@link ExceptionFactory#newApplicationException}
     *
     * @param serviceCode   код приложения
     * @param operationCode код операции
     * @param type          тип исключения (см {@link ExceptionCodeType})
     * @param code          код исключения
     * @param message       сообщение
     * @param cause         первопричина
     */
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
