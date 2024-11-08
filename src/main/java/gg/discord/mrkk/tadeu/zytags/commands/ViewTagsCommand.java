package gg.discord.mrkk.tadeu.zytags.commands;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.inventories.ViewTagsInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewTagsCommand implements CommandExecutor {

    private final Main plugin;

    public ViewTagsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;
        ViewTagsInventory viewTagsInventory = new ViewTagsInventory(plugin);
        viewTagsInventory.openInventory(player);
        return true;
    }
}
