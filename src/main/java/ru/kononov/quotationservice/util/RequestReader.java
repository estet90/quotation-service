package ru.kononov.quotationservice.util;

import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestReader {

    public static byte[] extractPayload(HttpExchange exchange) {
        try (var inputStream = requireNonNull(exchange.getRequestBody())) {
            var result = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, result);
            return result.toByteArray();
        } catch (IOException | NullPointerException e) {
            throw newApplicationException(resolve(), Z_SYSTEM, "Не удалось извлечь тело запроса", e.getMessage());
        }
    }

}
