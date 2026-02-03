package com.example.person.controller;

import com.example.person.model.Person;
import com.example.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    @Autowired
    private PersonRepository repository;
    private final RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<Person>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Optional<Person> person = repository.findById(id);
        return person.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name")
    public ResponseEntity<Person> getPersonByName(@RequestParam String name) {
        Optional<Person> person = repository.findByName(name);
        return person.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        try {
            if (repository.existsByName(person.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Person savedPerson = repository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(
            @PathVariable Integer id,
            @RequestBody Person updatedPerson) {

        Optional<Person> existingPersonOpt = repository.findById(id);
        if (existingPersonOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Person existingPerson = existingPersonOpt.get();
        existingPerson.setName(updatedPerson.getName());
        existingPerson.setLocation(updatedPerson.getLocation());

        Person savedPerson = repository.save(existingPerson);
        return ResponseEntity.ok(savedPerson);
    }

    @PutMapping("/name")
    public ResponseEntity<Person> updatePersonByName(
            @RequestParam String name,
            @RequestBody Person updatedPerson) {

        Optional<Person> existingPersonOpt = repository.findByName(name);
        if (existingPersonOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Person existingPerson = existingPersonOpt.get();
        existingPerson.setName(updatedPerson.getName());
        existingPerson.setLocation(updatedPerson.getLocation());

        Person savedPerson = repository.save(existingPerson);
        return ResponseEntity.ok(savedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonById(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name")
    public ResponseEntity<Void> deletePersonByName(@RequestParam String name) {
        if (!repository.existsByName(name)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteByName(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<?> getWeatherForPerson(@PathVariable Integer id) {
        Optional<Person> personOpt = repository.findById(id);
        if (personOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Person person = personOpt.get();
        String locationName = person.getLocation();

        try {
            String url = String.format("http://localhost:8083/location/weather?name=%s", locationName);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Weather service unavailable for location: " + locationName);
        }
    }

    @GetMapping("/name/weather")
    public ResponseEntity<?> getWeatherForPersonByName(@RequestParam String name) {
        Optional<Person> personOpt = repository.findByName(name);
        if (personOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Person person = personOpt.get();
        String locationName = person.getLocation();

        try {
            String url = String.format("http://localhost:8083/location/weather?name=%s", locationName);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Weather service unavailable for location: " + locationName);
        }
    }
}