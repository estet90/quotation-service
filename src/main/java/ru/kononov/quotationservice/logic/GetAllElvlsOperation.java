package ru.kononov.quotationservice.logic;

import ru.kononov.quotationservice.model.ElvlData;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GetAllElvlsOperation {

    @Inject
    public GetAllElvlsOperation() {
    }

    public List<ElvlData> process() {
        return List.of();
    }

}
