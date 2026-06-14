# Kouleej! A207503_AttarasyaAdyaJomantara_CikguIzwan_Project2

A Jetpack Compose quiz-builder app for **SDG 4: Quality Education**. Kouleej! lets
learners create their own quizzes ("Kouleejes"), study from live trivia pulled off
the internet, share quizzes with a community in the cloud, and open a quiz instantly
by scanning a QR code with the camera.

> **Course:** TK2323 / TM2213 — Mobile Programming
> **Student:** Attarasya Adya Jomantara (A207503)
> **Instructor:** Cikgu Izwan
> **Programme:** _TODO: add your programme_
> **SDG:** SDG 4 — Quality Education · *"Making it easy for anyone to create, share, and study from quizzes, anywhere."*

---

## SDG theme

This project continues the Project 1 theme of **SDG 4 (Quality Education)**. Kouleej!
lowers the barrier to creating and sharing study material: a learner can build a quiz
offline, pull fresh practice questions from a free public API, publish a quiz to a
shared community, and let classmates join a quiz simply by scanning a QR code.

---

## The four technical pillars

| Pillar | Technology | Where it lives in the code |
|--------|-----------|----------------------------|
| 1. Local persistence | **Room** | `data/KouleejEntity.kt`, `data/KouleejDao.kt`, `data/KouleejDatabase.kt`, `data/KouleejRepository.kt` |
| 2. Cloud integration | **Firebase Firestore** | `data/firebase/CommunityKouleej.kt`, `data/firebase/CommunityRepository.kt` |
| 3. Internet data | **Retrofit** → [Open Trivia DB](https://opentdb.com) | `data/network/TriviaApiService.kt`, `data/network/TriviaModels.kt`, `data/network/TriviaRepository.kt` |
| 4. Sensor | **Camera + ML Kit barcode scanning** (+ ZXing QR generation) | `QrScannerScreen.kt`, `util/QrCodeGenerator.kt` |

Shared state across all screens is held in a single **ViewModel** (`KouleejViewModel.kt`),
which talks to each pillar through its repository. Dependencies are wired by a small
manual DI container (`data/AppContainer.kt` + `AppViewModelProvider.kt`).

---

## Screens (9 total, minimum was 7)

1. **Home**: search, categories, and quick actions into the new features
2. **Profile Setup**: form to enter learner profile
3. **Profile Card**: displays the saved profile
4. **Library**: lists locally saved Kouleejes (Room)
5. **Create Kouleej**: form that inserts a new quiz into Room
6. **Kouleej Detail**: quiz details, **Share via QR code**, and **Share to community** (Firestore)
7. **Discover**: live trivia questions fetched from Open Trivia DB (Retrofit)
8. **Community**: quizzes shared by everyone, streamed live from Firestore
9. **QR Scanner**: camera scans a Kouleej QR and opens that quiz (Camera + ML Kit)

---

## Feature list

- Create, view, and delete personal quizzes that survive app restarts (Room).
- Browse live multiple-choice questions by educational category, tap to reveal answers (Retrofit).
- Publish a quiz to a shared cloud collection and see all community quizzes update in real time (Firestore).
- Generate a QR code for any quiz and scan it with the camera to jump straight to it (ML Kit + ZXing).
- Light/dark theme toggle, single shared ViewModel, Navigation Compose flow.

---

## Setup instructions

### Prerequisites
- Android Studio (latest stable), Android SDK with **minSdk 26+**
- A device or emulator with a camera (for QR scanning)

### Run the app
1. Clone the repo and open it in Android Studio.
2. Let Gradle sync (downloads Room, Retrofit, Firebase, CameraX, ML Kit, ZXing).
3. Click **Run ▶**.

Room, Retrofit, and the camera work out of the box. Firebase is optional at build
time — see below.

### Firebase setup (Pillar 2)
The Google Services plugin only applies when `app/google-services.json` is present, so
the project always builds. To enable the Community (cloud) feature:

1. Create a project at the [Firebase Console](https://console.firebase.google.com) with a Google account (free).
2. Add an **Android app** with package name:
   `com.example.a207503_attarasyaadyajomantara_cikguizwan_project2`
3. Download `google-services.json` and place it in the **`app/`** folder.
4. In the console, open **Firestore Database → Create database → test mode**.
5. Re-sync Gradle. The Community screen now syncs in real time.

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel + StateFlow ·
Room · Retrofit + Gson · OkHttp · Firebase Firestore · CameraX · Google ML Kit
(barcode scanning) · ZXing (QR generation).

---

## APIs, libraries & acknowledgements

- **Open Trivia DB** (https://opentdb.com) — free public trivia REST API.
- **Firebase Firestore** — cloud NoSQL database.
- **Google ML Kit** — on-device barcode/QR scanning.
- **ZXing core** — QR code generation.
- **AI assistance:** Code scaffolding and refactoring were assisted by an AI coding
  assistant; all generated code was reviewed and is explainable by the author.
