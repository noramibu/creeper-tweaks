package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "dropLoot", at = @At("HEAD"))
    protected void creepertweaks$onDropLoot(ServerWorld world, DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if ((Object)this instanceof CreeperEntity creeper && creeper instanceof CreeperMixinExtensions extensions) {
            if (CreeperTweaksConfig.enableHeadDrops && causedByPlayer) {
                double chance = extensions.creepertweaks$getHeadDropChance();
                if (chance > 0 && this.random.nextDouble() < chance) {
                     this.dropItem(world, Items.CREEPER_HEAD);
                }
            }
        }
    }
}

