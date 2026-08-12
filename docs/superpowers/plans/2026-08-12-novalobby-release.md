# NovaLobby Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebrand the VanteyLobby plugin into an open-source-ready "NovaLobby" release (code, metadata, docs, CI) published to `ItsDavloooDev/NovaLobby` + free BuiltByBit listing.

**Architecture:** Mechanical in-place rebrand. Package `dev.itsdavlooo.VanteyLobby` → `dev.itsdavlooo.novalobby`, class `VanteyLobby` → `NovaLobby`, command/permission prefix `lobbycore` → `novalobby`, plugin renamed `LobbyCore` → `NovaLobby`. Config cleaned to neutral defaults. Docs: `README.md` (GitHub), `BUILTBYBIT.md` (BuiltByBit paste page), MIT `LICENSE`, GitHub Actions release build. Verified by a real Gradle build before commit.

**Tech Stack:** Java 21, Paper API 1.21.5, Gradle (Kotlin DSL), GitHub Actions.

**Working dir:** `C:\Users\ardui\IdeaProjects\VanteyLobby` (this local repo). No commits exist yet in this git repo except the design doc commit.

## Global Constraints

- Exact rename map (verbatim, apply in this order per file):
  - `dev.itsdavlooo.VanteyLobby` → `dev.itsdavlooo.novalobby`
  - `VanteyLobby` → `NovaLobby`
  - `LobbyCore` → `NovaLobby`
  - `lobbycore` → `novalobby`
  - `lobbyCoreCommand` → `novaLobbyCommand` (camelCase local var in `VanteyLobby.java`)
- Feature name `EnderButt` is unchanged everywhere.
- No new features; no Folia claims. Compat statement is exactly: "Paper 1.20 – 1.21.5+".
- Version bump: `gradle.properties` → `version=1.0.0`; `settings.gradle.kts` rootProject.name → `NovaLobby`.
- GitHub repo URL: `https://github.com/ItsDavloooDev/NovaLobby`. Author/copyright name: `ItsDavlooo`.
- BuiltByBit URL: `https://www.builtbybit.com/` (resource link not created yet; leave this exact URL in README — user replaces once live).
- MIT license, year 2026.
- Final gate: `gradlew build` succeeds AND grep sweep for `Vantey|LobbyCore|lobbycore` (excluding `docs/` and `BUILTBYBIT.md` intentional mentions) returns 0.

---

### Task 1: Bulk source rename (package + class + command/perm prefix)

**Files:**
- Modify: all 33 files under `src/main/java/dev/itsdavlooo/VanteyLobby/`
- Rename: `src/main/java/dev/itsdavlooo/VanteyLobby/VanteyLobby.java` → `.../novalobby/NovaLobby.java`
- Rename: `src/main/java/dev/itsdavlooo/VanteyLobby/commands/LobbyCoreCommand.java` → `.../novalobby/commands/NovaLobbyCommand.java`

**Interfaces:**
- Consumes: nothing (applies spec rename map).
- Produces: main class `dev.itsdavlooo.novalobby.NovaLobby`, command executor class `dev.itsdavlooo.novalobby.commands.NovaLobbyCommand`, permission strings prefixed `novalobby.`, fallback prefix `novalobby`, namespace key `novalobby_item`. No class/type/field names change except those listed.

- [ ] **Step 1: Move directory + rename files (preserve git rename tracking)**

Run (PowerShell, from repo root — project is NOT a git worktree, this is the working repo; verify with `Test-Path` that the source dir exists first):

```powershell
Test-Path src/main/java/dev/itsdavlooo/VanteyLobby
# then:
git mv src/main/java/dev/itsdavlooo/VanteyLobby src/main/java/dev/itsdavlooo/novalobby
git mv src/main/java/dev/itsdavlooo/novalobby/VanteyLobby.java src/main/java/dev/itsdavlooo/novalobby/NovaLobby.java
git mv src/main/java/dev/itsdavlooo/novalobby/commands/LobbyCoreCommand.java src/main/java/dev/itsdavlooo/novalobby/commands/NovaLobbyCommand.java
git status --short
```

Expected: status shows `R` rename entries; the `novalobby` directory no longer contains `VanteyLobby.java` or `LobbyCoreCommand.java`.

- [ ] **Step 2: Apply the string replacements to every .java file**

Run exactly (PowerShell, repo root). Rules run per-file in the listed order:

