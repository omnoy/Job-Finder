package superapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(/*scanBasePackages= {"rest_controllers", "superapp.logic.mockup"}*/)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
