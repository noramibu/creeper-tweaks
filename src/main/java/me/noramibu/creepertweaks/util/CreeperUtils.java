package me.noramibu.creepertweaks.util;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.mixin.CreeperAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import java.util.Objects;

public class CreeperUtils {
    public static void applyCreeperType(Creeper creeper, CreeperType type) {
        // Apply scale
        if (type.scale != 1.0) {
            Objects.requireNonNull(creeper.getAttribute(Attributes.SCALE)).setBaseValue(type.scale);
        }

        // Apply speed
        if (type.speed != 0.25) {
            Objects.requireNonNull(creeper.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(type.speed);
        }

        // Apply explosion radius
        if (type.explosionRadius != 3) {
            ((CreeperAccessor) creeper).setExplosionRadius(type.explosionRadius);
        }

        if (CreeperTweaksConfig.setCustomFuseTime) {
            int fuseTime = Math.max(3, type.fuseTime);
            if (fuseTime != 30) {
                ((CreeperAccessor) creeper).setMaxSwell(fuseTime);
            }
        }

        // Apply charged
        if (type.charged) {
            creeper.getEntityData().set(CreeperAccessor.getCHARGED(), true);
        }

        // Apply health
        if (type.health != 20.0) {
            Objects.requireNonNull(creeper.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(type.health);
            creeper.setHealth((float) type.health);
        }

        if (creeper instanceof CreeperMixinExtensions extensions) {
            extensions.creepertweaks$setShearable(type.shearable);
            extensions.creepertweaks$setConfettiChance(type.confettiChance);

            // Apply eco-friendly logic (0.0-1.0 scale)
            boolean isEcoFriendly = creeper.getRandom().nextDouble() < type.ecoFriendlyDropChance;
            extensions.creepertweaks$setEcoFriendly(isEcoFriendly);
            
            // Apply head drop chance
            extensions.creepertweaks$setHeadDropChance(type.headDropChance);
            extensions.creepertweaks$setAllowMovementDuringFuse(type.allowMovementDuringFuse);

            // Apply lingering explosion settings
            extensions.creepertweaks$setLingering(type.lingering);
            extensions.creepertweaks$setLingeringType(type.lingeringType);
            extensions.creepertweaks$setLingeringDuration(type.lingeringDuration);
            extensions.creepertweaks$setLingeringRadius((float) type.lingeringRadius);
            
            // Apply block regeneration settings
            if (CreeperTweaksConfig.enableBlockRegeneration) {
                extensions.creepertweaks$setBlockRegeneration(type.blockRegeneration);
                extensions.creepertweaks$setRegenerationDelay(type.regenerationDelay);
                extensions.creepertweaks$setRegenerationSpeed(type.regenerationSpeed);
            }
        }

        // Apply silent logic (using vanilla silent tag)
        if (type.silent) {
            creeper.setSilent(true);
        }

        // Apply Custom Name
        if (CreeperTweaksConfig.enableNameTags && type.nameTag != null && !type.nameTag.isEmpty()) {
            // Replace & with section sign for simple formatting
            String formattedName = type.nameTag.replace("&", "§");
            creeper.setCustomName(Component.literal(formattedName));
            creeper.setCustomNameVisible(true);
        }
    }
}
