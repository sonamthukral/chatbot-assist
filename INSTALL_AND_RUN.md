# Complete Installation and Run Guide

## Step 1: Install Java JDK

### Quick Install:
1. **Download Java**: 
   - Go to: https://adoptium.net/temurin/releases/
   - Choose: **JDK 17** or **JDK 21** (LTS versions)
   - Under "Windows" column, click the **.msi** download link
   - File size: ~200-300 MB

2. **Install Java**:
   - Double-click the downloaded `.msi` file
   - Click **Next** through the wizard
   - **IMPORTANT**: Make sure "Add to PATH" is checked
   - Click **Install**
   - Wait for completion
   - Click **Finish**

3. **Verify Installation**:
   - **Close Cursor completely**
   - **Reopen Cursor**
   - Open terminal: Press `` Ctrl+` ``
   - Type: `java -version`
   - You should see version info (e.g., "openjdk version 17.0.x")

## Step 2: Install Java Extension Pack in Cursor

1. **Open Extensions**:
   - Press `Ctrl+Shift+X` in Cursor

2. **Install Extension**:
   - Search for: **"Extension Pack for Java"**
   - Author: **Microsoft**
   - Click **Install**
   - Wait for installation (may take a few minutes)
   - Restart Cursor if prompted

3. **Verify Extension**:
   - Look at bottom-right corner of Cursor for Java icon
   - If it shows an error, click it to see details

## Step 3: Run the Application

### Method 1: Use Run Button (Easiest)
1. Open `src/main/java/com/suicide/questionbank/CrisisChatbotApplication.java`
2. Look above the `main` method for a green ▶️ **Run** button
3. Click it!
4. Or right-click in the file → **"Run Java"**

### Method 2: Command Palette
1. Press `Ctrl+Shift+P`
2. Type: **"Java: Run Java"**
3. Select your main class

## Step 4: Access the Website

Once you see "Started CrisisChatbotApplication" in the terminal:
- Open your browser
- Go to: **http://localhost:8080**

## Troubleshooting

**Java not recognized after installation:**
- Make sure you restarted Cursor
- Check PATH: Press `Win + R`, type `sysdm.cpl`, go to Advanced → Environment Variables
- Verify Java is in PATH: `C:\Program Files\Eclipse Adoptium\jdk-XX\bin`

**No Run button appears:**
- Make sure Java Extension Pack is installed
- Restart Cursor
- Press `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"
- Restart Cursor again

**Port 8080 already in use:**
- Another application is using port 8080
- We can change the port if needed

