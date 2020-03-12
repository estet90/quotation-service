package ru.kononov.quotationservice.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import ru.kononov.quotationservice.error.exception.ApplicationException;
import ru.kononov.quotationservice.model.AddElvlRequest;
import ru.kononov.quotationservice.util.db.DbHelper;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.I_DB;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

class AddElvlOperationTest {

    @Inject
    AddElvlOperation operation;
    @Inject
    DbHelper dbHelper;

    @BeforeEach
    void init() {
        DaggerOperationTestComponent.builder()
                .build()
                .inject(this);
    }

    @Test
    void processNewElvlFromBid() {
        var request = givenAddElvlRequest();
        mockTransaction();
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), anyString(), any(), anyString())).thenReturn(List.of());
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any())).thenReturn(1L);

        var result = operation.process(request);

        assertThat(result).isEqualTo(request.getBid());
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getBid()));
    }

    @Test
    void processNewElvlFromAsk() {
        var request = givenAddElvlRequest()
                .bid(null);
        mockTransaction();
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), anyString(), any(), anyString())).thenReturn(List.of());
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any())).thenReturn(1L);

        var result = operation.process(request);

        assertThat(result).isEqualTo(request.getAsk());
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()));
    }

    @Test
    void processUpdateElvlFromBid() {
        var request = givenAddElvlRequest();
        mockTransaction();
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), anyString(), any(), anyString())).thenReturn(List.of(request.getBid().subtract(BigDecimal.ONE)));
        when(dbHelper.update(any(Connection.class), anyString(), any(), any())).thenReturn(1);

        var result = operation.process(request);

        assertThat(result).isEqualTo(request.getBid());
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper).update(any(Connection.class), anyString(), eq(request.getBid()), eq(request.getIsin()));
    }

    @Test
    void processUpdateElvlFromAsk() {
        var request = givenAddElvlRequest();
        mockTransaction();
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), anyString(), any(), anyString())).thenReturn(List.of(request.getAsk().add(BigDecimal.ONE)));
        when(dbHelper.update(any(Connection.class), anyString(), any(), any())).thenReturn(1);

        var result = operation.process(request);

        assertThat(result).isEqualTo(request.getAsk());
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper).update(any(Connection.class), anyString(), eq(request.getAsk()), eq(request.getIsin()));
    }

    @Test
    void processNoUpdate() {
        var request = givenAddElvlRequest();
        mockTransaction();
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenReturn(1L);
        var currentElvl = request.getBid().add(BigDecimal.ONE);
        when(dbHelper.select(any(Connection.class), anyString(), any(), anyString())).thenReturn(List.of(currentElvl));

        var result = operation.process(request);

        assertThat(result).isEqualTo(currentElvl);
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper, never()).update(any(Connection.class), anyString(), any(), any());
    }

    @Test
    void processException() {
        var request = givenAddElvlRequest();
        mockTransaction();
        var givenException = newApplicationException(new SQLException(), resolve(), I_DB, "error");
        when(dbHelper.insert(any(Connection.class), anyString(), any(), any(), any(), any())).thenThrow(givenException);

        var resultException = assertThrows(ApplicationException.class, () -> operation.process(request));

        assertThat(resultException).isEqualTo(givenException);
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper, never()).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper, never()).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getBid()));
    }

    private AddElvlRequest givenAddElvlRequest() {
        return new AddElvlRequest()
                .bid(BigDecimal.valueOf(2L))
                .ask(BigDecimal.TEN)
                .isin("isin");
    }

    private void mockTransaction() {
        when(dbHelper.inTransaction(any())).thenAnswer((Answer<BigDecimal>) invocation -> {
            var args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            var function = (Function<Connection, BigDecimal>) args[0];
            var connection = mock(Connection.class);
            return function.apply(connection);
        });
    }
}