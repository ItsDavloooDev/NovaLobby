# NovaLobby — Rebrand & Open-Source Release Design

Date: 2026-08-12
Status: Draft

## Goal

Turn the current VanteyLobby (working title "LobbyCore") plugin into **NovaLobby**, a
professionally branded, neutral-by-default lobby/hub plugin, released open source on
GitHub (`ItsDavloooDev/NovaLobby`) and as a free resource on BuiltByBit. Both landing
pages link to each other. No new features: rebrand + cleanup only.

## Scope

- Full source rebrand (package, class, plugin metadata, commands, permissions, messages, config).
- Neutral configuration defaults (no server-specific ranks/text/items).
- MIT license, README for GitHub, BuiltByBit description page, GitHub Actions CI with release jars.
- Verified build via Gradle before push.

## Non-goals

- No new gameplay features.
- No paid/premium distribution (free on BuiltByBit).
- No Folia code changes. Compat claims in README = "Paper 1.20 – 1.21.5+"; Folia NOT promoted (not tested).
- No multi-language localization system.

## Approach

Rebrand in-place in the current working tree (repo has no commits yet — clean history
is a single initial commit), then add remote `origin` for `ItsDavloooDev/NovaLobby`.

## A. Source rebrand map

| Current | Target |
|---|---|
| package `dev.itsdavlooo.VanteyLobby` | `dev.itsdavlooo.novalobby` |
| class `VanteyLobby` | `NovaLobby` |
| plugin name `LobbyCore` | `NovaLobby` |
| command `/lobbycore` (aliases `lc`, `lcore`) | `/novalobby` (aliases `nova`, `nl`) |
| permissions prefix `lobbycore.` | `novalobby.` |
| messages prefix "Vantey" | prefix "NovaLobby" (gradient) |
| feature name "EnderButt" | unchanged |

### plugin.yml
- `name: NovaLobby`, `main: dev.itsdavlooo.novalobby.NovaLobby`, author `ItsDavlooo`.
- Command `/novalobby`, usage `/novalobby reload`, aliases `[nova, nl]`, permission `novalobby.admin`.
- Permission tree `novalobby.*`, `novalobby.admin`, `.setspawn`, `.gamemode.*`, `.playtime`,
  `.fly`, `.fly.bypass.damage` — all renamed from `lobbycore.*`.
- Description becomes the short one-liner used on GitHub + BuiltByBit.

### messages.yml
- `prefix: "<gradient:#00E5FF:#7C4DFF>NovaLobby</gradient> &8» &7"`.

### config.yml cleanup (neutral defaults)
- Selector example: remove server-specific "?" question-mark skulls; keep one clean filler +
  a "Survival" example with neutral lore (`&7Right click to choose a server.` etc.).
- `login-messages.ranks`: replace Lifesteal/IT rank list with neutral examples
  (`owner`, `admin`, `moderator`) + comment that this is an example list.
- All remaining strings already English/neutral. Header comment retitled to "NovaLobby - config.yml".

## C. Build & CI

- Keep Gradle Kotlin DSL; toolchain Java 21; Paper API 1.21.5.
- `gradle.properties`: `version=1.0.0` (was `1.0`).
- New `.github/workflows/build.yml`:
  - Trigger: push (main), pull requests, tag `v*`.
  - Jobs: checkout, setup JDK 21, `gradlew build`.
  - Upload artifact `build/libs/NovaLobby-*.jar`.
  - On tag `v*`: attach jar to GitHub Release.

## D. Repository structure

```
NovaLobby/
  .github/workflows/build.yml
  .gitignore
  BUILTBYBIT.md
  LICENSE                 (MIT)
  README.md
  build.gradle.kts
  gradle.properties
  settings.gradle.kts
  gradle/wrapper/...
  gradlew, gradlew.bat
  src/main/...
```

Removed from repo root: `ZetsyHub.jar` (stale binary). `.gitignore` already covers
`.gradle/`, `build/`, `run/`, `.idea/` — no change needed.

### README.md (GitHub)
Sections:
1. Title banner (text "NovaLobby" with tagline).
2. Badges row: MIT license, MC versions (1.20 – 1.21.5+), Paper, latest release, **link BuiltByBit**.
3. Feature list (server selector GUI w/ BungeeCord/Velocity actions, launchpads, grappling
   hook, EnderButt dash, player hider, time selector, spawn handling, protection, playtime,
   gamemode/fly commands, rank-based login messages, PlaceholderAPI/LuckPerms soft-depend).
4. `[ADD SCREENSHOT]` placeholder blocks.
5. Requirements / Install: Paper 1.20+, drop jar in `plugins/`, optional PlaceholderAPI++, LuckPerms.
6. Build from source: `./gradlew build`.
7. Commands table (`/novalobby`, `/setspawn`, `/playtime`, `/fly`, `/gmc`/`/gms`/`/gmsp`/`/gma`).
8. Permissions summary (link to full plugin.yml).
9. Configuration overview pointer.
10. BuiltByBit link (callout).
11. MIT license + credits.

### BUILTBYBIT.md (BuiltByBit listing)
Ready-to-paste resource description:
- Short tagline + full description (copy-friendly plain/Markdown text BuiltByBit accepts).
- Feature highlights.
- Tested on: Version (1.0.0), MC 1.20 – 1.21.5+, Paper.
- Installation steps.
- Commands/permissions quick list.
- **Link back to GitHub source** (note: BSD-free; open source).
- Changelog stub.
- `[ADD SCREENSHOT]` placeholders.

## E. Extra cleanup

- `grep` sweep for `Vantey|LobbyCore|lobbycore|vanteylobby` must return 0 (excluding docs).
- Remove stale `ZetsyHub.jar`.

## F. Validation

1. `gradlew build` succeeds.
2. Inspect generated `build/libs/NovaLobby-1.0.0.jar` — `plugin.yml` present, main class resolves.
3. Final grep sweep clean.
4. `git status` clean after commit.

## Deliverables

- Rebranded, buildable source (single initial commit `NovaLobby 1.0.0`).
- `README.md`, `BUILTBYBIT.md`, `LICENSE` (MIT), CI workflow.
- Remote `origin` -> `https://github.com/ItsDavloooDev/NovaLobby.git`.

(Open items set before user: no feature changes; EnderButt name kept; free resource on BuiltByBit; CI with release jars; assets left as placeholders.)