package ru.kononov.quotationservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static ru.kononov.quotationservice.error.exception.ExceptionCode.V_REQUEST;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(byte[] payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (IOException e) {
            throw newApplicationException(e, resolve(), V_REQUEST, String.format("Не удалось выпонить десериализацию объекта типа '%s'", clazz.getName()));
        }
    }

    public static <T> byte[] toJson(T entity) {
        try {
            return objectMapper.writeValueAsBytes(entity);
        } catch (JsonProcessingException e) {
            throw newApplicationException(e, resolve(), Z_SYSTEM, String.format("Не удалось выполнить сериализацию объекта типа '%s' в массив байт", entity.getClass().getName()));
        }
    }

}
