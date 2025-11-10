# Setting Up Cursor to Run the Spring Boot Application

## Step 1: Install Java JDK

1. Download Java JDK 11 or later from:
   - **Oracle**: https://www.oracle.com/java/technologies/downloads/
   - **OpenJDK**: https://adoptium.net/ (Recommended - free)

2. Install Java JDK

3. Add Java to PATH:
   - Find where Java was installed (usually `C:\Program Files\Java\jdk-XX`)
   - Add `C:\Program Files\Java\jdk-XX\bin` to your PATH environment variable
   - Restart Cursor

4. Verify installation:
   ```bash
   java -version
   ```

## Step 2: Install Maven

1. Download Maven from: https://maven.apache.org/download.cgi
   - Download the `apache-maven-X.X.X-bin.zip` file

2. Extract to a folder (e.g., `C:\Program Files\Apache\maven`)

3. Add Maven to PATH:
   - Add `C:\Program Files\Apache\maven\bin` to your PATH environment variable
   - Restart Cursor

4. Verify installation:
   ```bash
   mvn -version
   ```

## Step 3: Install Cursor/VS Code Extensions

1. Open Cursor
2. Go to Extensions (Ctrl+Shift+X)
3. Install these extensions:
   - **Extension Pack for Java** (by Microsoft)
   - **Maven for Java** (by Microsoft)
   - **Spring Boot Extension Pack** (by VMware)

## Step 4: Run the Application

Once Java and Maven are installed:

1. Open the terminal in Cursor (Ctrl+`)
2. Run:
   ```bash
   mvn spring-boot:run
   ```

3. Wait for it to start (you'll see "Started CrisisChatbotApplication")

4. Open your browser to: http://localhost:8080

## Alternative: Use Cursor's Run Button

1. Open `CrisisChatbotApplication.java`
2. Click the "Run" button that appears above the `main` method
3. Or right-click and select "Run Java"

## Troubleshooting

If you get "command not found" errors:
- Make sure Java and Maven are in your PATH
- Restart Cursor after adding to PATH
- Check with `java -version` and `mvn -version` in terminal

