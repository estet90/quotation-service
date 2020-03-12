package ru.kononov.quotationservice.module;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.kononov.quotationservice.util.PropertyResolver;

import javax.inject.Singleton;

@Module
public class PropertyModule {

    private final String propertyPath;

    public PropertyModule(String[] args) {
        var options = new Options();
        options.addOption("c", "config", true, "Properties file location");
        try {
            var parsed = new DefaultParser().parse(options, args);
            this.propertyPath = parsed.getOptionValue("config", "classpath:/application.properties");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    PropertyResolver propertyResolver() {
        return new PropertyResolver(propertyPath);
    }

}
