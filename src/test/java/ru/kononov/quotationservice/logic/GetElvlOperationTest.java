package ru.kononov.quotationservice.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import ru.kononov.quotationservice.error.exception.ApplicationException;
import ru.kononov.quotationservice.util.db.DbHelper;
import ru.kononov.quotationservice.util.db.ResultSetExtractor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.I_DB;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

class GetElvlOperationTest {

    @Inject
    GetElvlOperation operation;
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
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), anyString())).thenReturn(List.of(BigDecimal.TEN));

        var result = operation.process("isin");

        assertThat(result).isEqualTo(BigDecimal.TEN);
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), eq("isin"));
    }

    @Test
    void processNotFound() {
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), anyString())).thenReturn(List.of());

        var result = operation.process("isin");

        assertNull(result);
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), eq("isin"));
    }

    @Test
    void processException() {
        var givenException = newApplicationException(new SQLException(), resolve(), I_DB, "error");
        when(dbHelper.select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), anyString())).thenThrow(givenException);

        var resultException = assertThrows(ApplicationException.class, () -> operation.process("isin"));

        assertThat(resultException).isEqualTo(givenException);
        verify(dbHelper).select(anyString(), ArgumentMatchers.<ResultSetExtractor<BigDecimal>>any(), eq("isin"));
    }
}