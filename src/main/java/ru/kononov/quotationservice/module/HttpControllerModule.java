package ru.kononov.quotationservice.module;

import dagger.Module;
import dagger.Provides;
import ru.kononov.quotationservice.builder.ErrorBuilder;
import ru.kononov.quotationservice.controller.ElvlsController;
import ru.kononov.quotationservice.controller.SwaggerController;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;
import ru.kononov.quotationservice.util.PropertyResolver;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class HttpControllerModule {

    @Provides
    @Named("contextPath")
    @Singleton
    String contextPath(PropertyResolver propertyResolver) {
        return propertyResolver.getStringProperty("server.path");
    }

    @Provides
    @Singleton
    ElvlsController elvlsController(@Named("contextPath") String contextPath,
                                    AddElvlOperation addElvlOperation,
                                    GetAllElvlsOperation getAllElvlsOperation,
                                    GetElvlOperation getElvlOperation,
                                    ErrorBuilder errorBuilder) {
        return new ElvlsController(contextPath + "/elvls", addElvlOperation, getAllElvlsOperation, getElvlOperation, errorBuilder);
    }

    @Provides
    @Singleton
    SwaggerController swaggerController(@Named("contextPath") String contextPath) {
        return new SwaggerController(contextPath + "/swagger");
    }

}
