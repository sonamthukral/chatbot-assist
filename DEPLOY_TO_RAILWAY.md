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
- `llm.api.key` = `your-openai-api-key-here` (your actual OpenAI API key)
- `llm.api.endpoint` = `https://api.openai.com/v1/chat/completions`
- `llm.model` = `gpt-3.5-turbo`

**Note:** 
- Railway automatically sets `PORT` - you don't need to add it. Spring Boot will use it automatically.
- The app is configured to bind to `0.0.0.0` (all interfaces) so Railway can access it.
- Spring Boot converts environment variables: `LLM_API_KEY` → `llm.api.key`, so either format works!

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

## ✅ Your Code is Ready!

Your code already uses `@Value` annotations which automatically read from environment variables. No code changes needed!

Spring Boot will:
1. First check environment variables
2. Then fall back to `application.properties` (if env vars not set)
3. Use defaults if neither is set

## Troubleshooting

**If deployment fails:**
- Check the build logs in Railway
- Make sure Java 11+ is detected
- Verify environment variables are set correctly

**If the site loads but LLM doesn't work:**
- Check that `llm.api.key` environment variable is set correctly
- Check Railway logs for API errors
- Verify your OpenAI API key is valid and has billing set up

## Next Steps After Deployment

1. **Test your site** - Visit the Railway URL
2. **Test the chatbot** - Make sure LLM responses work
3. **Set up custom domain** (optional) - Railway lets you add your own domain
4. **Monitor usage** - Check Railway dashboard for logs and metrics

