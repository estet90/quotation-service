package ru.kononov.quotationservice.module;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Module;
import dagger.Provides;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Module
@Log4j2
public class DbModule {

    @Provides
    @Singleton
    @Named("quotationDataSource")
    DataSource quotationDataSource() {
        var config = new HikariConfig();
        var dbUrl = "jdbc:h2:mem:" + resolveInitScript();
        log.info("DbModule.quotationDataSource {}", dbUrl);
        config.setJdbcUrl(dbUrl);
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

    @SneakyThrows
    private File[] resolveSqlScriptFiles(String dbResourceFolder) {
        var url = DbModule.class.getClassLoader().getResources(dbResourceFolder).nextElement();
        var directory = Paths.get(url.toURI());
        return Objects.requireNonNull(directory.toFile().listFiles());
    }

}
