package gg.discord.mrkk.tadeu.zytags.commands;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.configuration.Configuration;
import gg.discord.mrkk.tadeu.zytags.configuration.MessagesConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TagsCommand implements CommandExecutor {

    private final Configuration config;
    private final MessagesConfiguration messagesConfig;

    public TagsCommand(Main plugin) {
        this.config = plugin.getConfiguration();
        this.messagesConfig = plugin.getMessagesConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!sender.hasPermission("zytags.reload")) {
            sender.sendMessage(messagesConfig.getMessage("no-permission"));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUse /zytags reload");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.loadConfig();
            messagesConfig.reloadConfig();
            sender.sendMessage("§aConfigurações e mensagens recarregadas!");
            return true;
        }

        sender.sendMessage("§cUse /zytags reload");
        return false;
    }
}
