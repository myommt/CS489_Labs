package cs489.finalrevision.finalrevision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MoneyRequest(
    @NotBlank String currency,
    @NotNull Double amount
) {}
