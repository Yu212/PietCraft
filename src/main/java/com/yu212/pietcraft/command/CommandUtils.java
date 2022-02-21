package com.yu212.pietcraft.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.brigadier.ArgumentSerializerString;
import net.minecraft.server.commands.CommandEffect;

import java.util.function.Predicate;

public class CommandUtils {
    public static LiteralArgumentBuilder<CommandListenerWrapper> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, String> word(String name) {
        return RequiredArgumentBuilder.argument(name, StringArgumentType.word());
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, String> greedy(String name) {
        return RequiredArgumentBuilder.argument(name, StringArgumentType.greedyString());
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, EntitySelector> player(String name) {
        return RequiredArgumentBuilder.argument(name, ArgumentEntity.c());
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, Integer> integer(String name) {
        return RequiredArgumentBuilder.argument(name, IntegerArgumentType.integer());
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, Integer> integer(String name, int min) {
        return RequiredArgumentBuilder.argument(name, IntegerArgumentType.integer(min));
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, Integer> integer(String name, int min, int max) {
        return RequiredArgumentBuilder.argument(name, IntegerArgumentType.integer(min, max));
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, ArgumentChat.a> chat(String name) {
        return RequiredArgumentBuilder.argument(name, ArgumentChat.a());
    }

    public static RequiredArgumentBuilder<CommandListenerWrapper, IVectorPosition> position(String name) {
        return RequiredArgumentBuilder.argument(name, ArgumentPosition.a());
    }

    public static Predicate<CommandListenerWrapper> requirePermission(String name) {
        return clw -> clw.getBukkitSender().hasPermission(name);
    }
}
