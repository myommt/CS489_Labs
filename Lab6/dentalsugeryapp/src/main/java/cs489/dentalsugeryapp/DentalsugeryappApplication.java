package cs489.dentalsugeryapp;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cs489.dentalsugeryapp.exception.PatientNotFoundException;
import cs489.dentalsugeryapp.model.Patient;
import cs489.dentalsugeryapp.service.PatientService;

@SpringBootApplication
public class DentalsugeryappApplication implements CommandLineRunner {

	private PatientService patientService;
	
	public DentalsugeryappApplication(PatientService patientService) {
		this.patientService = patientService;
	}
	public static void main(String[] args) {
		SpringApplication.run(DentalsugeryappApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello Dental Surgery App");

		var patient1 = new Patient(null, "John", "Doe", "641-001-1234", "JD123@fairfield.org", LocalDate.parse("1984-10-20"));
		var patient2 = new Patient(null, "Steve", "Job", "641-001-1345", "Job@apple.org", LocalDate.parse("1965-04-14"));
		
		addPatients(patient1);
		addPatients(patient2);

		listAllPatients();

		System.out.println(getPatientById(6));
	}

	private Patient addPatients(Patient patient) {
		return patientService.addNewPatient(patient);
	}

	private void listAllPatients() {
		patientService.getAllPatients().forEach(System.out::println);
	}

	private Patient getPatientById(Integer id) {
		Patient PatientFound=null;
		try{
		 	PatientFound=patientService.getPatientById(id);
		}
		catch(PatientNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return PatientFound;
	}
}
