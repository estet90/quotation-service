package ru.kononov.quotationservice.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ru.kononov.quotationservice.error.exception.ExceptionCodeType.*;

/**
 *
 */
@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    I_DB(I, "01", "Ошибка при выполнении запроса к БД"),

    V_REQUEST(V, "01", "Ошибка при валидации запроса"), //валидация по схеме в конракте
    V_UNSUPPORTED_METHOD(V, "02", "Метод не поддерживается"),

    B_REQUEST(B, "01", "Ошибка при валидации запроса"), //валидация согласно бизнес-требованиям

    Z_SYSTEM(Z, "01", "Системная ошибка"),
    Z_INTERNAL(Z, "99", "Необработанная ошибка"),
    ;

    private final ExceptionCodeType type;
    private final String code;
    private final String message;

}
