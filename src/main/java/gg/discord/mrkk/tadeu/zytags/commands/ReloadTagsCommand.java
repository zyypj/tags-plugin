package gg.discord.mrkk.tadeu.zytags.commands;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.systems.top.TopManager;
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
