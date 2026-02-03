package com.example.location.controller;

import com.example.location.model.Location;
import com.example.location.model.Weather;
import com.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public LocationController(LocationRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;

        if (repository.count() == 0) {
            repository.save(new Location(45.0182, 53.1959, "Penza"));
            repository.save(new Location(45.1839, 54.1874, "Saransk"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok((List<Location>) repository.findAll());
    }

    @GetMapping(params = "name")
    public ResponseEntity<Location> getLocationByName(@RequestParam String name) {
        Optional<Location> location = repository.findByName(name);
        return location.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> saveLocation(@RequestBody Location location) {
        try {
            Location savedLocation = repository.save(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Location> updateLocation(
            @RequestParam String name,
            @RequestBody Location updatedLocation) {

        Optional<Location> existingLocationOpt = repository.findByName(name);
        if (existingLocationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Location existingLocation = existingLocationOpt.get();
        existingLocation.setLatitude(updatedLocation.getLatitude());
        existingLocation.setLongitude(updatedLocation.getLongitude());
        existingLocation.setName(updatedLocation.getName());

        Location savedLocation = repository.save(existingLocation);
        return ResponseEntity.ok(savedLocation);
    }

    @DeleteMapping
    public void deleteLocation(@RequestParam String name) {
        repository.findByName(name).ifPresent(repository::delete);
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> getWeather(@RequestParam String name) {
        Optional<Location> locationOpt = repository.findByName(name);
        if (locationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Location location = locationOpt.get();
        String url = String.format("http://localhost:8082/?lat=%s&lon=%s",
                location.getLatitude(), location.getLongitude());

        try {
            Weather weather = restTemplate.getForObject(url, Weather.class);
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}