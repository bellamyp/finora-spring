package com.bellamyphan.finora_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BankDailyBalanceDto(LocalDate date, BigDecimal balance) {}