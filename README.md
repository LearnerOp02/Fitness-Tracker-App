# 🏋️ Fitness & Activity Tracker with Personalized Workout Plans

A full-stack Android fitness tracking application that helps users monitor workouts, track progress, calculate BMI, and receive adaptive workout plans based on behavior and consistency.

---

## 📌 Project Overview

The **Fitness & Activity Tracker App** is designed to help users maintain discipline and achieve fitness goals through structured tracking and intelligent workout plan adjustments.

This project is developed as an academic mini-project using:

* **Frontend:** Android Studio (Java)
* **Backend:** Spring Boot (REST APIs)
* **Database:** MySQL (XAMPP for local development)

The system avoids complex AI/ML and external hardware dependencies, making it fully feasible and suitable for academic evaluation.

---

## 🚀 Features

### 🔐 1. User Registration & Login

* Secure authentication
* Password encryption using BCrypt
* JWT/session-based authentication (optional)

### 👤 2. User Profile Management

* Age, height, weight, gender
* Fitness goals
* Used for BMI and personalization

### 🏃 3. Workout Logging

* Add workouts (type, duration, intensity)
* Track daily activity

### 📊 4. Workout History

* View daily and weekly workout records
* Persistent storage using MySQL

### 📈 5. Monthly Progress Summary

* Total workouts
* Total duration
* Consistency percentage
* Visualized using charts (Bar/Line)

### ⚖ 6. BMI Calculator

* Automatic BMI calculation
* Categorizes underweight/normal/overweight/obese

### 📝 7. Personalized Workout Plans

* Generated using BMI and fitness goals
* Supports Beginner & Intermediate levels

---

## 💡 Innovative Features

### 🔄 Adaptive Workout Plan Engine

* Adjusts workout plan based on:

  * Missed workouts
  * Consistency
  * Recent activity
* Acts like a virtual coach

### 🏅 Fitness Discipline Score

* Calculated using:

  * Completion rate
  * Workout streaks
  * Goal achievement
* Encourages long-term discipline

### 📸 Monthly Visual Progress Tracking

* Upload before/after body photos
* Compare with workout statistics

---

## 🏗 System Architecture

Android App (Frontend)
⬇
Spring Boot REST APIs (Backend)
⬇
MySQL Database (Data Storage)

The Android application communicates with backend APIs using HTTP requests (JSON format).

---

## 🗂 Project Modules

1. Authentication Module
2. Profile Management Module
3. Workout Tracking Module
4. Progress Analytics Module
5. Adaptive Planning Module
6. Discipline Scoring Module

---

## 🛠 Tech Stack

| Layer    | Technology Used            |
| -------- | -------------------------- |
| Frontend | Android Studio (Java)      |
| Backend  | Spring Boot (Java)         |
| Database | MySQL (XAMPP)              |
| API Type | RESTful APIs               |
| Security | BCrypt Password Encryption |

---

## 🗄 Database Design (Major Tables)

* users
* user_profile
* workouts
* workout_plans
* discipline_scores
* progress_photos

Relational mapping ensures secure and structured data storage.

---

## 📱 UI Flow

Splash Screen
→ Login / Register
→ Profile Setup
→ Dashboard
→ Workout / Progress / Plans / Settings

---

## 🔐 Security Implementation

* Encrypted passwords (BCrypt)
* User-specific data isolation
* Backend validation
* REST API-based secure communication

---

## 🎯 Project Feasibility

* Fully implementable using Android + Spring Boot + MySQL
* No external devices required
* No AI/ML dependency
* Suitable for academic mini-project and viva explanation

---

## 📌 Future Enhancements

* Cloud deployment (AWS/Heroku)
* Social sharing features
* Diet tracking integration
* Wearable device integration
* Advanced analytics dashboard

---

## 👨‍💻 Author

Developed as an academic project for mini-project evaluation.

---

## 📄 License

This project is developed for educational purposes.
