# Doraemon Companion (Fabric mod)

An unofficial, non-commercial fan project for Minecraft. Adds **Doraemon**, a
shy robot-cat who shows up after your first night, shadows you until you earn
his trust, and once tamed follows you around and answers questions about the
Minecraft world in chat.

Not affiliated with, endorsed by, or sponsored by the Doraemon franchise
(Fujiko F. Fujio / Shogakukan / TV Asahi / ADK). Fan content, for personal
use — please don't redistribute or monetize it.

## What it does

- **He finds you** — once your first in-game night has passed, a wild,
  untamed Doraemon spawns near you (once per player per server session).
- **He's shy** — until tamed, he shadows you from a distance (roughly
  8-25 blocks) instead of approaching, and actively flees if you get within
  ~6 blocks or look straight at him. Crouch (sneak) to approach without
  spooking him — that's how you get close enough to tame him.
- **Taming** — sneak up and feed him a **Dorayaki** (craft: 2 wheat + 1 sugar
  + 1 beetroot → 2 Dorayaki) to tame him on the spot.
- **Doraemon's Bell** — craft with 8 gold ingots around a vanilla Bell
  (3x3 shaped recipe) to summon and instantly bond a companion if you'd
  rather skip the chase, or to re-summon one who wandered off. Won't summon a
  second one if you already have one nearby.
- Once tamed, he follows you, sits on command (right-click empty-handed), and
  swims/paths around obstacles.
- **Ask him anything** — type a message starting with "Doraemon" or ending in
  "?" while he's within ~32 blocks and tamed. He checks two sources, in order:
  1. **Live world queries** (`chat/DoraemonWorldQueries.java`) — ask where the
     nearest village, sand, water, or lava is, and he'll actually search the
     world around you (`ServerWorld#locateStructure` for villages,
     `BlockPos#findClosest` for blocks) and give you a direction and rough
     coordinates.
  2. **Static knowledge base** (`chat/DoraemonKnowledgeBase.java`) — ~25
     built-in facts about dimensions, mobs, mining, farming, redstone,
     enchanting, and more, matched by keyword.

  Both are local and rule-based — no network calls, no external AI service,
  entirely self-contained in the mod.

## Tech stack

- Fabric mod loader, Minecraft **1.21.11**, Java 21.
- This build environment has no network access to `fabricmc.net/develop` or
  Maven repositories, so the exact `yarn_mappings` build number,
  `loader_version`, and `fabric_version` in `gradle.properties` could not be
  looked up/verified here. Double-check them before your first build and bump
  if Loom can't resolve them.

## Building

This was written and organized in a sandboxed environment with **no access**
to Maven Central, `maven.fabricmc.net`, or `services.gradle.org`, so it could
not be compiled or run here — the Gradle wrapper couldn't even download its
own distribution. The code is complete and follows standard, well-established
Fabric API patterns, but **you'll need to do the first real build yourself**
on a machine with normal internet access:

```
./gradlew build
```

Gradle will download the wrapper, Loom, Minecraft, Yarn mappings, and Fabric
API automatically. Open the project in IntelliJ IDEA (with the Minecraft
Development plugin) or run `./gradlew genSources` for readable decompiled
Minecraft source and full autocomplete.

### If something doesn't compile

A few areas of the Minecraft/Fabric API change with almost every release. If
the first build turns up errors, these are the most likely spots to need a
small tweak (your IDE's autocomplete will make the fix obvious):

- `EntityAttributes.MAX_HEALTH` / `MOVEMENT_SPEED` / `FOLLOW_RANGE` / `ARMOR`
  field names (`entity/DoraemonEntity.java`) — Mojang has renamed and
  re-typed these (registry entries) more than once.
- `TameableEntity#setTamed(boolean)` — some versions add a second
  `updateAttributes` boolean parameter.
- `EntityType#create(World)` (`item/SummoningBellItem.java`,
  `DoraemonMod.java`) — occasionally gains/loses a `SpawnReason` parameter.
- `ServerWorld#locateStructure(TagKey, BlockPos, int, boolean)`
  (`chat/DoraemonWorldQueries.java`) — the structure-search plumbing is one of
  the more frequently reshuffled corners of the API; the search radius is in
  chunks, not blocks.
- `World#getTopPosition(Heightmap.Type, BlockPos)` (`DoraemonMod.java`,
  ground-height lookup for the first-night spawn).
- `Identifier.of(namespace, path)` vs `new Identifier(namespace, path)` — used
  consistently as `Identifier.of(...)` throughout; older versions used the
  constructor instead.

## Project layout

```
src/main/java/dev/doraemon/
  DoraemonMod.java                 - main entrypoint, chat listener,
                                      first-night spawn trigger
  chat/DoraemonKnowledgeBase.java  - static Q&A knowledge base
  chat/DoraemonWorldQueries.java   - live world/structure/block search
  entity/DoraemonEntity.java       - the companion mob
  entity/ModEntities.java          - entity type registration
  entity/ai/ShadowPlayerGoal.java  - pre-tame: loose distant follow
  entity/ai/HideFromPlayerGoal.java - pre-tame: flee when approached/watched
  item/ModItems.java               - item registration
  item/SummoningBellItem.java      - the bell's summon behavior
  client/                          - custom voxel model + renderer (client-only)
src/main/resources/
  fabric.mod.json
  assets/doraemon/                 - lang, textures, item models
  data/doraemon/                   - loot table, crafting recipes
```

Entity and item textures are simple placeholder pixel art generated
programmatically (no art tools were available in the build environment) —
swap in your own `textures/entity/doraemon.png`,
`textures/item/dorayaki.png`, and `textures/item/summoning_bell.png` for a
nicer look; the UV layout is documented in
`client/DoraemonEntityModel.java`.
