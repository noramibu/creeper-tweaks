# Creeper Tweaks

A server-side Fabric mod for Minecraft 1.21.10 that adds various tweaks and custom behaviors to Creepers.

## Features

### Creeper Shearing
Right-click a Creeper with Shears to defuse it. The creeper will drop gunpowder and become unable to explode, though it may still hiss and swell. Useful for farming gunpowder or keeping creepers as pets.

### Confetti Creepers
Creepers have a configurable chance to explode into confetti instead of destroying blocks. When this happens, the creeper spawns colorful firework particles and makes a twinkling sound, but causes no damage or block destruction.

### Eco-Friendly Creepers
Creepers can be configured to drop 100% of the blocks they destroy, making it easier to clean up explosion craters. This is configurable per-creeper type with a chance value.

### Custom Creeper Types
The mod supports multiple creeper variants that can spawn naturally (**also you can create your own custom creepers in the config file**):
- Regular Creepers: Standard vanilla behavior with configurable tweaks
- Baby Creepers: Smaller size (0.5 scale), faster movement, smaller explosion radius
- Charged Creepers: Naturally spawning charged creepers with larger explosions
- Baby Charged Creepers: Combination of baby and charged properties
- Titan Creepers: Large size (1.5 scale), slow movement, high health, massive explosion radius
- Silent Creepers: Make no sound before exploding, making them harder to detect
- Plague Creepers: Explode into lingering poison clouds instead of destroying blocks

Each creeper type can be customized with:
- Spawn chance (weight-based probability)
- Scale (size multiplier)
- Health
- Movement speed
- Explosion radius
- Charged state
- Shearable status
- Confetti chance
- Eco-friendly drop chance
- Silent status
- Head drop chance
- Custom name tag with color codes
- Lingering explosion effects
- Block regeneration settings

### Creeper Head Drops
Creepers have a configurable chance to drop their head when killed by a player. This chance can be set per-creeper type.

### Lingering Explosions
Creepers can leave behind area effect clouds when they explode. You can configure the effect type (poison, instant_damage, nausea, etc.), duration, and radius. This is useful for creating special creeper variants like the Plague Creeper.

### Custom Name Tags
Each creeper type can have a custom name tag displayed above it. Supports Minecraft color codes using the & symbol (e.g., &4 for dark red, &6 for gold, &l for bold).

### Block Regeneration
Blocks destroyed by creepers can be configured to regenerate over time. You can set:
- Whether regeneration is enabled for each creeper type
- Delay before regeneration starts (in ticks)
- Speed of regeneration (blocks per tick)
- Particle type to show during regeneration

The regeneration happens in a staggered manner, with blocks restoring from bottom to top for a natural rebuilding effect.

## Commands

- `/creepertweaks reload` - Reloads the configuration file without restarting the server
- `/creepertweaks spawn <type>` - Spawns a specific creeper type at your location
- `/creepertweaks debug <target>` - Shows detailed information about a creeper entity

All commands require operator level 2.

## Configuration

The mod is configured via `config/creepertweaks.toml`. The configuration file is automatically created on first run.

### Global Settings

```
[global]
enableCreeperShearing = true              # Master switch for shearing
enableConfettiCreepers = true             # Master switch for confetti explosions
enableEcoFriendlyCreepers = true          # Master switch for eco-friendly drops
enableHeadDrops = true                    # Master switch for head drops
enableNameTags = true                     # Master switch for custom name tags
enableBlockRegeneration = true             # Master switch for block regeneration
enableRegenerationParticles = true         # Enable particles during regeneration
regenerationParticleType = "block"        # Particle type: "block", "smoke", "cloud", "enchant", etc.
debug = false                             # Enable debug logging
```

### Creeper Type Configuration

Each creeper type is defined in a `[[creepers]]` section:

```
[[creepers]]
name = "regular-creeper"                  # Unique identifier
nameTag = ""                              # Custom name (use & for color codes)
spawn-chance = 0.63                       # Spawn weight (relative to total)
scale = 1.0                               # Size multiplier
health = 20.0                             # Maximum health
speed = 0.25                              # Movement speed
explosionRadius = 3                       # Explosion radius in blocks
charged = false                           # Spawn as charged creeper
shearable = true                          # Can be defused with shears
confettiChance = 0.05                     # Chance to explode into confetti (0.0-1.0)
ecoFriendlyDropChance = 1.0               # Chance to drop 100% blocks (0.0-1.0)
silent = false                            # Make no sound before exploding
headDropChance = 0.25                     # Chance to drop head when killed (0.0-1.0)
lingering = false                         # Leave area effect cloud on explosion
lingeringType = "poison"                  # Effect type (poison, instant_damage, etc.)
lingeringDuration = 600                   # Effect duration in ticks (20 ticks = 1 second)
lingeringRadius = 3.0                     # Cloud radius
blockRegeneration = true                  # Blocks regenerate after explosion
regenerationDelay = 200                   # Delay before regeneration starts (ticks)
regenerationSpeed = 0.5                   # Blocks regenerated per tick
```