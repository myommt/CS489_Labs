package cs489.finalrevision.finalrevision.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;
    @Column(nullable = false, unique = true)
    @NotNull(message="Product Number cannot be null")
    private long productNo;
    @Column
    @NotBlank(message="Product Name cannot be blank")
    private String name;
    private LocalDate dateSupplied;
    private int quantityInStock;

    @NotNull
    @Embedded 
    @AttributeOverrides({
        @AttributeOverride(name="currency",column=@Column(name="unitprice_currency")),
        @AttributeOverride(name = "amount", column = @Column(name = "unitprice_amount"))
    })
    private Money unitprice;
    
    @ManyToOne
    @JoinColumn(name="supplier_id",nullable=false)
    private Supplier supplier;
 
}
