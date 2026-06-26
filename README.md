Metro Route Navigation Engine

A production-grade, CLI-based transit navigation system that models real-world subway networks as directed weighted graphs. This engine calculates optimal commuter routes by evaluating geographical heuristics, track travel times, and dynamic transfer penalties.

---

## Features

* **Multi-Algorithm Routing Engine:** Dynamically switch between **Breadth-First Search (BFS)** for fewest-stops and **A* Search** for shortest-travel-time.
* **Geographical Heuristics:** Utilizes the **Haversine Formula** to calculate real-world great-circle distances between station coordinates.
* **Smart Transfer Penalties:** Automatically detects line changes (e.g., Red Line to Blue Line) and injects a weighted time penalty into edge relaxation logic.
* **Express & Local Line Support:** Centralized generation of distinct train behaviors utilizing the Factory Design Pattern.
* **Resilient Execution:** Custom runtime exception handling (`StationNotFoundException`, `InvalidRouteException`) ensures continuous CLI operation without fatal crashes.

---

## System Architecture

The application enforces strict SOLID principles and Separation of Concerns:

* `app/` - Central CLI execution loop and UI bridging.
* `domain/` - Core data models (`Station`, `ExpressLine`, `LocalLine`).
* `graph/` - Mathematical representation via Adjacency Lists (`MetroNetwork`, `Track`).
* `routing/` - Pathfinding logic isolated via the Strategy Pattern.
* `factory/` - Complex object instantiation logic.

---
