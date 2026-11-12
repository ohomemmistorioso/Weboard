# WebBoard

A small Android input method prototype that combines a compact keyboard with an embedded WebView panel.

## Version 0.1

- Android IME service registered as **WebBoard**.
- Compact starter QWERTY keyboard for basic text entry.
- 🌐 button switches to an embedded browser panel inside the keyboard area.
- Address/search field, back, forward, reload, and home controls.
- GitHub Actions workflow builds a debug APK on every push to `main`.

## Build

The project uses Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Java 17, and compile SDK 35.

Run:

```bash
gradle assembleDebug
```

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Install and enable

1. Install the APK.
2. Open WebBoard and tap **Ativar WebBoard**.
3. Enable WebBoard in Android's keyboard settings.
4. Return to WebBoard and tap **Escolher WebBoard**.
5. Select WebBoard from the input-method picker.

## Prototype notes

This is an early proof of concept, not yet a replacement for a full-featured keyboard. The browser and keyboard panels share the input-method window. Some websites may not behave perfectly in such a compact WebView, and the address field currently needs follow-up work for convenient text entry from within the IME. The starter keyboard is intentionally minimal.

The next milestone is to improve browser navigation and address entry, then evaluate integrating the mature FlorisBoard keyboard UI.
