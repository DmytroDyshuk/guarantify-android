[![support version](https://img.shields.io/badge/kotlin-2.3.0%2B-blueviolet.svg?style=flat&logo=kotlin&label=kotlin&labelColor=%23000&color=%23a97bff)](https://github.com/JetBrains/kotlin/releases/tag/v2.3.0)
[![support version](https://img.shields.io/badge/Compose-2025.09.00-blueviolet.svg?style=flat&logo=jetpackcompose&label=JCompose&labelColor=%23000&color=%234285F4)](https://developer.android.com/jetpack/androidx/releases/compose)
[![support version](https://img.shields.io/badge/Hilt-2.57.1-blueviolet.svg?style=flat&logo=android&label=Hilt&labelColor=%23000&color=%234cc71e)](https://github.com/google/dagger/releases/tag/dagger-2.57.1)
[![support version](https://img.shields.io/badge/Room-2.7.2-blueviolet.svg?style=flat&logo=sqlite&logoColor=%23d085a0&label=Room&labelColor=%23000&color=%23d085a0)](https://developer.android.com/jetpack/androidx/releases/room)

# Guarantify

> Smart and secure warranty management app

## Overview

**Guarantify** is a modern Android app that helps users store and manage their warranty documents in a convenient and reliable way.

## Features
- **Offline-first architecture** — Room database with automatic Firebase synchronization
- **Smart sync engine** — Automatic conflict resolution and incremental sync when online
- **Manage warranties with expiration tracking** — Never miss a warranty expiration
- **Add photos and documents** — Attach images and PDFs to warranty records
- **Search & filter** — Find warranties by product name, store, or date range
- **Secure cloud backup** — Firebase Auth + Firestore for authenticated data storage
- **Smooth animations & transitions** — Lottie animations and polished Material 3 transitions
- **Type-safe navigation** — Navigation Compose with type-safe arguments via Kotlin Serialization
- **Clean Architecture** — MVVM with separation of concerns: Domain → Data → Presentation

## Purpose
- **UI/UX Mastery:** Demonstrating intricate designs, custom components, and polished animations.
- **Navigation Techniques:** Implementing smooth transitions and multi-screen navigation using Jetpack Compose.
- **Modern Android Patterns:** Showcasing an up-to-date approach in Android development with best practices, even if the underlying logic is simple.
- **Code Quality:** Using well-structured code and maintainable architecture, along with automated quality checks to provide a professional standard of development.

## Continuous Integration & Code Quality
- **GitHub Actions:** Automated builds and tests are configured to ensure that every commit maintains the project's stability and performance.
- **Android Lint:** Integrated to catch potential issues and enforce best practices in the Android codebase.
- **ktlint:** Used for code style checking and formatting, ensuring consistency throughout the project.

## Tech Stack
| Layer        | Tooling                    |
|--------------|----------------------------|
| UI           | Jetpack Compose, Material 3 |
| DI           | Hilt       |
| Architecture | MVVM + Clean Architecture  |
| Local DB     | Room                       |
| Cloud        | Firebase Auth, Firestore   |
| Navigation   | Compose Navigation  |
| Image        | Coil                       |
| Animation    | Lottie                     |
| Other        | Kotlin Coroutines, Flow, Kotlin Serialization |
| Testing      | JUnit5, Mockk, Espresso    |

## Setup

### Prerequisites
- **JDK 11** or higher
- **Android Studio Ladybug (2024.2.1)** or newer
- **Kotlin Plugin** bundled with Android Studio

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DmytroDyshuk/guarantify-android.git
   cd guarantify-android
   ```

2. **Configure Firebase (required for authentication):**
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Add an Android app with package name `com.guarantify.app`
   - Download `google-services.json` and place it in `app/`
   - Enable **Authentication** → **Google Sign-In**
   - Enable **Firestore Database**

3. **Build the project:**
   ```bash
   ./gradlew build
   ```

4. **Run on device or emulator:**
   ```bash
   ./gradlew :app:installDebug
   ```

### Gradle Features
- **Type-safe project accessors** — Compile-time safety for module dependencies (`projects.core.domain`)
- **Version catalogs** — Centralized dependency management in `gradle/libs.versions.toml`
- **KSP** — Kotlin Symbol Processing for Room and Hilt

## Project Modules

### Core Modules
| Module | Purpose | Dependencies |
|--------|---------|--------------|
| `:app` | Application entry point, DI setup | All modules |
| `:core:common` | Common utilities, DI qualifiers | None (pure Kotlin) |
| `:core:domain` | Use cases, repository interfaces | `:core:common` |
| `:core:data` | Repository implementations, Firebase, Room | `:core:domain`, `:core:common` |
| `:core:ui` | Common UI components, theme | `:core:util` |
| `:core:navigation` | Type-safe navigation definitions | None (Kotlin Serialization) |
| `:core:util` | Utilities (ImageCompressor, DateFormatter) | None |

### Feature Modules
| Module | Purpose | Dependencies |
|--------|---------|--------------|
| `:feature:auth` | Google Sign-In flow | `:core:domain`, `:core:util` |
| `:feature:warranties` | Warranty CRUD operations | `:core:domain`, `:core:ui`, `:core:util`, `:core:navigation` |
| `:feature:insights` | Analytics and statistics | `:core:domain`, `:core:ui` |
| `:feature:settings` | User preferences | `:core:domain`, `:core:ui` |
| `:feature:home-navigation` | Bottom navigation container | `:feature:warranties`, `:feature:insights`, `:feature:settings` |

> **Note:** Type-safe project accessors are enabled (`TYPESAFE_PROJECT_ACCESSORS`) for compile-time module dependency safety.

### Dependency Rules
- ✅ `feature` → `core:domain`
- ✅ `core:data` → `core:domain`
- ✅ `core:ui` → `core:util`
- ❌ `core:domain` → `core:data` (never!)
- ❌ `core:data` → `feature:*` (never!)

## Kotlin code style:
Kotlin code follows the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

## Branching Strategy
This project follows the Git Flow branching model:
- **`main`** — production-ready code. Only release and hotfix branches are merged here.
- **`develop`** — main development branch. All feature and bugfix branches are merged here.
- **`feature/*`** — new features, branched from `develop`, merged back into `develop`.
- **`bugfix/*`** — bug fixes found during development, branched from `develop`, merged back into `develop`.
- **`release/x.x.x`** — pre-release stabilization branches, branched from `develop`. After release, merged into both `main` and `develop`.
- **`hotfix/*`** — urgent fixes for production, branched from main, merged into both main and develop.

## Committing Guidelines:
We use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) to maintain a clear and consistent commit history.

**Commit message format:**

> \<type>[optional scope]: \<description> [TASK-ID]
>
> [optional body]
> [optional footer(s)]

**where:**
- `<type>` — commit type (see list below).
- `[optional scope]` — scope of change (module, feature, etc.).
- `<description>` — brief description, in lowercase.
- `[TASK-ID]` — Trello task number (e.g., [CHORE-1], [FEAT-5], [FIX-12]).

**Examples:**

```
feat(auth): add biometric login [FEAT-3]
fix(api): correct warranty expiration date calculation [FIX-54]
chore(deps): update Retrofit to 3.1.0 [CHORE-154]
```

**Types:**
- `feat` — new feature
- `fix` — bug fix
- `docs` — documentation
- `style` — formatting
- `refactor` — code refactor
- `test` — add or update tests
- `chore` — tooling or dependencies
