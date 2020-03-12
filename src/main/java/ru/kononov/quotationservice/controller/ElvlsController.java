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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.kononov.quotationservice.constant.MdcKey.OPERATION_NAME;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.V_REQUEST;
import static ru.kononov.quotationservice.error.exception.ExceptionCode.Z_SYSTEM;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.*;
import static ru.kononov.quotationservice.util.ControllerMethodWrapper.wrap;
import static ru.kononov.quotationservice.util.JsonHelper.fromJson;
import static ru.kononov.quotationservice.util.JsonHelper.toJson;
import static ru.kononov.quotationservice.util.RequestReader.extractPayload;
import static ru.kononov.quotationservice.util.ResponseWriter.*;

@Log4j2
public class ElvlsController extends HttpController {

    private final AddElvlOperation addElvlOperation;
    private final GetAllElvlsOperation getAllElvlsOperation;
    private final GetElvlOperation getElvlOperation;
    private final ErrorBuilder errorBuilder;
    private final Pattern isinPattern = Pattern.compile("[\\d\\D]{12}");
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

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
                    var currentUri = exch.getRequestURI().toString();
                    switch (method) {
                        case "GET":
                            if (uri.equals(currentUri)) {
                                getAllElvls(point, exch);
                            } else {
                                tryGetElvl(point, exch, currentUri);
                            }
                            break;
                        case "POST":
                            addElvl(point, exch);
                            break;
                        default:
                            throw newApplicationException(UNKNOWN, Z_SYSTEM, String.format("Запрос типа '%s' не поддерживается", method));
                    }
                },
                (exch, e) -> writeClientErrorResponse(log, point, exch, e, this::errorToJson),
                (exch, e) -> writeServerErrorResponse(log, point, exch, e, this::errorToJson)
        );
    }

    private void addElvl(String point, HttpExchange exchange) {
        ThreadContext.put(OPERATION_NAME, ADD_ELVL.name());
        var payload = extractPayload(exchange);
        var request = fromJson(payload, AddElvlRequest.class);
        validate(request);
        var result = addElvlOperation.process(request);
        writeAcceptedResponse(log, point, exchange, () -> toJson(result));
    }

    private void validate(AddElvlRequest request) {
        var violations = factory.getValidator().validate(request);
        if (!violations.isEmpty()) {
            var message = violations.stream()
                    .map(this::buildErrorMessage)
                    .collect(Collectors.joining(";"));
            throw newApplicationException(resolve(), V_REQUEST, message);
        }
    }

    private String buildErrorMessage(ConstraintViolation<AddElvlRequest> violation) {
        return String.format("параметр: %s, текущее значение: %s, сообщение об ошибке: '%s'", violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage());
    }

    private void tryGetElvl(String point, HttpExchange exchange, String currentUri) {
        Optional.of(currentUri.substring(currentUri.indexOf(uri) + uri.length()))
                .filter(fragment -> fragment.startsWith("/"))
                .map(fragment -> fragment.substring(1))
                .filter(isin -> isinPattern.matcher(isin).matches())
                .ifPresentOrElse(
                        isin -> getElvl(point, exchange, isin),
                        () -> {
                            throw newApplicationException(resolve(), V_REQUEST, "Параметр isin имеет некорретный формат");
                        }
                );
    }

    private void getElvl(String point, HttpExchange exchange, String isin) {
        ThreadContext.put("isin", isin);
        ThreadContext.put(OPERATION_NAME, GET_ELVL.name());
        var result = getElvlOperation.process(isin);
        Supplier<byte[]> responseBuilder = nonNull(result)
                ? () -> toJson(result)
                : () -> null;
        writeOkResponse(log, point, exchange, responseBuilder);
    }

    private void getAllElvls(String point, HttpExchange exchange) {
        ThreadContext.put(OPERATION_NAME, GET_ALL_ELVLS.name());
        var result = getAllElvlsOperation.process();
        writeOkResponse(log, point, exchange, () -> toJson(result));
    }

    private byte[] errorToJson(ApplicationException e) {
        var error = errorBuilder.build(e);
        return toJson(error);
    }

}