package com.suicide.questionbank;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Chatbot function for Resource Retrieval Assistants at the Nashville Suicide Prevention Center.
 * Given a transcript and an array of resource objects, extracts needs, filters, ranks and justifies top 3 resources.
 * Outputs a JSON object structured as:
 * {
 *   "top_resources": [
 *      {
 *         "resource": <resource_object>,
 *         "justification": <string>
 *      },
 *      ...
 *   ]
 * }
 */
public class SPCchatbotDemo {
    
    /**
     * Represents a resource object.
     */
    public static class Resource {
        private String name;
        private String title;
        private String description;
        private String category;
        private String serviceArea;
        private String eligibility;
        private String cost;
        private String hours;
        private String language;
        private String status;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getServiceArea() { return serviceArea; }
        public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
        
        public String getEligibility() { return eligibility; }
        public void setEligibility(String eligibility) { this.eligibility = eligibility; }
        
        public String getCost() { return cost; }
        public void setCost(String cost) { this.cost = cost; }
        
        public String getHours() { return hours; }
        public void setHours(String hours) { this.hours = hours; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    /**
     * Represents extracted needs and context from a transcript.
     */
    public static class ExtractedContext {
        private List<String> needs;
        private Map<String, Object> context;
        
        public ExtractedContext() {
            this.needs = new ArrayList<>();
            this.context = new HashMap<>();
        }
        
        public List<String> getNeeds() { return needs; }
        public void setNeeds(List<String> needs) { this.needs = needs; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
    
    /**
     * Represents a top resource with justification.
     */
    public static class TopResource {
        private Resource resource;
        private String justification;
        
        public TopResource(Resource resource, String justification) {
            this.resource = resource;
            this.justification = justification;
        }
        
        public Resource getResource() { return resource; }
        public void setResource(Resource resource) { this.resource = resource; }
        
        public String getJustification() { return justification; }
        public void setJustification(String justification) { this.justification = justification; }
    }
    
    /**
     * Result containing top resources.
     */
    public static class Result {
        private List<TopResource> topResources;
        
        public Result(List<TopResource> topResources) {
            this.topResources = topResources;
        }
        
        public List<TopResource> getTopResources() { return topResources; }
        public void setTopResources(List<TopResource> topResources) { this.topResources = topResources; }
    }
    
    /**
     * Extract needs and context from transcript.
     * Naive extraction using keyword matching. In production, use an LLM or NLP model.
     */
    public static ExtractedContext extractNeedsAndContext(String transcript) {
        ExtractedContext extracted = new ExtractedContext();
        String lc = transcript.toLowerCase();
        
        // Identify crisis type keywords
        Map<String, List<String>> crisisTypes = new HashMap<>();
        crisisTypes.put("suicidal ideation", Arrays.asList("suicidal", "want to end my life", "kill myself", "suicide", "can't go on"));
        crisisTypes.put("domestic violence", Arrays.asList("abuse", "hit me", "violent home", "partner hurt", "domestic", "beaten"));
        crisisTypes.put("homelessness", Arrays.asList("homeless", "no place to stay", "nowhere to go", "live on the street"));
        crisisTypes.put("substance use", Arrays.asList("drugs", "addiction", "alcohol", "overdose", "substance", "drinking problem"));
        crisisTypes.put("grief", Arrays.asList("loss", "passed away", "grief", "mourning", "lost someone"));
        crisisTypes.put("imminent risk", Arrays.asList("immediate danger", "in danger", "hurt myself", "going to do it now", "can't keep myself safe", "overdose", "gun", "knife"));
        
        for (Map.Entry<String, List<String>> entry : crisisTypes.entrySet()) {
            String type = entry.getKey();
            for (String keyword : entry.getValue()) {
                if (lc.contains(keyword)) {
                    extracted.getNeeds().add(type);
                    if (type.equals("imminent risk")) {
                        extracted.getContext().put("imminentRisk", true);
                    }
                    break;
                }
            }
        }
        
        // Demographics: Very basic heuristics (production: NLP)
        java.util.regex.Pattern agePattern = java.util.regex.Pattern.compile("(\\d{1,2})\\s*(years old|yo)");
        java.util.regex.Matcher ageMatcher = agePattern.matcher(lc);
        if (ageMatcher.find()) {
            extracted.getContext().put("age", Integer.parseInt(ageMatcher.group(1)));
        } else if (lc.contains("teen")) {
            extracted.getContext().put("ageGroup", "teen");
        }
        
        if (lc.contains("woman") || lc.contains("female") || lc.contains("girl")) {
            extracted.getContext().put("gender", "female");
        } else if (lc.contains("man") || lc.contains("male") || lc.contains("boy")) {
            extracted.getContext().put("gender", "male");
        }
        
        if (lc.contains("veteran")) extracted.getContext().put("demographic", "veteran");
        if (lc.contains("lgbt") || lc.contains("gay") || lc.contains("lesbian") || lc.contains("trans")) {
            extracted.getContext().put("demographic", "lgbtq+");
        }
        
        // Family status
        if (lc.contains("single mother") || lc.contains("single mom")) {
            extracted.getContext().put("family", "single mother");
        }
        if (lc.contains("child") || lc.contains("children") || lc.contains("son") || lc.contains("daughter")) {
            extracted.getContext().put("hasChildren", true);
        }
        
        // Logistical constraints
        if (lc.contains("no car") || lc.contains("can't drive") || lc.contains("bus only") || lc.contains("no transportation")) {
            extracted.getContext().put("transportation", "limited");
        }
        if (lc.contains("no money") || lc.contains("can't afford") || lc.contains("broke") || lc.contains("no insurance") || lc.contains("uninsured")) {
            extracted.getContext().put("costSensitive", true);
        }
        
        if (lc.matches(".*\\b(davidson|nashville)\\b.*")) {
            extracted.getContext().put("location", "Davidson");
        }
        if (lc.matches(".*\\b(williamson|sumner|rutherford|robertson)\\b.*")) {
            extracted.getContext().put("location", "Middle TN Outside Davidson");
        }
        if (lc.contains("spanish")) extracted.getContext().put("language", "spanish");
        
        if (lc.contains("urgent") || lc.contains("now") || lc.contains("immediately") || 
            extracted.getContext().getOrDefault("imminentRisk", false).equals(true)) {
            extracted.getContext().put("urgency", "immediate");
        }
        
        return extracted;
    }
    
    /**
     * Filter resources based on context.
     */
    public static List<Resource> filterResources(List<Resource> resources, Map<String, Object> context) {
        return resources.stream()
            .filter(resource -> {
                // 1. By location/relevance
                boolean locationOk = false;
                Object location = context.get("location");
                if ("Davidson".equals(location)) {
                    if (resource.getServiceArea() == null) {
                        locationOk = true;
                    } else {
                        String sa = resource.getServiceArea().toLowerCase();
                        locationOk = sa.contains("nashville") || sa.contains("davidson") || 
                                    sa.contains("greater nashville") || sa.contains("statewide");
                    }
                } else if ("Middle TN Outside Davidson".equals(location)) {
                    if (resource.getServiceArea() == null) {
                        locationOk = true;
                    } else {
                        String sa = resource.getServiceArea().toLowerCase();
                        locationOk = sa.contains("sumner") || sa.contains("rutherford") || 
                                    sa.contains("williamson") || sa.contains("robertson") || 
                                    sa.contains("middle tennessee") || sa.contains("statewide");
                    }
                } else {
                    locationOk = true;
                }
                
                // 2. By eligibility
                boolean eligible = true;
                if (resource.getEligibility() != null) {
                    String elig = resource.getEligibility().toLowerCase();
                    Object age = context.get("age");
                    Object ageGroup = context.get("ageGroup");
                    Object gender = context.get("gender");
                    
                    if (elig.contains("adults only") && age != null && ((Integer) age) < 18) {
                        eligible = false;
                    }
                    if (elig.contains("teens only") && !"teen".equals(ageGroup) && 
                        (age == null || ((Integer) age) < 12 || ((Integer) age) > 19)) {
                        eligible = false;
                    }
                    if (elig.contains("women only") && "male".equals(gender)) {
                        eligible = false;
                    }
                    if (elig.contains("men only") && "female".equals(gender)) {
                        eligible = false;
                    }
                }
                
                // 3. Remove training, non-service agencies, outdated
                if (resource.getCategory() != null && resource.getCategory().toLowerCase().contains("training")) {
                    return false;
                }
                if ("outdated".equalsIgnoreCase(resource.getStatus())) {
                    return false;
                }
                if (resource.getDescription() != null) {
                    String desc = resource.getDescription().toLowerCase();
                    if (desc.contains("training program") || desc.contains("not for public use")) {
                        return false;
                    }
                }
                
                return locationOk && eligible;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate match score for a resource.
     */
    public static int matchScore(Resource resource, ExtractedContext extracted) {
        int score = 0;
        
        // 1. Crisis type relevance (primary)
        if (!extracted.getNeeds().isEmpty()) {
            int rel = 0;
            String rcat = ((resource.getCategory() != null ? resource.getCategory() : "") + 
                          (resource.getDescription() != null ? resource.getDescription() : "")).toLowerCase();
            
            for (String need : extracted.getNeeds()) {
                String[] words = need.split(" ");
                if (rcat.contains(words[0])) rel += 2;
                if (rcat.contains(need.replace(" ", ""))) rel += 3;
                if (rcat.contains(need)) rel += 4;
            }
            score += rel;
        }
        
        // 2. Proximity/accessibility
        Object location = extracted.getContext().get("location");
        if (resource.getServiceArea() != null && location != null) {
            if (resource.getServiceArea().toLowerCase().contains(location.toString().toLowerCase())) {
                score += 2;
            }
        } else if (resource.getServiceArea() == null) {
            score += 1;
        }
        
        // 3. Affordability
        if (resource.getCost() != null) {
            String cost = resource.getCost().toLowerCase();
            if (cost.contains("free") || cost.contains("no cost")) {
                score += 2;
            } else if (extracted.getContext().getOrDefault("costSensitive", false).equals(true)) {
                score -= 1;
            }
        }
        
        // 4. Language/demographics
        Object language = extracted.getContext().get("language");
        if (resource.getLanguage() != null && language != null) {
            if (resource.getLanguage().toLowerCase().contains(language.toString().toLowerCase())) {
                score += 1;
            }
        }
        if (resource.getEligibility() != null) {
            String elig = resource.getEligibility().toLowerCase();
            if ("teen".equals(extracted.getContext().get("ageGroup")) && elig.contains("teen")) {
                score += 1;
            }
            Object gender = extracted.getContext().get("gender");
            if (gender != null && elig.contains(gender.toString())) {
                score += 1;
            }
            Object demographic = extracted.getContext().get("demographic");
            if (demographic != null && elig.contains(demographic.toString())) {
                score += 1;
            }
        }
        
        // 5. Urgency
        if ("immediate".equals(extracted.getContext().get("urgency")) && 
            resource.getHours() != null && resource.getHours().contains("24")) {
            score += 1;
        }
        
        // Penalize generic referral/wrong-fit
        if (resource.getCategory() != null && resource.getCategory().toLowerCase().contains("training")) {
            score -= 3;
        }
        
        return score;
    }
    
    /**
     * Get top resources for a transcript.
     */
    public static Result getTopResources(String transcript, List<Resource> resources) {
        ExtractedContext extracted = extractNeedsAndContext(transcript);
        List<Resource> filtered = filterResources(resources, extracted.getContext());
        
        List<TopResource> topResources = new ArrayList<>();
        
        // Safety rule: Imminent risk? Then hardcode include 911 and 988 at start
        if (extracted.getContext().getOrDefault("imminentRisk", false).equals(true)) {
            Resource em911 = filtered.stream()
                .filter(r -> (r.getName() != null && r.getName().contains("911")) || 
                            (r.getTitle() != null && r.getTitle().contains("911")))
                .findFirst()
                .orElse(createEmergencyResource("911 Emergency Services", 
                    "Call 911 for immediate emergency assistance."));
            
            Resource em988 = filtered.stream()
                .filter(r -> (r.getName() != null && r.getName().contains("988")) || 
                            (r.getTitle() != null && r.getTitle().contains("988")))
                .findFirst()
                .orElse(createEmergencyResource("988 Suicide & Crisis Lifeline", 
                    "Contact 988 for immediate crisis and suicide prevention assistance."));
            
            topResources.add(new TopResource(em911, 
                "Transcript indicates imminent risk; 911 Emergency should be called for immediate assistance."));
            topResources.add(new TopResource(em988, 
                "For immediate suicide and crisis prevention support, the 988 Lifeline should be offered in all cases of potential imminent danger."));
            
            // Remove from filtered to avoid duplication
            filtered = filtered.stream()
                .filter(r -> (r.getName() == null || (!r.getName().contains("911") && !r.getName().contains("988"))) &&
                            (r.getTitle() == null || (!r.getTitle().contains("911") && !r.getTitle().contains("988"))))
                .collect(Collectors.toList());
        }
        
        // Score and rank other resources
        List<Map.Entry<Resource, Integer>> scored = filtered.stream()
            .map(resource -> new AbstractMap.SimpleEntry<>(resource, matchScore(resource, extracted)))
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .collect(Collectors.toList());
        
        // Compose top 3 (in addition to 911/988 if needed)
        int count = 0;
        for (Map.Entry<Resource, Integer> entry : scored) {
            if (count++ >= 3) break;
            Resource r = entry.getKey();
            
            List<String> justification = new ArrayList<>();
            if (!extracted.getNeeds().isEmpty()) {
                String firstNeed = extracted.getNeeds().get(0);
                String rcat = ((r.getCategory() != null ? r.getCategory() : "") + 
                              (r.getDescription() != null ? r.getDescription() : "")).toLowerCase();
                if (rcat.contains(firstNeed.split(" ")[0])) {
                    justification.add("Directly addresses the caller's primary concern.");
                }
            }
            Object location = extracted.getContext().get("location");
            if (r.getServiceArea() != null && location != null && 
                r.getServiceArea().toLowerCase().contains(location.toString().toLowerCase())) {
                justification.add("Located within the caller's geographic area.");
            }
            if (r.getCost() != null && r.getCost().toLowerCase().contains("free")) {
                justification.add("No cost/affordable, reducing barriers to access.");
            }
            Object language = extracted.getContext().get("language");
            if (r.getLanguage() != null && language != null && 
                r.getLanguage().toLowerCase().contains(language.toString().toLowerCase())) {
                justification.add("Service available in " + language + ".");
            }
            if (justification.isEmpty()) {
                justification.add("Matches several needs identified in the caller's context.");
            }
            
            topResources.add(new TopResource(r, String.join(" ", justification)));
        }
        
        return new Result(topResources);
    }
    
    private static Resource createEmergencyResource(String name, String description) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setDescription(description);
        return resource;
    }
}

