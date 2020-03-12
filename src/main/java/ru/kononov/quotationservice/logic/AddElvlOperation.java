package ru.kononov.quotationservice.logic;

import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.model.AddElvlRequest;
import ru.kononov.quotationservice.service.dao.QuotationDaoAdapter;
import ru.kononov.quotationservice.util.OperationWrapper;
import ru.kononov.quotationservice.util.db.DbHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Connection;

import static java.util.Optional.ofNullable;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.B_REQUEST;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@Singleton
@Log4j2
public class AddElvlOperation {

    private final QuotationDaoAdapter quotationDaoAdapter;
    private final DbHelper dbHelper;

    @Inject
    public AddElvlOperation(QuotationDaoAdapter quotationDaoAdapter, DbHelper dbHelper) {
        this.quotationDaoAdapter = quotationDaoAdapter;
        this.dbHelper = dbHelper;
    }

    public BigDecimal process(AddElvlRequest request) {
        var point = "AddElvlOperation.process";
        return OperationWrapper.wrap(log, point, () -> {
            var ask = request.getAsk();
            var bid = request.getBid();
            validateRequest(ask, bid);
            return dbHelper.inTransaction(connection -> {
                quotationDaoAdapter.insertHistory(connection, request);
                var isin = request.getIsin();
                return quotationDaoAdapter.getElvl(connection, isin)
                        .map(value -> updateElvl(point, ask, bid, connection, isin, value))
                        .orElseGet(() -> insertElvl(point, ask, bid, connection, isin));
            });
        });
    }

    private BigDecimal insertElvl(String point, BigDecimal ask, BigDecimal bid, Connection connection, String isin) {
        var elvl = ofNullable(bid).orElse(ask);
        quotationDaoAdapter.insertElvl(connection, isin, elvl);
        log.info("{} данные добавлены", point);
        return elvl;
    }

    private BigDecimal updateElvl(String point, BigDecimal ask, BigDecimal bid, Connection connection, String isin, BigDecimal value) {
        var elvl = ofNullable(bid)
                .filter(b -> b.compareTo(value) > 0)
                .orElseGet(() -> ask.compareTo(value) < 0 ? ask : value);
        if (!value.equals(elvl)) {
            quotationDaoAdapter.updateElvl(connection, isin, elvl);
            log.info("{} данные обновлены", point);
        } else {
            log.info("{} не требуется обновление данных", point);
        }
        return elvl;
    }

    private void validateRequest(BigDecimal ask, BigDecimal bid) {
        ofNullable(bid).ifPresent(b -> {
            if (b.compareTo(ask) >= 0) {
                throw newApplicationException(resolve(), B_REQUEST, "Параметр bid должен быть меньше ask");
            }
        });
    }

}
