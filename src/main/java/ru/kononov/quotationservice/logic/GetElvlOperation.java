package ru.kononov.quotationservice.logic;

import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
@Log4j2
public class GetElvlOperation {

    @Inject
    public GetElvlOperation() {
    }

    public BigDecimal process(String isin) {
        return null;
    }

}
