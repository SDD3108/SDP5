# SDP5 — Smart Delivery & Route Optimization Engine

Java 17 / Maven / JUnit 5. Assignment 5: **Strategy Pattern**.

## Run

```bash
mvn test
mvn exec:java
```

## Architecture and assignment questions

1. **Problem:** Delivery optimization has different interchangeable algorithms. Strategy avoids a growing conditional chain.
2. **Context:** `RoutePlanner` holds a `RouteStrategy` and delegates route selection.
3. **Strategy:** `RouteStrategy` defines `selectRoute` and `getStrategyName`.
4. **Concrete strategies:** `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, and the separately introduced `DistanceFirstRouteStrategy`.
5. **Runtime switching:** `Main` calls `planner.setStrategy(...)` repeatedly using the *same* planner, including five switches in comparison mode.
6. **Why Strategy:** algorithms are encapsulated and can be exchanged without modifying `RoutePlanner`; a large if/else would couple context to implementations.
7. **Extension:** implement `RouteStrategy`, then optionally register the implementation with `StrategySelector` without changing `RoutePlanner`.
8. **Strategy vs Bridge:** Strategy exchanges an algorithm at runtime, while Bridge separates two independently evolving abstraction/implementation hierarchies.
9. **SOLID:** SRP (planner vs selector vs algorithms), OCP (fifth strategy), DIP (`RoutePlanner` depends on `RouteStrategy`).
10. **Limitation:** all candidate routes are passed in memory; realistic road traffic, weather, currency, and geospatial routing APIs are not integrated. Relative scoring changes when candidate routes change.

## Business logic

All strategies use the same hard constraint filter **before** ranking: availability, total cost <= budget, traffic-adjusted time <= deadline. If no candidates survive, `NoSuitableRouteException` explains why.

- **Effective time** = estimated minutes × traffic multiplier.
- **Total cost** = base cost + 75 × distance(km) + urgent surcharge (500 for URGENT, otherwise 0) + additional fees.
- **Eco score** = 0.8 × carbonEmission(kg) + 0.2 × distance(km).
- **Balanced score** = 0.4 × normalized time + 0.3 × normalized cost + 0.1 × normalized distance + 0.2 × normalized emission; min-max normalization is calculated on eligible candidates, with a zero value when the metric is constant. All weights must be finite, within [0,1], and sum to 1.

Eco scoring belongs in the Eco strategy because environmental weighting is an algorithm-specific business decision, not a responsibility of the planner. The selector uses a configurable `EnumMap` of priorities, with STANDARD + ecoPreferred selecting the eco strategy.

## Data and results

`Main` creates **eight routes**, including one unavailable candidate. Four delivery requests demonstrate automatic selection. The same STANDARD request is then compared across all five strategies. Each decision prints its winner, score, reason, and rejected routes.

Sample console output (from local Java execution):

```text
AUTO URGENT -> Fastest -> R2
AUTO ECONOMY -> Cheapest -> R6
AUTO ECO -> Eco friendly -> R6
AUTO STANDARD -> Balanced -> R8

STRATEGY COMPARISON (same request)
Fastest: R2 | score 19.800 | time 19.8 min | cost 3725 | CO2 4.5 kg
Cheapest: R6 | score 1150.000 | time 52.8 min | cost 1150 | CO2 0.5 kg
Eco friendly: R6 | score 1.600 | time 52.8 min | cost 1150 | CO2 0.5 kg
Balanced: R8 | score 0.317 | time 36.0 min | cost 1800 | CO2 1.0 kg
Distance first: R6 | score 6.000 | time 52.8 min | cost 1150 | CO2 0.5 kg
```

## UML

- [Class diagram](docs/class-diagram.puml)
- [Sequence diagram](docs/sequence-diagram.puml)

Render both `.puml` files with PlantUML, or use the PlantUML VS Code extension.

## Tests

`src/test/java/kz/sdp/RouteEngineTest.java` contains **21 JUnit 5 tests**, covering every requested strategy, switching, selection, constraints, invalid weights, empty and impossible sets, boundary values, explanations, and invalid inputs. Run `mvn test` to obtain an authoritative test report. The project has additionally been compiled and its demo run with `javac` / `java`.
