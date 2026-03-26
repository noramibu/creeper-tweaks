package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.BlockRegenerationManager;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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

@Mixin(ServerExplosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private ServerLevel level;
    @Shadow @Final @Nullable private Entity source;

    @Inject(method = "interactWithBlocks", at = @At("HEAD"))
    private void creepertweaks$onDestroyBlocks(List<BlockPos> positions, CallbackInfo ci) {
        if (!CreeperTweaksConfig.enableBlockRegeneration) return;

        if (source instanceof Creeper creeper && creeper instanceof CreeperMixinExtensions extensions) {
            if (extensions.creepertweaks$isBlockRegeneration()) {
                int initialDelay = extensions.creepertweaks$getRegenerationDelay();
                double speed = extensions.creepertweaks$getRegenerationSpeed();

                // Create a copy to sort for staggered regeneration
                List<BlockPos> sortedPositions = new ArrayList<>(positions);
                
                // Sort by Y level (ascending) then by distance from center to look like it's building up
                BlockPos center = creeper.blockPosition();
                sortedPositions.sort(Comparator.comparingInt(Vec3i::getY)
                    .thenComparingDouble(pos -> pos.distSqr(center)));

                int count = 0;
                for (BlockPos pos : sortedPositions) {
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir() && !state.is(Blocks.BEDROCK) && !state.is(Blocks.BARRIER)) {
                        // Calculate delay based on speed (blocks per tick)
                        // delay = initial + (index / speed)
                        int staggerDelay = (int) (count / speed);
                        BlockRegenerationManager.scheduleRegeneration(level, pos, state, initialDelay + staggerDelay);
                        count++;
                    }
                }
                
                // Manually set blocks to air to prevent vanilla logic from dropping items
                for (BlockPos pos : positions) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
                positions.clear(); // Prevent vanilla logic from processing these blocks
            }
        }
    }
}
