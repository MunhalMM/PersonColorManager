package com.example.personcolormanager.service;

import com.example.personcolormanager.model.Person;

import java.util.List;

public interface PersonService {
    List<Person> getPersons();

    Person getPersonById(long id);

    List<Person> getPersonByColor(String color);

    Person addPerson(Person person);
}
