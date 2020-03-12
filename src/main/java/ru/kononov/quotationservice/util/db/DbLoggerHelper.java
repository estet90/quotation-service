package ru.kononov.quotationservice.util.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbLoggerHelper {

    /**
     * Выполнение запросов с логированием при уровнях логирования DEBUG/TRACE
     *
     * @param logger логгер, который будет вести запись
     * @param point шаблон для логирования
     * @param sqlSupplier коллбэк, возвращающий строку запроса к БД
     * @param parametersSupplier коллбэк, возвращающий параметры запроса
     * @param query коллбэк, возвращающий результат запроса
     * @param <T> тип результата
     * @return результат запроса
     */
    public static <T> T executeWithLogging(Logger logger,
                                           String point,
                                           Supplier<String> sqlSupplier,
                                           Supplier<Object> parametersSupplier,
                                           Supplier<T> query) {
        ThreadContext.put("queryId", UUID.randomUUID().toString());
        try {
            logIn(point, logger, sqlSupplier.get(), parametersSupplier.get());
            var result = query.get();
            logOut(point, logger, result);
            return result;
        } catch (Exception e) {
            logger.error("{}.thrown {}", point, e.getMessage());
            throw e;
        } finally {
            ThreadContext.remove("queryId");
        }
    }

    private static void logIn(String point, Logger logger, String query, Object... parameters) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}.in\n\tзапрос: {}\n\tпараметры:{}", point, query, parameters);
        } else if (logger.isDebugEnabled()) {
            logger.debug("{}.in", point);
        }
    }

    private static <T> void logOut(String point, Logger logger, T result) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}.out результат: {}", point, result);
        } else if (logger.isDebugEnabled()) {
            if (result instanceof List) {
                logger.debug("{}.out количество строк: {}", point, ((List<?>) result).size());
            } else {
                logger.debug("{}.out", point);
            }
        }
    }

}
