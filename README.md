# VibeChat (Android - Kotlin & Jetpack Compose)

VibeChat is a modern, responsive chat messenger application built for Android using **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Room Database**.

## Features Ported from Original Web App
- **Authentication**: Sign in and Sign up with validation, account switching, and demo accounts.
- **Direct Messaging**: 1-on-1 real-time conversation stream with WhatsApp-inspired message bubbles, delivered indicators, and formatted timestamps.
- **Contacts & Discovery**: Contact directory with active presence indicators (online/offline badges) and instant search filtering.
- **Responsive Layout**: Single-pane navigation for phones with `BackHandler`, and adaptive dual-pane side-by-side layout for tablets and landscape displays.
- **Local Persistence**: Full offline-first data caching and persistence using Room (SQLite) with pre-seeded starter contacts and conversations.
- **Dynamic Theming**: Light and Dark theme modes with WhatsApp-inspired color accents.
- **Interactive Simulation**: Contextual automatic replies from contacts with dynamic typing indicator status.

## Architecture
- **Language**: Kotlin 2.1.0
- **UI Framework**: Jetpack Compose with Material 3
- **Data Layer**: Room Database (`UserDao`, `MessageDao`, `AppDatabase`), Repository Pattern
- **State Management**: `ViewModel`, `StateFlow`, and `collectAsState`
- **Iconography**: Custom Material 3 Adaptive Launcher Icon
