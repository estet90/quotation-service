package ru.kononov.quotationservice.module;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@Module
public class DbModule {

    @Provides
    @Singleton
    @Named("quotationDataSource")
    DataSource quotationDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:" + resolveInitScript());
        return new HikariDataSource(config);
    }

    private String resolveInitScript() {
        var dbResourceFolder = "db";
        var files = resolveSqlScriptFiles(dbResourceFolder);
        return Arrays.stream(files)
                .map(File::getName)
                .map(name -> String.format("runscript from 'classpath:/%s/%s'", dbResourceFolder, name))
                .collect(Collectors.joining("\\;", "quotation;INIT=", ""));
    }

    private File[] resolveSqlScriptFiles(String dbResourceFolder) {
        try {
            var url = DbModule.class.getClassLoader().getResources(dbResourceFolder).nextElement();
            var directory = Paths.get(url.toURI());
            return Objects.requireNonNull(directory.toFile().listFiles());
        } catch (IOException | URISyntaxException e) {
            throw newApplicationException(e, resolve(), Z_SYSTEM, "Ошибка при чтении файлов");
        }
    }

}
