# Fixing "No JRE Installed" Errors in Cursor

## ✅ What I've Fixed

1. **Created `.vscode/settings.json`** with the correct Java path
2. **Removed deprecated settings** that were causing warnings

## 🔧 Next Steps to Fix JRE Errors

### Step 1: Reload Cursor Window
1. Press `Ctrl+Shift+P` (Command Palette)
2. Type: **"Developer: Reload Window"**
3. Press Enter
4. Wait for Cursor to reload

### Step 2: Install Java Extension Pack (If Not Already Installed)
1. Press `Ctrl+Shift+X` to open Extensions
2. Search for: **"Extension Pack for Java"** (by Microsoft)
3. Click **Install** if not already installed
4. Wait for installation to complete

### Step 3: Check Java Extension Status
1. Look at the **bottom-right corner** of Cursor
2. You should see a Java icon (coffee cup ☕)
3. Click it to see Java status
4. If it shows an error, click to see details

### Step 4: Clean Java Language Server Workspace
1. Press `Ctrl+Shift+P`
2. Type: **"Java: Clean Java Language Server Workspace"**
3. Press Enter
4. Click **Restart and Delete** when prompted
5. Wait for Java extension to reinitialize

### Step 5: Verify Java Detection
1. Open `CrisisChatbotApplication.java`
2. Look for a green ▶️ **Run** button above the `main` method
3. If you see it, Java is detected correctly!

## 🚀 Running the Application

### Option 1: Use Run Button (Easiest - No Maven Needed)
1. Open `src/main/java/com/suicide/questionbank/CrisisChatbotApplication.java`
2. Click the green ▶️ **Run** button above `main` method
3. Or right-click → **"Run Java"**
4. Wait for "Started CrisisChatbotApplication" message
5. Open: **http://localhost:8080**

### Option 2: Use Command Palette
1. Press `Ctrl+Shift+P`
2. Type: **"Java: Run Java"**
3. Select `CrisisChatbotApplication`
4. Wait for startup
5. Open: **http://localhost:8080**

## ❓ Still Having Issues?

If you still see "No JRE installed" errors:

1. **Check Java is in PATH:**
   - Open terminal in Cursor (`Ctrl+` `)
   - Type: `java -version`
   - Should show: `openjdk version "25.0.1"`

2. **Verify settings.json:**
   - File: `.vscode/settings.json`
   - Should contain path: `C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot`

3. **Restart Cursor completely:**
   - Close all Cursor windows
   - Reopen Cursor
   - Open the project again

4. **Check Java Extension Logs:**
   - Press `Ctrl+Shift+P`
   - Type: **"Java: Show Java Extension Log"**
   - Look for errors or warnings

## 📝 Current Configuration

Your `.vscode/settings.json` is configured with:
- Java Path: `C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot`
- Java Version: 25.0.1

This should work once Cursor reloads and detects Java properly.

