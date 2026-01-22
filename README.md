## Setup

- Clone the repo.
- Add your Google Maps API key at `android/app/src/main/AndroidManifest.xml`
- Install all dependencies.
- Run:

    npm run android

---

## Note

If you are on Windows machine and error like **"IBM.SEMURU vendor"** appears, go to:

    node_modules/@react-native/gradle-plugin/settings.gradle.kt

Change:

    plugins {
      id("org.gradle.toolchains.foojay-resolver-convention").version("0.5.0")
    }

to:

    plugins {
      id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
    }

---

## MVVM Architecture

This project follows the MVVM (Model–View–ViewModel) architecture to keep the codebase clean, modular, and easy to maintain.

---

## Model

- It contains only data classes (`UserLocation`, `Place`, `LocationResult`)
- Represents the structure of the data used in the app
- No bussiness logic

---

## ViewModel

- Bridge between View and Model
- Calls the repository to fetch data like userLocation and nearby places
- Returns ready-to-use data for the View in format specified by model
- It has the bussiness logic

---

## View

- React Native UI layer
- Only renders UI, has no bussiness logic
- Calls native methods `LocationModule` and displays data on the map
- Does not contain business logic

---

## Repository

- Single source for data
- Fetches real location or provides mock location
- Completely independent of UI and permission handling
