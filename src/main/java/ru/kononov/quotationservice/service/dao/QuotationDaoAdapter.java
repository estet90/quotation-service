package ru.kononov.quotationservice.service.dao;

import ru.kononov.quotationservice.model.AddElvlRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigInteger;
import java.sql.Connection;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Singleton
public class QuotationDaoAdapter {

    private final QuotationDao dao;

    @Inject
    public QuotationDaoAdapter(QuotationDao dao) {
        this.dao = dao;
    }

    public Optional<Integer> getElvl(Connection connection, String isin) {
        return dao.getElvl(connection, isin);
    }

    public void insertElvl(Connection connection, String isin, Integer value) {
        dao.insertElvl(connection, isin, value);
    }

    public void updateElvl(Connection connection, String isin, Integer value) {
        dao.updateElvl(connection, isin, value);
    }
    public void insertHistory(Connection connection, AddElvlRequest request) {
        dao.insertHistory(connection, request.getIsin(), request.getAsk(), request.getBid(), OffsetDateTime.now(UTC));
    }

}
