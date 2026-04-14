
# 🧠 HealthGenieAI

![Platform](https://img.shields.io/badge/platform-Android-green)
![Tech](https://img.shields.io/badge/Tech-Firebase%20%7C%20AI-blue)
![Status](https://img.shields.io/badge/status-Active-success)

<p align="center">
  <img src="banner.png" alt="HealthGenieAI Banner" width="100%" />
</p>

HealthGenieAI is an AI-powered health and fitness mobile application that helps users track their daily activities, monitor health metrics, and get intelligent recommendations for a healthier lifestyle.

---

## 📱 App Features

### 🔐 Authentication
- Firebase Login & Signup
- Email Verification Required
- Secure user access

---

### 🏃 Fitness Tracking
- Step Counter
- Calories Burned
- BMI Calculation
- Water Intake Tracker
- Goal Setting (Steps & Hydration)

---

### 🤖 AI Features
- AI Symptom Analysis
- AI Diet Plan Generator
- Personalized Recommendations

---

### ⏰ Health Utilities
- Medical Reminder (Notifications)
- Exercise Guidance
- Weekly Health Reports
- Nearby Medical Locations (Maps Integration)

---

## 🧩 Tech Stack

| Technology | Usage |
|----------|------|
| Kotlin | Android Development |
| Firebase Auth | Login/Signup |
| Firestore | User Data Storage |
| Firebase Storage | Profile Images |
| Firebase Analytics | User Tracking |
| Google Maps API | Nearby Locations |
| Retrofit | API Calls |
| Glide | Image Loading |

---


## 🏗️ System Architecture
```mermaid
flowchart TD
    A[User 👤] --> B[Android App]

    B --> C[Firebase Auth 🔐]
    B --> D[(Firestore DB ☁️)]
    B --> E[Storage 📦]

    B --> F[AI Engine\nSymptom + Diet 🤖]

    B --> G[Maps API 🗺️]
    B --> H[Notifications 🔔]
```

## 🔄 App Flow
```mermaid
flowchart TD
    A[🚀 Start App] --> B[✨ Splash Screen]
    B --> C[🔐 Login / Signup]
    C --> D[📧 Email Verification]
    D --> E[🏠 Dashboard]

    E --> F[🏃 Fitness Tracking]
    E --> G[🤖 AI Analysis]
    E --> H[🥗 Diet Plan]
    E --> I[⏰ Reminders]
    E --> J[📊 Reports]
    E --> K[🗺️ Maps]

```

## 📂 Project Structure
com.healthgenieai.app
│
├── models/
├── network/
├── ui/
│   ├── home/
│   ├── fitness/
│   ├── diet/
│   ├── chat/
│   ├── maps/
│   └── reminder/
│
├── utils/
│
├── LoginActivity.kt
├── SignUpActivity.kt
├── MainActivity.kt
└── SplashActivity.kt


## ⚙️ Setup Instructions

1. Clone the repository
```bash
git clone https://github.com/yourusername/HealthGenieAI.git

Open in Android Studio

Add API keys in local.properties

GEMINI_API_KEY=your_key
MAPS_API_KEY=your_key

Connect Firebase

Run the app

🔒 Permissions Used
Permission	Purpose
INTERNET	API Calls
ACTIVITY_RECOGNITION	Step Tracking
LOCATION	Nearby Hospitals
POST_NOTIFICATIONS	Reminders
BODY_SENSORS	Fitness Data
⚠️ Disclaimer

This app is intended for fitness and general wellness purposes only.
It does not provide medical advice, diagnosis, or treatment.

📧 Contact

Developer: Ananta Kumari
📩 Email: kumariananta01@gmail.com

⭐ Contribution

Feel free to fork this repo and contribute!

🚀 Future Improvements

Wearable Integration

Advanced AI Health Reports

Chatbot Assistant

Cloud Sync Improvements



