package ru.kononov.quotationservice.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import ru.kononov.quotationservice.TestCase;
import ru.kononov.quotationservice.util.db.DbHelper;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElvlsControllerTest {

    private final ElvlsController controller = TestCase.controller();

    @Test
    void handle() {
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls/123456789123");
        var dbHelper = mock(DbHelper.class);
        when(dbHelper.select(anyString(), any(), anyString())).thenReturn(List.of(BigDecimal.valueOf(1)));

        controller.handle(exchange);
    }

    private static void mockExchange(HttpExchange exchange, String method, String query) {
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getRequestURI()).thenReturn(URI.create(query));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
    }

}
