package ru.kononov.quotationservice.logic;

import lombok.extern.log4j.Log4j2;
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

    @Inject
    public GetAllElvlsOperation(QuotationDaoAdapter quotationDaoAdapter) {
        this.quotationDaoAdapter = quotationDaoAdapter;
    }

    public List<ElvlData> process() {
        return OperationWrapper.wrap(log, "GetAllElvlsOperation.process", () -> quotationDaoAdapter.getAllElvls().stream()
                .map(elvl -> new ElvlData()
                        .isin(elvl.getIsin())
                        .value(elvl.getValue())
                )
                .collect(Collectors.toList()));
    }

}
