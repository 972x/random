# Doraemon Companion (Fabric mod)

An unofficial, non-commercial fan project for Minecraft. Adds **Doraemon**, a
shy robot-cat who shows up after your first night, shadows you until you earn
his trust, and — once tamed — follows you around, sits on command, and
answers questions about the Minecraft world in chat.

Not affiliated with, endorsed by, or sponsored by the Doraemon franchise
(Fujiko F. Fujio / Shogakukan / TV Asahi / ADK). Fan content, for personal
use — please don't redistribute or monetize it.

## Features

- **He finds you.** Once your first in-game night has passed, a wild,
  untamed Doraemon spawns near you (once per player per server session).
- **He's shy.** Until tamed, he shadows you from a loose distance (roughly
  8–25 blocks) instead of walking up to you, and actively flees if you get
  within ~6 blocks or look straight at him. Crouch (sneak) to approach
  without spooking him — that's the intended way to close the distance.
- **Taming.** Sneak up and feed him a **Dorayaki** to tame him on the spot —
  once he lets you get close enough to feed him, he always accepts.
- **Doraemon's Bell.** Rings once to instantly summon and bond a tamed
  companion — useful if you'd rather skip the chase, or to call back one who
  wandered off. Won't summon a second one if you already have one nearby.
- **Loyal companion behavior.** Once tamed, he follows you, sits on command
  (right-click him empty-handed), swims, and paths around obstacles like any
  vanilla pet.
- **Ask him anything.** Type a message starting with "Doraemon" or ending in
  "?" while he's tamed and within ~32 blocks. He checks two sources, in order:
  1. **Live world queries** — ask where the nearest village, sand, water, or
     lava is, and he'll actually search the world around you and answer with
     a direction and rough coordinates (e.g. *"The nearest village looks to
     be about 340 blocks to the northeast, around (512, 68, -120)."*).
  2. **Static knowledge base** — ~25 built-in facts about dimensions, mobs,
     mining, farming, redstone, enchanting, and more, matched by keyword.

  Both are local and rule-based — no network calls, no external AI service,
  entirely self-contained in the mod.
- **Craftable, not creative-only.** Both Dorayaki and Doraemon's Bell have
  survival-obtainable crafting recipes (see below) — nothing requires
  creative mode or commands.
- **No ears.** Canon-accurate: Doraemon famously lost his to a robot mouse,
  hence the round, ear-less head.

### Crafting

| Item | Recipe |
|---|---|
| **Dorayaki** ×2 | Shapeless: 2× Wheat + 1× Sugar + 1× Beetroot |
| **Doraemon's Bell** | Shaped 3×3: Gold Ingot border around a vanilla Bell (8 gold ingots + 1 bell) |

## Getting a compiled mod jar (no computer needed)

This repository includes a GitHub Actions workflow (`.github/workflows/build.yml`)
that compiles the mod automatically. You can get a working `.jar` entirely
from a phone or iPad — no desktop required for this step:

1. Open the repo on **github.com** (Safari/Chrome, not the GitHub mobile app —
   see note below) and go to the **Actions** tab.
2. Every push to `main` triggers a build automatically. If you want to
   re-run the latest one manually, open the **Build** workflow and use
   **"Run workflow"**.
3. Once a run shows a green checkmark, open it and scroll to **Artifacts**.
   Download `doraemon-mod` (a `.zip` containing the built `.jar`).

**About the GitHub mobile app:** it can show you Actions run status (green/red),
but it does not reliably support downloading workflow artifacts — that's a
file download the app doesn't expose. Use the "Open in Safari/Chrome" option
from the app, or just navigate to the repo in your mobile browser, to reach
the Artifacts section and download the zip.

If a run shows a red ❌ instead, open it and check the logs — see
"If something doesn't compile" below for the most likely culprits.

## Installing the mod

You'll still need an actual Minecraft-capable device (PC/Mac/Linux — not the
iPad itself) to play with the mod:

