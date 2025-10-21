package cs489.finalrevision.finalrevision;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FinalrevisionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalrevisionApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Server is running...");
		};
	}

}
