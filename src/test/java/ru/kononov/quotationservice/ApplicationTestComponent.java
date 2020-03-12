package ru.kononov.quotationservice;

import dagger.Component;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;
import ru.kononov.quotationservice.module.DbTestModule;
import ru.kononov.quotationservice.module.HttpControllerModule;
import ru.kononov.quotationservice.module.PropertyModule;

import javax.inject.Singleton;

@Component(modules = {
        PropertyModule.class,
        HttpControllerModule.class,
        DbTestModule.class
})
@Singleton
interface ApplicationTestComponent {

    AddElvlOperation addElvlOperation();

    GetAllElvlsOperation getAllElvlsOperation();

    GetElvlOperation getElvlOperation();

}
