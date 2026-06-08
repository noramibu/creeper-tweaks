package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;
import java.util.Optional;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster implements CreeperMixinExtensions {

    private boolean creepertweaks$defused = false;
    private boolean creepertweaks$shearable = true;
    private double creepertweaks$confettiChance = 0.0;
    private boolean creepertweaks$ecoFriendly = false;
    private double creepertweaks$headDropChance = 0.25;
    private boolean creepertweaks$allowMovementDuringFuse = false;

    // Lingering fields
    private boolean creepertweaks$lingering = false;
    private String creepertweaks$lingeringType = "POISON";
    private int creepertweaks$lingeringDuration = 600;
    private float creepertweaks$lingeringRadius = 3.0f;

    // Block Regeneration fields
    private boolean creepertweaks$blockRegeneration = false;
    private int creepertweaks$regenerationDelay = 1200;
    private double creepertweaks$regenerationSpeed = 10.0;

    protected CreeperMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void creepertweaks$setShearable(boolean shearable) {
        this.creepertweaks$shearable = shearable;
    }

    @Override
    public void creepertweaks$setConfettiChance(double chance) {
        this.creepertweaks$confettiChance = chance;
    }

    @Override
    public void creepertweaks$setEcoFriendly(boolean ecoFriendly) {
        this.creepertweaks$ecoFriendly = ecoFriendly;
    }
    
    @Override
    public void creepertweaks$setHeadDropChance(double chance) {
        this.creepertweaks$headDropChance = chance;
    }

    @Override
    public boolean creepertweaks$isShearable() {
        return this.creepertweaks$shearable;
    }

    @Override
    public double creepertweaks$getConfettiChance() {
        return this.creepertweaks$confettiChance;
    }

    @Override
    public boolean creepertweaks$isEcoFriendly() {
        return this.creepertweaks$ecoFriendly;
    }
    
    @Override
    public double creepertweaks$getHeadDropChance() {
        return this.creepertweaks$headDropChance;
    }

    @Override
    public void creepertweaks$setAllowMovementDuringFuse(boolean allowMovementDuringFuse) {
        this.creepertweaks$allowMovementDuringFuse = allowMovementDuringFuse;
    }

    @Override
    public boolean creepertweaks$allowsMovementDuringFuse() {
        return CreeperTweaksConfig.allowMovementDuringFuse && this.creepertweaks$allowMovementDuringFuse;
    }

    @Override
    public void creepertweaks$setLingering(boolean lingering) {
        this.creepertweaks$lingering = lingering;
    }

    @Override
    public boolean creepertweaks$isLingering() {
        return this.creepertweaks$lingering;
    }

    @Override
    public void creepertweaks$setLingeringType(String type) {
        this.creepertweaks$lingeringType = type;
    }

    @Override
    public String creepertweaks$getLingeringType() {
        return this.creepertweaks$lingeringType;
    }

    @Override
    public void creepertweaks$setLingeringDuration(int duration) {
        this.creepertweaks$lingeringDuration = duration;
    }

    @Override
    public int creepertweaks$getLingeringDuration() {
        return this.creepertweaks$lingeringDuration;
    }

    @Override
    public void creepertweaks$setLingeringRadius(float radius) {
        this.creepertweaks$lingeringRadius = radius;
    }

    @Override
    public float creepertweaks$getLingeringRadius() {
        return this.creepertweaks$lingeringRadius;
    }

    @Override
    public void creepertweaks$setBlockRegeneration(boolean enabled) {
        this.creepertweaks$blockRegeneration = enabled;
    }

    @Override
    public boolean creepertweaks$isBlockRegeneration() {
        return this.creepertweaks$blockRegeneration;
    }

    @Override
    public void creepertweaks$setRegenerationDelay(int delay) {
        this.creepertweaks$regenerationDelay = delay;
    }

    @Override
    public int creepertweaks$getRegenerationDelay() {
        return this.creepertweaks$regenerationDelay;
    }
    
    @Override
    public void creepertweaks$setRegenerationSpeed(double speed) {
        this.creepertweaks$regenerationSpeed = speed;
    }

    @Override
    public double creepertweaks$getRegenerationSpeed() {
        return this.creepertweaks$regenerationSpeed;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void creepertweaks$writeNbt(net.minecraft.world.level.storage.ValueOutput view, CallbackInfo ci) {
        if (creepertweaks$defused) {
            view.putBoolean("CreeperTweaks_Defused", true);
        }
        view.putBoolean("CreeperTweaks_Shearable", creepertweaks$shearable);
        view.putDouble("CreeperTweaks_ConfettiChance", creepertweaks$confettiChance);
        view.putBoolean("CreeperTweaks_EcoFriendly", creepertweaks$ecoFriendly);
        view.putDouble("CreeperTweaks_HeadDropChance", creepertweaks$headDropChance);
        view.putBoolean("CreeperTweaks_AllowMovementDuringFuse", creepertweaks$allowMovementDuringFuse);

        view.putBoolean("CreeperTweaks_Lingering", creepertweaks$lingering);
        view.putString("CreeperTweaks_LingeringType", creepertweaks$lingeringType);
        view.putInt("CreeperTweaks_LingeringDuration", creepertweaks$lingeringDuration);
        view.putFloat("CreeperTweaks_LingeringRadius", creepertweaks$lingeringRadius);

        view.putBoolean("CreeperTweaks_BlockRegeneration", creepertweaks$blockRegeneration);
        view.putInt("CreeperTweaks_RegenerationDelay", creepertweaks$regenerationDelay);
        view.putDouble("CreeperTweaks_RegenerationSpeed", creepertweaks$regenerationSpeed);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void creepertweaks$readNbt(net.minecraft.world.level.storage.ValueInput view, CallbackInfo ci) {
        creepertweaks$defused = view.getBooleanOr("CreeperTweaks_Defused", false);
        
        creepertweaks$shearable = view.getBooleanOr("CreeperTweaks_Shearable", true);
        creepertweaks$confettiChance = view.getDoubleOr("CreeperTweaks_ConfettiChance", 0.0);
        creepertweaks$ecoFriendly = view.getBooleanOr("CreeperTweaks_EcoFriendly", false);
        creepertweaks$headDropChance = view.getDoubleOr("CreeperTweaks_HeadDropChance", 0.25);
        creepertweaks$allowMovementDuringFuse = view.getBooleanOr("CreeperTweaks_AllowMovementDuringFuse", false);

        creepertweaks$lingering = view.getBooleanOr("CreeperTweaks_Lingering", false);
        creepertweaks$lingeringType = view.getStringOr("CreeperTweaks_LingeringType", "POISON");
        creepertweaks$lingeringDuration = view.getIntOr("CreeperTweaks_LingeringDuration", 600);
        creepertweaks$lingeringRadius = view.getFloatOr("CreeperTweaks_LingeringRadius", 3.0f);

        creepertweaks$blockRegeneration = view.getBooleanOr("CreeperTweaks_BlockRegeneration", false);
        creepertweaks$regenerationDelay = view.getIntOr("CreeperTweaks_RegenerationDelay", 1200);
        creepertweaks$regenerationSpeed = view.getDoubleOr("CreeperTweaks_RegenerationSpeed", 10.0);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void creepertweaks$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!CreeperTweaksConfig.enableCreeperShearing) return;
        
        if (!this.creepertweaks$shearable) return;
        
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && !creepertweaks$defused) {
            if (!this.level().isClientSide()) {
                 creepertweaks$defused = true;
                 this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.HOSTILE, 1.0F, 1.0F);
                 stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                 
                 if (this.level() instanceof ServerLevel serverWorld) {
                     this.spawnAtLocation(serverWorld, Items.GUNPOWDER);
                 }
                 
                 // Reset the fuse to 0 and prevent ignition
                 Creeper creeper = (Creeper)(Object)this;
                 creeper.setSwellDir(-1); // Force fuse to decrease
                 creeper.setTarget(null); // Clear target to stop aggression
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    public void creepertweaks$onExplode(CallbackInfo ci) {
        if (creepertweaks$defused) {
            ci.cancel();
            return;
        }
        
        if (CreeperTweaksConfig.enableConfettiCreepers && creepertweaks$confettiChance > 0) {
            if (this.random.nextDouble() < creepertweaks$confettiChance) {
                ci.cancel();
                this.dead = true;
                
                Level world = this.level();
                if (!world.isClientSide()) {
                     world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.HOSTILE, 1.0F, 1.0F);
                     
                     // Create Firework Rocket
                     ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
                     
                     // Generate random colors
                     IntList colors = new IntArrayList();
                     // Add some random RGB colors
                     int colorCount = 3 + this.random.nextInt(3);
                     for (int i = 0; i < colorCount; i++) {
                         colors.add(this.random.nextInt(0xFFFFFF));
                     }
                     
                     // Create explosion component
                     FireworkExplosion explosion = new FireworkExplosion(
                         FireworkExplosion.Shape.BURST,
                         colors,
                         IntList.of(), // fade colors
                         false, // trail
                         false // twinkle
                     );
                     
                     // Create fireworks component
                     Fireworks fireworks = new Fireworks(0, List.of(explosion));
                     
                     // Apply to stack
                     stack.set(DataComponents.FIREWORKS, fireworks);
                     
                     // Spawn and explode
                     FireworkRocketEntity rocket = new FireworkRocketEntity(world, this.getX(), this.getY() + 0.5, this.getZ(), stack);
                     world.addFreshEntity(rocket);
                     // Force explode effect on clients
                     world.broadcastEntityEvent(rocket, EntityEvent.FIREWORKS_EXPLODE);
                     // Remove entity
                     rocket.discard();
                     
                     this.discard(); 
                }
                return; // Stop here if confetti happened
            }
        }

        // Lingering Effect Logic
        if (creepertweaks$lingering && !this.level().isClientSide()) {
            creepertweaks$spawnLingeringCloud();
        }
    }

    @Unique
    private void creepertweaks$spawnLingeringCloud() {
        Level world = this.level();
        AreaEffectCloud cloud = new AreaEffectCloud(world, this.getX(), this.getY(), this.getZ());
        cloud.setRadius(creepertweaks$lingeringRadius);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(creepertweaks$lingeringDuration);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());

        Identifier effectId = Identifier.parse(creepertweaks$lingeringType.toLowerCase());
        Optional<net.minecraft.core.Holder.Reference<MobEffect>> effectEntry = BuiltInRegistries.MOB_EFFECT.get(effectId);

        if (effectEntry.isPresent()) {
             cloud.addEffect(new MobEffectInstance(effectEntry.get(), creepertweaks$lingeringDuration, 0));
        } else {
             // Fallback to Poison if invalid
             BuiltInRegistries.MOB_EFFECT.get(Identifier.parse("poison")).ifPresent(entry -> 
                cloud.addEffect(new MobEffectInstance(entry, creepertweaks$lingeringDuration, 0))
             );
        }

        world.addFreshEntity(cloud);
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    public void creepertweaks$onTick(CallbackInfo ci) {
        if (creepertweaks$defused) {
            Creeper creeper = (Creeper)(Object)this;
            // Force the fuse speed to be negative (decreasing) if it's trying to explode
            if (creeper.getSwellDir() > 0) {
                creeper.setSwellDir(-1);
            }
        }
    }
}
