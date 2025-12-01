package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.BlockRegenerationManager;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionMixin {

    @Shadow @Final private ServerWorld world;
    @Shadow @Final @Nullable private Entity entity;

    @Inject(method = "destroyBlocks", at = @At("HEAD"))
    private void creepertweaks$onDestroyBlocks(List<BlockPos> positions, CallbackInfo ci) {
        if (!CreeperTweaksConfig.enableBlockRegeneration) return;

        if (entity instanceof CreeperEntity creeper && creeper instanceof CreeperMixinExtensions extensions) {
            if (extensions.creepertweaks$isBlockRegeneration()) {
                int initialDelay = extensions.creepertweaks$getRegenerationDelay();
                double speed = extensions.creepertweaks$getRegenerationSpeed();

                // Create a copy to sort for staggered regeneration
                List<BlockPos> sortedPositions = new ArrayList<>(positions);
                
                // Sort by Y level (ascending) then by distance from center to look like it's building up
                BlockPos center = creeper.getBlockPos();
                sortedPositions.sort(Comparator.comparingInt(Vec3i::getY)
                    .thenComparingDouble(pos -> pos.getSquaredDistance(center)));

                int count = 0;
                for (BlockPos pos : sortedPositions) {
                    BlockState state = world.getBlockState(pos);
                    if (!state.isAir() && !state.isOf(Blocks.BEDROCK) && !state.isOf(Blocks.BARRIER)) {
                        // Calculate delay based on speed (blocks per tick)
                        // delay = initial + (index / speed)
                        int staggerDelay = (int) (count / speed);
                        BlockRegenerationManager.scheduleRegeneration(world, pos, state, initialDelay + staggerDelay);
                        count++;
                    }
                }
                
                // Manually set blocks to air to prevent vanilla logic from dropping items
                for (BlockPos pos : positions) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                }
                positions.clear(); // Prevent vanilla logic from processing these blocks
            }
        }
    }
}
