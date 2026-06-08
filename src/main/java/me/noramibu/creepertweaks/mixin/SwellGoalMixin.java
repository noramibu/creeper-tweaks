package me.noramibu.creepertweaks.mixin;

import java.util.EnumSet;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin extends Goal {
    @Shadow @Final private Creeper creeper;

    @Override
    public EnumSet<Goal.Flag> getFlags() {
        if (creepertweaks$allowsMovementDuringFuse()) {
            return EnumSet.noneOf(Goal.Flag.class);
        }
        return super.getFlags();
    }

    @Redirect(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;stop()V"
        )
    )
    private void creepertweaks$stopNavigationUnlessMovementAllowed(PathNavigation navigation) {
        if (!creepertweaks$allowsMovementDuringFuse()) {
            navigation.stop();
        }
    }

    @Unique
    private boolean creepertweaks$allowsMovementDuringFuse() {
        return this.creeper instanceof CreeperMixinExtensions extensions
            && extensions.creepertweaks$allowsMovementDuringFuse();
    }
}
