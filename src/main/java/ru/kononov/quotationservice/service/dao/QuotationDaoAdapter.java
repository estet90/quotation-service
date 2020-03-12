package ru.kononov.quotationservice.service.dao;

import ru.kononov.quotationservice.dto.Elvl;
import ru.kononov.quotationservice.model.AddElvlRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Singleton
public class QuotationDaoAdapter {

    private final QuotationDao dao;

    @Inject
    public QuotationDaoAdapter(QuotationDao dao) {
        this.dao = dao;
    }

    public Optional<BigDecimal> getElvl(Connection connection, String isin) {
        return dao.getElvl(connection, isin);
    }

    public Optional<Elvl> getElvl(String isin) {
        return dao.getElvl(isin);
    }

    public List<Elvl> getAllElvls() {
        return dao.getAllElvls();
    }

    public void insertElvl(Connection connection, String isin, BigDecimal value) {
        dao.insertElvl(connection, isin, value);
    }

    public void updateElvl(Connection connection, String isin, BigDecimal value) {
        dao.updateElvl(connection, isin, value);
    }

    public void insertHistory(Connection connection, AddElvlRequest request) {
        dao.insertHistory(connection, request.getIsin(), request.getAsk(), request.getBid(), OffsetDateTime.now(UTC));
    }

}
