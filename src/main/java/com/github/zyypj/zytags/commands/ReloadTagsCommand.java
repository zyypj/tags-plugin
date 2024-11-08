package com.github.zyypj.zytags.commands;

import com.github.zyypj.zytags.Main;
import com.github.zyypj.zytags.systems.top.TopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadTagsCommand implements CommandExecutor {

    private final TopManager topManager;

    public ReloadTagsCommand(Main plugin) {
        this.topManager = plugin.getTopManager();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        topManager.updateTopPlayers();
        sender.sendMessage("§eTags recarregadas!");
        return false;
    }
}