1. Install [Fabric Loader](https://fabricmc.net/use/) for **Minecraft 1.21.11**
   (or whatever version `gradle.properties` ends up targeting — see the Tech
   stack note below).
2. Download **Fabric API** for the same Minecraft version from
   [Modrinth](https://modrinth.com/mod/fabric-api) or
   [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) —
   this mod depends on it and won't load without it.
3. Drop both the Fabric API jar and this mod's jar (from the Actions
   artifact, or your own `./gradlew build` output in `build/libs/`) into your
   `.minecraft/mods` folder.
4. Launch Minecraft with the Fabric profile and start/join a world.

## Playing

1. Load into a fresh world (or any world about to hit its first sunrise).
2. Once the first night ends, watch for the chat message about "a strange
   round shape watching you from a distance" — Doraemon has spawned nearby.
3. Approach slowly. If you get too close or stare directly at him while
   standing, he'll bolt. **Hold sneak (crouch)** and approach — he tolerates
   that.
4. Right-click him with a Dorayaki in hand to tame him.
5. Right-click him empty-handed to toggle sit/follow.
6. Ask him things in chat: *"Doraemon, where's the nearest village?"*,
   *"how do I make a nether portal?"*, or just end a message in `?` while
   he's nearby.

## Tech stack

- Fabric mod loader, Minecraft **1.21.11**, Java 21.
- This mod was written and organized in a sandboxed environment with **no
  network access** to `fabricmc.net/develop`, Mojang's servers, or any
  Minecraft-related Maven repository (only generic Maven Central was
  reachable, which doesn't host any of the actual game/API dependencies).
  That means:
  - The exact `yarn_mappings` build number, `loader_version`, and
    `fabric_version` in `gradle.properties` could not be looked up or
    verified — double-check them against
    [fabricmc.net/develop](https://fabricmc.net/develop/) and bump if Loom
    can't resolve them.
  - **The build has never actually been compiled.** Everything below reflects
    a careful manual review of the code, not a verified green build. The
    included GitHub Actions workflow runs on GitHub's own infrastructure
    (which has normal internet access) specifically so the first real compile
    can happen automatically instead of requiring a local dev machine.

## Building from source (desktop)

```
./gradlew build
```

Gradle will download the wrapper, Loom, Minecraft, Yarn mappings, and Fabric
API automatically. Open the project in IntelliJ IDEA (with the Minecraft
Development plugin) or run `./gradlew genSources` for readable decompiled
Minecraft source and full autocomplete. The built jar lands in `build/libs/`.

### If something doesn't compile

A handful of Minecraft/Fabric APIs used here change fairly often between
versions, and a few were used without being able to verify the exact
signature. Most of these are wrapped defensively (see below) so a mismatch
degrades gracefully at runtime rather than crashing outright — but a couple
are structural and would need a compile-time fix. In rough order of risk:

- `EntityAttributes.MAX_HEALTH` / `MOVEMENT_SPEED` / `FOLLOW_RANGE` / `ARMOR`
  field names and types (`entity/DoraemonEntity.java`) — Mojang has renamed
  and re-typed these (registry entries) more than once across versions.
- `TameableEntity#setTamed(boolean)` (`entity/DoraemonEntity.java`,
  `item/SummoningBellItem.java`) — some versions add a second
  `updateAttributes` boolean parameter.
- `EntityType#create(World)` (`item/SummoningBellItem.java`, `DoraemonMod.java`)
  — occasionally gains/loses a `SpawnReason` parameter.
- `World#getTopPosition(Heightmap.Type, BlockPos)` (`DoraemonMod.java`,
  ground-height lookup for the first-night spawn).
- `Identifier.of(namespace, path)` vs `new Identifier(namespace, path)` — used
  consistently as `Identifier.of(...)` throughout; older versions used the
  constructor instead.
- Custom model API (`client/DoraemonEntityModel.java`,
  `client/DoraemonEntityRenderer.java`) — `ModelPartBuilder`/`ModelData`/
  `TexturedModelData`/`MobEntityRenderer` have been stable for a long stretch
  of versions, so this is lower-risk, but a rendering-pipeline rewrite in a
  given version could still affect it.

Your IDE's autocomplete/quick-fix will make any of these obvious once you're
actually building against the real dependencies.

### Built-in defensive fallbacks

A few of the higher-risk calls above — specifically the live world-query
lookups (`chat/DoraemonWorldQueries.java`, which uses
`ServerWorld#locateStructure` and `BlockPos#findClosest`) — are wrapped in a
`catch (Throwable ...)` that falls back to the static knowledge base instead
of erroring, and the chat listener, first-night spawn tick, and the
Summoning Bell's use handler are each wrapped so a bad guess in one of these
areas logs an error and fails gracefully instead of destabilizing the rest
of the mod or the server tick loop. This can't fix a genuine compile-time
mismatch (the game won't launch if a referenced method doesn't exist at all),
but it means a *runtime* API surprise degrades one feature instead of
breaking everything.

## Project layout

```
src/main/java/dev/doraemon/
  DoraemonMod.java                   - main entrypoint, chat listener,
                                        first-night spawn trigger
  chat/DoraemonKnowledgeBase.java    - static Q&A knowledge base
  chat/DoraemonWorldQueries.java     - live world/structure/block search
  entity/DoraemonEntity.java         - the companion mob
  entity/ModEntities.java            - entity type registration
  entity/ai/ShadowPlayerGoal.java    - pre-tame: loose distant follow
  entity/ai/HideFromPlayerGoal.java  - pre-tame: flee when approached/watched
  item/ModItems.java                 - item registration
  item/SummoningBellItem.java        - the bell's summon behavior
  client/
    DoraemonEntityModel.java         - tapered multi-tier voxel model
    DoraemonEntityRenderer.java      - renderer + texture binding
    DoraemonModClient.java           - client entrypoint
    ModModelLayers.java              - model layer registration
src/main/resources/
  fabric.mod.json
  assets/doraemon/                   - lang, textures, item/entity models
  data/doraemon/                     - loot table, crafting recipes
.github/workflows/build.yml          - CI build (see "Getting a compiled
                                        mod jar" above)
```

## About the art

Entity and item textures are pixel art generated programmatically by a small
Python script (no image-editing tools were available in the environment this
was built in) rather than hand-painted or AI-generated. The model geometry
itself is also hand-built: Minecraft's entity model system is fundamentally
voxel/cuboid-based with no support for smooth curved surfaces, so the head
and body are each three stacked, tapered cuboid tiers (narrow cap → wide
middle → narrow cap) to approximate roundness, rather than a single box. The
UV texture layout was computed by the same script that generated the
texture, so the Java model and the PNG are guaranteed to line up — see the
doc comment at the top of `client/DoraemonEntityModel.java` for details.

Swap in your own `textures/entity/doraemon.png`, `textures/item/dorayaki.png`,
`textures/item/summoning_bell.png`, or `assets/doraemon/icon.png` any time —
the UV layout for the entity texture is documented in
`client/DoraemonEntityModel.java` if you want to line up custom art with the
existing model.
