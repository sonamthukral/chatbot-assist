package com.suicide.questionbank;

/**
 * Represents a single question in the suicide risk assessment question bank.
 */
public class Question {
    private int id;
    private String question;
    private String tone;
    private String riskLevel;
    private int escalationTier;
    private boolean useAfterRapport;
    private String notes;

    // Default constructor for JSON deserialization
    public Question() {
    }

    // Constructor with all fields
    public Question(int id, String question, String tone, String riskLevel, 
                   int escalationTier, boolean useAfterRapport, String notes) {
        this.id = id;
        this.question = question;
        this.tone = tone;
        this.riskLevel = riskLevel;
        this.escalationTier = escalationTier;
        this.useAfterRapport = useAfterRapport;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getEscalationTier() {
        return escalationTier;
    }

    public void setEscalationTier(int escalationTier) {
        this.escalationTier = escalationTier;
    }

    public boolean isUseAfterRapport() {
        return useAfterRapport;
    }

    public void setUseAfterRapport(boolean useAfterRapport) {
        this.useAfterRapport = useAfterRapport;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", tone='" + tone + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", escalationTier=" + escalationTier +
                ", useAfterRapport=" + useAfterRapport +
                ", notes='" + notes + '\'' +
                '}';
    }
}

