package ru.kononov.quotationservice.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import ru.kononov.quotationservice.dto.Elvl;
import ru.kononov.quotationservice.error.exception.ApplicationException;
import ru.kononov.quotationservice.model.ElvlData;
import ru.kononov.quotationservice.util.db.DbHelper;
import ru.kononov.quotationservice.util.db.ResultSetExtractor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.I_DB;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

class GetAllElvlsOperationTest {

    @Inject
    GetAllElvlsOperation operation;
    @Inject
    DbHelper dbHelper;

    @BeforeEach
    void init() {
        DaggerOperationTestComponent.builder()
                .build()
                .inject(this);
    }

    @Test
    void process() {
        var givenElvls = List.of(
                new Elvl("isin1", BigDecimal.ONE),
                new Elvl("isin2", BigDecimal.TEN)
        );
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any())).thenReturn(givenElvls);

        var result = operation.process();

        assertThat(result).hasSize(givenElvls.size());
        var expectedList = givenElvls.stream()
                .map(elvl -> new ElvlData()
                        .isin(elvl.getIsin())
                        .value(elvl.getValue())
                )
                .collect(Collectors.toList());
        assertThat(result).containsAll(expectedList);
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any());
    }

    @Test
    void processEmptyResult() {
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any())).thenReturn(List.of());

        var result = operation.process();

        assertThat(result).isEmpty();
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any());
    }

    @Test
    void processException() {
        var givenException = newApplicationException(new SQLException(), resolve(), I_DB, "error");
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any())).thenThrow(givenException);

        var resultException = assertThrows(ApplicationException.class, () -> operation.process());

        assertThat(resultException).isEqualTo(givenException);
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<Elvl>>any());
    }

}