package ru.kononov.quotationservice.util;

import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import ru.kononov.quotationservice.error.exception.ApplicationException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_INTERNAL;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerMethodWrapper {

    public static void wrap(Logger logger,
                            String point,
                            HttpExchange exchange,
                            Consumer<HttpExchange> handler,
                            BiConsumer<HttpExchange, ApplicationException> clientErrorHandler,
                            BiConsumer<HttpExchange, ApplicationException> serverErrorHandler) {
        try {
            handler.accept(exchange);
        } catch (ApplicationException e) {
            logger.error("{}.thrown", point, e);
            if (e.isValidationException()) {
                clientErrorHandler.accept(exchange, e);
            } else {
                serverErrorHandler.accept(exchange, e);
            }
        } catch (Exception e) {
            var applicationException = newApplicationException(e, resolve(), Z_INTERNAL);
            logger.error("{}.thrown", point, applicationException);
            serverErrorHandler.accept(exchange, applicationException);
        }
    }

}
