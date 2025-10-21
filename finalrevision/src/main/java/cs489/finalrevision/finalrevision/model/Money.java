package cs489.finalrevision.finalrevision.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record Money(  
    String currency,
    Double amount
){
  
}
