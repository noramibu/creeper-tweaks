package me.noramibu.creepertweaks.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface CreeperAccessor {
    @Accessor("DATA_IS_POWERED")
    static EntityDataAccessor<Boolean> getCHARGED() {
        throw new UnsupportedOperationException();
    }

    @Accessor("explosionRadius")
    void setExplosionRadius(int radius);

    @Accessor("maxSwell")
    void setMaxSwell(int maxSwell);
}
