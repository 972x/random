# Doraemon Companion (Fabric mod)

An unofficial, non-commercial fan project for Minecraft. Adds **Doraemon**, a
tameable robot-cat companion who follows you around and answers questions
about the Minecraft world in chat.

Not affiliated with, endorsed by, or sponsored by the Doraemon franchise
(Fujiko F. Fujio / Shogakukan / TV Asahi / ADK). Fan content, for personal
use — please don't redistribute or monetize it.

## What it does

- **Doraemon's Bell** — right-click to summon Doraemon at your location, tamed
  and bonded to you on the spot. Won't summon a second one if you already
  have one nearby.
- **Dorayaki** — feed it to a wild/untamed Doraemon to tame him (like feeding
  a wolf bones), if you'd rather not use the bell.
- Once tamed, Doraemon follows you, sits on command (right-click empty-handed),
  and swims/paths around obstacles.
- **Ask him anything** — type a message starting with "Doraemon" or ending in
  "?" while he's within ~32 blocks, and he'll answer from a built-in
  knowledge base covering dimensions, mobs, mining, farming, redstone,
  enchanting, and more (`dev.doraemon.chat.DoraemonKnowledgeBase`). This is a
  local, rule-based keyword matcher — no network calls, no external AI
  service, entirely self-contained in the mod.

## Tech stack

- Fabric mod loader, Minecraft **1.21.1**, Java 21.
- "1.21.11" (as originally requested) isn't a real Minecraft version — Mojang's
  1.21.x patch numbers never reached double digits as of this writing. 1.21.1
  is the closest real, stable, Fabric-supported release. If a newer 1.21.x is
  current by the time you build this, bump `minecraft_version`,
  `yarn_mappings`, `loader_version`, and `fabric_version` in
  `gradle.properties` (check https://fabricmc.net/develop/ for current
  numbers).

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
- `EntityType#create(World)` (`item/SummoningBellItem.java`) — occasionally
  gains/loses a `SpawnReason` parameter.
- `Identifier.of(namespace, path)` vs `new Identifier(namespace, path)` — used
  consistently as `Identifier.of(...)` throughout; older versions used the
  constructor instead.

## Project layout

```
src/main/java/dev/doraemon/
  DoraemonMod.java              - main entrypoint, chat listener wiring
  chat/DoraemonKnowledgeBase.java - the Q&A knowledge base
  entity/DoraemonEntity.java    - the companion mob
  entity/ModEntities.java       - entity type registration
  item/ModItems.java            - item registration
  item/SummoningBellItem.java   - the bell's summon behavior
  client/                       - custom voxel model + renderer (client-only)
src/main/resources/
  fabric.mod.json
  assets/doraemon/              - lang, textures, item models
  data/doraemon/                - loot table
```

Entity and item textures are simple placeholder pixel art generated
programmatically (no art tools were available in the build environment) —
swap in your own `textures/entity/doraemon.png`,
`textures/item/dorayaki.png`, and `textures/item/summoning_bell.png` for a
nicer look; the UV layout is documented in
`client/DoraemonEntityModel.java`.
