# How to Make Your Local Site Public

You have several options to make your localhost site accessible to the public:

---

## Option 1: ngrok (Quick & Easy - Temporary) ⚡

**Best for:** Quick sharing, testing, demos  
**Cost:** Free (with limitations)  
**Duration:** Temporary (URL changes when you restart)

### Steps:

1. **Sign up for ngrok** (free):
   - Go to: https://dashboard.ngrok.com/signup
   - Create a free account

2. **Get your authtoken**:
   - Go to: https://dashboard.ngrok.com/get-started/your-authtoken
   - Copy your authtoken

3. **Install ngrok**:
   - Download: https://ngrok.com/download
   - Or run: `.\setup_ngrok.ps1` (if you have the script)

4. **Configure ngrok**:
   ```powershell
   ngrok config add-authtoken YOUR_AUTH_TOKEN_HERE
   ```

5. **Start the tunnel** (while your app is running on localhost:8080):
   ```powershell
   ngrok http 8080
   ```

6. **Get your public URL**:
   - ngrok will show a URL like: `https://abc123.ngrok-free.app`
   - Share this URL with anyone!
   - **Note:** Free URLs change each time you restart ngrok

### Limitations:
- Free tier: URL changes on restart
- Free tier: May show ngrok branding page
- Your computer must be on and app running

---

## Option 2: Deploy to Cloud (Permanent) 🌐

**Best for:** Production, permanent public site  
**Cost:** Free tier available on most platforms  
**Duration:** Permanent (until you delete it)

### Recommended Platforms:

#### A. Railway (Easiest) ⭐

1. **Sign up**: https://railway.app
2. **Connect GitHub**:
   - Push your code to GitHub
   - Connect Railway to your GitHub repo
3. **Deploy**:
   - Railway auto-detects Spring Boot
   - Sets environment variables
   - Deploys automatically
4. **Set API Key**:
   - Go to Variables tab
   - Add: `LLM_API_KEY` = your OpenAI key
5. **Get URL**: Railway gives you a permanent URL like `yourapp.railway.app`

**Cost:** Free tier available, then ~$5/month

#### B. Render (Free Tier)

1. **Sign up**: https://render.com
2. **New Web Service**:
   - Connect GitHub repo
   - Build command: `mvn clean package`
   - Start command: `java -jar target/question-bank-1.0.0.jar`
3. **Environment Variables**:
   - Add: `LLM_API_KEY` = your OpenAI key
4. **Deploy**: Render builds and deploys automatically

**Cost:** Free tier (spins down after inactivity), then ~$7/month

#### C. Heroku (Classic)

1. **Sign up**: https://heroku.com
2. **Install Heroku CLI**: https://devcenter.heroku.com/articles/heroku-cli
3. **Deploy**:
   ```bash
   heroku login
   heroku create your-app-name
   git push heroku main
   heroku config:set LLM_API_KEY=your-key-here
   ```
4. **Get URL**: `yourapp.herokuapp.com`

**Cost:** No free tier anymore, ~$5-7/month

---

## Option 3: VPS/Server (Full Control) 🖥️

**Best for:** Full control, custom domain  
**Cost:** $5-20/month  
**Duration:** Permanent

### Steps:

1. **Rent a VPS**:
   - DigitalOcean: https://digitalocean.com ($6/month)
   - Linode: https://linode.com ($5/month)
   - AWS EC2: https://aws.amazon.com/ec2

2. **Set up server**:
   - Install Java 11+
   - Install Maven
   - Clone your code

3. **Deploy**:
   ```bash
   mvn clean package
   java -jar target/question-bank-1.0.0.jar
   ```

4. **Configure firewall**:
   - Open port 8080 (or use reverse proxy like nginx)

5. **Set up domain** (optional):
   - Point domain to server IP
   - Use nginx for SSL/HTTPS

---

## Quick Comparison

| Option | Setup Time | Cost | Permanent? | Best For |
|--------|------------|------|------------|----------|
| **ngrok** | 5 min | Free | ❌ No | Quick sharing, demos |
| **Railway** | 10 min | Free/$5 | ✅ Yes | Easy deployment |
| **Render** | 10 min | Free/$7 | ✅ Yes | Free tier available |
| **Heroku** | 15 min | $5-7 | ✅ Yes | Classic platform |
| **VPS** | 30+ min | $5-20 | ✅ Yes | Full control |

---

## My Recommendation

### For Quick Sharing (Now):
**Use ngrok** - Takes 5 minutes, free, works immediately

### For Permanent Site:
**Use Railway** - Easiest setup, auto-detects Spring Boot, free tier available

---

## Next Steps

**Want to use ngrok now?** I can help you set it up!  
**Want to deploy to cloud?** I can guide you through Railway or Render!

Which option do you prefer?

