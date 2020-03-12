package ru.kononov.quotationservice.service.dao;

import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.util.db.DbHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigInteger;
import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static ru.kononov.quotationservice.util.db.DbLoggerHelper.executeWithLogging;

@Singleton
@Log4j2
public class QuotationDao {

    private final DbHelper dbHelper;

    @Inject
    public QuotationDao(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Optional<Integer> getElvl(Connection connection, String isin) {
        var sql = "SELECT value FROM elvls WHERE isin = ?";
        Supplier<List<Integer>> resultCallback = () -> dbHelper.select(connection, sql, rs -> rs.getInt("value"));
        var result = executeWithLogging(log, "QuotationDao.getElvl", () -> sql, () -> isin, resultCallback);
        return ofNullable(result)
                .filter(Predicate.not(List::isEmpty))
                .map(list -> list.get(0));
    }

    public void insertElvl(Connection connection, String isin, Integer value) {
        var sql = "INSERT INTO elvls (isin, value) VALUES (?, ?)";
        Supplier<Long> resultCallback = () -> dbHelper.insert(connection, sql, isin, value);
        executeWithLogging(log, "QuotationDao.insertElvl", () -> sql, () -> new Object[]{isin, value}, resultCallback);
    }

    public void updateElvl(Connection connection, String isin, Integer value) {
        var sql = "UPDATE elvls SET value=? WHERE isin = ?";
        Supplier<Long> resultCallback = () -> dbHelper.insert(connection, sql, isin, value);
        executeWithLogging(log, "QuotationDao.insertElvl", () -> sql, () -> new Object[]{isin, value}, resultCallback);
    }

    public void insertHistory(Connection connection, String isin, Integer ask, Integer bid, OffsetDateTime date) {
        var sql = "INSERT INTO history (isin, ask, bid, date) VALUES (?, ?, ?, ?)";
        Supplier<Long> resultCallback = () -> dbHelper.insert(connection, sql, isin, ask, bid, date);
        executeWithLogging(log, "QuotationDao.insertElvl", () -> sql, () -> new Object[]{isin, ask, bid, date}, resultCallback);
    }

}
