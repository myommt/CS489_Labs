package cs489.miu.dentalsurgeryapp.model;

import java.time.LocalDate; 
 
import jakarta.persistence.*;  
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;
    
    @Column(name = "firstName", nullable = false, length = 100)
    @NotBlank(message="First Name is required and cannot be blank or empty.")
    private String firstName;
    
    @Column(name = "lastName", nullable = false, length = 100)
    @NotBlank(message="Last Name is required and cannot be blank or empty.")
    private String lastName;
    
    @Column(name = "contactNumber")
    private String contactNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "billing_address_id", nullable= true )// @JoinColumn(name = "address_id", nullable= true,unique=true )
    private Address address;

}
