package me.noramibu.creepertweaks.util;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockRegenerationManager {
    private static final Map<BlockPos, RegenerationEntry> pendingRegenerations = new ConcurrentHashMap<>();

    private record RegenerationEntry(ServerWorld world, BlockState originalState, long regenerateAt) {}

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(BlockRegenerationManager::onTick);
    }

    public static void scheduleRegeneration(ServerWorld world, BlockPos pos, BlockState originalState, int delayTicks) {
        if (originalState.isAir() || originalState.isOf(Blocks.BEDROCK) || originalState.isOf(Blocks.BARRIER)) {
            return; // Don't regenerate air or unbreakable blocks
        }
        long regenerateAt = world.getTime() + delayTicks;
        pendingRegenerations.put(pos.toImmutable(), new RegenerationEntry(world, originalState, regenerateAt));
    }

    private static void onTick(ServerWorld world) {
        long currentTime = world.getTime();
        List<BlockPos> toRemove = new ArrayList<>();

        // Iterate safely
        for (Map.Entry<BlockPos, RegenerationEntry> entry : pendingRegenerations.entrySet()) {
            BlockPos pos = entry.getKey();
            RegenerationEntry data = entry.getValue();

            // Check if it belongs to this world and if it's time
            if (data.world() == world && currentTime >= data.regenerateAt()) {
                // Only restore if currently Air (prevent overwriting player builds if they built there already)
                // Or we could force overwrite. For now, let's be safe and only replace air/fluid.
                BlockState currentState = world.getBlockState(pos);
                if (currentState.isAir() || currentState.getFluidState().isStill()) {
                    BlockState originalState = data.originalState();
                    world.setBlockState(pos, originalState);
                    
                    // Spawn particles if enabled
                    if (CreeperTweaksConfig.enableRegenerationParticles) {
                        ParticleEffect particleEffect = getParticleEffect(originalState);
                        if (particleEffect != null) {
                            world.spawnParticles(
                                particleEffect,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                8, // count
                                0.25, // deltaX
                                0.25, // deltaY
                                0.25, // deltaZ
                                0.1 // speed
                            );
                        }
                    }
                }
                toRemove.add(pos);
            }
        }

        for (BlockPos pos : toRemove) {
            pendingRegenerations.remove(pos);
        }
    }

    private static ParticleEffect getParticleEffect(BlockState blockState) {
        String particleTypeStr = CreeperTweaksConfig.regenerationParticleType;
        if (particleTypeStr == null || particleTypeStr.isEmpty()) {
            particleTypeStr = "block"; // Default
        }

        Identifier particleId = Identifier.of(particleTypeStr.toLowerCase());
        ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(particleId);

        if (particleType == null) {
            // Fallback to default BLOCK particle
            return new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
        }

        // SimpleParticleType implements ParticleEffect directly, so we can use it as-is
        if (particleType instanceof SimpleParticleType simpleParticleType) {
            return simpleParticleType;
        }

        // Special handling for BLOCK and FALLING_DUST particles (they need BlockStateParticleEffect)
        if (particleType == ParticleTypes.BLOCK || particleType == ParticleTypes.FALLING_DUST || particleType == ParticleTypes.BLOCK_MARKER) {
            return new BlockStateParticleEffect((ParticleType<BlockStateParticleEffect>) particleType, blockState);
        }

        // For other particle types, fallback to block particle
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
    }
}
