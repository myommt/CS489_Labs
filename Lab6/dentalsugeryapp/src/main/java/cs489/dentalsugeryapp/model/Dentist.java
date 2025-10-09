package cs489.dentalsugeryapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "dentists")
public class Dentist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dentistId")
    private Integer dentistId;
    
    @Column(name = "firstName", nullable = false, length = 100)
    @NotBlank(message = "First Name is required and cannot be blank or empty.")
    private String firstName;
    
    @Column(name = "lastName", nullable = false, length = 100)
    @NotBlank(message = "Last Name is required and cannot be blank or empty.")
    private String lastName;
    
    @Column(name = "contactNumber")
    private String contactNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "specialization", length = 100)
    private String specialization;
}
