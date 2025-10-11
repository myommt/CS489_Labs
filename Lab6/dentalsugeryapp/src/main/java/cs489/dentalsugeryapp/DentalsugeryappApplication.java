package cs489.dentalsugeryapp;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cs489.dentalsugeryapp.exception.PatientNotFoundException;
import cs489.dentalsugeryapp.model.Patient;
import cs489.dentalsugeryapp.model.Address;
import cs489.dentalsugeryapp.service.PatientService;
import cs489.dentalsugeryapp.service.AddressService;

@SpringBootApplication
public class DentalsugeryappApplication implements CommandLineRunner {

	private PatientService patientService;
	private AddressService addressService;
	
	public DentalsugeryappApplication(PatientService patientService,
			AddressService addressService) {
		this.patientService = patientService;
		this.addressService = addressService;
	}
	public static void main(String[] args) {
		SpringApplication.run(DentalsugeryappApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello Dental Surgery App");


		// Create 
		var address1 = new Address(null, "123 Main St", "Fairfield", "IA", "52556");
		var address2 = new Address(null, "456 Elm St", "Los Angeles", "CA", "90001");
		
		// Create patients
		var patient1 = new Patient(null, "John", "Doe", "641-001-1234", "JD123@fairfield.org", LocalDate.parse("1984-10-20"), address1);
		var patient2 = new Patient(null, "Steve", "Job", "641-001-1345", "Job@apple.org", LocalDate.parse("1965-04-14"), address2);
		var patient3 = new Patient(null, "Sean", "Neil", "641-001-1241", "S.neil3@fairfield.org", LocalDate.parse("1984-10-11"),null);
		
		// Save patients
		var savePatient1=addPatient(patient1);
		var savePatient2=addPatient(patient2);
		var savePatient3 = addPatient(patient3);
		
		
		savePatient3.setAddress(address1);
		var updatedPatient3 = updatePatient(savePatient3);


		// Display all patients
		//listAllPatients();
 
		System.out.println("Patient 1 with Address: " + getPatientById(savePatient1.getPatientId()));
		System.out.println("Patient 2 with Address: " + getPatientById(savePatient2.getPatientId()));
		System.out.println("Patient 3 with Address: " + getPatientById(updatedPatient3.getPatientId()));
	}

	
	private Patient addPatient(Patient patient) {
		return patientService.addNewPatient(patient);
	}

	private void listAllPatients() {
		System.out.println("=== All Patients ===");
		patientService.getAllPatients().forEach(System.out::println);
	}

	private Patient getPatientById(Integer patientId) throws PatientNotFoundException {
		 return patientService.getPatientById(patientId);
	}

	private Patient updatePatient(Patient patient) {
		return patientService.updatePatient(patient);
	}

	private Address addAddress(Address address) {
		return addressService.addNewAddress(address);	
	}
}
