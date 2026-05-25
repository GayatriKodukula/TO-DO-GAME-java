# TO-DO-GAME-java
# 🎮 TODO Game – Gamified Task Manager

A gamified Java task manager that turns productivity into an RPG experience with XP, levels, streaks, rewards, an in-game store, and inventory system.

## Requirements
- Java 17+ (any JDK)

## How to Run

**Linux / macOS:**
```bash
chmod +x run.sh && ./run.sh
```

**Windows:**
```
run.bat
```

**Or manually:**
```bash
mkdir out
javac -d out -sourcepath src src/todogame/*.java
java -cp out todogame.Main
```

## Features
| Feature | Detail |
|---|---|
| Task CRUD | Add / Edit / Delete / Complete |
| Priorities | Low (10 XP), Medium (20 XP), High (30 XP) |
| Level System | 10 levels with increasing XP thresholds |
| Streak Bonus | Up to +50 XP for daily consistency |
| Filtering | View all / pending / completed tasks |
| Sorting | By priority or due date |
| Persistence | Auto-save to `save/` directory on exit |
| Store System | Buy cosmetic upgrades and rewards using earned currency |
| Inventory System | Manage unlocked items, rewards, and collectibles |
| Currency Economy | Earn coins through completed tasks and streaks |
| Reward Progression | Unlock achievements and upgrades through gameplay |

## Gameplay Loop

1. Complete tasks
2. Earn XP and coins
3. Level up your character
4. Maintain daily streaks
5. Spend coins in the store
6. Unlock and manage rewards in inventory

## XP Thresholds
| Level | XP Required |
|---|---|
| 1 | 0 |
| 2 | 50 |
| 3 | 120 |
| 4 | 220 |
| 5 | 350 |
| 6 | 520 |
| 7 | 740 |
| 8 | 1020 |
| 9 | 1370 |
| 10 | 1800 |

## Project Structure
```
src/todogame/
├── Task.java         – Task model + serialization
├── User.java         – Player state (XP, level, streak)
├── TaskManager.java  – CRUD & filtering operations
├── GameSystem.java   – XP/level-up logic & display helpers
├── StorageManager.java – File persistence (tasks.dat, user.dat)
└── Main.java         – Entry point & console UI
├── Store.java        – Handles shop items and purchases
├── Inventory.java    – Manages owned items and rewards
├── CurrencySystem.java – Coin rewards and spending
```

---

## 🖥️ GUI Mode (Swing)

**Linux / macOS:**
```bash
chmod +x run-gui.sh && ./run-gui.sh
```

**Windows:**
```
run-gui.bat
```

**Or manually:**
```bash
mkdir out
javac -d out -sourcepath src src/todogame/*.java src/todogame/ui/*.java
java -cp out todogame.ui.GameUI
```

### UI Features
- Dark RPG theme with gold/cyan accent palette
- Animated XP progress bar (smooth eased fill)
- Floating XP popup with level-up animation
- Task cards with colour-coded priority bars
- Filter buttons: All / Pending / Done
- Double-click any task to complete it
- Add / Edit / Delete via toolbar buttons
- Auto-saves on window close
- In-game reward store with purchasable items
- Inventory system for unlocked rewards and collectibles
- Currency earning system tied to task completion

### UI Classes (in `src/todogame/ui/`)
| File | Role |
|---|---|
| `GameUI.java` | Entry point, bootstraps backend + Swing |
| `GameWindow.java` | Main window: header, task list, action bar |
| `AddTaskDialog.java` | Modal dialog for creating tasks |
| `TaskCellRenderer.java` | Rich card renderer for each task row |
| `XPBar.java` | Animated gold progress bar component |
| `XPPopup.java` | Fade-in/out XP earned overlay |
| `GameButton.java` | Flat rounded button with hover effects |
| `RoundedPanel.java` | Reusable rounded-rect panel |
| `Theme.java` | All colours, fonts, spacing constants |
