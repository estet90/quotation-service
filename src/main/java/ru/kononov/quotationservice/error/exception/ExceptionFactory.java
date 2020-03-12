package ru.kononov.quotationservice.error.exception;

import ru.kononov.quotationservice.error.operation.ModuleOperationCode;

import static java.util.Arrays.stream;

public class ExceptionFactory {

    private static final String DEFAULT_MESSAGE = "Сообщение для ошибки не определено";
    private static final String SERVICE_CODE = "001";

    public static ApplicationException newApplicationException(ModuleOperationCode moduleOperationCode, ExceptionCode exceptionCode, String... args) {
        var message = prepareMessage(exceptionCode, args);
        return new ApplicationException(SERVICE_CODE, moduleOperationCode.getCode(), exceptionCode.getType(), exceptionCode.getCode(), message, null);
    }

    public static ApplicationException newApplicationException(Throwable cause, ModuleOperationCode moduleOperationCode, ExceptionCode exceptionCode, String... args) {
        var message = prepareMessage(exceptionCode, args);
        return new ApplicationException(SERVICE_CODE, moduleOperationCode.getCode(), exceptionCode.getType(), exceptionCode.getCode(), message, cause);
    }

    private static String prepareMessage(ExceptionCode exceptionCode, String... args) {
        var message = exceptionCode.getMessage();
        return appendArguments(message != null ? message : DEFAULT_MESSAGE, args);
    }

    private static String appendArguments(String message, String... args) {
        return message + stream(args).map(o -> ", '" + o + "'").reduce(String::concat).orElse("");
    }

}
