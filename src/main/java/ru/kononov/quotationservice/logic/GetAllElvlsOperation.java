package ru.kononov.quotationservice.logic;

import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.builder.ElvlDataBuilder;
import ru.kononov.quotationservice.model.ElvlData;
import ru.kononov.quotationservice.service.dao.QuotationDaoAdapter;
import ru.kononov.quotationservice.util.OperationWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Log4j2
public class GetAllElvlsOperation {

    private final QuotationDaoAdapter quotationDaoAdapter;
    private final ElvlDataBuilder elvlDataBuilder;

    @Inject
    public GetAllElvlsOperation(QuotationDaoAdapter quotationDaoAdapter, ElvlDataBuilder elvlDataBuilder) {
        this.quotationDaoAdapter = quotationDaoAdapter;
        this.elvlDataBuilder = elvlDataBuilder;
    }

    public List<ElvlData> process() {
        return OperationWrapper.wrap(
                log, "GetAllElvlsOperation.process",
                () -> quotationDaoAdapter.getAllElvls().stream()
                        .map(elvlDataBuilder::build)
                        .collect(Collectors.toList())
        );
    }

}
