package me.noramibu.creepertweaks;

import me.noramibu.creepertweaks.command.CreeperTweaksCommand;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.BlockRegenerationManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreeperTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("creepertweaks");
    public static final String VERSION = "1.0.2";
    public static final String MINECRAFT = "26.1.1";

    @Override
    public void onInitialize() {
        CreeperTweaksConfig.load();
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CreeperTweaksCommand.register(dispatcher);
        });

        // Initialize Block Regeneration Manager
        BlockRegenerationManager.init();

        LOGGER.info("Creeper Tweaks initialized!");
    }

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
}
