# How to Install Java - Step by Step Guide

## ⚡ Quick Installation (5 minutes)

### Step 1: Download Java
1. Open your web browser
2. Go to: **https://adoptium.net/temurin/releases/**
3. You'll see a table with Java versions
4. Find **JDK 17** or **JDK 21** (LTS versions)
5. Under "Windows" column, click the **.msi** download link
6. The file will download (it's about 200-300 MB)

### Step 2: Install Java
1. Open the downloaded `.msi` file
2. Click **Next** through the installation wizard
3. **IMPORTANT**: Make sure "Add to PATH" or "Set JAVA_HOME" is checked
4. Click **Install**
5. Wait for installation to complete
6. Click **Finish**

### Step 3: Verify Installation
1. **Close Cursor completely** (important!)
2. **Reopen Cursor**
3. Open terminal in Cursor: Press `` Ctrl+` `` (backtick key)
4. Type: `java -version`
5. You should see something like:
   ```
   openjdk version "17.0.x" ...
   ```

### Step 4: Install Java Extension in Cursor
1. Press `Ctrl+Shift+X` to open Extensions
2. Search for: **"Extension Pack for Java"**
3. Click **Install** (it's by Microsoft)
4. Wait for it to install
5. Restart Cursor if prompted

### Step 5: Run Your Application
1. Open `CrisisChatbotApplication.java`
2. Look for the green ▶️ **Run** button above the `main` method
3. Click it!
4. Or right-click in the file → **"Run Java"**

## 🎯 Alternative: If You Don't See the Run Button

After installing Java and the extension:
1. Press `Ctrl+Shift+P`
2. Type: **"Java: Clean Java Language Server Workspace"**
3. Press Enter
4. Restart Cursor
5. The run button should appear

## 📝 What You'll See When It Works

When you click Run, you'll see output in the terminal like:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.18)

Started CrisisChatbotApplication in 3.456 seconds
```

Then open: **http://localhost:8080**

## ❓ Troubleshooting

**"java is not recognized"**
- Java isn't in your PATH
- Restart Cursor after installation
- Or manually add Java to PATH (see below)

**"No run button appears"**
- Make sure Java Extension Pack is installed
- Restart Cursor
- Check bottom-right corner for Java icon/status

**"Port 8080 already in use"**
- Something else is using port 8080
- We can change the port if needed

## 🔧 Manual PATH Setup (if needed)

If Java doesn't work after installation:

1. Find where Java was installed (usually `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`)
2. Copy that path
3. Press `Win + R`, type `sysdm.cpl`, press Enter
4. Click **Advanced** tab → **Environment Variables**
5. Under **System Variables**, find **Path**, click **Edit**
6. Click **New**, paste the Java path + `\bin` (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\bin`)
7. Click **OK** on all windows
8. **Restart Cursor**

---

**Once Java is installed, come back and I can help you run the application!** 🚀

