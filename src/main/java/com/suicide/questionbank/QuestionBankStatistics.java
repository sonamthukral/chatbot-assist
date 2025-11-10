package com.suicide.questionbank;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistics about the question bank.
 */
public class QuestionBankStatistics {
    private int totalQuestions;
    private Map<String, Integer> categories;
    private Map<Integer, Integer> escalationTiers;
    private Map<String, Integer> riskLevels;
    private Map<String, Integer> tones;

    public QuestionBankStatistics() {
        this.categories = new HashMap<>();
        this.escalationTiers = new HashMap<>();
        this.riskLevels = new HashMap<>();
        this.tones = new HashMap<>();
    }

    // Getters and Setters
    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Map<String, Integer> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Integer> categories) {
        this.categories = categories;
    }

    public Map<Integer, Integer> getEscalationTiers() {
        return escalationTiers;
    }

    public void setEscalationTiers(Map<Integer, Integer> escalationTiers) {
        this.escalationTiers = escalationTiers;
    }

    public Map<String, Integer> getRiskLevels() {
        return riskLevels;
    }

    public void setRiskLevels(Map<String, Integer> riskLevels) {
        this.riskLevels = riskLevels;
    }

    public Map<String, Integer> getTones() {
        return tones;
    }

    public void setTones(Map<String, Integer> tones) {
        this.tones = tones;
    }
}

