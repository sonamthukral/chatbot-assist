package com.suicide.questionbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the crisis chatbot web interface.
 */
@Controller
public class ChatbotController {
    
    private QuestionBankManager questionManager;
    private ResourceManager resourceManager;
    private ChatService chatService;
    
    @Autowired
    public ChatbotController(
            @Value("${llm.api.key:}") String llmApiKey,
            @Value("${llm.api.endpoint:https://api.openai.com/v1/chat/completions}") String llmEndpoint,
            @Value("${llm.model:gpt-3.5-turbo}") String llmModel) {
        try {
            this.questionManager = new QuestionBankManager("suicide_question_bank.json");
            this.resourceManager = new ResourceManager("resources_full.json");
            
            // Initialize LLM service if API key is provided
            System.out.println("Checking LLM configuration...");
            System.out.println("  API Key provided: " + (llmApiKey != null && !llmApiKey.trim().isEmpty()));
            System.out.println("  API Key length: " + (llmApiKey != null ? llmApiKey.length() : 0));
            System.out.println("  Endpoint: " + llmEndpoint);
            System.out.println("  Model: " + llmModel);
            
            if (llmApiKey != null && !llmApiKey.trim().isEmpty() && !llmApiKey.equals("YOUR_OPENAI_API_KEY_HERE")) {
                try {
                    LLMService llmService = new LLMService(llmApiKey, llmEndpoint, llmModel);
                    this.chatService = new ChatService(llmService, resourceManager, questionManager);
                    System.out.println("✅ LLM service enabled - using AI-powered responses.");
                    System.out.println("✅ Ready to use OpenAI API. When you add billing, responses will be AI-generated.");
                    System.out.println("✅ Until then, the system will use helpful rule-based fallback responses.");
                } catch (Exception e) {
                    System.err.println("❌ Error creating LLM service: " + e.getMessage());
                    e.printStackTrace();
                    this.chatService = new ChatService(resourceManager, questionManager);
                    System.out.println("⚠️ Falling back to rule-based responses due to LLM initialization error.");
                    System.out.println("⚠️ This is normal if your API key has quota issues. Add billing to enable LLM.");
                }
            } else {
                // Use fallback mode - rule-based conversational responses
                this.chatService = new ChatService(resourceManager, questionManager);
                System.out.println("⚠️ LLM API key not configured. Using rule-based conversational responses.");
                System.out.println("Set 'llm.api.key' in application.properties or as environment variable to enable AI-powered responses.");
            }
        } catch (IOException e) {
            System.err.println("Error initializing managers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Serve the main chatbot page.
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }
    
    /**
     * Get questions for a specific situation.
     */
    @GetMapping("/api/questions")
    @ResponseBody
    public ResponseEntity<?> getQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer escalationTier,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false, defaultValue = "false") boolean hasRapport) {
        try {
            List<Question> questions;
            if (category != null) {
                questions = questionManager.getQuestionsForSituation(category, hasRapport);
            } else {
                // Get all questions by filtering with null category
                questions = questionManager.filterQuestions(
                    null, null, null, null, null, hasRapport
                );
            }
            
            // Apply filters
            if (escalationTier != null || riskLevel != null) {
                questions = questionManager.filterQuestions(
                    category, escalationTier, riskLevel, null, null, hasRapport
                );
            }
            
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get resources based on transcript.
     */
    @PostMapping("/api/resources")
    @ResponseBody
    public ResponseEntity<?> getResources(@RequestBody Map<String, String> request) {
        try {
            String transcript = request.get("transcript");
            if (transcript == null || transcript.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Transcript is required"));
            }
            
            // Convert Resource objects to SPCchatbotDemo.Resource format
            List<com.suicide.questionbank.Resource> allResources = resourceManager.getAllResources();
            List<SPCchatbotDemo.Resource> chatbotResources = convertToChatbotResources(allResources);
            
            // Use SPCchatbotDemo to get top resources
            SPCchatbotDemo.Result result = SPCchatbotDemo.getTopResources(transcript, chatbotResources);
            
            // Convert result to response format
            Map<String, Object> response = new HashMap<>();
            List<Map<String, Object>> topResources = new ArrayList<>();
            
            for (SPCchatbotDemo.TopResource topResource : result.getTopResources()) {
                Map<String, Object> resourceData = new HashMap<>();
                resourceData.put("name", topResource.getResource().getName());
                resourceData.put("description", topResource.getResource().getDescription());
                resourceData.put("category", topResource.getResource().getCategory());
                resourceData.put("serviceArea", topResource.getResource().getServiceArea());
                resourceData.put("eligibility", topResource.getResource().getEligibility());
                resourceData.put("cost", topResource.getResource().getCost());
                resourceData.put("hours", topResource.getResource().getHours());
                resourceData.put("language", topResource.getResource().getLanguage());
                resourceData.put("justification", topResource.getJustification());
                topResources.add(resourceData);
            }
            
            response.put("top_resources", topResources);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Search resources by category, county, or name.
     */
    @GetMapping("/api/resources/search")
    @ResponseBody
    public ResponseEntity<?> searchResources(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String county,
            @RequestParam(required = false) String search) {
        try {
            List<com.suicide.questionbank.Resource> resources = resourceManager.filterResources(
                category, county, search
            );
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Chat endpoint for conversational responses using LLM.
     */
    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        if (chatService == null) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "Chat service not available. Please check server logs."
            ));
        }
        
        try {
            String message = (String) request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, String>> conversationHistory = (List<Map<String, String>>) request.get("history");
            
            ChatService.ChatResponse response = chatService.generateChatResponse(message, conversationHistory);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", response.getMessage());
            
            // Convert resources to response format
            List<Map<String, Object>> resourcesList = new ArrayList<>();
            for (com.suicide.questionbank.Resource r : response.getSuggestedResources()) {
                Map<String, Object> resourceData = new HashMap<>();
                resourceData.put("name", r.getName());
                resourceData.put("description", r.getDescription());
                if (r.getCategories() != null && !r.getCategories().isEmpty()) {
                    resourceData.put("categories", r.getCategories());
                }
                Map<String, String> contact = new HashMap<>();
                if (r.getPhones() != null) {
                    if (r.getPhones().getPrimary() != null && r.getPhones().getPrimary().getNumber() != null) {
                        contact.put("phone", r.getPhones().getPrimary().getNumber());
                    } else if (r.getPhones().getHotline() != null && r.getPhones().getHotline().getNumber() != null) {
                        contact.put("phone", r.getPhones().getHotline().getNumber());
                    }
                }
                if (r.getContact() != null && r.getContact().getWebsite() != null) {
                    contact.put("website", r.getContact().getWebsite());
                }
                if (!contact.isEmpty()) {
                    resourceData.put("contact", contact);
                }
                resourceData.put("fees", r.getFees());
                resourceData.put("hours", r.getHours());
                resourcesList.add(resourceData);
            }
            responseMap.put("suggestedResources", resourcesList);
            
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get statistics about questions and resources.
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            QuestionBankStatistics qStats = questionManager.getStatistics();
            stats.put("totalQuestions", qStats.getTotalQuestions());
            stats.put("questionCategories", qStats.getCategories());
            
            stats.put("totalResources", resourceManager.getTotalResources());
            stats.put("resourceCategories", resourceManager.getAllCategories());
            stats.put("counties", resourceManager.getAllCounties());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
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
            
            // Convert categories list to single category string
            if (r.getCategories() != null && !r.getCategories().isEmpty()) {
                cr.setCategory(String.join(", ", r.getCategories()));
            }
            
            // Convert service area
            if (r.getServiceArea() != null && r.getServiceArea().getAreasCovered() != null) {
                cr.setServiceArea(String.join(", ", r.getServiceArea().getAreasCovered()));
            }
            
            // Convert eligibility
            if (r.getEligibility() != null) {
                cr.setEligibility(r.getEligibility().getGeneral());
            }
            
            cr.setCost(r.getFees());
            cr.setHours(r.getHours());
            
            // Convert languages
            if (r.getLanguagesOffered() != null && !r.getLanguagesOffered().isEmpty()) {
                cr.setLanguage(String.join(", ", r.getLanguagesOffered()));
            }
            
            chatbotResources.add(cr);
        }
        
        return chatbotResources;
    }
}

