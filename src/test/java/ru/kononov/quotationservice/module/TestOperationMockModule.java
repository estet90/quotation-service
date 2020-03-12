package ru.kononov.quotationservice.module;

import dagger.Module;
import dagger.Provides;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public class TestOperationMockModule {

    @Provides
    @Singleton
    AddElvlOperation addElvlOperation() {
        return mock(AddElvlOperation.class);
    }

    @Provides
    @Singleton
    GetElvlOperation getElvlOperation() {
        return mock(GetElvlOperation.class);
    }

    @Provides
    @Singleton
    GetAllElvlsOperation getAllElvlsOperation() {
        return mock(GetAllElvlsOperation.class);
    }

}
