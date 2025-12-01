package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.CreeperTweaks;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperUtils;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MobEntity.class)
public abstract class MobSpawnMixin {

    @Inject(method = "initialize", at = @At("TAIL"))
    public void creepertweaks$onFinalizeSpawn(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if ((Object)this instanceof CreeperEntity creeper) {
             if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.SPAWNER) {
                CreeperType type = CreeperTweaksConfig.getRandomCreeperType();
                
                if (CreeperTweaksConfig.debug) {
                    CreeperTweaks.LOGGER.info("Spawning custom creeper: {} at {}", type.name, creeper.getBlockPos());
                }
                
                CreeperUtils.applyCreeperType(creeper, type);
            }
        }
    }
}