```powershell
$rules = @(
  'dev.itsdavlooo.VanteyLobby|dev.itsdavlooo.novalobby',
  'VanteyLobby|NovaLobby',
  'LobbyCore|NovaLobby',
  'lobbycore|novalobby',
  'lobbyCoreCommand|novaLobbyCommand'
)
Get-ChildItem src/main/java -Recurse -Filter *.java | ForEach-Object {
  $c = [System.IO.File]::ReadAllText($_.FullName)
  foreach ($r in $rules) {
    $parts = $r -split '\|', 2
    $c = $c.Replace($parts[0], $parts[1])
  }
  [System.IO.File]::WriteAllText($_.FullName, $c)
}
```

- [ ] **Step 3: Verify rename — grep sweep must return 0**

Run:

```bash
rg -i -c "vantey|lobbycore|lobbycore|vanteylobby" src/main/java
```

Expected: no output (exit 1 / no matches). Also check the class/file rename line drove through:

```bash
rg -n "public final class NovaLobby|extends JavaPlugin" src/main/java/dev/itsdavlooo/novalobby/NovaLobby.java
```

Expected: line `public final class NovaLobby extends JavaPlugin {`.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "refactor: rebrand source to NovaLobby (package, class, commands, permissions)"
```

---

### Task 2: Plugin metadata, messages, config, build files

**Files:**
- Modify: `src/main/resources/plugin.yml` (rewrite full content, below)
- Modify: `src/main/resources/messages.yml` (only the `prefix` line)
- Modify: `src/main/resources/config.yml` (header comment, selector example items, login rank list)
- Modify: `gradle.properties` (version)
- Modify: `settings.gradle.kts` (rootProject.name)

**Interfaces:**
- Consumes: Task 1's `<main>` class.
- Produces: plugin identity `NovaLobby`, command `/novalobby`, permission tree `novalobby.*`, prefix string `<gradient:#00E5FF:#7C4DFF>NovaLobby</gradient> &8» &7`, artifact name `NovaLobby-1.0.0.jar`.

- [ ] **Step 1: Rewrite `src/main/resources/plugin.yml` with this exact content**

```yaml
name: NovaLobby
version: '${version}'
main: dev.itsdavlooo.novalobby.NovaLobby
api-version: '1.20'
load: POSTWORLD
author: ItsDavlooo
description: Hub/Lobby core plugin with server selector, launchpads, grappling hook and more.
softdepend:
  - PlaceholderAPI
  - LuckPerms

# NOTE: feature commands (/gmc, /gms, /gmsp, /gma, /fly, /setspawn, /playtime)
# are NOT declared here: they are registered at runtime through the CommandMap.
commands:
  novalobby:
    description: Main NovaLobby command
    usage: /novalobby reload
    permission: novalobby.admin
    aliases: [nova, nl]

permissions:
  novalobby.admin:
    description: Access to /novalobby (reload)
    default: op
  novalobby.setspawn:
    description: Set the lobby spawn point with /setspawn
    default: op

  # Base gamemode permissions.
  # If you prefer to manage everything through LuckPerms, change these defaults to false:
  #   default: false
  novalobby.gamemode.creative:
    description: Use /gmc
    default: op
  novalobby.gamemode.survival:
    description: Use /gms
    default: op
  novalobby.gamemode.spectator:
    description: Use /gmsp
    default: op
  novalobby.gamemode.adventure:
    description: Use /gma
    default: op
  novalobby.gamemode.*:
    description: Access to all gamemode commands
    default: op
    children:
      novalobby.gamemode.creative: true
      novalobby.gamemode.survival: true
      novalobby.gamemode.spectator: true
      novalobby.gamemode.adventure: true
  novalobby.gamemode.others:
    description: Change gamemode of another player (combined in AND with the specific gamemode permission)
    default: false

  novalobby.playtime:
    description: Use /playtime (session time in the lobby)
    default: true
  novalobby.playtime.others:
    description: Check session time of another player (/playtime <player>)
    default: op

  novalobby.fly:
    description: Use /fly
    default: op
  novalobby.fly.others:
    description: Toggle fly for another player (/fly <player>)
    default: false
  novalobby.fly.bypass.damage:
    description: Keep flight enabled on damage even if fly.disable-on-damage is true
    default: false
```

- [ ] **Step 2: Update `src/main/resources/messages.yml` prefix**

Replace line 9:

```yaml
prefix: "<gradient:#00E5FF:#7C4DFF>NovaLobby</gradient> &8» &7"
```

Also replace the header line 2 (`#  VanteyLobby - messages.yml`) with `#  NovaLobby - messages.yml`.

