package me.noramibu.creepertweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import me.noramibu.creepertweaks.util.CreeperUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class CreeperTweaksCommand {
    private static final SimpleCommandExceptionType TYPE_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Creeper type not found"));
    private static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Entity not found"));
    private static final SimpleCommandExceptionType NOT_A_CREEPER_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Entity is not a creeper"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("creepertweaks")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("reload")
                .executes(CreeperTweaksCommand::executeReload)
            )
            .then(CommandManager.literal("spawn")
                .then(CommandManager.argument("type", StringArgumentType.string())
                    .suggests((context, builder) -> CommandSource.suggestMatching(CreeperTweaksConfig.creeperTypes.stream().map(type -> type.name), builder))
                    .executes(CreeperTweaksCommand::executeSpawn)
                )
            )
            .then(CommandManager.literal("debug")
                .then(CommandManager.argument("target", EntityArgumentType.entity())
                    .executes(CreeperTweaksCommand::executeDebug)
                )
            )
        );
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        CreeperTweaksConfig.load();
        context.getSource().sendFeedback(() -> Text.literal("Creeper Tweaks config reloaded!"), true);
        return 1;
    }

    private static int executeSpawn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String typeName = StringArgumentType.getString(context, "type");
        CreeperType type = CreeperTweaksConfig.creeperTypes.stream()
                .filter(t -> t.name.equals(typeName))
                .findFirst()
                .orElseThrow(TYPE_NOT_FOUND_EXCEPTION::create);

        ServerCommandSource source = context.getSource();
        Vec3d pos = source.getPosition();
        
        CreeperEntity creeper = EntityType.CREEPER.create(source.getWorld(), net.minecraft.entity.SpawnReason.COMMAND);
        if (creeper != null) {
            creeper.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0, 0);
            CreeperUtils.applyCreeperType(creeper, type);
            source.getWorld().spawnEntity(creeper);
            source.sendFeedback(() -> Text.literal("Spawned custom creeper: " + type.name), true);
            return 1;
        }
        return 0;
    }

    private static int executeDebug(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity entity = EntityArgumentType.getEntity(context, "target");

        if (!(entity instanceof CreeperEntity creeper)) {
            throw NOT_A_CREEPER_EXCEPTION.create();
        }

        if (creeper instanceof CreeperMixinExtensions extensions) {
            context.getSource().sendFeedback(() -> Text.literal("§6Creeper Debug Info:"), false);
            context.getSource().sendFeedback(() -> Text.literal("UUID: " + creeper.getUuid()), false);
            context.getSource().sendFeedback(() -> Text.literal("Shearable: " + extensions.creepertweaks$isShearable()), false);
            context.getSource().sendFeedback(() -> Text.literal("Confetti Chance: " + extensions.creepertweaks$getConfettiChance()), false);
            context.getSource().sendFeedback(() -> Text.literal("Eco-Friendly: " + extensions.creepertweaks$isEcoFriendly()), false);
            context.getSource().sendFeedback(() -> Text.literal("Silent: " + creeper.isSilent()), false);
            context.getSource().sendFeedback(() -> Text.literal("Powered: " + creeper.isCharged()), false);
            context.getSource().sendFeedback(() -> Text.literal("Health: " + creeper.getHealth() + "/" + creeper.getMaxHealth()), false);
            return 1;
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Error: Creeper does not implement mixin extensions."), false);
            return 0;
        }
    }
}
