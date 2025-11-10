package com.suicide.questionbank;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resource Manager for loading and querying crisis resources.
 */
public class ResourceManager {
    private Path jsonPath;
    private List<Resource> resources;
    private Map<String, List<Resource>> categoryIndex;
    private Map<String, List<Resource>> countyIndex;
    
    /**
     * Initialize the resource manager.
     * 
     * @param jsonPath Path to the JSON file containing resources
     */
    public ResourceManager(String jsonPath) throws IOException {
        this.jsonPath = Paths.get(jsonPath);
        this.resources = loadResources();
        buildIndices();
    }
    
    /**
     * Load resources from JSON file.
     * Tries file path first, then classpath resource.
     */
    private List<Resource> loadResources() throws IOException {
        Gson gson = new Gson();
        Reader reader = null;
        
        // Try file path first
        if (Files.exists(jsonPath)) {
            reader = Files.newBufferedReader(jsonPath);
        } else {
            // Try classpath resource
            String resourcePath = jsonPath.getFileName().toString();
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Resource file not found: " + jsonPath);
            }
            reader = new InputStreamReader(is);
        }
        
        try {
            TypeToken<List<Resource>> typeToken = new TypeToken<List<Resource>>() {};
            return gson.fromJson(reader, typeToken.getType());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /**
     * Build indices for faster searching.
     */
    private void buildIndices() {
        categoryIndex = new HashMap<>();
        countyIndex = new HashMap<>();
        
        for (Resource resource : resources) {
            // Index by category
            if (resource.getCategories() != null) {
                for (String category : resource.getCategories()) {
                    categoryIndex.computeIfAbsent(category.toLowerCase(), k -> new ArrayList<>()).add(resource);
                }
            }
            
            // Index by county
            if (resource.getServiceArea() != null && resource.getServiceArea().getCoverageByCounty() != null) {
                for (String county : resource.getServiceArea().getCoverageByCounty()) {
                    countyIndex.computeIfAbsent(county.toLowerCase(), k -> new ArrayList<>()).add(resource);
                }
            }
        }
    }
    
    /**
     * Get all resources.
     */
    public List<Resource> getAllResources() {
        return new ArrayList<>(resources);
    }
    
    /**
     * Get resources by category.
     */
    public List<Resource> getResourcesByCategory(String category) {
        return categoryIndex.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }
    
    /**
     * Get resources by county.
     */
    public List<Resource> getResourcesByCounty(String county) {
        return countyIndex.getOrDefault(county.toLowerCase(), new ArrayList<>());
    }
    
    /**
     * Search resources by name (case-insensitive partial match).
     */
    public List<Resource> searchByName(String query) {
        String lowerQuery = query.toLowerCase();
        return resources.stream()
            .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Search resources by description (case-insensitive partial match).
     */
    public List<Resource> searchByDescription(String query) {
        String lowerQuery = query.toLowerCase();
        return resources.stream()
            .filter(r -> r.getDescription() != null && r.getDescription().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter resources by multiple criteria.
     */
    public List<Resource> filterResources(String category, String county, String searchTerm) {
        return resources.stream()
            .filter(r -> {
                if (category != null && !category.isEmpty()) {
                    // Case-insensitive partial match on categories
                    String lowerCategory = category.toLowerCase();
                    if (r.getCategories() == null || 
                        !r.getCategories().stream()
                            .anyMatch(cat -> cat != null && cat.toLowerCase().contains(lowerCategory))) {
                        return false;
                    }
                }
                if (county != null && !county.isEmpty()) {
                    if (r.getServiceArea() == null || r.getServiceArea().getCoverageByCounty() == null ||
                        !r.getServiceArea().getCoverageByCounty().stream()
                            .anyMatch(c -> c.toLowerCase().contains(county.toLowerCase()))) {
                        return false;
                    }
                }
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    String lowerTerm = searchTerm.toLowerCase();
                    boolean matches = (r.getName() != null && r.getName().toLowerCase().contains(lowerTerm)) ||
                                     (r.getDescription() != null && r.getDescription().toLowerCase().contains(lowerTerm));
                    if (!matches) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get total number of resources.
     */
    public int getTotalResources() {
        return resources.size();
    }
    
    /**
     * Get all unique categories.
     */
    public Set<String> getAllCategories() {
        return resources.stream()
            .filter(r -> r.getCategories() != null)
            .flatMap(r -> r.getCategories().stream())
            .collect(Collectors.toSet());
    }
    
    /**
     * Get all unique counties.
     */
    public Set<String> getAllCounties() {
        return resources.stream()
            .filter(r -> r.getServiceArea() != null && r.getServiceArea().getCoverageByCounty() != null)
            .flatMap(r -> r.getServiceArea().getCoverageByCounty().stream())
            .collect(Collectors.toSet());
    }
}

