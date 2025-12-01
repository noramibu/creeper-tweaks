package me.noramibu.creepertweaks.mixin;

import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;
import java.util.Optional;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends HostileEntity implements CreeperMixinExtensions {

    private boolean creepertweaks$defused = false;
    private boolean creepertweaks$shearable = true;
    private double creepertweaks$confettiChance = 0.0;
    private boolean creepertweaks$ecoFriendly = false;
    private double creepertweaks$headDropChance = 0.25;

    // Lingering fields
    private boolean creepertweaks$lingering = false;
    private String creepertweaks$lingeringType = "POISON";
    private int creepertweaks$lingeringDuration = 600;
    private float creepertweaks$lingeringRadius = 3.0f;

    // Block Regeneration fields
    private boolean creepertweaks$blockRegeneration = false;
    private int creepertweaks$regenerationDelay = 1200;
    private double creepertweaks$regenerationSpeed = 10.0;

    protected CreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
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

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void creepertweaks$writeNbt(net.minecraft.storage.WriteView view, CallbackInfo ci) {
        if (creepertweaks$defused) {
            view.putBoolean("CreeperTweaks_Defused", true);
        }
        view.putBoolean("CreeperTweaks_Shearable", creepertweaks$shearable);
        view.putDouble("CreeperTweaks_ConfettiChance", creepertweaks$confettiChance);
        view.putBoolean("CreeperTweaks_EcoFriendly", creepertweaks$ecoFriendly);
        view.putDouble("CreeperTweaks_HeadDropChance", creepertweaks$headDropChance);

        view.putBoolean("CreeperTweaks_Lingering", creepertweaks$lingering);
        view.putString("CreeperTweaks_LingeringType", creepertweaks$lingeringType);
        view.putInt("CreeperTweaks_LingeringDuration", creepertweaks$lingeringDuration);
        view.putFloat("CreeperTweaks_LingeringRadius", creepertweaks$lingeringRadius);

        view.putBoolean("CreeperTweaks_BlockRegeneration", creepertweaks$blockRegeneration);
        view.putInt("CreeperTweaks_RegenerationDelay", creepertweaks$regenerationDelay);
        view.putDouble("CreeperTweaks_RegenerationSpeed", creepertweaks$regenerationSpeed);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void creepertweaks$readNbt(net.minecraft.storage.ReadView view, CallbackInfo ci) {
        creepertweaks$defused = view.getBoolean("CreeperTweaks_Defused", false);
        
        creepertweaks$shearable = view.getBoolean("CreeperTweaks_Shearable", true);
        creepertweaks$confettiChance = view.getDouble("CreeperTweaks_ConfettiChance", 0.0);
        creepertweaks$ecoFriendly = view.getBoolean("CreeperTweaks_EcoFriendly", false);
        creepertweaks$headDropChance = view.getDouble("CreeperTweaks_HeadDropChance", 0.25);

        creepertweaks$lingering = view.getBoolean("CreeperTweaks_Lingering", false);
        creepertweaks$lingeringType = view.getString("CreeperTweaks_LingeringType", "POISON");
        creepertweaks$lingeringDuration = view.getInt("CreeperTweaks_LingeringDuration", 600);
        creepertweaks$lingeringRadius = view.getFloat("CreeperTweaks_LingeringRadius", 3.0f);

        creepertweaks$blockRegeneration = view.getBoolean("CreeperTweaks_BlockRegeneration", false);
        creepertweaks$regenerationDelay = view.getInt("CreeperTweaks_RegenerationDelay", 1200);
        creepertweaks$regenerationSpeed = view.getDouble("CreeperTweaks_RegenerationSpeed", 10.0);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void creepertweaks$onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!CreeperTweaksConfig.enableCreeperShearing) return;
        
        if (!this.creepertweaks$shearable) return;
        
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.SHEARS) && !creepertweaks$defused) {
            if (!this.getEntityWorld().isClient()) {
                 creepertweaks$defused = true;
                 this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.HOSTILE, 1.0F, 1.0F);
                 stack.damage(1, player, EquipmentSlot.MAINHAND);
                 
                 if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                     this.dropItem(serverWorld, Items.GUNPOWDER);
                 }
                 
                 // Reset the fuse to 0 and prevent ignition
                 CreeperEntity creeper = (CreeperEntity)(Object)this;
                 creeper.setFuseSpeed(-1); // Force fuse to decrease
                 creeper.setTarget(null); // Clear target to stop aggression
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    public void creepertweaks$onExplode(CallbackInfo ci) {
        if (creepertweaks$defused) {
            ci.cancel();
            return;
        }
        
        if (CreeperTweaksConfig.enableConfettiCreepers && creepertweaks$confettiChance > 0) {
            if (this.random.nextDouble() < creepertweaks$confettiChance) {
                ci.cancel();
                this.dead = true;
                
                World world = this.getEntityWorld();
                if (!world.isClient()) {
                     world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.HOSTILE, 1.0F, 1.0F);
                     
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
                     FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                         FireworkExplosionComponent.Type.BURST,
                         colors,
                         IntList.of(), // fade colors
                         false, // trail
                         false // twinkle
                     );
                     
                     // Create fireworks component
                     FireworksComponent fireworks = new FireworksComponent(0, List.of(explosion));
                     
                     // Apply to stack
                     stack.set(DataComponentTypes.FIREWORKS, fireworks);
                     
                     // Spawn and explode
                     FireworkRocketEntity rocket = new FireworkRocketEntity(world, this.getX(), this.getY() + 0.5, this.getZ(), stack);
                     world.spawnEntity(rocket);
                     // Force explode effect on clients
                     world.sendEntityStatus(rocket, EntityStatuses.EXPLODE_FIREWORK_CLIENT);
                     // Remove entity
                     rocket.discard();
                     
                     this.discard(); 
                }
                return; // Stop here if confetti happened
            }
        }

        // Lingering Effect Logic
        if (creepertweaks$lingering && !this.getEntityWorld().isClient()) {
            spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        World world = this.getEntityWorld();
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, this.getX(), this.getY(), this.getZ());
        cloud.setRadius(creepertweaks$lingeringRadius);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(creepertweaks$lingeringDuration);
        cloud.setRadiusGrowth(-cloud.getRadius() / (float)cloud.getDuration());

        Identifier effectId = Identifier.of(creepertweaks$lingeringType.toLowerCase());
        Optional<net.minecraft.registry.entry.RegistryEntry.Reference<StatusEffect>> effectEntry = Registries.STATUS_EFFECT.getEntry(effectId);

        if (effectEntry.isPresent()) {
             cloud.addEffect(new StatusEffectInstance(effectEntry.get(), creepertweaks$lingeringDuration, 0));
        } else {
             // Fallback to Poison if invalid
             Registries.STATUS_EFFECT.getEntry(Identifier.of("poison")).ifPresent(entry -> 
                cloud.addEffect(new StatusEffectInstance(entry, creepertweaks$lingeringDuration, 0))
             );
        }

        world.spawnEntity(cloud);
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    public void creepertweaks$onTick(CallbackInfo ci) {
        if (creepertweaks$defused) {
            CreeperEntity creeper = (CreeperEntity)(Object)this;
            // Force the fuse speed to be negative (decreasing) if it's trying to explode
            if (creeper.getFuseSpeed() > 0) {
                creeper.setFuseSpeed(-1);
            }
        }
    }
}
