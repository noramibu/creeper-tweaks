package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerWorld.class)
public abstract class ServerLevelMixin {

    @ModifyArgs(method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/util/collection/Pool;Lnet/minecraft/registry/entry/RegistryEntry;)V", 
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionImpl;<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;Lnet/minecraft/util/math/Vec3d;FZLnet/minecraft/world/explosion/Explosion$DestructionType;)V"))
    private void creepertweaks$modifyExplosionBehavior(Args args) {
        Entity entity = args.get(1); // The exploding entity
        Explosion.DestructionType originalDestructionType = args.get(7); // The DestructionType (index 7)

        if (entity instanceof CreeperEntity creeper) {
            if (creeper instanceof CreeperMixinExtensions extensions) {
                if (CreeperTweaksConfig.enableEcoFriendlyCreepers && extensions.creepertweaks$isEcoFriendly()) {
                    if (originalDestructionType == Explosion.DestructionType.DESTROY_WITH_DECAY) {
                        args.set(7, Explosion.DestructionType.DESTROY);
                    }
                }
            }
        }
    }
}
