# Expense Tracker (Offline Android App)

A simple, fully offline expense tracker built with Kotlin, Jetpack Compose, and Room.
No internet permission, no server — all data is stored locally on the device.

## Features
- Add an expense (title + amount)
- View all expenses, newest first
- Delete an expense
- Running total shown at the top

## Option A: Get an APK without installing anything (GitHub Actions)
1. Create a new repository on [github.com](https://github.com) (public repos build for free).
2. Upload this entire folder to that repo (drag-and-drop on the GitHub website works, or use `git push`).
3. GitHub will automatically start a build (see the "Actions" tab). Wait for it to finish (a couple of minutes).
4. Open the finished run, scroll down to "Artifacts", and download `expense-tracker-debug-apk`. Unzip it — that's your `app-debug.apk`.
5. Transfer the APK to your Android phone (e.g. via Google Drive, email, or USB) and open it to install.
   You'll need to allow "Install unknown apps" for whichever app you use to open it — Android will prompt you.

## Option B: Build locally with Android Studio
1. Install [Android Studio](https://developer.android.com/studio) (free).
2. Open this folder as a project (File → Open).
3. Let Gradle sync, then click Run ▶️ with a connected device or emulator,
   or Build → Build Bundle(s)/APK(s) → Build APK(s) to get an installable file.

## Project structure
```
app/src/main/java/com/example/expensetracker/
  MainActivity.kt          # UI (Jetpack Compose)
  ExpenseViewModel.kt       # State + business logic
  data/Expense.kt           # Room entity
  data/ExpenseDao.kt         # Database queries
  data/ExpenseDatabase.kt   # Room database setup
  ui/theme/                 # Colors, typography
```

## Notes
- The debug APK is unsigned for release but perfectly installable/runnable — fine for personal use.
- Currency symbol is set to ₹ (INR) — change it in `MainActivity.kt` if you'd like a different one.
