package cs489.finalrevision.finalrevision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long supplierId;
    @NotBlank
    @Column(nullable=false)
    private String name; 
    private String contactNumber;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "primary_address_id",nullable=false)
    private Address primaryAddress;

    @OneToMany(fetch=FetchType.EAGER,cascade = CascadeType.ALL,mappedBy="supplier")
    private Set<Product> products;
 
}
