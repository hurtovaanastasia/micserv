package com.example.person.repository;

import com.example.person.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    Optional<Person> findByName(String name);
    List<Person> findAll();
    boolean existsByName(String name);
    void deleteByName(String name);
}