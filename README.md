<div align="center">

# NovaLobby

**The all-in-one hub & lobby plugin for Paper servers.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20%20–%201.21.5%2B-success)](https://www.minecraft.net)
[![Paper](https://img.shields.io/badge/Platform-Paper-informational)](https://papermc.io)
![Java](https://img.shields.io/badge/Java-21-blue)
[![BuiltByBit](https://img.shields.io/badge/Download-BuiltByBit-00b48a)](https://www.builtbybit.com/)

**Free on [BuiltByBit](https://www.builtbybit.com/) · Open source on GitHub**

</div>

## Features

- **Server Selector** — fully configurable GUI with custom head textures, glow,
  fillers and actions (`[PROXY]`, `[MESSAGE]`, `[CONSOLE]`, `[PLAYER]`, `[CLOSE]`);
  works with BungeeCord **and** Velocity via the plugin messaging channel.
- **Launchpads** — place a pressure plate on any block, done. Power, particles and
  sound fully configurable.
- **Grappling Hook** — configurable pull strength, wall-stick, line particles.
- **EnderButt** — ender-pearl dash / double jump with directional control.
- **Player Hider** — one-click toggle to hide/show other players (client-side).
- **Time Selector** — change the time *only for your eye* (client-side).
- **Spawn management** — `/setspawn`, teleport on join, void teleport back to spawn.
- **World protection** — block damage, hunger, item dropping; freeze day/night cycle.
- **Playtime** — `/playtime` tracks time spent in the lobby this session.
- **Gamemode & Fly commands** — `/gmc`, `/gms`, `/gmsp`, `/gma`, `/fly` (+ for other players).
- **Rank-based login messages** — show a formatted join message for specific
  LuckPerms groups.
- Optional **PlaceholderAPI** and **LuckPerms** (soft-depend).

## Install

1. Use **Paper 1.20 – 1.21.5+**.
2. Drop `NovaLobby-1.0.0.jar` into the `plugins/` folder.
3. Restart the server.
4. Configure `plugins/NovaLobby/config.yml` and reload with `/novalobby reload`.
5. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
   and/or [LuckPerms](https://luckperms.net) to use those integrations.

> [!IMPORTANT]
> Locate the spawn with `/setspawn` after first start.

## Build from source

Requires JDK 21.

```bash
./gradlew build
```

The jar is output to `build/libs/NovaLobby-1.0.0.jar`.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/novalobby reload` | Reload configuration | `novalobby.admin` |
| `/setspawn` | Set the lobby spawn point | `novalobby.setspawn` |
| `/playtime [player]` | Show session playtime | `novalobby.playtime` / `novalobby.playtime.others` |
| `/fly [player]` | Toggle flight | `novalobby.fly` / `novalobby.fly.others` |
| `/gmc`, `/gms`, `/gmsp`, `/gma` | Change gamemode | `novalobby.gamemode.*` |

All permissions are documented in [`plugin.yml`](src/main/resources/plugin.yml).

## Configuration

Everything is centralized in `config.yml`:

- Selector GUI, items and actions
- Launchpad physics and effects
- Grappling hook and EnderButt tuning
- Player hider / time selector items
- Spawn, world, fly and login-message settings

Compatible colors everywhere: legacy codes (`&a`), hex (`&#RRGGBB` / `<#RRGGBB>`)
and MiniMessage tags (`<gradient:#RRGGBB:#RRGGBB>...`.

## License

Released under the [MIT License](LICENSE) — free to use, modify and distribute.

---

<div align="center">

**Enjoying NovaLobby? Leave a review on [BuiltByBit](https://www.builtbybit.com/).**

</div>