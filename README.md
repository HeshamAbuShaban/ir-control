# Control IR 

> An Android app for controlling devices via the phone’s IR blaster.  
> Built with **Kotlin**, **Jetpack Compose**, **Hilt**, and **MVVM** architecture.

## ✨ Features
- Detect if the device has an IR emitter
- Send IR signals using `ConsumerIrManager`
- NEC protocol encoder for common TV commands
- Brand presets (Samsung TV with basic commands)
- Modern UI with Jetpack Compose (Material3)
- Clean architecture with DI and ViewModel separation

## 📂 Project Structure
app/src/main/java/dev/training/ir_control/ 
├── App.kt
├── MainActivity.kt
├── data/ # Preset data sources
├── di/ # Hilt modules
├── ir/ # IR abstraction + implementations
│ └── protocol/ # Protocol encoders (NEC, etc.)
├── model/ # Data classes (Brand, Device, Command, Payload)
└── ui/ # Compose UI + ViewModel


## 🚀 Getting Started
1. Clone the repo:
   
   ```bash
   git clone https://github.com/HeshamAbuShaban/ir-control.git
   ```
2. Open in Android Studio (latest version recommended).

3. Sync Gradle and run on a real device with IR blaster (emulators don’t support IR).

## 🛠️ Tech Stack
Kotlin 2.x

- Jetpack Compose (Material3)

- Hilt for dependency injection

- MVVM architecture

- Gradle Kotlin DSL

📌 Roadmap
- [ ] Add Room database for custom presets

- [ ] Support more brands and protocols (SIRC, RC5)

- [ ] Remote-style UI layout

- [ ] Import/Export presets (JSON)

- [ ] Unit tests for encoders and UI tests

## 🤝 Contributing
> Pull requests are welcome. For major changes, please open an issue first to discuss.

## 📜 License

MIT License — see [LICENSE]() file.