package ru.kononov.quotationservice.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Elvl {
    String isin;
    BigDecimal value;
}
