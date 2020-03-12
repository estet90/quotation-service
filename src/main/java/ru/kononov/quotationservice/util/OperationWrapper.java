package ru.kononov.quotationservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationWrapper {

    public static <T> T wrap(Logger logger, String point, Supplier<T> operation) {
        try {
            logger.info("{}.in", point);
            var result = operation.get();
            logger.info("{}.out result={}", point, result);
            return result;
        } catch (Exception e) {
            logger.error("{}.thrown {}", point, e.getMessage());
            throw e;
        }
    }

}
