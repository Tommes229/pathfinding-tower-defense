# LibGDX Tower Defense Game (Dijkstra Pathfinding)

This is a tower defense game developed with **LibGDX** as a university project.  
The main feature of this project is the use of **Dijkstra’s algorithm** for real-time pathfinding, allowing enemies to find the shortest path through the map while dynamically responding to player-built towers and obstacles.

---

## Features

- **Dijkstra Algorithm** for dynamic pathfinding
- Basic tower defense mechanics (placing towers, spawning enemies, health, etc.)
- Real-time path recalculation when the map changes

---

## Demo

**Gameplay Animation:**  
![Gameplay Animation](example_video.gif)

---

## Getting Started

### Prerequisites

- Java 8+ (tested with OpenJDK 11, see [Adoptium OpenJDK 11](https://adoptium.net/?variant=openjdk11))
- [Gradle](https://gradle.org/) (or use the Gradle wrapper)
- [LibGDX](https://libgdx.com/)

### How to Run

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/your-tower-defense-repo.git
    cd your-tower-defense-repo
    ```
2. **Build and run:**
    ```bash
    ./gradlew desktop:run
    ```

---

## Algorithms

This project demonstrates **Dijkstra’s algorithm** for shortest-pathfinding on a 2D grid.  
When the player places a tower (blocking a grid cell), the enemy path is recalculated.

---

## Template & Credits

This project is based on the **Dune-TD template** provided by Dennis Jehle for the Dune-TD assignment at University Ulm.  
The template was modified and extended to implement my own tower defense game.

### Template Requirements

Tested with OpenJDK Runtime Environment Temurin-11.0.12+7 (build 11.0.12+7) on:
- Windows 10
- Linux

Link to the used JDK: https://adoptium.net/?variant=openjdk11

---

## Resources

- [LibGDX Homepage](https://libgdx.com/)
- [LibGDX GitHub](https://github.com/libgdx/libgdx)
- [LibGDX Wiki](https://github.com/libgdx/libgdx/wiki)
- [LibGDX API Docs](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/)
- [LibGDX Community](https://libgdx.com/community/)
- [gdx-skins](https://github.com/czyzby/gdx-skins)
- [gdx-gltf](https://github.com/mgsx-dev/gdx-gltf)
- [imgui-java](https://github.com/SpaiR/imgui-java)
- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
- [Google Gson](https://github.com/google/gson)
- [Unsplash](https://unsplash.com/)
- [Itch.io Game Assets](https://itch.io/game-assets)
- [Sketchfab Free Models](https://sketchfab.com/features/free-3d-models)
- [Spherical Coordinate System (Wikipedia)](https://en.wikipedia.org/wiki/Spherical_coordinate_system)
- [Kenney Tower Defense Kit](https://www.kenney.nl/assets/tower-defense-kit)
- [Kenney Tower Defense Top Down](https://www.kenney.nl/assets/tower-defense-top-down)

**In the tools folder** there is a GLTF model viewer from [gdx-gltf](https://github.com/mgsx-dev/gdx-gltf) to check if GLTF files are working.

---

## Licenses

Have a look at the `dune/core/assets/` folder to see the licenses for the used game assets.  

---

## Acknowledgments

- University Ulm
- Dennis Jehle (Dune-TD template)
- LibGDX community

---

## License

This project was for educational purposes.
