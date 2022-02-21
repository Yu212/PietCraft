package com.yu212.pietcraft;

import com.mojang.brigadier.CommandDispatcher;
import com.yu212.pietcraft.command.PietCommand;
import com.yu212.pietcraft.command.PietPlaceCommand;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class PietCraft extends JavaPlugin {
    private static PietCraft instance;

    @Override
    public void onEnable() {
        instance = this;
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.a();
        PietCommand.register(dispatcher);
        PietPlaceCommand.register(dispatcher);
    }

    public static PietCraft getInstance() {
        return instance;
    }
}
