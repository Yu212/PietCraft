package com.yu212.pietcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.yu212.pietcraft.PietCraft;
import com.yu212.pietcraft.piet.PietCodel;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.yu212.pietcraft.command.CommandUtils.*;

public class PietPlaceCommand {
    private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new ChatComponentText("error"));

    public static void register(CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> builder = literal("pp");
        builder.then(position("from").then(integer("width", 1).then(integer("height", 1).then(word("name").executes(PietPlaceCommand::execute)))));
        dispatcher.register(builder);
    }

    private static int execute(CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        try {
            WorldServer world = context.getSource().getWorld();
            BlockPosition from = ArgumentPosition.a(context, "from");
            int width = IntegerArgumentType.getInteger(context, "width");
            int height = IntegerArgumentType.getInteger(context, "height");
            String name = StringArgumentType.getString(context, "name");
            Path path = Path.of("piet").resolve(name + ".png");
            if (Files.notExists(path)) {
                context.getSource().sendMessage(new ChatComponentText("File not found").a(EnumChatFormat.m), false);
                return 0;
            }
            BufferedImage image = ImageIO.read(path.toFile());
            PietCodel[][] code = new PietCodel[height][width];
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int x = imageWidth * (j * 2 + 1) / (width * 2);
                    int y = imageHeight * (i * 2 + 1) / (height * 2);
                    int rgb = image.getRGB(x, y) & 0xFFFFFF;
                    PietCodel codel = PietCodel.getClosest(rgb);
                    code[i][j] = codel;
                }
            }
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Material material = code[i][j].material;
                    CraftBlock.at(world, from.c(j, 0, i)).setType(material);
                }
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw ERROR.create();
        }
    }
}
