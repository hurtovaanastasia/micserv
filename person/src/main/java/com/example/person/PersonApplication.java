package com.example.person;

import com.example.person.model.Person;
import com.example.person.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PersonApplication {
	public static void main(String[] args) {
		SpringApplication.run(PersonApplication.class, args);
	}

	@Bean
	CommandLineRunner initPersons(PersonRepository repository) {
		return args -> {

			// очищаем, чтобы при каждом старте не дублировались данные
			repository.deleteAll();

			repository.save(new Person(
					"Ingeborga",
					"Riga"
			));

			repository.save(new Person(
					"Natalia",
					"Voronezh"
			));
		};
	}
}
