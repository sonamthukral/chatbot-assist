package com.suicide.questionbank;

import java.io.IOException;
import java.util.*;

/**
 * Service that handles conversational chat using LLM with RAG (Retrieval-Augmented Generation).
 * Uses resources and questions as context for personalized responses.
 * Falls back to rule-based responses if LLM is not available.
 */
public class ChatService {
    private final LLMService llmService;
    private final ResourceManager resourceManager;
    private final QuestionBankManager questionManager;
    private final boolean useLLM;
    
    public ChatService(LLMService llmService, ResourceManager resourceManager, QuestionBankManager questionManager) {
        this.llmService = llmService;
        this.resourceManager = resourceManager;
        this.questionManager = questionManager;
        this.useLLM = (llmService != null);
    }
    
    /**
     * Create ChatService without LLM (fallback mode).
     */
    public ChatService(ResourceManager resourceManager, QuestionBankManager questionManager) {
        this(null, resourceManager, questionManager);
    }
    
    /**
     * Generate a chat response using LLM with relevant resources and questions as context.
     */
    public ChatResponse generateChatResponse(String userMessage, List<Map<String, String>> conversationHistory) throws IOException {
        // Extract context from user message to find relevant resources
        String transcript = userMessage;
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            // Build full transcript from conversation history
            StringBuilder transcriptBuilder = new StringBuilder();
            for (Map<String, String> msg : conversationHistory) {
                if ("user".equals(msg.get("role"))) {
                    transcriptBuilder.append(msg.get("content")).append(" ");
                }
            }
            transcriptBuilder.append(userMessage);
            transcript = transcriptBuilder.toString();
        }
        
        // Get relevant resources using existing SPCchatbotDemo logic
        List<com.suicide.questionbank.Resource> allResources = resourceManager.getAllResources();
        List<SPCchatbotDemo.Resource> chatbotResources = convertToChatbotResources(allResources);
        SPCchatbotDemo.Result result = SPCchatbotDemo.getTopResources(transcript, chatbotResources);
        
        // Convert back to Resource objects
        List<com.suicide.questionbank.Resource> relevantResources = new ArrayList<>();
        for (SPCchatbotDemo.TopResource topResource : result.getTopResources()) {
            // Find matching resource in allResources - try exact match first, then partial
            String resourceName = topResource.getResource().getName();
            com.suicide.questionbank.Resource matching = allResources.stream()
                .filter(r -> r.getName() != null && r.getName().equals(resourceName))
                .findFirst()
                .orElse(null);
            
            // If exact match fails, try partial match
            if (matching == null && resourceName != null) {
                matching = allResources.stream()
                    .filter(r -> r.getName() != null && 
                               (r.getName().contains(resourceName) || resourceName.contains(r.getName())))
                    .findFirst()
                    .orElse(null);
            }
            
            if (matching != null) {
                relevantResources.add(matching);
            } else {
                // If still no match, create a resource from the chatbot resource
                System.out.println("⚠️ Could not find exact match for: " + resourceName);
            }
        }
        
        // Get relevant questions based on extracted context
        List<Question> relevantQuestions = getRelevantQuestions(transcript);
        
