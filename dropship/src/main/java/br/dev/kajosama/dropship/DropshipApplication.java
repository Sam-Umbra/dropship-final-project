package br.dev.kajosama.dropship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Sam_Umbra
 * @Description Main entry point for the Dropship Spring Boot application.
 *              This class initializes and runs the Spring application context.
 *              It also enables asynchronous method execution using
 *              {@link EnableAsync}.
 */
@SpringBootApplication
@EnableAsync
public class DropshipApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args Command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DropshipApplication.class, args);
	}

}
