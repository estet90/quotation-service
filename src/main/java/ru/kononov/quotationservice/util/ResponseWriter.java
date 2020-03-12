package ru.kononov.quotationservice.util;

import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import ru.kononov.quotationservice.error.exception.ApplicationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.UNKNOWN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWriter {

    public static void writeOkResponse(Logger logger,
                                       String point,
                                       HttpExchange exchange,
                                       Supplier<byte[]> responseBuilder) {
        writeResponse(logger, point, exchange, responseBuilder, HttpURLConnection.HTTP_OK);
    }

    public static void writeAcceptedResponse(Logger logger,
                                             String point,
                                             HttpExchange exchange,
                                             Supplier<byte[]> responseBuilder) {
        writeResponse(logger, point, exchange, responseBuilder, HttpURLConnection.HTTP_ACCEPTED);
    }

    public static void writeClientErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                ApplicationException e,
                                                Function<ApplicationException, byte[]> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static void writeServerErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                ApplicationException e,
                                                Function<ApplicationException, byte[]> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    private static void writeResponse(Logger logger,
                                      String point,
                                      HttpExchange exchange,
                                      Supplier<byte[]> responseBuilder,
                                      int status) {
        try {
            exchange.getResponseHeaders().add("Content-type", "application/json;charset=UTF-8");
            var response = responseBuilder.get();
            if (nonNull(response)) {
                exchange.sendResponseHeaders(status, response.length);
                writeResponseBody(exchange, response);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                exchange.getResponseBody().close();
                logger.warn(point + ".warn метод вернул null");
            }
        } catch (Exception exception) {
            var applicationException = newApplicationException(UNKNOWN, Z_SYSTEM, "Ошибка при отправке ответа");
            logger.error(point + ".thrown", applicationException);
            throw applicationException;
        }
    }

    private static void writeResponseBody(HttpExchange exchange, byte[] response) throws IOException {
        try (var outputStream = exchange.getResponseBody();
             var bodyStream = new ByteArrayOutputStream()) {
            outputStream.write(response);
            bodyStream.write(response);
            exchange.setStreams(exchange.getRequestBody(), bodyStream);
        }
    }

    private static void writeErrorResponse(Logger logger,
                                           String point,
                                           HttpExchange exchange,
                                           ApplicationException e,
                                           Function<ApplicationException, byte[]> errorResponseBuilder,
                                           int status) {
        writeResponse(logger, point, exchange, () -> errorResponseBuilder.apply(e), status);
    }

}
