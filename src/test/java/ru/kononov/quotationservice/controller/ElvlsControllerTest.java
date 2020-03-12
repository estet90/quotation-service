package ru.kononov.quotationservice.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;
import ru.kononov.quotationservice.model.ElvlData;
import ru.kononov.quotationservice.module.PropertyModule;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.*;

class ElvlsControllerTest {

    @Inject
    ElvlsController controller;
    @Inject
    AddElvlOperation addElvlOperation;
    @Inject
    GetElvlOperation getElvlOperation;
    @Inject
    GetAllElvlsOperation getAllElvlsOperation;

    @BeforeEach
    void init() {
        DaggerControllerTestComponent.builder()
                .propertyModule(new PropertyModule(new String[]{}))
                .build()
                .inject(this);
    }

    @Nested
    class GetElvlTest {
        @Test
        void handle() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls/123456789123");
            when(getElvlOperation.process(anyString())).thenReturn(BigDecimal.ONE);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_OK), eq(1L));
            verify(exchange).getResponseBody();
        }

        @Test
        void handleNotFound() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls/123456789123");
            when(getElvlOperation.process(any())).thenReturn(null);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_NO_CONTENT), eq(-1L));
            verify(exchange).getResponseBody();
        }

        @Test
        void handleClientError() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls/12345678912");

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), anyLong());
            verify(exchange).getResponseBody();
        }

        @Test
        void handleServerError() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls/123456789123");
            when(getElvlOperation.process(anyString())).thenThrow(new RuntimeException());

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_INTERNAL_ERROR), anyLong());
            verify(exchange).getResponseBody();
        }
    }

    @Nested
    class GetAllElvlsTest {
        @Test
        void handle() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls");
            when(getAllElvlsOperation.process()).thenReturn(List.of(new ElvlData()));

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_OK), anyLong());
            verify(exchange).getResponseBody();
        }

        @Test
        void handleServerError() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "GET", "/quotation-service/api/v1/elvls");
            when(getAllElvlsOperation.process()).thenThrow(new RuntimeException());

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_INTERNAL_ERROR), anyLong());
            verify(exchange).getResponseBody();
        }
    }

    @Nested
    class AddElvlOperationTest {
        @Test
        void handle() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "POST", "/quotation-service/api/v1/elvls");
            when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{\"isin\": \"123456789123\", \"ask\": 124, \"bid\": 123}".getBytes()));
            when(addElvlOperation.process(any())).thenReturn(BigDecimal.ONE);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_ACCEPTED), anyLong());
            verify(exchange).getResponseBody();
        }

        @Test
        void handleClientError() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "POST", "/quotation-service/api/v1/elvls");
            when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{\"isin\": \"1234567\", \"ask\": 124, \"bid\": 123}".getBytes()));

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), anyLong());
            verify(exchange).getResponseBody();
        }

        @Test
        void handleServerError() throws IOException {
            var exchange = mock(HttpExchange.class);
            mockExchange(exchange, "POST", "/quotation-service/api/v1/elvls");
            when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{\"isin\": \"123456789123\", \"ask\": 124, \"bid\": 123}".getBytes()));
            when(addElvlOperation.process(any())).thenThrow(new RuntimeException());

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_INTERNAL_ERROR), anyLong());
            verify(exchange).getResponseBody();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void handleUnknownMethod(String method) throws IOException {
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, method, "/quotation-service/api/v1/elvls");

        controller.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), anyLong());
        verify(exchange).getResponseBody();
    }

    private static void mockExchange(HttpExchange exchange, String method, String query) {
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getRequestURI()).thenReturn(URI.create(query));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
    }

}
