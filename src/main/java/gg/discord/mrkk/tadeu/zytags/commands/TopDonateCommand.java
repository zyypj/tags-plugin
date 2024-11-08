package gg.discord.mrkk.tadeu.zytags.commands;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.configuration.MessagesConfiguration;
import gg.discord.mrkk.tadeu.zytags.systems.top.TopManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopDonateCommand implements CommandExecutor {

    private final TopManager topManager;
    private final MessagesConfiguration messages;

    public TopDonateCommand(Main plugin) {
        this.topManager = plugin.getTopManager();
        this.messages = plugin.getMessagesConfiguration();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("settopdonate.use")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUso correto: /settopdonate <jogador> <quantidadeQueEleDoou>");
            return false;
        }

        String playerName = args[0];
        int donateAmount;

        try {
            donateAmount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cA quantidade deve ser um número válido.");
            return false;
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(messages.getMessage("player-not-found")
                .replace("{PLAYER}", playerName));
            return false;
        }

        // Atualiza o topo de doações com o novo valor doado
        topManager.updateTopDonate(playerName, donateAmount);
        sender.sendMessage(messages.getMessage("update-top-donate")
                .replace("{PLAYER}", player.getName())
                .replace("{AMOUNT}", String.valueOf(donateAmount)));
        return true;
    }
}