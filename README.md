
# GreenCity – 0‑1 Knapsack Optimizer ✨🌱

A small **Java 17** console application that helps municipalities (or curious students)
pick the most sustainable mix of energy‑saving devices without blowing the budget.
It reads a CSV with device specs, runs an exact 0‑1 knapsack algorithm, and
prints / saves the optimal selection.

---

## 📂 Project Layout

```
greencity/
├── src/
│   └── greencity/
│       ├── Device.java
│       ├── DatasetLoader.java
│       ├── KnapsackSolver.java
│       └── Main.java
├── dataset/
│   └── green_city_devices.csv   # ⬅ optional: 300‑row sample (git‑ignored)
├── outputs/                     # ⬅ summaries appear here after each run
├── .gitignore
└── README.md                    # ← you’re reading it
```

*Compiled classes go in `bin/` (ignored by Git).*

---

## 🚀 Quick Start

1. **Clone**

   ```bash
   git clone git@github.com:radiophp/greencity.git && cd greencity
   ```

2. **Add / verify dataset**

   Either keep the sample CSV at `dataset/green_city_devices.csv`
   or pass your own file path when running.

3. **Compile & run**

   ```bash
   javac -d bin src/greencity/*.java
   java -cp bin greencity.Main              # uses default dataset
   # or
   java -cp bin greencity.Main /path/to/your_devices.csv
   ```

   You’ll be prompted for a budget in Turkish Lira:

   ```
   Enter your available budget in TRY (0 = exit): ₺50000
   ```

   The programme lists selected devices and writes a report to
   **outputs/{cost}TL_with{n}device_{timestamp}.txt**.

---

## ❓ How It Works

* **Device model** → immutable `record` with cost & impact fields  
* **Dynamic‑programming knapsack** → scales cost to whole Lira; exact optimum  
* **java.util.logging** → INFO‑level progress (‘Processed 50 / 300 devices…’)  
* **Interactive loop** → test multiple budgets without restarting

---

## 🗄 Outputs

Each run creates a text file in **outputs/**, e.g.

```
49814TL_with12device_20250531_154712.txt
```

The content mirrors the console summary for archival or grading.

---

## 🛠 Development Notes

* Tested on **OpenJDK 21** & **OpenJDK 17**  
* No external libraries required  
* CSV loader is a simple `String.split(",")`; for production
  consider using OpenCSV to handle quoted fields.

---

## 📄 License

MIT — see `LICENSE` (feel free to adjust for coursework).

*(README generated 2025-05-31)*
