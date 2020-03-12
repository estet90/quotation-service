package ru.kononov.quotationservice.module;

import dagger.Module;
import dagger.Provides;
import ru.kononov.quotationservice.util.db.DbHelper;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public class DbTestModule {

    @Provides
    @Singleton
    DbHelper dbHelper() {
        return mock(DbHelper.class);
    }

}
