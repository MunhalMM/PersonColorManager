package com.example.personcolormanager.controller;

import com.example.personcolormanager.model.Person;
import com.example.personcolormanager.service.PersonService;
import com.example.personcolormanager.util.ColorMappingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllPersons() {
        List<Person> mockPersons = new ArrayList<>();

        mockPersons.add(new Person(1, "Adam", "rot"));
        mockPersons.add(new Person(2, "Billi", "blau"));

        when(personService.getPersons()).thenReturn(mockPersons);

        ResponseEntity<List<Person>> response = personController.getAllPersons();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPersons, response.getBody());

    }

    @Test
    void getPersonId() {
        Person mockPerson = new Person(1, "Anna", "schwarz");
        when(personService.getPersonById(1)).thenReturn(mockPerson);
        when(personService.getPersonById(2)).thenReturn(null);

        ResponseEntity<Person> responseFoundPerson = personController.getPersonId(1);
        ResponseEntity<Person> responseNotFoundPerson = personController.getPersonId(2);

        assertEquals(HttpStatus.OK, responseFoundPerson.getStatusCode());
        assertEquals(mockPerson, responseFoundPerson.getBody());

        assertEquals(HttpStatus.NOT_FOUND, responseNotFoundPerson.getStatusCode());

    }

    @Test
    void getPersonsByColor() {
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(new Person(1, "Mark", "grau"));
        mockPersons.add(new Person(1, "Mark", "grau"));

        when(personService.getPersonByColor("grau")).thenReturn(mockPersons);
        when(personService.getPersonByColor("gelb")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Person>> responseFoundPerson = personController.getPersonsByColor("grau");
        ResponseEntity<List<Person>> responseNotFoundPerson = personController.getPersonsByColor("gelb");

        assertEquals(HttpStatus.OK, responseFoundPerson.getStatusCode());
        assertEquals(mockPersons, responseFoundPerson.getBody());

        assertEquals(HttpStatus.NOT_FOUND, responseNotFoundPerson.getStatusCode());
    }

    @Test
    void addPerson_ValidColorName_ReturnsCreated() {
        Person personToAdd = new Person();
        personToAdd.setName("John");
        personToAdd.setColor("grau");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setName("John");
        savedPerson.setColor("grau");

        ColorMappingUtil colorMappingUtil = mock(ColorMappingUtil.class);
        when(colorMappingUtil.getColorIdByColorName("grau")).thenReturn(1); // Mocking the behavior for getColorIdByColorName

        PersonService personService = mock(PersonService.class);
        when(personService.addPerson(personToAdd)).thenReturn(savedPerson); // Mocking the behavior for addPerson

        PersonController personController = new PersonController(personService, colorMappingUtil);

        ResponseEntity<Person> response = personController.addPerson(personToAdd);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedPerson, response.getBody());
        verify(personService, times(1)).addPerson(personToAdd); // Verifying that addPerson was called exactly once
    }

    @Test
    void addPerson_InvalidColorName_ReturnsBadRequest() {
        Person personToAdd = new Person();
        personToAdd.setName("John");
        personToAdd.setColor("invalid_color");

        ColorMappingUtil colorMappingUtil = mock(ColorMappingUtil.class);
        when(colorMappingUtil.getColorIdByColorName("invalid_color")).thenReturn(null); // Mocking the behavior for getColorIdByColorName

        PersonController personController = new PersonController(mock(PersonService.class), colorMappingUtil);

        ResponseEntity<Person> response = personController.addPerson(personToAdd);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(colorMappingUtil, times(1)).getColorIdByColorName("invalid_color"); // Verifying that getColorIdByColorName was called exactly once with the specified argument
        verifyNoInteractions(personService); // Verifying that no interaction with personService occurred
    }
}