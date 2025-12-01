package me.noramibu.creepertweaks.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import me.noramibu.creepertweaks.CreeperTweaks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CreeperTweaksConfig {
    private static final String CONFIG_FILE = "config/creepertweaks.toml";
    private static CommentedFileConfig config;

    public static boolean enableCreeperShearing = true;
    public static boolean enableConfettiCreepers = true;
    public static boolean enableEcoFriendlyCreepers = true;
    public static boolean enableHeadDrops = true;
    public static boolean enableNameTags = true;
    public static boolean enableBlockRegeneration = true;
    public static boolean enableRegenerationParticles = true;
    public static String regenerationParticleType = "block";
    public static boolean debug = false;
    
    public static final List<CreeperType> creeperTypes = new ArrayList<>();
    public static double totalWeight = 0.0; // Changed to double

    public static void load() {
        Path path = Paths.get(CONFIG_FILE);
        
        path.getParent().toFile().mkdirs();

        if (!Files.exists(path)) {
            try (InputStream in = CreeperTweaksConfig.class.getResourceAsStream("/creepertweaks.toml")) {
                if (in != null) {
                    Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    CreeperTweaks.LOGGER.warn("Could not find default config file in resources!");
                }
            } catch (IOException e) {
                CreeperTweaks.LOGGER.error("Failed to copy default config file", e);
            }
        }

        config = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        config.load();

        enableCreeperShearing = config.getOrElse("global.enableCreeperShearing", true);
        enableConfettiCreepers = config.getOrElse("global.enableConfettiCreepers", true);
        enableEcoFriendlyCreepers = config.getOrElse("global.enableEcoFriendlyCreepers", true);
        enableHeadDrops = config.getOrElse("global.enableHeadDrops", true);
        enableNameTags = config.getOrElse("global.enableNameTags", true);
        enableBlockRegeneration = config.getOrElse("global.enableBlockRegeneration", true);
        enableRegenerationParticles = config.getOrElse("global.enableRegenerationParticles", true);
        regenerationParticleType = config.getOrElse("global.regenerationParticleType", "block");
        debug = config.getOrElse("global.debug", false);
        
        creeperTypes.clear();
        totalWeight = 0.0;
        
        if (config.contains("creepers")) {
            List<Config> creepers = config.get("creepers");
            for (Config c : creepers) {
                // Reading numbers as double can sometimes be tricky if they are ints in TOML, but getOrElse with double default should handle it or use getNumber
                Number spawnChance = c.getOrElse("spawn-chance", 0.1);
                
                CreeperType type = new CreeperType(
                    c.getOrElse("name", "unknown"),
                    spawnChance.doubleValue(),
                    c.getOrElse("scale", 1.0),
                    c.getOrElse("health", 20.0),
                    c.getOrElse("speed", 0.25),
                    c.getOrElse("explosionRadius", 3),
                    c.getOrElse("charged", false),
                    c.getOrElse("shearable", true),
                    c.getOrElse("confettiChance", 0.0),
                    c.getOrElse("ecoFriendlyDropChance", 1.0),
                    c.getOrElse("silent", false),
                    c.getOrElse("headDropChance", 0.25),
                    c.getOrElse("lingering", false),
                    c.getOrElse("lingeringType", "POISON"),
                    c.getOrElse("lingeringDuration", 600), // 30 seconds
                    c.getOrElse("lingeringRadius", 3.0),
                    c.getOrElse("nameTag", ""),
                    c.getOrElse("blockRegeneration", false),
                    c.getOrElse("regenerationDelay", 200), // 10 seconds
                    c.getOrElse("regenerationSpeed", 10.0)
                );
                creeperTypes.add(type);
                totalWeight += type.spawnChance;
            }
        }
        
        if (creeperTypes.isEmpty()) {
            CreeperTweaks.LOGGER.warn("No creeper types found in config! Using fallback.");
            CreeperType fallback = new CreeperType("fallback", 1.0, 1.0, 20.0, 0.25, 3, false, true, 0.0, 1.0, false, 0.25, false, "POISON", 600, 3.0, "", false, 200, 10.0);
            creeperTypes.add(fallback);
            totalWeight = 1.0;
        }
    }
    
    public static CreeperType getRandomCreeperType() {
        if (totalWeight <= 0) return creeperTypes.get(0);
        
        double random = java.util.concurrent.ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (CreeperType type : creeperTypes) {
            currentWeight += type.spawnChance;
            if (random < currentWeight) {
                return type;
            }
        }
        
        return creeperTypes.get(0);
    }
}
