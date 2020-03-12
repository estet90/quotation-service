package ru.kononov.quotationservice.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import ru.kononov.quotationservice.model.AddElvlRequest;
import ru.kononov.quotationservice.util.db.DbHelper;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        when(dbHelper.insert(any(Connection.class), any(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), any(), any(), anyString())).thenReturn(List.of());
        when(dbHelper.insert(any(Connection.class), any(), any(), any())).thenReturn(1L);

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
        when(dbHelper.insert(any(Connection.class), any(), any(), any(), any(), any())).thenReturn(1L);
        when(dbHelper.select(any(Connection.class), any(), any(), anyString())).thenReturn(List.of());
        when(dbHelper.insert(any(Connection.class), any(), any(), any())).thenReturn(1L);

        var result = operation.process(request);

        assertThat(result).isEqualTo(request.getAsk());
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()), eq(request.getBid()), any());
        verify(dbHelper).select(any(Connection.class), anyString(), any(), eq(request.getIsin()));
        verify(dbHelper).insert(any(Connection.class), anyString(), eq(request.getIsin()), eq(request.getAsk()));
    }

    private AddElvlRequest givenAddElvlRequest() {
        return new AddElvlRequest()
                .bid(BigDecimal.ONE)
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