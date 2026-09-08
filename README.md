# BoatHider-Paper

A Paper plugin for spawning **collisionless boats** and optionally hiding boats/players from each other — useful for anti-crowding, custom transport setups, or hiding player stacks on boats.

Supported Minecraft/Paper versions:
- `1.21.4`
- `1.21.8`
- `26.2`

Each version is maintained on its own branch, since Paper's internal (NMS) APIs changed enough between them to need version-specific code.

## How it works

The plugin uses Paper's NMS internals to spawn a custom boat entity (`CollisionlessBoat`) that overrides collision behavior so it:
- Never collides with other entities
- Isn't pushable
- Doesn't push other entities

Whenever a normal boat spawns in the world (player-placed, from a dispenser, etc.), the plugin detects it and silently replaces it with a `CollisionlessBoat` in the same location, carrying over any passengers. This means collisionless behavior applies automatically, without players needing to do anything.

On top of that, there's an optional **hiding mode**: when enabled, players riding in boats (and the boats themselves) become invisible to everyone else except their own passengers. This is handled by tracking vehicle enter/exit events and selectively showing/hiding entities per-player using Bukkit's `showEntity`/`hideEntity` API.

This plugin requires you to hide the boats in order that there is no collision.

## Commands

All under /dev, OP-only:

- `/dev spawnboat [world] [x] [y] [z]` — spawn a collisionless boat
- `/dev forceinboat <player|@a>` — spawn a boat under a player and put them in it
- `/dev hideboats <true|false>` — toggle hiding mode
- `/dev togglecollisions <true|false>` — toggle player collision globally

All commands tab-complete their arguments (player names, world names, `true`/`false`, and current coordinates for `spawnboat`).

## API

For other plugins, `BoatHiderMain.getApi()` exposes a small API (`BoatHiderAPI`):

- `forceIntoBoat(Player target, boolean doHideBoat)` — put one player into a collisionless boat, optionally enabling hiding mode at the same time
- `forceAllIntoBoat(boolean doHideBoat)` — same, for every online player
- `hideBoats(boolean value)` — toggle hiding mode
- `removeAllCollisionlessBoats()` — despawn every collisionless boat currently in the world

## Building

Each branch is a self-contained Gradle project targeting its respective Paper/Java version:

| Branch | Minecraft/Paper version | Java version |
|---|---|---|
| `1.21.4` | 1.21.4 | 21 |
| `1.21.8` | 1.21.8 | 21 |
| `26.2` | 26.2 | 25 |

```bash
git clone https://github.com/T0rtel/BoatHider-Paper.git
cd BoatHider-Paper
git checkout <branch>
./gradlew build
```

The built jar is output to `build/libs/`.
