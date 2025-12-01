package me.noramibu.creepertweaks.util;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.mixin.CreeperAccessor;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class CreeperUtils {
    public static void applyCreeperType(CreeperEntity creeper, CreeperType type) {
        // Apply scale
        if (type.scale != 1.0) {
            Objects.requireNonNull(creeper.getAttributeInstance(EntityAttributes.SCALE)).setBaseValue(type.scale);
        }

        // Apply speed
        if (type.speed != 0.25) {
            Objects.requireNonNull(creeper.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(type.speed);
        }

        // Apply explosion radius
        if (type.explosionRadius != 3) {
            ((CreeperAccessor) creeper).setExplosionRadius(type.explosionRadius);
        }

        // Apply charged
        if (type.charged) {
            creeper.getDataTracker().set(CreeperAccessor.getCHARGED(), true);
        }

        // Apply health
        if (type.health != 20.0) {
            Objects.requireNonNull(creeper.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(type.health);
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
            creeper.setCustomName(Text.literal(formattedName));
            creeper.setCustomNameVisible(true);
        }
    }
}
