[![support version](https://img.shields.io/badge/kotlin-2.2.0%2B-blueviolet.svg?style=flat&logo=kotlin&label=kotlin&labelColor=%23000&color=%23a97bff)](https://github.com/JetBrains/kotlin/releases/tag/v2.2.0)
[![support version](https://img.shields.io/badge/Compose-1.8.3%2B-blueviolet.svg?style=flat&logo=jetpackcompose&label=JCompose&labelColor=%23000&color=%234285F4)](https://developer.android.com/jetpack/androidx/releases/compose)
[![support version](https://img.shields.io/badge/Hilt-2.57.x%2B-blueviolet.svg?style=flat&logo=android&label=Hilt&labelColor=%23000&color=%234cc71e)](https://github.com/google/dagger/releases/tag/dagger-2.57.x)
[![support version](https://img.shields.io/badge/Room-2.7.x-blueviolet.svg?style=flat&logo=sqlite&logoColor=%23d085a0&label=Room&labelColor=%23000&color=%23d085a0)](https://developer.android.com/jetpack/androidx/releases/room)
[![support version](https://img.shields.io/badge/Retrofit-3.0.0-blueviolet.svg?style=flat&logo=postman&label=Retrofit&labelColor=%23000&color=%23FF6C37)](https://github.com/square/retrofit/releases/tag/3.0.0)

# Guarantify

> Smart and secure warranty management app

## Overview

**Guarantify** is a modern Android app that helps users store and manage their warranty documents in a convenient and reliable way.

## Features
- **Offline-first architecture (Room + Firebase sync)**
- **Manage warranties with expiration tracking**
- **Add photos, PDFs and tags to warranties**
- **Search & filter by product, date or store**
- **Cloud backup (Firebase)**
- **Secure data storage**
- **Smooth Animations & Transitions:** Eye-catching animations that enhance the user experience.
- **Type-Safe Navigation:** Multi-screen navigation architecture implemented with type-safe navigation, ensuring secure and reliable argument passing between screens.
- **Clean Architecture:** Although the logic is hardcoded, the project uses patterns like separation of concerns (via ViewModels and state management) that serve as a reference for scalable production applications.

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
| Layer        | Tooling                     |
|--------------|-----------------------------|
| UI           | Jetpack Compose, Material 3 |
| DI           | Hilt                        |
| Architecture | MVVM + Clean Architecture   |
| Local DB     | Room                        |
| Cloud        | Firebase Auth, Firestore    |
| Other        | Kotlin Coroutines, Flow     |
| Testing      | JUnit5, Mockk, Espresso     |

## Project Modules

- `:app` – Main app module
- `:core` – Shared utils, theme, design system
- `:feature_auth` – Auth flow
- `:feature_warranties` – Warranty list, details
- `:data` – Data sources, repositories
- `:domain` – Use cases, business logic

## Kotlin code style:
Kotlin code follows the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

### Branching Strategy
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