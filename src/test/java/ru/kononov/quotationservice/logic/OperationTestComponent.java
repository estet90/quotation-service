package ru.kononov.quotationservice.logic;

import dagger.Component;
import ru.kononov.quotationservice.module.PropertyModule;
import ru.kononov.quotationservice.module.TestDbModule;

import javax.inject.Singleton;

@Component(modules = {
        PropertyModule.class,
        TestDbModule.class
})
@Singleton
public interface OperationTestComponent {

    void inject(AddElvlOperationTest operationTest);

}
