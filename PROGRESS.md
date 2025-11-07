# 📑 PROGRESS.md (tracking file)

```markdown
# Progress Log — Smart IR Control

This file tracks development steps, decisions, and milestones.

---

## ✅ Completed
- Initial project setup with Kotlin, Compose, Hilt
- IR abstraction (`IrController`, `IrControllerImpl`)
- NEC protocol encoder (`NecEncoder`)
- Samsung TV presets (Power, Volume +/-, Channel +/-, Mute, Menu, Home, Back, Enter)
- ViewModel (`IrViewModel`) with state management
- Compose UI components: BrandSelector, DeviceSelector, CommandsGrid, ResultLine
- Main screen (`IrControlScreen`) with Material3 layout

---

## 🔜 Next Steps
1. **Persistence Layer**
   - Integrate Room database
   - Entities: Device, Command
   - DAO + Repository
   - Save/load custom presets

2. **Protocol Expansion**
   - Implement SIRC (Sony) encoder
   - Implement RC5 (Philips) encoder
   - Extend `IrController` interface

3. **UI Enhancements**
   - Remote-style layout (grid resembling physical remote)
   - Dark/Light theme polish
   - Snackbar feedback for errors/success

4. **Data Management**
   - Import/Export presets (JSON)
   - Share presets between devices

5. **Testing**
   - Unit tests for encoders
   - Compose UI tests
   - Integration tests with Room

---

## 📅 Log
- **2025-11-07**: Initial commit with full project structure and IR control basics.