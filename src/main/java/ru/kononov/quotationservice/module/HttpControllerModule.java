package ru.kononov.quotationservice.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import ru.kononov.quotationservice.builder.ErrorBuilder;
import ru.kononov.quotationservice.controller.ElvlsController;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;
import ru.kononov.quotationservice.util.PropertyResolver;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class HttpControllerModule {

    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

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

}
