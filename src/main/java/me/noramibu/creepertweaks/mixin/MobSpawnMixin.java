package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.CreeperTweaks;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperUtils;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Mob.class)
public abstract class MobSpawnMixin {

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void creepertweaks$onFinalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if ((Object)this instanceof Creeper creeper) {
             if (spawnReason == EntitySpawnReason.NATURAL || spawnReason == EntitySpawnReason.CHUNK_GENERATION || spawnReason == EntitySpawnReason.SPAWNER) {
                CreeperType type = CreeperTweaksConfig.getRandomCreeperType();
                
                if (CreeperTweaksConfig.debug) {
                    CreeperTweaks.LOGGER.info("Spawning custom creeper: {} at {}", type.name, creeper.blockPosition());
                }
                
                CreeperUtils.applyCreeperType(creeper, type);
            }
        }
    }
}
