package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "dropFromLootTable", at = @At("HEAD"))
    protected void creepertweaks$onDropLoot(ServerLevel world, DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if ((Object)this instanceof Creeper creeper && creeper instanceof CreeperMixinExtensions extensions) {
            if (CreeperTweaksConfig.enableHeadDrops && causedByPlayer) {
                double chance = extensions.creepertweaks$getHeadDropChance();
                if (chance > 0 && this.random.nextDouble() < chance) {
                     this.spawnAtLocation(world, Items.CREEPER_HEAD);
                }
            }
        }
    }
}

