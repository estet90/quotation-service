package ru.kononov.quotationservice.builder;

import ru.kononov.quotationservice.dto.Elvl;
import ru.kononov.quotationservice.model.ElvlData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ElvlDataBuilder {

    @Inject
    public ElvlDataBuilder() {
    }

    public ElvlData build(Elvl elvl) {
        return new ElvlData()
                .isin(elvl.getIsin())
                .value(elvl.getValue());
    }
}
