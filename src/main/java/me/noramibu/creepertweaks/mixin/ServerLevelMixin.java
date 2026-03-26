package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @ModifyArgs(method = "explode", 
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;Lnet/minecraft/world/phys/Vec3;FZLnet/minecraft/world/level/Explosion$BlockInteraction;)V"))
    private void creepertweaks$modifyExplosionBehavior(Args args) {
        Entity entity = args.get(1); // The exploding entity
        Explosion.BlockInteraction originalDestructionType = args.get(7); // The DestructionType (index 7)

        if (entity instanceof Creeper creeper) {
            if (creeper instanceof CreeperMixinExtensions extensions) {
                if (CreeperTweaksConfig.enableEcoFriendlyCreepers && extensions.creepertweaks$isEcoFriendly()) {
                    if (originalDestructionType == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                        args.set(7, Explosion.BlockInteraction.DESTROY);
                    }
                }
            }
        }
    }
}
