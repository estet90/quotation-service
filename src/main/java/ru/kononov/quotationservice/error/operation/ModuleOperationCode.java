package ru.kononov.quotationservice.error.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;

import static java.util.Optional.ofNullable;
import static ru.kononov.quotationservice.constant.ThreadContextKey.OPERATION_NAME;

@RequiredArgsConstructor
@Getter
public enum ModuleOperationCode {

    ADD_ELVL("01"),
    GET_ALL_ELVLS("02"),
    GET_ELVL("03"),

    UNKNOWN("99");

    private final String code;

    public static ModuleOperationCode resolve() {
        return ofNullable(ThreadContext.get(OPERATION_NAME))
                .map(ModuleOperationCode::valueOf)
                .orElse(UNKNOWN);
    }

}
