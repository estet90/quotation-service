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

    /**
     * Формирование ответа со статусом 200 (либо 204, если тело ответа пустое)
     *
     * @param logger          {@link Logger} используется для логирования ошибок и предупреждений
     * @param point           точка логирования из контроллера
     * @param exchange        {@link HttpExchange}, в который будет записан ответ
     * @param responseBuilder коллбэк, формирующий ответ
     */
    public static void writeOkResponse(Logger logger,
                                       String point,
                                       HttpExchange exchange,
                                       Supplier<byte[]> responseBuilder) {
        writeResponse(logger, point, exchange, responseBuilder, HttpURLConnection.HTTP_OK);
    }

    /**
     * Формирование ответа со статусом 202 (либо 204, если тело ответа пустое)
     *
     * @param logger          {@link Logger} используется для логирования ошибок и предупреждений
     * @param point           точка логирования из контроллера
     * @param exchange        {@link HttpExchange}, в который будет записан ответ
     * @param responseBuilder коллбэк, формирующий ответ
     */
    public static void writeAcceptedResponse(Logger logger,
                                             String point,
                                             HttpExchange exchange,
                                             Supplier<byte[]> responseBuilder) {
        writeResponse(logger, point, exchange, responseBuilder, HttpURLConnection.HTTP_ACCEPTED);
    }

    /**
     * Формирование ответа со статусом 400
     *
     * @param logger               {@link Logger} используется для логирования ошибок и предупреждений
     * @param point                точка логирования из контроллера
     * @param exchange             {@link HttpExchange}, в который будет записан ответ
     * @param e                    {@link ApplicationException} возникшая ошибка
     * @param errorResponseBuilder коллбэк, формирующий ответ
     */
    public static void writeClientErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                ApplicationException e,
                                                Function<ApplicationException, byte[]> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    /**
     * Формирование ответа со статусом 500
     *
     * @param logger               {@link Logger} используется для логирования ошибок и предупреждений
     * @param point                точка логирования из контроллера
     * @param exchange             {@link HttpExchange}, в который будет записан ответ
     * @param e                    {@link ApplicationException} возникшая ошибка
     * @param errorResponseBuilder коллбэк, формирующий ответ
     */
    public static void writeServerErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                ApplicationException e,
                                                Function<ApplicationException, byte[]> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    /**
     * Формирование ответа с произвольным статусом (либо 204, если тело ответа пустое)
     *
     * @param logger          {@link Logger} используется для логирования ошибок и предупреждений
     * @param point           точка логирования из контроллера
     * @param exchange        {@link HttpExchange}, в который будет записан ответ
     * @param responseBuilder коллбэк, формирующий ответ
     * @param status          статус ответа
     */
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

    /**
     * Добавление тела ответа в {@link HttpExchange}
     *
     * @param exchange {@link HttpExchange}, в который будет записан ответ
     * @param response массив байт ответа
     * @throws IOException может возникнуть формировании ответа
     */
    private static void writeResponseBody(HttpExchange exchange, byte[] response) throws IOException {
        try (var outputStream = exchange.getResponseBody();
             var bodyStream = new ByteArrayOutputStream()) {
            outputStream.write(response);
            bodyStream.write(response);
            exchange.setStreams(exchange.getRequestBody(), bodyStream);
        }
    }

    /**
     * Формирование ответа с произвольным статусом на основе ошибки
     *
     * @param logger               {@link Logger} используется для логирования ошибок и предупреждений
     * @param point                точка логирования из контроллера
     * @param exchange             {@link HttpExchange}, в который будет записан ответ
     * @param e                    {@link ApplicationException} возникшая ошибка
     * @param errorResponseBuilder коллбэк, формирующий ответ
     * @param status               статус ответа
     */
    private static void writeErrorResponse(Logger logger,
                                           String point,
                                           HttpExchange exchange,
                                           ApplicationException e,
                                           Function<ApplicationException, byte[]> errorResponseBuilder,
                                           int status) {
        writeResponse(logger, point, exchange, () -> errorResponseBuilder.apply(e), status);
    }

}
