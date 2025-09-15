# 📱 QRunch – Mess Management App

QRunch is an Android app that streamlines hostel mess management using **Google Sign-In, Firebase Auth, Firestore, and QR codes**.

---

## Features

- Google Sign-In with Firebase Auth  
- Profile creation & editing (name + roll number)  
- QR code generation & display  
- Vendor menu popup dialog  
- Sign out & account deletion (with re-authentication)  
- Cache busting for fresh QR/menu images  

---

## Tech Stack

- **Language**: Java (Android)  
- **Auth**: Google Sign-In + Firebase Auth  
- **Database**: Firebase Firestore  
- **UI**: Material Design, XML layouts  

## Setup

1. Clone the repo and open in **Android Studio**  
2. Add `google-services.json` from Firebase to `app/`  
3. Replace Google Client ID in `LoginActivity.java`  
4. Sync Gradle & run on device  