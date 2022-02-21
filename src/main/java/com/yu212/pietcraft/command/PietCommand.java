package com.yu212.pietcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.yu212.pietcraft.piet.PietCodel;
import com.yu212.pietcraft.piet.PietException;
import com.yu212.pietcraft.piet.PietInterpreter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;

import java.util.Arrays;
import java.util.function.BiFunction;

import static com.yu212.pietcraft.command.CommandUtils.*;

public class PietCommand {
    private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new ChatComponentText("error"));

    public static void register(CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> builder = literal("piet");
        builder.then(position("from").then(position("to")
                .executes(context -> execute(context, ""))
                .then(chat("input").executes(context -> execute(context, CraftChatMessage.fromComponent(ArgumentChat.a(context, "input")))))));
        dispatcher.register(builder);
    }

    private static int execute(CommandContext<CommandListenerWrapper> context, String input) throws CommandSyntaxException {
        WorldServer world = context.getSource().getWorld();
        BlockPosition from = ArgumentPosition.a(context, "from");
        BlockPosition to = ArgumentPosition.a(context, "to");
        int sizeX = Math.abs(to.getX() - from.getX()) + 1;
        int sizeY = Math.abs(to.getY() - from.getY()) + 1;
        int sizeZ = Math.abs(to.getZ() - from.getZ()) + 1;
        PietCodel[][] code;
        if (sizeY == 1) {
            BlockPosition start = new BlockPosition(from.getX(), from.getY(), from.getZ());
            if (from.getX() < to.getX() && from.getZ() <= to.getZ()) {
                // positive X -> positive Z
                code = convert(world, sizeX, sizeZ, (dx, dy) -> start.c(dx, 0, dy));
            } else if (from.getX() > to.getX() && from.getZ() >= to.getZ()) {
                // negative X -> negative Z
                code = convert(world, sizeX, sizeZ, (dx, dy) -> start.c(-dx, 0, -dy));
            } else if (from.getX() >= to.getX() && from.getZ() < to.getZ()) {
                // positive Z -> negative X
                code = convert(world, sizeZ, sizeX, (dx, dy) -> start.c(-dy, 0, dx));
            } else {
                // negative Z -> positive X
                code = convert(world, sizeZ, sizeX, (dx, dy) -> start.c(dy, 0, -dx));
            }
        } else if (sizeX == 1) {
            BlockPosition start = new BlockPosition(from.getX(), Math.max(from.getY(), to.getY()), from.getZ());
            if (from.getZ() <= to.getZ()) {
                // positive Z -> negative Y
                code = convert(world, sizeZ, sizeY, (dx, dy) -> start.c(0, -dy, dx));
            } else {
                // negative Z -> negative Y
                code = convert(world, sizeZ, sizeY, (dx, dy) -> start.c(0, -dy, -dx));
            }
        } else if (sizeZ == 1) {
            BlockPosition start = new BlockPosition(from.getX(), Math.max(from.getY(), to.getY()), from.getZ());
            if (from.getX() <= to.getX()) {
                // positive X -> negative Y
                code = convert(world, sizeX, sizeY, (dx, dy) -> start.c(dx, -dy, 0));
            } else {
                // negative X -> negative Y
                code = convert(world, sizeX, sizeY, (dx, dy) -> start.c(-dx, -dy, 0));
            }
        } else {
            throw ERROR.create();
        }
        try {
            String output = new PietInterpreter(code).execute(input);
            if (!output.isEmpty()) {
                context.getSource().sendMessage(new ChatComponentText(output), false);
            }
        } catch (Exception e) {
            throw ERROR.create();
        }
        return 1;
    }

    private static PietCodel[][] convert(WorldServer world, int width, int height, BiFunction<Integer, Integer, BlockPosition> func) throws CommandSyntaxException {
        PietCodel[][] code = new PietCodel[height + 2][width + 2];
        for (int i = 0; i < height + 2; i++) {
            Arrays.fill(code[i], PietCodel.BLACK);
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Material material = CraftBlock.at(world, func.apply(j, i)).getType();
                PietCodel codel = PietCodel.of(material);
                if (codel == null) {
                    codel = material == Material.AIR ? PietCodel.BLACK : PietCodel.WHITE;
                }
                code[i + 1][j + 1] = codel;
            }
        }
        return code;
    }
}
