package cs489.finalrevision.finalrevision.dto;

import cs489.finalrevision.finalrevision.model.Money;
public record ProductResponse(
    long productId,
    long productNo,
    String name,
    int quantityInStock,
    Money unitprice
) {}