        // Log what we're sending to the LLM
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("📊 CONTEXT BEING SENT TO LLM (for Crisis Responder):");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Responder's message: " + userMessage.substring(0, Math.min(100, userMessage.length())));
        System.out.println("\n✅ Relevant Resources found: " + relevantResources.size());
        if (relevantResources.isEmpty()) {
            System.out.println("   ⚠️ WARNING: No resources matched! LLM may give generic responses.");
        } else {
            for (int i = 0; i < relevantResources.size(); i++) {
                com.suicide.questionbank.Resource r = relevantResources.get(i);
                System.out.println("   " + (i+1) + ". " + (r.getName() != null ? r.getName() : "Unnamed"));
                if (r.getPhones() != null) {
                    String phone = null;
                    if (r.getPhones().getPrimary() != null && r.getPhones().getPrimary().getNumber() != null) {
                        phone = r.getPhones().getPrimary().getNumber();
                    } else if (r.getPhones().getHotline() != null && r.getPhones().getHotline().getNumber() != null) {
                        phone = r.getPhones().getHotline().getNumber();
                    }
                    if (phone != null) {
                        System.out.println("      Phone: " + phone);
                    }
                }
            }
        }
        System.out.println("\n✅ Relevant Questions found: " + relevantQuestions.size());
        if (relevantQuestions.isEmpty()) {
            System.out.println("   ⚠️ WARNING: No questions matched!");
        } else {
            for (int i = 0; i < relevantQuestions.size(); i++) {
                Question q = relevantQuestions.get(i);
                System.out.println("   " + (i+1) + ". " + q.getQuestion());
            }
        }
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        // Generate response - use LLM if available, otherwise use fallback
        String response;
        if (useLLM && llmService != null) {
            System.out.println("\n🤖 Attempting to use LLM to generate AI-powered response...");
            try {
                response = llmService.generateResponseWithContext(
                    userMessage,
                    relevantResources,
                    relevantQuestions,
                    conversationHistory
                );
                System.out.println("✅ LLM response generated successfully (length: " + response.length() + " chars)");
                System.out.println("✅ Response is AI-generated using OpenAI API\n");
            } catch (IOException e) {
                String errorMsg = e.getMessage();
                System.err.println("\n❌ LLM API call failed, using helpful fallback response");
                System.err.println("   Error: " + errorMsg);
                
                // Check for specific error types and provide helpful messages
                if (errorMsg != null) {
                    if (errorMsg.contains("insufficient_quota") || errorMsg.contains("429") || errorMsg.contains("quota")) {
                        System.err.println("⚠️ OpenAI quota exceeded or no credits available.");
                        System.err.println("   → Add billing: https://platform.openai.com/account/billing");
                        System.err.println("   → Once billing is added, LLM will work automatically!");
                    } else if (errorMsg.contains("401") || errorMsg.contains("Invalid API key")) {
                        System.err.println("⚠️ Invalid API key! Check your API key in application.properties or environment variables.");
                    } else if (errorMsg.contains("rate limit")) {
                        System.err.println("⚠️ Rate limit exceeded. Please wait a moment and try again.");
                    } else {
                        System.err.println("⚠️ API error. Check your OpenAI account status.");
                    }
                }
                
                System.out.println("📝 Using rule-based fallback response (still helpful, but not AI-generated)\n");
                response = generateFallbackResponse(userMessage, relevantResources, relevantQuestions, transcript);
            }
        } else {
            System.out.println("📝 Using rule-based response (LLM not configured)");
            response = generateFallbackResponse(userMessage, relevantResources, relevantQuestions, transcript);
        }
        
