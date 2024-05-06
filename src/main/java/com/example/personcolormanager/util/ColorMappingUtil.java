package com.example.personcolormanager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ColorMappingUtil {
    private final Resource colorCsvResource;

    // CSV file resource for color mappings
    private Map<Integer, String> colorMapping = new HashMap<>();
    private Map<String, Integer> reverseColorMapping = new HashMap<>();

    // Constructor injection of color CSV resource
    public ColorMappingUtil(@Value("classpath:colors.csv") Resource colorCsvResource) {
        this.colorCsvResource = colorCsvResource;
    }

    // Method called after bean initialization to load color mappings
    @PostConstruct
    public void init() {
        if (colorCsvResource == null) {
            // Log an error and throw an exception if the CSV resource is empty or not found
            log.error("The CSV resource for colors is empty. Please check if the file exists.");
            throw new IllegalArgumentException("The CSV resource for colors is empty.");
        }
        loadColorMapping();
    }

    // Load color mappings from the CSV file
    private void loadColorMapping() {
        try (InputStream inputStream = colorCsvResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    int colorId = Integer.parseInt(data[0].trim());
                    String colorName = data[1].trim();
                    colorMapping.put(colorId, colorName);
                    reverseColorMapping.put(colorName.toLowerCase(), colorId); // To handle case-insensitive color name search
                }
            }
        } catch (IOException e) {
            // Log an error and throw a runtime exception if there's an error reading the CSV file
            log.error("Error reading the CSV file for colors: {}", e.getMessage());
            throw new RuntimeException("Error reading the CSV file for colors.", e);
        }
    }

    // Get the color mapping
    public Map<Integer, String> getColorMapping() {
        return colorMapping;
    }

    // Get color name by color ID
    public String getColorNameByColorId(int colorId) {
        return colorMapping.getOrDefault(colorId, "");
    }

    // Get color ID by color name (case-insensitive)
    public Integer getColorIdByColorName(String colorName) {
        return reverseColorMapping.getOrDefault(colorName.toLowerCase(), null);
    }
}
