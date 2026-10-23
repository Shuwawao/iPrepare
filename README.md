# iPrepare — Disaster Preparedness & Management Mobile App

[![Build & Deploy iPrepare APK](https://github.com/Shuwawao/iPrepare/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/Shuwawao/iPrepare/actions/workflows/build-and-release.yml)
[![Download APK](https://img.shields.io/badge/Download-iPrepare.apk-00796B?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Shuwawao/iPrepare/releases/latest/download/iPrepare.apk)

**iPrepare** is a comprehensive, production-grade Android mobile application tailored for the Philippines, designed to empower citizens and communities with disaster readiness guides, interactive emergency checklists, knowledge assessments, and rapid emergency dispatch tools.

---

## 📲 Download & Install on Your Phone

You can download and install the app directly on your Android phone without needing a computer:

1. Tap **[Download iPrepare.apk](https://github.com/Shuwawao/iPrepare/releases/latest/download/iPrepare.apk)** on your phone.
2. When the download finishes, tap the notification or open your phone's **Downloads** folder.
3. If prompted with *"Install unknown apps"*, tap **Settings** $\rightarrow$ enable **Allow from this source**.
4. Tap **Install** and you're ready to go!

---

## ✨ Key Features

- **🛡️ Disaster Preparedness Modules:** In-depth educational guides and downloadable offline PDFs for:
  - 🌋 **Earthquakes** (Drop, Cover, and Hold on; evacuation protocols)
  - 🌊 **Floods** (Water rise signals, survival prep, hazard avoidance)
  - 🔥 **Fires** (P.A.S.S. extinguisher method, smoke evasion, escape plans)
  - 🌪️ **Typhoons** (PAGASA storm warnings, roof security, emergency bag)
- **📋 Interactive Emergency Supply Checklists:** Track go-bag readiness with persistent checklist tracking and dynamic completion progress percentages.
- **🧠 Knowledge Quizzes:** Practice scenarios with interactive response feedback and profile progress tracking.
- **☎️ One-Tap Emergency Hotlines:** Direct dialers for national emergency agencies:
  - **911** — Unified Nationwide Emergency Dispatch (Police, Fire, Ambulance)
  - **NDRRMC** — Disaster Risk Reduction & Calamity Coordination
  - **PNP (117)** — Philippine National Police
  - **BFP** — Bureau of Fire Protection
  - **DOH** — Department of Health
- **📢 Real-Time Community Bulletins:** Live updates and weather advisories powered by Cloud Firestore.
- **⚙️ Administrator Console:** Official role-based broadcasting console for posting disaster announcements.

---

## 🛠️ Technical Stack

- **Platform:** Native Android (Min SDK 24 / Target SDK 34)
- **Languages:** Java & Kotlin
- **Architecture:** Android Jetpack with ViewBinding
- **Design System:** Material Components 3 with responsive semantic disaster theming
- **Backend Services:**
  - Firebase Authentication (Citizen & Admin role routing)
  - Cloud Firestore (Live announcements, quiz questions & score history)
  - Firebase Realtime Database
  - Firebase Storage (Offline emergency PDF guides)
- **CI/CD:** Automated GitHub Actions build pipeline deploying release APKs on push.

---

## 💻 Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Shuwawao/iPrepare.git
   ```
2. Open the project in **Android Studio** (Hedgehog or newer recommended).
3. Ensure JDK 17 is configured under **Settings $\rightarrow$ Build, Execution, Deployment $\rightarrow$ Build Tools $\rightarrow$ Gradle**.
4. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```