        return new ChatResponse(response, relevantResources);
    }
    
    /**
     * Get relevant questions based on transcript context.
     */
    private List<Question> getRelevantQuestions(String transcript) {
        List<Question> questions = new ArrayList<>();
        String lc = transcript.toLowerCase();
        
        // Determine category based on keywords
        String category = null;
        if (lc.contains("suicidal") || lc.contains("suicide") || lc.contains("kill myself") || 
            lc.contains("end my life") || lc.contains("want to die")) {
            category = "attempt_in_progress";
        } else if (lc.contains("teen") || lc.contains("adolescent") || lc.contains("young")) {
            category = "adolescent";
        } else if (lc.contains("veteran")) {
            category = "veteran";
        } else if (lc.contains("elderly") || lc.contains("senior")) {
            category = "elderly";
        } else if (lc.contains("depression") || lc.contains("depressed") || lc.contains("sad")) {
            // For general mental health, get questions from multiple categories
            questions = questionManager.getQuestionsForSituation("recent_suicidal_thoughts", false);
            if (questions.size() > 2) {
                questions = questions.subList(0, 2);
            }
            return questions;
        }
        
        // Get questions for the category
        if (category != null) {
            questions = questionManager.getQuestionsForSituation(category, false);
            // Limit to top 3 most relevant
            if (questions.size() > 3) {
                questions = questions.subList(0, 3);
            }
        } else {
            // If no specific category, get general questions
            questions = questionManager.getQuestionsForSituation("recent_suicidal_thoughts", false);
            if (questions.size() > 2) {
                questions = questions.subList(0, 2);
            }
        }
        
        return questions;
    }
    
    /**
     * Convert Resource objects to SPCchatbotDemo.Resource format.
     */
    private List<SPCchatbotDemo.Resource> convertToChatbotResources(
            List<com.suicide.questionbank.Resource> resources) {
        List<SPCchatbotDemo.Resource> chatbotResources = new ArrayList<>();
        
        for (com.suicide.questionbank.Resource r : resources) {
            SPCchatbotDemo.Resource cr = new SPCchatbotDemo.Resource();
            cr.setName(r.getName());
            cr.setDescription(r.getDescription());
            
            if (r.getCategories() != null && !r.getCategories().isEmpty()) {
                cr.setCategory(String.join(", ", r.getCategories()));
            }
            
            if (r.getServiceArea() != null && r.getServiceArea().getAreasCovered() != null) {
                cr.setServiceArea(String.join(", ", r.getServiceArea().getAreasCovered()));
            }
            
            if (r.getEligibility() != null) {
                cr.setEligibility(r.getEligibility().getGeneral());
            }
            
            cr.setCost(r.getFees());
            cr.setHours(r.getHours());
            
            if (r.getLanguagesOffered() != null && !r.getLanguagesOffered().isEmpty()) {
                cr.setLanguage(String.join(", ", r.getLanguagesOffered()));
            }
            
            chatbotResources.add(cr);
        }
        
        return chatbotResources;
    }
    
    /**
     * Generate a conversational response without LLM using rule-based templates.
     * Creates natural-sounding responses based on resources and questions.
     */
    private String generateFallbackResponse(
            String userMessage,
            List<com.suicide.questionbank.Resource> relevantResources,
            List<Question> relevantQuestions,
            String transcript) {
        
        StringBuilder response = new StringBuilder();
        String lc = userMessage.toLowerCase();
        
        // Check for crisis keywords
        boolean hasCrisisKeywords = lc.contains("suicidal") || lc.contains("kill myself") || 
                                   lc.contains("end my life") || lc.contains("can't go on");
        boolean hasImmediateRisk = lc.contains("right now") || lc.contains("immediately") || 
                                  lc.contains("going to do it") || lc.contains("plan to");
        
        // Opening - empathetic and validating
        if (hasCrisisKeywords || hasImmediateRisk) {
            response.append("I'm really glad you reached out. It takes courage to ask for help, ");
            response.append("and I want you to know that you're not alone in this. ");
            
            if (hasImmediateRisk) {
                response.append("\n\nIf you're in immediate danger, please call 911 right away, ");
                response.append("or contact the 988 Suicide & Crisis Lifeline. Your safety is the most important thing right now. ");
            }
        } else {
            response.append("Thank you for sharing that with me. I'm here to help you find the support you need. ");
        }
        
        // Reference resources
        if (relevantResources != null && !relevantResources.isEmpty()) {
            response.append("\n\nBased on what you've shared, I'd like to connect you with some resources that might help: ");
            
            for (int i = 0; i < Math.min(relevantResources.size(), 3); i++) {
                com.suicide.questionbank.Resource r = relevantResources.get(i);
                response.append("\n\n");
                
                if (r.getName() != null) {
                    response.append("• ").append(r.getName());
                }
                
                if (r.getDescription() != null && !r.getDescription().isEmpty()) {
                    String desc = r.getDescription();
                    if (desc.length() > 150) {
                        desc = desc.substring(0, 147) + "...";
                    }
                    response.append(" - ").append(desc);
                }
                
                // Add contact info if available
                if (r.getPhones() != null) {
                    if (r.getPhones().getPrimary() != null && r.getPhones().getPrimary().getNumber() != null) {
                        response.append(" You can reach them at ").append(r.getPhones().getPrimary().getNumber()).append(".");
                    } else if (r.getPhones().getHotline() != null && r.getPhones().getHotline().getNumber() != null) {
                        response.append(" Their hotline is ").append(r.getPhones().getHotline().getNumber()).append(".");
                    }
                }
            }
        } else {
            response.append("\n\nI want to help you find the right support. ");
        }
        
        // Add relevant question naturally
        if (relevantQuestions != null && !relevantQuestions.isEmpty()) {
            Question q = relevantQuestions.get(0);
            response.append("\n\n");
            
            // Adapt question to be more conversational
            String questionText = q.getQuestion();
            if (questionText.endsWith("?")) {
                response.append(questionText);
            } else {
                response.append(questionText);
                if (!questionText.endsWith(".") && !questionText.endsWith("!")) {
                    response.append("?");
                }
            }
        }
        
        // Closing
        response.append("\n\nRemember, reaching out for help is a sign of strength, not weakness. ");
        response.append("You deserve support, and there are people who want to help you through this.");
        
        return response.toString();
    }
    
    /**
     * Response object for chat messages.
     */
    public static class ChatResponse {
        private String message;
        private List<com.suicide.questionbank.Resource> suggestedResources;
        
        public ChatResponse(String message, List<com.suicide.questionbank.Resource> suggestedResources) {
            this.message = message;
            this.suggestedResources = suggestedResources != null ? suggestedResources : new ArrayList<>();
        }
        
        public String getMessage() {
            return message;
        }
        
        public List<com.suicide.questionbank.Resource> getSuggestedResources() {
            return suggestedResources;
        }
    }
}

