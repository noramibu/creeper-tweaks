package me.noramibu.creepertweaks;

import me.noramibu.creepertweaks.command.CreeperTweaksCommand;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.util.BlockRegenerationManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreeperTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("creepertweaks");
    public static final String VERSION = /*$ mod_version*/ "1.0.0";
    public static final String MINECRAFT = /*$ minecraft*/ "1.21.10";

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
        return Identifier.of(namespace, path);
    }
}
