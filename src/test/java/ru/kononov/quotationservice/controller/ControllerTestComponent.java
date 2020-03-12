package ru.kononov.quotationservice.controller;

import dagger.Component;
import ru.kononov.quotationservice.module.HttpControllerModule;
import ru.kononov.quotationservice.module.PropertyModule;
import ru.kononov.quotationservice.module.TestOperationMockModule;

import javax.inject.Singleton;

@Component(modules = {
        PropertyModule.class,
        HttpControllerModule.class,
        TestOperationMockModule.class
})
@Singleton
interface ControllerTestComponent {

    void inject(ElvlsControllerTest controllerTest);

}