- [ ] **Step 3: Update `src/main/resources/config.yml`**

3a. Header comment line 2: `#  VanteyLobby - config.yml` → `#  NovaLobby - config.yml`.

3b. In the `selector.gui.items` section, REMOVE the `question_left` and `question_right` entries (the two `PLAYER_HEAD` items with `base64` + `display_name: "&e?"` / lore "&7Select a gamemode!"). Keep the `filler` and `survival` entries. Resulting `items:` block must be:

```yaml
    items:
      filler:
        material: GRAY_STAINED_GLASS_PANE
        slot: -1

      survival:
        material: DIRT
        slot: 13
        amount: 1
        glow: false
        display_name: "&aSurvival"
        lore:
          - "&7Right click to choose"
          - "&7a server to play on."
        actions:
          - "[CLOSE]"
          - "[MESSAGE] &7Sending you to: &aSurvival"
          - "[PROXY] survival"
```

3c. `login-messages.ranks`: replace the whole list block with a neutral example + comment:

```yaml
  # List of LuckPerms groups that trigger the notification.
  # Example values below — replace with your own group names.
  ranks:
    - "owner"
    - "admin"
    - "moderator"
```

- [ ] **Step 4: Update build files**

`gradle.properties`: change `version=1.0` → `version=1.0.0`.

`settings.gradle.kts`: short line 1 to `rootProject.name = "NovaLobby"`.

- [ ] **Step 5: Verify — rebuild artifact name expected**

Run: `.\gradlew.bat build --no-daemon -q`
Expected: BUILD SUCCESSFUL; file `build/libs/NovaLobby-1.0.0.jar` exists.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: rebrand metadata, commands, permissions and config defaults to NovaLobby"
```

---

### Task 3: Remove stale jar, add MIT LICENSE

**Files:**
- Delete: `ZetsyHub.jar`
- Create: `LICENSE`

**Interfaces:**
- Consumes: nothing.
- Produces: `LICENSE` (MIT, 2026, ItsDavlooo); repo root cleaned.

- [ ] **Step 1: Delete stale binary**

```powershell
Remove-Item ZetsyHub.jar
```

(The jar is untracked, so a plain delete is enough; confirm with `Test-Path ZetsyHub.jar` → False.)

- [ ] **Step 2: Create `LICENSE`**

```text
MIT License

Copyright (c) 2026 ItsDavlooo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

- [ ] **Step 3: Verify**

Run:

```bash
Test-Path LICENSE
Test-Path ZetsyHub.jar
```

Expected: `LICENSE` → True; `ZetsyHub.jar` → False.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: add MIT license, remove stale jar"
```

---

### Task 4: GitHub README.md

**Files:**
- Create: `README.md`

**Interfaces:**
- Consumes: plugin features (config/commands knowledge from Task 1-2), repo URL.
- Produces: installable instructions; BuiltByBit link.

- [ ] **Step 1: Create `README.md` with this exact content**

````markdown
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
````

- [ ] **Step 2: Verify feature list matches actual plugin**

Run:

```bash
rg -n "novalobby\." src/main/java | Select-Object -First 8
```

Expected: permission strings match those documented in the README table (`novalobby.admin`, `novalobby.playtime`, `novalobby.fly`, `novalobby.gamemode.*`, `novalobby.setspawn`).

- [ ] **Step 3: Commit**

```bash
git add README.md
git commit -m "docs: add GitHub README"
```

---

### Task 5: BUILTBYBIT.md (BuiltByBit listing text)

**Files:**
- Create: `BUILTBYBIT.md`

**Interfaces:**
- Consumes: repo URL, feature list.
- Produces: copy-paste description for the BuiltByBit resource page that links back to GitHub.

- [ ] **Step 1: Create `BUILTBYBIT.md` with this exact content**

````markdown
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
````

- [ ] **Step 2: Verify GitHub link present**

Run:

```bash
rg -n "github.com/ItsDavloooDev/NovaLobby" BUILTBYBIT.md
```

Expected: match on the "open source" note and the Links section (2 hits).

- [ ] **Step 3: Commit**

```bash
git add BUILTBYBIT.md
git commit -m "docs: add BuiltByBit listing page"
```

---

### Task 6: GitHub Actions build + release workflow

**Files:**
- Create: `.github/workflows/build.yml`

**Interfaces:**
- Consumes: Gradle wrapper (committed).
- Produces: `.jar` artifact on every push; GitHub Release attachment on `v*` tags.

- [ ] **Step 1: Create `.github/workflows/build.yml` with this exact content**

```yaml
name: Build

