package me.noramibu.creepertweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.noramibu.creepertweaks.config.CreeperTweaksConfig;
import me.noramibu.creepertweaks.config.CreeperType;
import me.noramibu.creepertweaks.util.CreeperMixinExtensions;
import me.noramibu.creepertweaks.util.CreeperUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public class CreeperTweaksCommand {
    private static final SimpleCommandExceptionType TYPE_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Creeper type not found"));
    private static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Entity not found"));
    private static final SimpleCommandExceptionType NOT_A_CREEPER_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Entity is not a creeper"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("creepertweaks")
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.literal("reload")
                .executes(CreeperTweaksCommand::executeReload)
            )
            .then(Commands.literal("spawn")
                .then(Commands.argument("type", StringArgumentType.string())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggest(CreeperTweaksConfig.creeperTypes.stream().map(type -> type.name), builder))
                    .executes(CreeperTweaksCommand::executeSpawn)
                )
            )
            .then(Commands.literal("debug")
                .then(Commands.argument("target", EntityArgument.entity())
                    .executes(CreeperTweaksCommand::executeDebug)
                )
            )
        );
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        CreeperTweaksConfig.load();
        context.getSource().sendSuccess(() -> Component.literal("Creeper Tweaks config reloaded!"), true);
        return 1;
    }

    private static int executeSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String typeName = StringArgumentType.getString(context, "type");
        CreeperType type = CreeperTweaksConfig.creeperTypes.stream()
                .filter(t -> t.name.equals(typeName))
                .findFirst()
                .orElseThrow(TYPE_NOT_FOUND_EXCEPTION::create);

        CommandSourceStack source = context.getSource();
        Vec3 pos = source.getPosition();
        
        Creeper creeper = EntityTypes.CREEPER.create(source.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (creeper != null) {
            creeper.snapTo(pos.x, pos.y, pos.z, 0, 0);
            CreeperUtils.applyCreeperType(creeper, type);
            source.getLevel().addFreshEntity(creeper);
            source.sendSuccess(() -> Component.literal("Spawned custom creeper: " + type.name), true);
            return 1;
        }
        return 0;
    }

    private static int executeDebug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "target");

        if (!(entity instanceof Creeper creeper)) {
            throw NOT_A_CREEPER_EXCEPTION.create();
        }

        if (creeper instanceof CreeperMixinExtensions extensions) {
            context.getSource().sendSuccess(() -> Component.literal("§6Creeper Debug Info:"), false);
            context.getSource().sendSuccess(() -> Component.literal("UUID: " + creeper.getUUID()), false);
            context.getSource().sendSuccess(() -> Component.literal("Shearable: " + extensions.creepertweaks$isShearable()), false);
            context.getSource().sendSuccess(() -> Component.literal("Confetti Chance: " + extensions.creepertweaks$getConfettiChance()), false);
            context.getSource().sendSuccess(() -> Component.literal("Eco-Friendly: " + extensions.creepertweaks$isEcoFriendly()), false);
            context.getSource().sendSuccess(() -> Component.literal("Silent: " + creeper.isSilent()), false);
            context.getSource().sendSuccess(() -> Component.literal("Powered: " + creeper.isPowered()), false);
            context.getSource().sendSuccess(() -> Component.literal("Health: " + creeper.getHealth() + "/" + creeper.getMaxHealth()), false);
            return 1;
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Error: Creeper does not implement mixin extensions."), false);
            return 0;
        }
    }
}
