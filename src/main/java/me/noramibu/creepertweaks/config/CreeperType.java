package me.noramibu.creepertweaks.config;

public class CreeperType {
    public String name;
    public double spawnChance;
    public double scale;
    public double health;
    public double speed;
    public int explosionRadius;
    public boolean charged;
    public boolean shearable;
    public double confettiChance;
    public double ecoFriendlyDropChance;
    public boolean silent;
    public double headDropChance;
    public boolean lingering;
    public String lingeringType;
    public int lingeringDuration;
    public double lingeringRadius;
    public String nameTag;
    public boolean blockRegeneration;
    public int regenerationDelay;
    public double regenerationSpeed;

    public CreeperType(String name, double spawnChance, double scale, double health, double speed, int explosionRadius, boolean charged, boolean shearable, double confettiChance, double ecoFriendlyDropChance, boolean silent, double headDropChance, boolean lingering, String lingeringType, int lingeringDuration, double lingeringRadius, String nameTag, boolean blockRegeneration, int regenerationDelay, double regenerationSpeed) {
        this.name = name;
        this.spawnChance = spawnChance;
        this.scale = scale;
        this.health = health;
        this.speed = speed;
        this.explosionRadius = explosionRadius;
        this.charged = charged;
        this.shearable = shearable;
        this.confettiChance = confettiChance;
        this.ecoFriendlyDropChance = ecoFriendlyDropChance;
        this.silent = silent;
        this.headDropChance = headDropChance;
        this.lingering = lingering;
        this.lingeringType = lingeringType;
        this.lingeringDuration = lingeringDuration;
        this.lingeringRadius = lingeringRadius;
        this.nameTag = nameTag;
        this.blockRegeneration = blockRegeneration;
        this.regenerationDelay = regenerationDelay;
        this.regenerationSpeed = regenerationSpeed;
    }
}
