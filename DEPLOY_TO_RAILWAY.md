# Deploy to Railway - Step by Step Guide

Railway is the easiest way to deploy your Spring Boot app. It auto-detects Spring Boot and handles everything.

## Step 1: Sign Up for Railway

1. Go to: https://railway.app
2. Click "Start a New Project"
3. Sign up with GitHub (easiest - connects to your account)
4. Authorize Railway to access your GitHub

## Step 2: Create New Project

1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. Find and select: `sonamthukral/chatbot-assist`
4. Click "Deploy Now"

## Step 3: Configure Environment Variables

Railway will start deploying. While it's building:

1. Go to your project settings
2. Click "Variables" tab
3. Add these environment variables:

**Required:**
- `LLM_API_KEY` = `your-openai-api-key-here` (your actual OpenAI API key)
- `LLM_API_ENDPOINT` = `https://api.openai.com/v1/chat/completions`
- `LLM_MODEL` = `gpt-3.5-turbo`

**Note:** Railway automatically sets `PORT` - you don't need to add it. Spring Boot will use it automatically.

## Step 4: Wait for Deployment

- Railway will automatically:
  - Detect it's a Spring Boot app
  - Install Java and Maven
  - Build your project
  - Start the application

This takes about 2-5 minutes.

## Step 5: Get Your Public URL

1. Once deployed, Railway gives you a URL like: `yourapp.up.railway.app`
2. Click on it to open your public site!
3. You can also set a custom domain if you want

## Step 6: Update Application Code (if needed)

Your code should work, but you might need to update `ChatbotController.java` to read from environment variables instead of just `application.properties`.

Let me check if we need to update the code...

