package com.suicide.questionbank;

import java.io.IOException;
import java.util.List;

/**
 * Example usage of the Question Bank Manager
 * Demonstrates how to use the question bank for different scenarios.
 */
public class ExampleUsage {
    
    public static void exampleBasicUsage() throws IOException {
        System.out.println("=".repeat(60));
        System.out.println("BASIC USAGE EXAMPLE");
        System.out.println("=".repeat(60));
        
        // Initialize manager
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Get all categories
        System.out.println("\nAvailable categories:");
        for (String category : manager.getCategories()) {
            System.out.println("  - " + category);
        }
        
        // Get statistics
        QuestionBankStatistics stats = manager.getStatistics();
        System.out.println("\nTotal questions in bank: " + stats.getTotalQuestions());
    }
    
    public static void exampleAttemptInProgress() throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCENARIO: Attempt in Progress (No Rapport)");
        System.out.println("=".repeat(60));
        
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Get immediate questions (no rapport needed)
        List<Question> questions = manager.getQuestionsForSituation(
            "attempt_in_progress",
            false
        );
        
        System.out.println("\nFound " + questions.size() + " questions for immediate use:");
        int count = 0;
        for (Question q : questions) {
            if (count++ >= 3) break; // Show first 3
            System.out.println("\n" + count + ". [ID: " + q.getId() + ", Tier: " + q.getEscalationTier() + "]");
            System.out.println("   " + q.getQuestion());
            System.out.println("   Tone: " + q.getTone() + " | Notes: " + q.getNotes());
        }
    }
    
    public static void exampleAdolescentAssessment() throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCENARIO: Adolescent Assessment");
        System.out.println("=".repeat(60));
        
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Get all adolescent questions
        List<Question> questions = manager.getQuestionsByCategory("adolescent");
        
        System.out.println("\nFound " + questions.size() + " questions for adolescents:");
        for (Question q : questions) {
            String rapportNeeded = q.isUseAfterRapport() ? "✓" : "✗";
            System.out.println("\n[ID: " + q.getId() + "] " + rapportNeeded + " Rapport Required");
            System.out.println("  " + q.getQuestion());
            System.out.println("  Escalation Tier: " + q.getEscalationTier() + " | Risk: " + q.getRiskLevel());
        }
    }
    
    public static void exampleFiltering() throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FILTERING EXAMPLE: High Priority, High Clarity Questions");
        System.out.println("=".repeat(60));
        
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Find all Tier 3, high-clarity questions
        List<Question> highPriority = manager.filterQuestions(
            null, // all categories
            3,    // escalation tier
            "high-clarity", // risk level
            null, // all tones
            null, // all rapport settings
            false
        );
        
        System.out.println("\nFound " + highPriority.size() + " high-priority, high-clarity questions:");
        int count = 0;
        for (Question q : highPriority) {
            if (count++ >= 5) break; // Show first 5
            String category = manager.getCategoryForQuestionId(q.getId());
            System.out.println("\n[" + category + "] ID: " + q.getId());
            System.out.println("  " + q.getQuestion());
            System.out.println("  Tone: " + q.getTone());
        }
    }
    
    public static void exampleIntrusiveThoughts() throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCENARIO: Intrusive Suicidal Thoughts (Creative/Non-Threatening)");
        System.out.println("=".repeat(60));
        
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Get gentle, non-threatening questions for initial assessment
        List<Question> questions = manager.filterQuestions(
            "intrusive_suicide_creative",
            1,    // escalation tier
            null, // all risk levels
            "gentle",
            false, // useAfterRapport
            false
        );
        
        System.out.println("\nFound " + questions.size() + " gentle, low-tier questions (no rapport needed):");
        int count = 0;
        for (Question q : questions) {
            if (count++ >= 5) break; // Show first 5
            System.out.println("\n[ID: " + q.getId() + "]");
            System.out.println("  " + q.getQuestion());
            System.out.println("  Tone: " + q.getTone() + " | Risk: " + q.getRiskLevel());
        }
        
        // Get creative/metaphorical questions
        System.out.println("\n--- Creative/Metaphorical Questions ---");
        List<Question> creative = manager.filterQuestions(
            "intrusive_suicide_creative",
            null,
            null,
            "metaphorical",
            null,
            false
        );
        System.out.println("Found " + creative.size() + " metaphorical questions:");
        count = 0;
        for (Question q : creative) {
            if (count++ >= 3) break; // Show first 3
            System.out.println("\n[ID: " + q.getId() + "]");
            System.out.println("  " + q.getQuestion());
        }
    }
    
    public static void exampleSituationalFlow() throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SITUATIONAL FLOW: Unclear/Ambivalent → Building Rapport");
        System.out.println("=".repeat(60));
        
        QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");
        
        // Phase 1: No rapport
        System.out.println("\n--- Phase 1: Initial Assessment (No Rapport) ---");
        List<Question> phase1 = manager.getQuestionsForSituation(
            "unclear_or_ambivalent",
            false
        );
        System.out.println("Questions available: " + phase1.size());
        for (Question q : phase1) {
            System.out.println("  [" + q.getId() + "] " + q.getQuestion());
        }
        
        // Phase 2: After rapport established
        System.out.println("\n--- Phase 2: After Rapport Established ---");
        List<Question> phase2 = manager.getQuestionsForSituation(
            "unclear_or_ambivalent",
            true
        );
        System.out.println("Questions available: " + phase2.size());
        for (Question q : phase2) {
            System.out.println("  [" + q.getId() + "] " + q.getQuestion());
        }
    }
    
    public static void main(String[] args) {
        try {
            exampleBasicUsage();
            exampleAttemptInProgress();
            exampleAdolescentAssessment();
            exampleFiltering();
            exampleIntrusiveThoughts();
            exampleSituationalFlow();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("All examples completed successfully!");
            System.out.println("=".repeat(60));
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Make sure suicide_question_bank.json is in the same directory.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

