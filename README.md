# Suicide Risk Assessment Question Bank

A comprehensive question bank for suicide risk assessment, organized by situation type and risk level.

## Structure

The question bank contains 21 categories of questions:

## Active Suicide Risk (62 questions)
1. **attempt_in_progress** - Questions for active suicide attempts (8 questions)
2. **current_intent_with_plan** - Questions when intent and plan are present (8 questions)
3. **unclear_or_ambivalent** - Questions for ambiguous situations (8 questions)
4. **impulsive_overwhelmed** - Questions for impulsive/overwhelmed individuals (8 questions)
5. **intoxicated** - Questions when substance use is involved (6 questions)
6. **adolescent** - Age-appropriate questions for adolescents (6 questions)
7. **third_party** - Questions when a third party reports concern (6 questions)
8. **means_access** - Questions about access to lethal means (6 questions)
9. **location_and_rescue** - Questions for location and rescue coordination (6 questions)

## Intrusive Suicidal Thoughts (60 questions)
10. **intrusive_suicide_creative** - Questions for intrusive suicidal thoughts (60 questions)

## Passive Suicide Risk (88 questions)
11. **passive_suicide_hysterical** - Questions for highly emotional/overwhelmed callers (12 questions)
12. **passive_suicide_numb** - Questions for callers with flat affect/detachment (10 questions)
13. **passive_suicide_adolescent** - Age-appropriate passive ideation questions (12 questions)
14. **passive_suicide_grief** - Questions for grief-related passive ideation (8 questions)
15. **passive_suicide_deflecting** - Questions when callers minimize or deflect (8 questions)
16. **passive_suicide_chronic** - Questions for chronic/recurring passive ideation (10 questions)
17. **passive_suicide_reflective** - Questions for existential/meaning-based callers (10 questions)
18. **passive_suicide_intoxicated** - Questions when substance use is involved (8 questions)
19. **passive_suicide_third_party** - Questions when a third party reports concern (10 questions)

## Recent Suicidal Thoughts (99 questions)
20. **recent_suicidal_thoughts** - Questions about recent suicidal thoughts (last few days) (50 questions)
21. **recent_suicidal_thoughts_continued** - Additional questions for recent suicidal thoughts (49 questions)

## Question Properties

Each question includes:
- `id`: Unique identifier (1-60 for intrusive thoughts, 1001-1062 for active risk, 2001-2088 for passive risk, 3001-3099 for recent thoughts)
- `question`: The question text
- `tone`: Communication style (direct, gentle, reflective, grounding, urgent, metaphorical, humorous, creative, poetic, pop_culture)
- `risk_level`: Assessment clarity level (high-clarity, moderate-checkin, low-indirect)
- `escalation_tier`: Priority level (1=low, 2=medium, 3=high)
- `use_after_rapport`: Whether question requires established rapport
- `notes`: Clinical guidance for use

## Usage

### Java Example

```java
import com.suicide.questionbank.QuestionBankManager;
import com.suicide.questionbank.Question;
import java.io.IOException;
import java.util.List;

// Initialize the manager
QuestionBankManager manager = new QuestionBankManager("suicide_question_bank.json");

// Get questions for a specific situation
List<Question> questions = manager.getQuestionsForSituation("attempt_in_progress", false);

// Get high priority questions (Tier 3)
List<Question> highPriority = manager.getHighPriorityQuestions("attempt_in_progress");

// Filter questions by multiple criteria
List<Question> filtered = manager.filterQuestions(
    "adolescent",  // category
    3,             // escalation tier
    "high-clarity", // risk level
    null,          // tone (null for all)
    null,          // use after rapport (null for all)
    false          // require rapport
);

// Get statistics
QuestionBankStatistics stats = manager.getStatistics();
```

### Command Line

```bash
# Compile and run
mvn compile
mvn exec:java -Dexec.mainClass="com.suicide.questionbank.QuestionBankManager"
# Or run examples
mvn exec:java -Dexec.mainClass="com.suicide.questionbank.ExampleUsage"
```

## LLM Integration

The chatbot uses **Retrieval-Augmented Generation (RAG)** to provide personalized, conversational responses:

1. **Retrieval**: Analyzes user messages to find relevant resources and questions
2. **Augmentation**: Provides this context to the LLM
3. **Generation**: LLM generates empathetic, personalized responses

### Quick Setup

1. **Get an API key** from your LLM provider (OpenAI, Vanderbilt.ai, etc.)
2. **Configure** in `src/main/resources/application.properties`:
   ```properties
   llm.api.key=your-api-key-here
   llm.api.endpoint=https://api.openai.com/v1/chat/completions
   llm.model=gpt-3.5-turbo
   ```
3. **Restart** the application

### Configuration

The chatbot automatically uses LLM if an API key is configured, otherwise falls back to rule-based responses.