on:
  push:
    branches: [main]
    tags: ['v*']
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Build with Gradle
        run: ./gradlew build --no-daemon

      - name: Upload jar
        uses: actions/upload-artifact@v4
        with:
          name: NovaLobby-jar
          path: build/libs/*.jar

      - name: Release
        if: startsWith(github.ref, 'refs/tags/')
        uses: softprops/action-gh-release@v2
        with:
          files: build/libs/*.jar
```

- [ ] **Step 2: Verify file tree**

Run:

```bash
Test-Path .github/workflows/build.yml
git check-ignore build/libs  # should print nothing (build/ ignored)
```

Expected: `Test-Path` → True.

- [ ] **Step 3: Commit**

```bash
git add .github/workflows/build.yml
git commit -m "ci: add GitHub Actions build and release workflow"
```

---

### Task 7: Full validation gate

**Files:**
- None modified (pure verification).

**Interfaces:**
- Consumes: everything from Tasks 1-6.

- [ ] **Step 1: Full clean build**

Run: `.\gradlew.bat build --no-daemon`
Expected: BUILD SUCCESSFUL, jar at `build/libs/NovaLobby-1.0.0.jar`.

- [ ] **Step 2: Inspect packaged `plugin.yml` inside the jar**

Run:

```powershell
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead((Resolve-Path 'build/libs/NovaLobby-1.0.0.jar'))
$entry = $zip.Entries | Where-Object { $_.FullName -eq 'plugin.yml' }
$reader = New-Object System.IO.StreamReader($entry.Open())
$reader.ReadToEnd(); $reader.Dispose(); $zip.Dispose()
```

Expected output contains `name: NovaLobby`, `main: dev.itsdavlooo.novalobby.NovaLobby`.

- [ ] **Step 3: Grep sweep for leaked old branding (exclude docs)**

Run:

```bash
rg -n -i "vantey|lobbycore" --glob '!docs/**' --glob '*.md' .
rg -n -i "vantey|lobbycore" --glob '!docs/**' --glob '!*.md' .
```

Expected: no matches (both commands empty). Two separate sweeps to catch md (README may contain the word "lobby" but not "lobbycore"/"vantey") and source.

- [ ] **Step 4: `git status` clean**

Run: `git status --short`
Expected: empty (all tasks committed). `BUILTBYBIT.md` and `.github` and `LICENSE` present.

- [ ] **Step 5: Commit any stragglers if the sweep found leftovers**

Only if Steps 3 caught something: fix the file(s) per the rename map, re-run Step 1 and 3, then `git add -A && git commit -m "refactor: finish NovaLobby rename leftovers"`.

---

### Task 8: Publish wiring (remote + tag + push)

**Files:**
- `.git/config` (remote).

**Interfaces:**
- Consumes: clean validated tree.
- Produces: `origin` → `https://github.com/ItsDavloooDev/NovaLobby.git`, pushed `main` + tag `v1.0.0`.

- [ ] **Step 1: Verify the target remote is the user's repo**

Check the repo actually exists publicly (user created it; if 404, ask user to create `NovaLobby` under `ItsDavloooDev` first):

```bash
git ls-remote https://github.com/ItsDavloooDev/NovaLobby.git HEAD
```

Expected: a commit SHA line (repo exists/empty).

- [ ] **Step 2: Point origin to the new repo**

```bash
git remote remove origin   # ignore error if it doesn't exist
git remote add origin https://github.com/ItsDavloooDev/NovaLobby.git
git branch -M main
```

- [ ] **Step 3: Tag the release**

```bash
git tag -a v1.0.0 -m "NovaLobby v1.0.0"
```

- [ ] **Step 4: Push (requires user auth)**

```bash
git push -u origin main --tags
```

Expected: remote shows `main` + tag `v1.0.0`; GitHub Actions runs and attaches the jar to the `v1.0.0` Release.

- [ ] **Step 5: Final checklist for the user**

- [ ] Download `build/libs/NovaLobby-1.0.0.jar` from the GitHub Release.
- [ ] Publish the free resource on BuiltByBit; paste `BUILTBYBIT.md` content into the Description.
- [ ] Replace the `https://www.builtbybit.com/` link in `README.md` + the `[ADD SCREENSHOT URL]` placeholders in `BUILTBYBIT.md` with the live resource URL / screenshots once published.