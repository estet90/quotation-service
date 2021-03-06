package ru.kononov.quotationservice.controller;

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.quotationservice.builder.ErrorBuilder;
import ru.kononov.quotationservice.error.exception.ApplicationException;
import ru.kononov.quotationservice.logic.AddElvlOperation;
import ru.kononov.quotationservice.logic.GetAllElvlsOperation;
import ru.kononov.quotationservice.logic.GetElvlOperation;
import ru.kononov.quotationservice.model.AddElvlRequest;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static ru.kononov.quotationservice.constant.ThreadContextKey.OPERATION_NAME;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.V_REQUEST;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.V_UNSUPPORTED_METHOD;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.*;
import static ru.kononov.quotationservice.util.ControllerMethodWrapper.wrap;
import static ru.kononov.quotationservice.util.JsonHelper.fromJson;
import static ru.kononov.quotationservice.util.JsonHelper.toJson;
import static ru.kononov.quotationservice.util.RequestReader.extractPayload;
import static ru.kononov.quotationservice.util.ResponseWriter.*;
import static ru.kononov.quotationservice.validator.ElvlsControllerValidator.validate;

@Log4j2
public class ElvlsController extends HttpController {

    private final AddElvlOperation addElvlOperation;
    private final GetAllElvlsOperation getAllElvlsOperation;
    private final GetElvlOperation getElvlOperation;
    private final ErrorBuilder errorBuilder;
    private final Pattern isinPattern = Pattern.compile("[\\d\\D]{12}");

    public ElvlsController(String uri,
                           AddElvlOperation addElvlOperation,
                           GetAllElvlsOperation getAllElvlsOperation,
                           GetElvlOperation getElvlOperation,
                           ErrorBuilder errorBuilder) {
        super(uri);
        this.addElvlOperation = addElvlOperation;
        this.getAllElvlsOperation = getAllElvlsOperation;
        this.getElvlOperation = getElvlOperation;
        this.errorBuilder = errorBuilder;
    }

    public void handle(HttpExchange exchange) {
        var point = "ElvlsController.handle";
        wrap(
                log, point, exchange,
                exch -> {
                    var method = exch.getRequestMethod();
                    switch (method) {
                        case "GET" -> {
                            var currentUri = exch.getRequestURI().toString();
                            if (uri.equals(currentUri)) {
                                getAllElvls(point, exch);
                            } else {
                                getElvl(point, exch, currentUri);
                            }
                        }
                        case "POST" -> addElvl(point, exch);
                        default -> throw newApplicationException(UNKNOWN, V_UNSUPPORTED_METHOD, method);
                    }
                },
                (exch, e) -> writeClientErrorResponse(log, point, exch, e, this::errorToJson),
                (exch, e) -> writeServerErrorResponse(log, point, exch, e, this::errorToJson)
        );
    }

    private void getAllElvls(String point, HttpExchange exchange) {
        ThreadContext.put(OPERATION_NAME, GET_ALL_ELVLS.name());
        var result = getAllElvlsOperation.process();
        writeOkResponse(log, point, exchange, () -> toJson(result));
    }

    private void getElvl(String point, HttpExchange exchange, String currentUri) {
        var isin = extractIsin(currentUri);
        ThreadContext.put(OPERATION_NAME, GET_ELVL.name());
        ThreadContext.put("isin", isin);
        var result = getElvlOperation.process(isin);
        Supplier<byte[]> responseBuilder = nonNull(result)
                ? () -> toJson(result)
                : () -> null;
        writeOkResponse(log, point, exchange, responseBuilder);
    }

    private void addElvl(String point, HttpExchange exchange) {
        ThreadContext.put(OPERATION_NAME, ADD_ELVL.name());
        var payload = extractPayload(exchange);
        var request = fromJson(payload, AddElvlRequest.class);
        validate(request);
        var result = addElvlOperation.process(request);
        writeAcceptedResponse(log, point, exchange, () -> toJson(result));
    }

    private String extractIsin(String currentUri) {
        return Optional.of(currentUri.substring(currentUri.indexOf(uri) + uri.length()))
                .filter(fragment -> fragment.startsWith("/"))
                .map(fragment -> fragment.substring(1))
                .filter(fragment -> isinPattern.matcher(fragment).matches())
                .orElseThrow(() -> newApplicationException(resolve(), V_REQUEST, "Параметр isin имеет некорретный формат"));
    }

    private byte[] errorToJson(ApplicationException e) {
        var error = errorBuilder.build(e);
        return toJson(error);
    }

}
