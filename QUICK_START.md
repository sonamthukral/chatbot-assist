# Quick Start Guide - Running the Application in Cursor

## Option 1: Install Java Extension (Easiest)

1. **Install Java JDK first** (required):
   - Download from: https://adoptium.net/temurin/releases/
   - Choose: Windows x64, JDK 11 or later
   - Install it (default location is fine)

2. **Install Extension in Cursor**:
   - Press `Ctrl+Shift+X` to open Extensions
   - Search for: **"Extension Pack for Java"** (by Microsoft)
   - Click **Install**
   - Restart Cursor

3. **Find the Run Button**:
   - Open `CrisisChatbotApplication.java`
   - Look above the `main` method for a green ▶️ play button
   - Or right-click in the file → "Run Java"

## Option 2: Use Terminal (After Installing Java & Maven)

1. **Install Java JDK**: https://adoptium.net/
2. **Install Maven**: https://maven.apache.org/download.cgi
3. **Add both to PATH** (restart Cursor after)
4. **Open terminal in Cursor** (`Ctrl+` `)
5. **Run**: `mvn spring-boot:run`

## Option 3: Double-Click Batch File (After Installing Java & Maven)

1. Install Java and Maven (see above)
2. Double-click `run.bat` in the project folder

## Troubleshooting: No Run Button?

If you don't see the run button:

1. **Check if Java is installed**:
   - Open terminal in Cursor (`Ctrl+` `)
   - Type: `java -version`
   - If it says "not recognized", Java isn't installed

2. **Install Java Extension**:
   - `Ctrl+Shift+X` → Search "Extension Pack for Java" → Install

3. **Reload Cursor**:
   - `Ctrl+Shift+P` → Type "Reload Window" → Enter

4. **Check Java Extension Status**:
   - Look at bottom-right of Cursor for Java icon
   - If it shows an error, click it to see what's missing

## Still Having Issues?

The run button only appears when:
- ✅ Java JDK is installed
- ✅ Java Extension Pack is installed
- ✅ Cursor has detected Java

If you're still having trouble, you'll need to install Java JDK first - the extension can't work without it.

