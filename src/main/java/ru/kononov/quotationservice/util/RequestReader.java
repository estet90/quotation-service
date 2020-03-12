package ru.kononov.quotationservice.util;

import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestReader {

    public static byte[] extractPayload(HttpExchange exchange) {
        try (var inputStream = requireNonNull(exchange.getRequestBody())) {
            var result = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, result);
            return result.toByteArray();
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Не удалось извлечь тело запроса", e);
        }
    }

}
