package ru.kononov.quotationservice.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@Log4j2
public class LoggingFilter extends Filter {

    @Inject
    LoggingFilter() {
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        ThreadContext.put("requestId", UUID.randomUUID().toString());
        try {
            if (log.isDebugEnabled()) {
                logRequest(exchange, chain);
                logResponse(exchange);
            } else {
                chain.doFilter(exchange);
            }
        } finally {
            ThreadContext.clearAll();
        }
    }

    private void logRequest(HttpExchange exchange, Chain chain) {
        try (var inputStream = exchange.getRequestBody()) {
            var byteStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteStream);
            var body = byteStream.toString(StandardCharsets.UTF_8.name());
            exchange.setStreams(new ByteArrayInputStream(byteStream.toByteArray()), exchange.getResponseBody());
            if (nonNull(body) && body.length() > 0) {
                logIn(exchange, body);
            } else {
                logIn(exchange);
            }
            chain.doFilter(exchange);
        } catch (Exception e) {
            var applicationException = newApplicationException(resolve(), Z_SYSTEM, e.getMessage());
            log.error("LoggingFilter.logRequest.thrown", e);
            throw applicationException;
        }
    }

    private void logIn(HttpExchange exchange, String body) {
        log.debug(
                "request\n\tmethod={}\n\turi={}\n\theaders={}\n\tbody={}",
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders(),
                body
        );
    }

    private void logIn(HttpExchange exchange) {
        log.debug(
                "request\n\tmethod={}\n\turi={}\n\theaders={}",
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders()
        );
    }

    private void logResponse(HttpExchange exchange) throws IOException {
        try (var bodyStream = exchange.getResponseBody()) {
            if (bodyStream instanceof ByteArrayOutputStream && ((ByteArrayOutputStream) bodyStream).size() > 0) {
                var body = ((ByteArrayOutputStream) bodyStream).toString(StandardCharsets.UTF_8.name());
                logOut(exchange, body);
            } else {
                logOut(exchange);
            }
        }
    }

    private void logOut(HttpExchange exchange, String body) {
        log.debug(
                "response\n\tstatus={}\n\theaders={}\n\tbody={}",
                exchange.getResponseCode(),
                exchange.getResponseHeaders(),
                body
        );
    }

    private void logOut(HttpExchange exchange) {
        log.debug(
                "response\n\tstatus={}\n\theaders={}",
                exchange.getResponseCode(),
                exchange.getResponseHeaders()
        );
    }

    @Override
    public String description() {
        return "LoggingFilter";
    }

}
