# NovaLobby — BuiltByBit Listing

Paste the block below into the BuiltByBit resource **Description** after publishing
the free resource. Replace `[ADD SCREENSHOT URL]` and `[ADD VIDEO URL (optional)]`
with your media (BuiltByBit requires at least one image).

---
## NovaLobby

**The all-in-one hub & lobby plugin for Paper servers.**

Turn your hub into a polished experience: a fully configurable **server selector**,
**launchpads**, a **grappling hook**, the classic **EnderButt dash**, a **player hider**,
client-side **time selector**, spawn handling, world protection and much more — all in
one lightweight plugin.

> **Free resource.** Source code is publicly available on **[GitHub](https://github.com/ItsDavloooDev/NovaLobby)**.

### Features

- Configurable **Server Selector GUI** — custom heads, glow, fillers and actions
  (`[PROXY]`, `[MESSAGE]`, `[CONSOLE]`, `[PLAYER]`, `[CLOSE]`). Works with BungeeCord and Velocity.
- **Launchpads** — no commands: place a pressure plate, configure power/particles/sound.
- **Grappling Hook** and **EnderButt** mobility items.
- **Player Hider** and **Time Selector** client-side toggles.
- **Spawn management**: `/setspawn`, teleport on join, void teleport.
- **World protection**: no hunger, no item drops, damage/fall handling, frozen time.
- **Gamemode & Fly commands**: `/gmc /gms /gmsp /gma /fly`.
- **Playtime tracking** with `/playtime`.
- **Rank-based login messages** via LuckPerms + PlaceholderAPI support.

### Compatibility

- **Platform:** Paper 1.20 – 1.21.5+
- **Java:** 21
- **Version:** 1.0.0
- **Dependencies:** optional PlaceholderAPI, optional LuckPerms

### Install

1. Drop `NovaLobby-1.0.0.jar` into `plugins/`.
2. Restart the server.
3. Run `/setspawn` to set the lobby spawn.
4. Edit `plugins/NovaLobby/config.yml`, then `/novalobby reload`.

### Commands & Permissions

| Command | Description | Permission |
|---|---|---|
| `/novalobby reload` | Reload configuration | `novalobby.admin` |
| `/setspawn` | Set the lobby spawn | `novalobby.setspawn` |
| `/playtime [player]` | Session lobby playtime | `novalobby.playtime.others` |
| `/fly [player]` | Toggle flight | `novalobby.fly` / `novalobby.fly.others` |
| `/gmc` `/gms` `/gmsp` `/gma` | Change gamemode | `novalobby.gamemode.creative` / `.survival` / `.spectator` / `.adventure` |

### Screenshots

[ADD SCREENSHOT URL](url)
[ADD SCREENSHOT URL](url)

### Changelog

**v1.0.0**
- Initial public release.

### Links

- [GitHub source](https://github.com/ItsDavloooDev/NovaLobby)
- Contributions, bug reports and feature requests welcome on GitHub.