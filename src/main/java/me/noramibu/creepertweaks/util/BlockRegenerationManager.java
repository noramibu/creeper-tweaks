package me.noramibu.creepertweaks.util;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockRegenerationManager {
    private static final Map<BlockPos, RegenerationEntry> pendingRegenerations = new ConcurrentHashMap<>();

    private record RegenerationEntry(ServerLevel world, BlockState originalState, long regenerateAt) {}

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(BlockRegenerationManager::onTick);
    }

    public static void scheduleRegeneration(ServerLevel world, BlockPos pos, BlockState originalState, int delayTicks) {
        if (originalState.isAir() || originalState.is(Blocks.BEDROCK) || originalState.is(Blocks.BARRIER)) {
            return; // Don't regenerate air or unbreakable blocks
        }
        long regenerateAt = world.getGameTime() + delayTicks;
        pendingRegenerations.put(pos.immutable(), new RegenerationEntry(world, originalState, regenerateAt));
    }

    private static void onTick(ServerLevel world) {
        long currentTime = world.getGameTime();
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
                if (currentState.isAir() || currentState.getFluidState().isSource()) {
                    BlockState originalState = data.originalState();
                    world.setBlockAndUpdate(pos, originalState);
                    
                    // Spawn particles if enabled
                    if (CreeperTweaksConfig.enableRegenerationParticles) {
                        ParticleOptions particleEffect = getParticleEffect(originalState);
                        if (particleEffect != null) {
                            world.sendParticles(
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

    private static ParticleOptions getParticleEffect(BlockState blockState) {
        String particleTypeStr = CreeperTweaksConfig.regenerationParticleType;
        if (particleTypeStr == null || particleTypeStr.isEmpty()) {
            particleTypeStr = "block"; // Default
        }

        Identifier particleId = Identifier.parse(particleTypeStr.toLowerCase());
        ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(particleId);

        if (particleType == null) {
            // Fallback to default BLOCK particle
            return new BlockParticleOption(ParticleTypes.BLOCK, blockState);
        }

        // SimpleParticleType implements ParticleEffect directly, so we can use it as-is
        if (particleType instanceof SimpleParticleType simpleParticleType) {
            return simpleParticleType;
        }

        // Special handling for BLOCK and FALLING_DUST particles (they need BlockStateParticleEffect)
        if (particleType == ParticleTypes.BLOCK || particleType == ParticleTypes.FALLING_DUST || particleType == ParticleTypes.BLOCK_MARKER) {
            return new BlockParticleOption((ParticleType<BlockParticleOption>) particleType, blockState);
        }

        // For other particle types, fallback to block particle
        return new BlockParticleOption(ParticleTypes.BLOCK, blockState);
    }
}
