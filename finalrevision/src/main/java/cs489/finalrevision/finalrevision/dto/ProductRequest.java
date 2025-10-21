package cs489.finalrevision.finalrevision.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
    @NotNull Long productNo,
    @NotBlank String name,
    @NotNull LocalDate dateSupplied,
    @NotNull Integer quantityInStock,
    @NotNull MoneyRequest unitprice
) {}
