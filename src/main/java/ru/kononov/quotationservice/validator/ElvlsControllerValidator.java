package ru.kononov.quotationservice.validator;

import ru.kononov.quotationservice.model.AddElvlRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.stream.Collectors;

import static ru.kononov.quotationservice.error.exception.ExceptionCode.V_REQUEST;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

public class ElvlsControllerValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    public static void validate(AddElvlRequest request) {
        var violations = factory.getValidator().validate(request);
        if (!violations.isEmpty()) {
            var message = violations.stream()
                    .map(ElvlsControllerValidator::buildErrorMessage)
                    .collect(Collectors.joining(";"));
            throw newApplicationException(resolve(), V_REQUEST, message);
        }
    }

    private static String buildErrorMessage(ConstraintViolation<AddElvlRequest> violation) {
        return String.format("параметр: %s, текущее значение: %s, сообщение об ошибке: '%s'", violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage());
    }

}
