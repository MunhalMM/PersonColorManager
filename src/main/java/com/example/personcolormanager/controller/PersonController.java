package com.example.personcolormanager.controller;

import com.example.personcolormanager.model.Person;
import com.example.personcolormanager.service.PersonService;
import com.example.personcolormanager.util.ColorMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;
    private final ColorMappingUtil colorMappingUtil;

    @Autowired
    public PersonController(PersonService personService, ColorMappingUtil colorMappingUtil) {
        this.personService = personService;
        this.colorMappingUtil = colorMappingUtil;
    }

    // Get all persons
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personService.getPersons();
        return ResponseEntity.ok(persons);
    }

    // Get person by ID
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonId(@PathVariable int id) {
        Person person = personService.getPersonById(id);
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.notFound().build(); // Handling case when person is not found
        }
    }

    // Get persons by color
    @GetMapping("/color/{color}")
    public ResponseEntity<List<Person>> getPersonsByColor(@PathVariable String color) {
        List<Person> persons = personService.getPersonByColor(color);
        if (!persons.isEmpty()) {
            return ResponseEntity.ok(persons);
        } else {
            return ResponseEntity.notFound().build(); // Handling case when no persons found with the given color
        }
    }

    // Add a new person
    @PostMapping
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        String colorName = person.getColor();
        if (!colorName.isEmpty()) {
            Integer colorId = colorMappingUtil.getColorIdByColorName(colorName);
            if (colorId != null) {
                person.setColorId(colorId);
            } else {
                return ResponseEntity.badRequest().build(); // Handling case when colorName is not found
            }
        }
        Person savedPerson = personService.addPerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }
}
