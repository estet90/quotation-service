package ru.kononov.quotationservice;

import dagger.Component;
import ru.kononov.quotationservice.module.DbModule;
import ru.kononov.quotationservice.module.HttpControllerModule;
import ru.kononov.quotationservice.module.PropertyModule;
import ru.kononov.quotationservice.server.Server;

import javax.inject.Singleton;

@Component(modules = {
        PropertyModule.class,
        HttpControllerModule.class,
        DbModule.class
})
@Singleton
interface ApplicationComponent {

    Server server();

}
