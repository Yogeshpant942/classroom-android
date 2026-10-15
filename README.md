# School Management System (SMS) - Kotlin Jetpack Compose & Node.js

This project is a 1:1 mobile transformation of the web-based School Management System (`Practicum`), re-architected with:
- **Frontend**: Native Android with **Kotlin**, **Jetpack Compose**, **Material Design 3**, **Retrofit 2**, **Coroutines & StateFlow**.
- **Backend**: **Node.js** with **Express.js**, **Mongoose (MongoDB)**, and **JWT / bcrypt**.

---

## 📁 Project Structure

```
d:\new_project\
├── android\                    # Android Studio Jetpack Compose Project
│   ├── app\
│   │   ├── build\outputs\apk\debug\app-debug.apk   # Pre-built, ready-to-install debug APK!
│   │   └── src\main\java\com\practicum\schoolmanagement\
│   │       ├── data\
│   │       │   ├── api\        # Retrofit endpoints & Base URL configurator
│   │       │   ├── model\      # Full data models (Admin, Teacher, Student, Class, Quiz, etc.)
│   │       │   └── repository\ # StateFlow repository & session manager
│   │       ├── ui\
│   │       │   ├── admin\      # Admin dashboards & management tabs
│   │       │   ├── teacher\    # Teacher dashboards, attendance & quiz creator
│   │       │   ├── student\    # Student portal, interactive quiz & attendance calculator
│   │       │   ├── auth\       # Role picker, login & admin registration
│   │       │   ├── components\ # Reusable Material 3 cards, dialogs, badges
│   │       │   ├── navigation\ # Compose Navigation routing
│   │       │   └── theme\      # Modern Indigo/Violet styling & typography
│   │       ├── MainActivity.kt # Entry point
│   │       └── SchoolApp.kt    # Application singleton
│   └── build.gradle.kts
│
├── backend\                    # Node.js + Express + MongoDB REST API
│   ├── controllers\            # admin, teacher, student, notice, complain, etc.
│   ├── models\                 # Mongoose schemas matching original functionality
│   ├── routes\                 # REST endpoints
│   ├── index.js                # Server entry point (Port 5000)
│   └── .env                    # PORT=5000, MONGO_URL, SECRET_KEY
│
├── mongodb_data\               # Local MongoDB database storage on D: drive
├── run_mongodb.bat             # 1-Click MongoDB launcher
├── run_backend.bat             # 1-Click Node.js backend launcher
├── install_apk.bat             # 1-Click ADB installer & app launcher
└── README.md                   # Complete documentation
```

---

## 🚀 How to Run the Project

### Step 1: Start MongoDB
Double-click `run_mongodb.bat` or run in terminal:
```cmd
d:\new_project\run_mongodb.bat
```
*Note: This automatically stores data on `D:\new_project\mongodb_data`, safely avoiding C: drive storage limits.*

### Step 2: Start the Node.js Backend Server
Double-click `run_backend.bat` or run in terminal:
```cmd
d:\new_project\run_backend.bat
```
*The server will start listening at `http://localhost:5000` (or `http://10.0.2.2:5000` for Android emulator).*

### Step 3: Run the Android App

#### Option A: 1-Click Install to Connected Phone / Emulator
Connect your Android phone with USB debugging enabled (or open an Android emulator), then run:
```cmd
d:\new_project\install_apk.bat
```

#### Option B: Open in Android Studio
1. Open **Android Studio**.
2. Select **Open** and choose `d:\new_project\android`.
3. Click the green **Run (▶)** button.

---

## 📱 Feature Mapping (Web vs Kotlin Jetpack Compose)

| Feature | Original Web | Jetpack Compose Android App |
| :--- | :--- | :--- |
| **Authentication** | Multi-role dropdown login | `ChooseUserScreen`, `LoginScreen`, `AdminRegisterScreen` with demo autofill |
| **Server IP Settings** | Hardcoded `localhost` | Dynamic Server Config dialog (Switch between `10.0.2.2` emulator & LAN IP `192.168.x.x` without recompiling) |
| **Admin Portal** | MUI Tables, Sidebar | Compose Navigation Rail / Top Tabs with real-time stats cards |
| **Class & Section** | Add/View Classes | Class list, section details, student counter, one-touch class deletion |
| **Teacher Management**| Register & assign subject | Teacher cards, assigned classes, subject links, delete actions |
| **Student Roster** | Student table & registration | Roll number sorting, class filter, attendance rate chips |
| **Attendance** | Table checkmarks | Two modes: Individual student attendance & **Bulk Class Attendance** with Present/Absent/Late switches |
| **Gemini AI Quiz** | Web quiz form | AI Quiz Generator with Gemini topic input + manual question creator |
| **Student Quiz Taking**| Web quiz test | Interactive timed quiz engine with option selection, immediate scoring & review |
| **Attendance Calculator**| Percentage display | Smart Attendance Goal Calculator (calculates classes required to reach 75%) |
| **Homework / Assignments**| Upload / list | Assignment creation with due dates, submission viewer |
| **Notices & Bulletins**| Notice board | Announcement board with timestamp cards |
| **Grievance / Complains**| Complain register | Multi-party complaint filing & admin resolution tracker |
| **RAG Campus Study Tutor**| *New AI Feature* | RAG-powered student tutor retrieving grounded answers from class notes with citations |
| **RAG Smart Quiz Generator**| *New AI Feature* | Grounded MCQ generation from course notes with citation verification & 1-click publishing |
| **Policy & Notice Helpdesk**| *New AI Feature* | Multi-role RAG bot answering questions over school circulars and standing rules |
| **AI Rubric Grading**| *New AI Feature* | Automated rubric assignment evaluation, criterion score breakdown & student pre-check |

---

## 🔑 Demo & Test Credentials

- **Admin Registration**: You can register your own school directly from the mobile app (or use one-click "Fill Demo Admin").
- **Teacher Login**: Log in with any registered teacher name & school code, or create one in the Admin dashboard.
- **Student Login**: Log in with student's Name, Roll Number, and School Code.
