package me.noramibu.creepertweaks.util;

public interface CreeperMixinExtensions {
    void creepertweaks$setShearable(boolean shearable);
    void creepertweaks$setConfettiChance(double chance);
    void creepertweaks$setEcoFriendly(boolean ecoFriendly);
    boolean creepertweaks$isShearable();
    double creepertweaks$getConfettiChance();
    boolean creepertweaks$isEcoFriendly();
    void creepertweaks$setHeadDropChance(double chance);
    double creepertweaks$getHeadDropChance();

    // Lingering explosion properties
    void creepertweaks$setLingering(boolean lingering);
    boolean creepertweaks$isLingering();
    void creepertweaks$setLingeringType(String type);
    String creepertweaks$getLingeringType();
    void creepertweaks$setLingeringDuration(int duration);
    int creepertweaks$getLingeringDuration();
    void creepertweaks$setLingeringRadius(float radius);
    float creepertweaks$getLingeringRadius();

    // Block regeneration properties
    void creepertweaks$setBlockRegeneration(boolean enabled);
    boolean creepertweaks$isBlockRegeneration();
    void creepertweaks$setRegenerationDelay(int delay);
    int creepertweaks$getRegenerationDelay();
    void creepertweaks$setRegenerationSpeed(double speed);
    double creepertweaks$getRegenerationSpeed();
}
