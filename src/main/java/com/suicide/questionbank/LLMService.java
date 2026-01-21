package com.suicide.questionbank;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with LLM APIs (OpenAI-compatible).
 * Supports both OpenAI and other OpenAI-compatible APIs like Vanderbilt.ai if they support the same format.
 * 
 * This service uses Retrieval-Augmented Generation (RAG) to provide personalized responses
 * based on relevant resources and questions from the question bank.
 */
public class LLMService {
    private final String apiKey;
    private final String apiEndpoint;
    private final String model;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final boolean useCustomAuthHeader;
    private final String customAuthHeaderName;
    
    // Default to OpenAI, but can be configured for other providers
    private static final String DEFAULT_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    
    /**
     * Create LLM service with default OpenAI settings.
     */
    public LLMService(String apiKey) {
        this(apiKey, DEFAULT_ENDPOINT, DEFAULT_MODEL);
    }
    
    /**
     * Create LLM service with custom endpoint (for Vanderbilt.ai or other providers).
     */
    public LLMService(String apiKey, String apiEndpoint, String model) {
        this(apiKey, apiEndpoint, model, false, null);
    }
    
    /**
     * Create LLM service with custom authentication header support.
     * Some providers (like Vanderbilt.ai) may use different authentication methods.
     */
    public LLMService(String apiKey, String apiEndpoint, String model, 
                     boolean useCustomAuthHeader, String customAuthHeaderName) {
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint;
        this.model = model;
        this.useCustomAuthHeader = useCustomAuthHeader;
        this.customAuthHeaderName = customAuthHeaderName != null ? customAuthHeaderName : "Authorization";
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
        
        // Log configuration (without exposing API key)
        System.out.println("LLM Service initialized:");
        System.out.println("  Endpoint: " + apiEndpoint);
        System.out.println("  Model: " + model);
        System.out.println("  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***configured***" : "NOT SET"));
    }
    
    /**
     * Generate a response using the LLM with a system prompt and user message.
     */
    public String generateResponse(String systemPrompt, String userMessage) throws IOException {
        return generateResponse(systemPrompt, userMessage, null);
    }
    
    /**
     * Generate a response using the LLM with conversation history.
     */
    public String generateResponse(String systemPrompt, String userMessage, List<Map<String, String>> conversationHistory) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        
        JsonArray messages = new JsonArray();
        
        // Add system message
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);
        
        // Add conversation history if provided
        if (conversationHistory != null) {
            for (Map<String, String> msg : conversationHistory) {
                JsonObject msgObj = new JsonObject();
                msgObj.addProperty("role", msg.get("role"));
                msgObj.addProperty("content", msg.get("content"));
                messages.add(msgObj);
            }
        }
        
        // Add current user message
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1000);
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request.Builder requestBuilder = new Request.Builder()
            .url(apiEndpoint)
            .post(body)
            .addHeader("Content-Type", "application/json");
        
        // Add authentication header (skip for Ollama/local LLMs)
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("not-needed")) {
            if (useCustomAuthHeader) {
                // For providers that might use different auth formats
                requestBuilder.addHeader(customAuthHeaderName, apiKey);
            } else {
                // Standard OpenAI format: "Bearer <token>"
                requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
            }
        }
        // If apiKey is "not-needed", skip authentication (for Ollama)
        
        Request request = requestBuilder.build();
        
        System.out.println("Making LLM API call to: " + apiEndpoint);
        System.out.println("Model: " + model);
        System.out.println("Message count: " + messages.size());
        
        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("LLM API Response Code: " + response.code());
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                System.err.println("❌ LLM API Error Response: " + errorBody);
                System.err.println("Response Code: " + response.code());
                
                // Provide helpful error messages
                if (response.code() == 401) {
                    throw new IOException("Invalid API key. Please check your OpenAI API key in application.properties or environment variables.");
                } else if (response.code() == 429) {
                    throw new IOException("Rate limit or quota exceeded. Please add billing to your OpenAI account: https://platform.openai.com/account/billing");
                } else if (response.code() == 500) {
                    throw new IOException("OpenAI server error. Please try again in a moment.");
                }
                
                throw new IOException("LLM API request failed: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            // Check for errors in response
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMsg = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new IOException("LLM API error: " + errorMsg);
            }
            
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                throw new IOException("No choices in LLM response");
            }
            
            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            if (message == null || !message.has("content")) {
                throw new IOException("Invalid response format: missing message content");
            }
            String llmResponse = message.get("content").getAsString();
            System.out.println("✅ LLM API call successful! Response length: " + llmResponse.length() + " chars");
            return llmResponse;
        } catch (IOException e) {
            System.err.println("❌ Error calling LLM API: " + e.getMessage());
            System.err.println("Full error: ");
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Generate a response with resources and questions as context (RAG approach).
     */
    public String generateResponseWithContext(
            String userMessage,
            List<com.suicide.questionbank.Resource> relevantResources,
            List<Question> relevantQuestions,
            List<Map<String, String>> conversationHistory) throws IOException {
        
        // Build context from resources
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("You are an AI assistant for CRISIS RESPONDERS at the Nashville Suicide Prevention Center. ");
        contextBuilder.append("Your role is to help crisis responders (hotline workers, counselors, support staff) ");
        contextBuilder.append("find appropriate questions to ask and resources to suggest when helping people in crisis.\n\n");
        contextBuilder.append("IMPORTANT: The user is a CRISIS RESPONDER helping someone else, NOT a person in crisis themselves.\n\n");
        
        contextBuilder.append("═══════════════════════════════════════════════════════════════\n");
        contextBuilder.append("⚠️ CRITICAL INSTRUCTIONS - READ CAREFULLY ⚠️\n");
        contextBuilder.append("═══════════════════════════════════════════════════════════════\n");
        contextBuilder.append("The resources and questions below have been SPECIFICALLY matched to this user's situation.\n");
        contextBuilder.append("You MUST reference them in your response. Do NOT give generic advice.\n\n");
        
        contextBuilder.append("📋 AVAILABLE RESOURCES TO SUGGEST:\n");
        contextBuilder.append("These resources are matched to the situation. Recommend at least one to the responder.\n");
        if (relevantResources != null && !relevantResources.isEmpty()) {
            for (int i = 0; i < Math.min(relevantResources.size(), 5); i++) {
                com.suicide.questionbank.Resource r = relevantResources.get(i);
                contextBuilder.append(String.format("%d. %s\n", i + 1, r.getName()));
                if (r.getDescription() != null) {
                    contextBuilder.append("   Description: ").append(r.getDescription()).append("\n");
                }
                if (r.getCategories() != null && !r.getCategories().isEmpty()) {
                    contextBuilder.append("   Categories: ").append(String.join(", ", r.getCategories())).append("\n");
                }
                if (r.getPhones() != null) {
                    if (r.getPhones().getPrimary() != null && r.getPhones().getPrimary().getNumber() != null) {
                        contextBuilder.append("   Phone: ").append(r.getPhones().getPrimary().getNumber()).append("\n");
                    } else if (r.getPhones().getHotline() != null && r.getPhones().getHotline().getNumber() != null) {
                        contextBuilder.append("   Phone: ").append(r.getPhones().getHotline().getNumber()).append("\n");
                    }
                }
                if (r.getFees() != null) {
                    contextBuilder.append("   Cost: ").append(r.getFees()).append("\n");
                }
                contextBuilder.append("\n");
            }
        } else {
            contextBuilder.append("No specific resources matched, but you can still provide general support.\n\n");
        }
        
        // Add questions context if available
        if (relevantQuestions != null && !relevantQuestions.isEmpty()) {
            contextBuilder.append("\n❓ RELEVANT QUESTIONS FOR THE RESPONDER TO ASK:\n");
            contextBuilder.append("These questions have been matched to the situation. Recommend at least one to the responder.\n");
            for (int i = 0; i < Math.min(relevantQuestions.size(), 3); i++) {
                Question q = relevantQuestions.get(i);
                contextBuilder.append(String.format("%d. %s\n", i + 1, q.getQuestion()));
                if (q.getTone() != null) {
                    contextBuilder.append("   (Tone: ").append(q.getTone()).append(")\n");
                }
            }
            contextBuilder.append("\n");
        } else {
            contextBuilder.append("\n(No specific questions matched for this situation)\n\n");
        }
        
        contextBuilder.append("═══════════════════════════════════════════════════════════════\n");
        contextBuilder.append("📝 RESPONSE REQUIREMENTS (MANDATORY - DO NOT IGNORE):\n");
        contextBuilder.append("═══════════════════════════════════════════════════════════════\n");
        contextBuilder.append("⚠️ CRITICAL: You MUST use the resources and questions listed above. ");
        contextBuilder.append("Do NOT give generic advice. Do NOT make up resources. ");
        contextBuilder.append("ONLY use the resources and questions provided.\n\n");
        contextBuilder.append("Your response MUST include ALL of the following:\n\n");
        contextBuilder.append("1. CONTEXT: Acknowledge what the responder is dealing with (1-2 sentences)\n");
        contextBuilder.append("2. RESOURCES (REQUIRED): Suggest at least ONE resource by its EXACT NAME from the list above.\n");
        contextBuilder.append("   - Use the EXACT resource name as shown above\n");
        contextBuilder.append("   - Include the phone number if provided\n");
        contextBuilder.append("   - Explain why this resource is relevant\n");
        contextBuilder.append("   - Format: \"I recommend suggesting [EXACT RESOURCE NAME FROM ABOVE] to the person you're helping. ");
        contextBuilder.append("They specialize in [what they do]. You can reach them at [PHONE NUMBER FROM ABOVE].\"\n\n");
        contextBuilder.append("3. QUESTIONS (REQUIRED): Recommend at least ONE question from the RELEVANT QUESTIONS section above.\n");
        contextBuilder.append("   - Use the EXACT question text or adapt it naturally\n");
        contextBuilder.append("   - Format: \"Consider asking the person: '[Question from above, word-for-word or naturally adapted]'\"\n");
        contextBuilder.append("   - Explain why this question is helpful for this situation\n\n");
        contextBuilder.append("4. GUIDANCE: Provide brief professional guidance (2-3 sentences) on how to use these resources/questions\n\n");
        
        contextBuilder.append("EXAMPLE RESPONSE STRUCTURE (follow this format):\n");
        contextBuilder.append("\"Based on what you've described, here's how you can help:\n\n");
        contextBuilder.append("RESOURCE RECOMMENDATION:\n");
        contextBuilder.append("I recommend suggesting [EXACT RESOURCE NAME FROM THE LIST ABOVE] to the person you're helping. ");
        contextBuilder.append("This resource [brief description from above]. ");
        contextBuilder.append("You can contact them at [PHONE NUMBER FROM ABOVE].\n\n");
        contextBuilder.append("QUESTION TO ASK:\n");
        contextBuilder.append("Consider asking: '[EXACT QUESTION TEXT FROM THE RELEVANT QUESTIONS SECTION ABOVE]' ");
        contextBuilder.append("This question will help you [explain why it's relevant].\n\n");
        contextBuilder.append("GUIDANCE:\n");
        contextBuilder.append("[2-3 sentences of professional guidance on how to use these resources and questions effectively].\"\n\n");
        
        contextBuilder.append("⚠️ REMEMBER:\n");
        contextBuilder.append("- You're helping a CRISIS RESPONDER, not a person in crisis\n");
        contextBuilder.append("- Provide professional guidance and recommendations\n");
        contextBuilder.append("- Reference specific resources and questions from the lists above\n");
        contextBuilder.append("- Help the responder help others effectively\n\n");
        
        String systemPrompt = contextBuilder.toString();
        
        // Enhance user message to include resource names directly
        StringBuilder enhancedUserMessage = new StringBuilder(userMessage);
        if (relevantResources != null && !relevantResources.isEmpty()) {
            enhancedUserMessage.append("\n\n[CONTEXT FOR RESPONDER: The following resources are available to suggest to the person in crisis: ");
            for (int i = 0; i < Math.min(relevantResources.size(), 3); i++) {
                com.suicide.questionbank.Resource r = relevantResources.get(i);
                if (r.getName() != null) {
                    enhancedUserMessage.append(r.getName());
                    if (i < Math.min(relevantResources.size(), 3) - 1) {
                        enhancedUserMessage.append(", ");
                    }
                }
            }
            enhancedUserMessage.append(". Recommend at least one of these to the responder.]");
        }
        
        // Log the system prompt (first 800 chars) to verify resources/questions are included
        System.out.println("\n📝 System prompt preview (first 800 chars):");
        System.out.println(systemPrompt.substring(0, Math.min(800, systemPrompt.length())) + "...\n");
        
        return generateResponse(systemPrompt, enhancedUserMessage.toString(), conversationHistory);
    }
}

