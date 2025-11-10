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
 * Question Bank Manager for Suicide Risk Assessment
 * Provides utilities to load, filter, and retrieve questions from the question bank.
 */
public class QuestionBankManager {
    private Path jsonPath;
    private Map<String, List<Question>> questionBank;
    private Map<Integer, String> idToCategory;
    private Map<Integer, Question> idToQuestion;
    
    /**
     * Initialize the question bank manager.
     * 
     * @param jsonPath Path to the JSON file containing the question bank
     */
    public QuestionBankManager(String jsonPath) throws IOException {
        this.jsonPath = Paths.get(jsonPath);
        this.questionBank = loadQuestionBank();
        buildIndex();
    }
    
    /**
     * Initialize with default path.
     */
    public QuestionBankManager() throws IOException {
        this("suicide_question_bank.json");
    }
    
    /**
     * Load the question bank from JSON file.
     * Tries file path first, then classpath resource.
     */
    private Map<String, List<Question>> loadQuestionBank() throws IOException {
        Gson gson = new Gson();
        Reader reader = null;
        
        // Try file path first
        if (Files.exists(jsonPath)) {
            reader = Files.newBufferedReader(jsonPath);
        } else {
            // Try classpath resource
            String resourcePath = jsonPath.getFileName().toString();
            java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream != null) {
                reader = new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8);
            } else {
                throw new IOException("Question bank file not found: " + jsonPath + 
                    " (also checked classpath for: " + resourcePath + ")");
            }
        }
        
        try (Reader r = reader) {
            TypeToken<Map<String, List<Question>>> typeToken = new TypeToken<Map<String, List<Question>>>() {};
            return gson.fromJson(r, typeToken.getType());
        }
    }
    
    /**
     * Build an index mapping question IDs to their categories.
     */
    private void buildIndex() {
        this.idToCategory = new HashMap<>();
        this.idToQuestion = new HashMap<>();
        
        for (Map.Entry<String, List<Question>> entry : questionBank.entrySet()) {
            String category = entry.getKey();
            List<Question> questions = entry.getValue();
            
            for (Question question : questions) {
                int qid = question.getId();
                idToCategory.put(qid, category);
                idToQuestion.put(qid, question);
            }
        }
    }
    
    /**
     * Get all available question categories.
     */
    public List<String> getCategories() {
        return new ArrayList<>(questionBank.keySet());
    }
    
    /**
     * Get all questions for a specific category.
     * 
     * @param category The category name (e.g., 'attempt_in_progress')
     * @return List of questions
     */
    public List<Question> getQuestionsByCategory(String category) {
        return questionBank.getOrDefault(category, new ArrayList<>());
    }
    
    /**
     * Get a specific question by its ID.
     * 
     * @param questionId The question ID
     * @return Question or null if not found
     */
    public Question getQuestionById(int questionId) {
        return idToQuestion.get(questionId);
    }
    
    /**
     * Filter questions based on multiple criteria.
     * 
     * @param category Filter by category name (null for all)
     * @param escalationTier Filter by escalation tier (null for all)
     * @param riskLevel Filter by risk level (null for all)
     * @param tone Filter by tone (null for all)
     * @param useAfterRapport Filter by whether question should be used after rapport (null for all)
     * @param requireRapport If true, only return questions where useAfterRapport is true
     * @return List of filtered questions
     */
    public List<Question> filterQuestions(
            String category,
            Integer escalationTier,
            String riskLevel,
            String tone,
            Boolean useAfterRapport,
            boolean requireRapport) {
        
        List<Question> questions = new ArrayList<>();
        
        // Get questions from specified category or all categories
        List<String> categories = (category != null) 
            ? Collections.singletonList(category) 
            : getCategories();
        
        for (String cat : categories) {
            if (!questionBank.containsKey(cat)) {
                continue;
            }
            
            for (Question question : questionBank.get(cat)) {
                // Apply filters
                if (escalationTier != null && question.getEscalationTier() != escalationTier) {
                    continue;
                }
                if (riskLevel != null && !question.getRiskLevel().equals(riskLevel)) {
                    continue;
                }
                if (tone != null && !question.getTone().equals(tone)) {
                    continue;
                }
                if (useAfterRapport != null && question.isUseAfterRapport() != useAfterRapport) {
                    continue;
                }
                if (requireRapport && !question.isUseAfterRapport()) {
                    continue;
                }
                
                questions.add(question);
            }
        }
        
        return questions;
    }
    
    /**
     * Get questions with the highest escalation tier (Tier 3).
     * 
     * @param category Optional category to filter by
     * @return List of high priority questions
     */
    public List<Question> getHighPriorityQuestions(String category) {
        return filterQuestions(category, 3, null, null, null, false);
    }
    
    /**
     * Get questions recommended for a specific situation.
     * 
     * @param situation Situation type (e.g., 'attempt_in_progress', 'adolescent')
     * @param hasRapport Whether rapport has been established
     * @return List of appropriate questions for the situation
     */
    public List<Question> getQuestionsForSituation(String situation, boolean hasRapport) {
        List<Question> questions = getQuestionsByCategory(situation);
        
        // If no rapport, filter out questions that require rapport
        if (!hasRapport) {
            questions = questions.stream()
                .filter(q -> !q.isUseAfterRapport())
                .collect(Collectors.toList());
        }
        
        return questions;
    }
    
    /**
     * Get recommended questions based on situation and context.
     * 
     * @param category The situation category
     * @param escalationTier Optional escalation tier filter
     * @param hasRapport Whether rapport has been established
     * @return Sorted list of recommended questions (by escalation tier, then ID)
     */
    public List<Question> getQuestionRecommendations(
            String category,
            Integer escalationTier,
            boolean hasRapport) {
        
        List<Question> questions = getQuestionsForSituation(category, hasRapport);
        
        if (escalationTier != null) {
            questions = questions.stream()
                .filter(q -> q.getEscalationTier() == escalationTier)
                .collect(Collectors.toList());
        }
        
        // Sort by escalation tier (descending) then by ID (ascending)
        questions.sort((q1, q2) -> {
            int tierCompare = Integer.compare(q2.getEscalationTier(), q1.getEscalationTier());
            if (tierCompare != 0) {
                return tierCompare;
            }
            return Integer.compare(q1.getId(), q2.getId());
        });
        
        return questions;
    }
    
    /**
     * Get all question IDs in the bank.
     */
    public Set<Integer> getAllQuestionIds() {
        return new HashSet<>(idToQuestion.keySet());
    }
    
    /**
     * Get statistics about the question bank.
     */
    public QuestionBankStatistics getStatistics() {
        QuestionBankStatistics stats = new QuestionBankStatistics();
        stats.setTotalQuestions(idToQuestion.size());
        
        // Count categories
        Map<String, Integer> categories = new HashMap<>();
        for (Map.Entry<String, List<Question>> entry : questionBank.entrySet()) {
            categories.put(entry.getKey(), entry.getValue().size());
        }
        stats.setCategories(categories);
        
        // Count escalation tiers, risk levels, and tones
        Map<Integer, Integer> escalationTiers = new HashMap<>();
        Map<String, Integer> riskLevels = new HashMap<>();
        Map<String, Integer> tones = new HashMap<>();
        
        for (Question question : idToQuestion.values()) {
            // Count escalation tiers
            int tier = question.getEscalationTier();
            escalationTiers.put(tier, escalationTiers.getOrDefault(tier, 0) + 1);
            
            // Count risk levels
            String risk = question.getRiskLevel();
            riskLevels.put(risk, riskLevels.getOrDefault(risk, 0) + 1);
            
            // Count tones
            String tone = question.getTone();
            tones.put(tone, tones.getOrDefault(tone, 0) + 1);
        }
        
        stats.setEscalationTiers(escalationTiers);
        stats.setRiskLevels(riskLevels);
        stats.setTones(tones);
        
        return stats;
    }
    
    /**
     * Get the category for a question ID.
     */
    public String getCategoryForQuestionId(int questionId) {
        return idToCategory.get(questionId);
    }
    
    /**
     * Main method for example usage.
     */
    public static void main(String[] args) {
        try {
            // Initialize the manager
            QuestionBankManager manager = new QuestionBankManager();
            
            // Display statistics
            System.out.println("Question Bank Statistics:");
            System.out.println("=".repeat(50));
            QuestionBankStatistics stats = manager.getStatistics();
            System.out.println("Total Questions: " + stats.getTotalQuestions());
            System.out.println("\nCategories:");
            for (Map.Entry<String, Integer> entry : stats.getCategories().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " questions");
            }
            System.out.println("\nEscalation Tiers:");
            stats.getEscalationTiers().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println("  Tier " + entry.getKey() + ": " + entry.getValue() + " questions"));
            System.out.println("\nRisk Levels:");
            for (Map.Entry<String, Integer> entry : stats.getRiskLevels().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " questions");
            }
            System.out.println("\nTones:");
            for (Map.Entry<String, Integer> entry : stats.getTones().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " questions");
            }
            
            // Example: Get high priority questions for attempt in progress
            System.out.println("\n" + "=".repeat(50));
            System.out.println("High Priority Questions (Tier 3) - Attempt in Progress:");
            System.out.println("=".repeat(50));
            List<Question> highPriority = manager.getHighPriorityQuestions("attempt_in_progress");
            for (Question q : highPriority) {
                System.out.println("\nID: " + q.getId());
                System.out.println("Question: " + q.getQuestion());
                System.out.println("Tone: " + q.getTone() + " | Risk: " + q.getRiskLevel() + " | Notes: " + q.getNotes());
            }
            
            // Example: Get questions for a specific situation without rapport
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Questions for 'adolescent' situation (no rapport):");
            System.out.println("=".repeat(50));
            List<Question> adolescentQuestions = manager.getQuestionsForSituation("adolescent", false);
            for (Question q : adolescentQuestions) {
                System.out.println("\nID: " + q.getId() + " | Tier: " + q.getEscalationTier());
                System.out.println("Question: " + q.getQuestion());
            }
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Make sure suicide_question_bank.json is in the same directory or in classpath.");
        }
    }
}