**OpenAI Setup:**
1. Get API key from: https://platform.openai.com/api-keys
2. Add billing if needed: https://platform.openai.com/account/billing
3. Configure in `application.properties` (see file for details)

**Local LLM (Ollama):**
- Install Ollama: https://ollama.ai/download
- Run: `ollama pull llama3.2:1b`
- Update `application.properties` to use Ollama endpoint

See `DEVELOPMENT_WORKFLOW.md` for current development workflow.

## Important Notes

- **Use with professional training**: These questions are tools for trained mental health professionals
- **Not a replacement for clinical judgment**: Always use professional assessment alongside these questions
- **Emergency situations**: In immediate risk situations, contact emergency services (911 or local crisis line)
- **Cultural sensitivity**: Adapt questions as needed for cultural context and individual needs
- **LLM responses**: LLM-generated responses should be reviewed by professionals and are not a substitute for human judgment

## Crisis Resources

The project includes a comprehensive resource database for crisis intervention and referral. Resources include:

- **Mental Health Services**: Counseling, therapy, crisis centers
- **Domestic & Sexual Violence**: Shelters, hotlines, support services
- **Substance Use**: Treatment programs, support groups
- **Medical Services**: Clinics, dental care, health services
- **Social Services**: Housing, transportation, food assistance
- **Legal Services**: Legal aid, advocacy
- **And more**: Hundreds of resources across Tennessee and surrounding areas

### Resource Manager Usage

```java
import com.suicide.questionbank.ResourceManager;
import com.suicide.questionbank.Resource;
import java.io.IOException;
import java.util.List;

// Initialize the resource manager
ResourceManager manager = new ResourceManager("resources_full.json");

// Get all resources
List<Resource> allResources = manager.getAllResources();

// Get resources by category
List<Resource> mentalHealth = manager.getResourcesByCategory("Mental Health Services");

// Get resources by county
List<Resource> davidson = manager.getResourcesByCounty("TN - Davidson County");

// Search by name
List<Resource> searchResults = manager.searchByName("Centerstone");

// Filter by multiple criteria
List<Resource> filtered = manager.filterResources(
    "Mental Health Services",  // category
    "TN - Davidson County",     // county
    "crisis"                   // search term
);

// Get statistics
int total = manager.getTotalResources();
Set<String> categories = manager.getAllCategories();
Set<String> counties = manager.getAllCounties();
```

## File Structure

```
suicide_question_bank/
├── pom.xml                       # Maven build configuration
├── README.md                     # This file
├── DEVELOPMENT_WORKFLOW.md        # Development workflow guide
├── install_maven.ps1             # Maven installation script
├── run.bat                       # Run application script
├── setup_ngrok.ps1               # ngrok setup for sharing
├── setup_ollama.ps1              # Ollama local LLM setup
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── suicide/
        │           └── questionbank/
        │               ├── Question.java                 # Question POJO class
        │               ├── QuestionBankStatistics.java   # Statistics POJO class
        │               ├── QuestionBankManager.java       # Java management class
        │               ├── Resource.java                 # Resource POJO class
        │               ├── ResourceManager.java           # Resource management class
        │               ├── ExampleUsage.java             # Java usage examples
        │               ├── SPCchatbotDemo.java           # Chatbot resource retrieval
        │               ├── LLMService.java               # LLM API integration service
        │               ├── ChatService.java              # Conversational chat service with RAG
        │               ├── ChatbotController.java        # Web controller for chatbot
        │               └── CrisisChatbotApplication.java  # Spring Boot application
        └── resources/
            ├── application.properties                   # Configuration (LLM API keys, etc.)
            ├── suicide_question_bank.json                # Question bank data (classpath)
            ├── resources_full.json                       # Crisis resources database
            └── templates/
                └── index.html                           # Web interface
```

## Building and Running

### Web Application (Recommended)

The project includes a web interface that you can access in your browser:

```bash
# Install dependencies and compile
mvn clean compile

# Run the web application
mvn spring-boot:run

# Or package and run
mvn package
java -jar target/question-bank-1.0.0.jar
```

Once running, open your browser and navigate to:
- **http://localhost:8080**

The web interface includes:
- **Chatbot Tab**: Conversational AI chatbot that provides personalized responses using LLM integration
  - Uses Retrieval-Augmented Generation (RAG) to respond based on relevant resources and questions
  - Supports OpenAI and other OpenAI-compatible APIs
  - Automatically falls back to rule-based responses if LLM is unavailable
- **Questions Tab**: Browse and filter questions by category, tier, risk level
- **Resources Tab**: Search resources by category, county, or name

### Command Line

```bash
# Install dependencies and compile
mvn clean compile

# Run the main class
mvn exec:java -Dexec.mainClass="com.suicide.questionbank.QuestionBankManager"

# Run examples
mvn exec:java -Dexec.mainClass="com.suicide.questionbank.ExampleUsage"

# Package as JAR
mvn package
```

