package ru.kononov.quotationservice.logic;

import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.dto.Elvl;
import ru.kononov.quotationservice.service.dao.QuotationDaoAdapter;
import ru.kononov.quotationservice.util.OperationWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
@Log4j2
public class GetElvlOperation {

    private final QuotationDaoAdapter quotationDaoAdapter;

    @Inject
    public GetElvlOperation(QuotationDaoAdapter quotationDaoAdapter) {
        this.quotationDaoAdapter = quotationDaoAdapter;
    }

    public BigDecimal process(String isin) {
        return OperationWrapper.wrap(log, "GetAllElvlsOperation.process", () -> quotationDaoAdapter.getElvl(isin)
                .map(Elvl::getValue).orElse(null));
    }

}
