package ru.kononov.quotationservice.util;

import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Log4j2
public class PropertyResolver {

    private final Properties properties;

    public PropertyResolver(String propertyPath) {
        var isClassPath = propertyPath.startsWith("classpath:/");
        try (var inputStream = isClassPath
                ? getClass().getResourceAsStream(propertyPath.substring(10))
                : Files.newInputStream(Paths.get(propertyPath))) {
            this.properties = new Properties();
            properties.load(requireNonNull(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringProperty(String key) {
        return getProperty(key, Function.identity());
    }

    public int getIntProperty(String key) {
        return getProperty(key, Integer::parseInt);
    }

    private <T> T getProperty(String key, Function<String, T> transformer) {
        try {
            requireNonNull(key);
            var value = requireNonNull(properties.getProperty(key));
            log.info("PropertyResolver.getProperty key={} value={}", key, value);
            return transformer.apply(value);
        } catch (Exception e) {
            log.error("PropertyResolver.getProperty.thrown key={}", key, e);
            throw e;
        }
    }

}
