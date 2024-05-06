package com.example.personcolormanager.service;

import com.example.personcolormanager.model.Person;
import com.example.personcolormanager.repository.PersonRepository;
import com.example.personcolormanager.util.ColorMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "database.enabled", havingValue = "true", matchIfMissing = true)
public class DbPersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final ColorMappingUtil colorMappingUtil;

    // Constructor injection of dependencies
    @Autowired
    public DbPersonServiceImpl(PersonRepository personRepository, ColorMappingUtil colorMappingUtil) {
        this.personRepository = personRepository;
        this.colorMappingUtil = colorMappingUtil;
    }

    // Retrieve all persons from the database
    @Override
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    // Retrieve a person by their ID from the database
    @Override
    public Person getPersonById(long id) {
        return personRepository.findById(id).orElse(null);
    }

    // Retrieve persons by their color from the database
    @Override
    public List<Person> getPersonByColor(String color) {
        return personRepository.findByColor(color);
    }

    // Add a new person to the database
    @Override
    public Person addPerson(Person person) {
        // Get colorName based on colorId
        int colorId = person.getColorId();
        String colorName = colorMappingUtil.getColorNameByColorId(colorId);
        // Set the obtained colorName to the person
        person.setColor(colorName);

        // Save person to repository
        Person savedPerson = personRepository.save(person);
        return savedPerson;
    }
}
