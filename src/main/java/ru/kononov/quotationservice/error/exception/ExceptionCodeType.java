package ru.kononov.quotationservice.error.exception;

/**
 * Типы возникающих ошибок
 */
public enum ExceptionCodeType {

    /**
     * Ошибки вызова сторонних систем, в т.ч. запросы к БД
     */
    I,

    /**
     * Бизнесовые ошибки
     */
    B,

    /**
     * Ошибки при валидации по контракту
     */
    V,

    /**
     * Прочие ошибки
     */
    Z

}
