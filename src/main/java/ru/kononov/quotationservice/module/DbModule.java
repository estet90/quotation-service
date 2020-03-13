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
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(files, Spliterator.ORDERED), false)
                .map(Path::getFileName)
                .map(name -> String.format("runscript from 'classpath:/%s/%s'", dbResourceFolder, name))
                .collect(Collectors.joining("\\;", "quotation;INIT=", ""));
    }

    @SneakyThrows
    private Iterator<Path> resolveSqlScriptFiles(String dbResourceFolder) {
        var url = DbModule.class.getClassLoader().getResources(dbResourceFolder).nextElement();
        Path directory;
        var partsOfUri = url.toURI().toString().split("!");
        if (partsOfUri.length == 1) { //запуск приложения из IDE
            directory = Paths.get(url.toURI());
        } else { //запуск jar
            var fileSystem = FileSystems.newFileSystem(URI.create(partsOfUri[0]), new HashMap<String, String>());
            directory = fileSystem.getPath(partsOfUri[1]);
        }
        return Files.newDirectoryStream(directory)
                .iterator();
    }

}
