package com.example.personcolormanager.service;

import com.example.personcolormanager.model.Person;
import com.example.personcolormanager.util.ColorMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
@ConditionalOnProperty(value = "database.enabled", havingValue = "false")
public class CsvPersonServiceImpl implements PersonService {
    private final Resource csvResource;
    private final ColorMappingUtil colorMappingUtil;
    private List<Person> persons;

    @Autowired
    public CsvPersonServiceImpl(@Value("classpath:sample-input.csv") Resource csvResource, ColorMappingUtil colorMappingUtil) {
        this.csvResource = csvResource;
        this.colorMappingUtil = colorMappingUtil;
        this.persons = loadPersonsFromCsv(); // Load persons from CSV file on initialization
    }

    // Load persons from CSV file
    private List<Person> loadPersonsFromCsv() {
        List<Person> loadedPersons = new ArrayList<>();
        Map<Integer, String> colorMappings = colorMappingUtil.getColorMapping(); // Initialize color mappings
        try (InputStream inputStream = csvResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int id = 1;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    // Extract zip code and city from CSV data
                    String[] zipCity = data[2].trim().split(" ");
                    String zipCode = zipCity[0];
                    String city = "";
                    if (zipCity.length > 1) {
                        city = String.join(" ", Arrays.copyOfRange(zipCity, 1, zipCity.length));
                    }
                    String colorIdStr = data[3].trim();
                    int colorId;
                    if (colorIdStr.contains(" ")) {
                        colorIdStr = colorIdStr.substring(0, colorIdStr.indexOf(" "));
                    }
                    try {
                        // Parse the color id string to integer
                        colorId = Integer.parseInt(colorIdStr);
                    } catch (NumberFormatException e) {
                        // Handle the case when color id is not a valid integer
                        System.err.println("Invalid color id format: " + colorIdStr);
                        // You can choose to skip this line or handle it differently based on your requirement
                        continue; // Skip this line and continue with the next line
                    }
                    String colorName = colorMappings.getOrDefault(colorId, "");
                    // Create Person object and add to the list
                    loadedPersons.add(new Person(id++, data[1].trim(), data[0].trim(), zipCode, city, colorName, colorId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPersons;
    }

    @Override
    public List<Person> getPersons() {
        return persons;
    }

    @Override
    public Person getPersonById(long id) {
        return persons.stream()
                .filter(person -> person.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Person> getPersonByColor(String color) {
        if (color == null) {
            return Collections.emptyList();
        }

        return persons.stream()
                .filter(person -> {
                    String personColor = person.getColor();
                    return personColor != null && personColor.equalsIgnoreCase(color);
                })
                .toList();

    }

    @Override
    public Person addPerson(Person person) {
        try (FileWriter writer = new FileWriter(csvResource.getFile(), true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            // Determining the next available identifier
            long nextId = persons.stream()
                    .mapToLong(Person::getId)
                    .max()
                    .orElse(0) + 1;

            // Assigning the identifier to the new person
            person.setId(nextId);

            // If colorId is not provided, try to determine it from colorName
            if (person.getColorId() == 0 && !person.getColor().isEmpty()) {
                Integer colorId = colorMappingUtil.getColorIdByColorName(person.getColor());
                if (colorId != null) {
                    person.setColorId(colorId);
                } else {
                    // Handle the case when colorName is not found
                    return null;
                }
            }

            // Adding the new person's data to the CSV file
            bufferedWriter.write(person.getLastName() + "," +
                    person.getName() + "," +
                    person.getZipCode() + "" + person.getCity() + "," +
                    person.getColorId() + "," +
                    person.getId());
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Adding the new person to the list of persons
            persons.add(person);
            return person;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}