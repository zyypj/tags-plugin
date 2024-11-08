package gg.discord.mrkk.tadeu.zytags.systems.top;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.configuration.Configuration;
import gg.discord.mrkk.tadeu.zytags.systems.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TopManager {

    private final Main plugin;
    private final Configuration config;
    private final BalanceIntegration balanceIntegration;
    private final TimeIntegration timeIntegration;
    private final KDRIntegration kdrIntegration;
    private final SkillsIntegration skillsIntegration;
    private final VoteIntegration voteIntegration;

    private final Map<String, String> previousTopPlaceholders = new HashMap<>();

    private int topDonateAmount = 0;
    private String topDonatePlayer = "&cNinguém";

    public TopManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.balanceIntegration = plugin.getBalanceIntegration();
        this.timeIntegration = plugin.getTimeIntegration();
        this.kdrIntegration = plugin.getKdrIntegration();
        this.skillsIntegration = plugin.getSkillsIntegration();
        this.voteIntegration = plugin.getVoteIntegration();

        plugin.debug("Inicializando TopManager e iniciando a tarefa de verificação de tops.");
        startTopCheckTask();
    }

    private void startTopCheckTask() {
        long intervalTicks = config.getTopCheckInterval() * 20 * 60L;
        plugin.debug("Tarefa de verificação de tops configurada para rodar a cada " + config.getTopCheckInterval() + " minutos.");
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateTopPlayers, 0L, intervalTicks);
    }

    public void updateTopPlayers() {
        plugin.debug("Iniciando atualização dos tops...");
        checkTopBalance();
        checkTopTime();
        checkTopKDR();
        checkTopSkills();
        checkTopVotes();
        plugin.debug("Atualização dos tops concluída.");
    }

    private void executeCommands(String playerName, String category, boolean isNewTop) {
        String commandPath = isNewTop ? "commands-to-new-top" : "command-to-old-top";
        plugin.debug("Executando comandos para " + (isNewTop ? "novo" : "antigo") + " top na categoria: " + category);

        config.getPlugin().getConfig().getStringList("tags." + category + "." + commandPath).forEach(command -> {
            if (playerName != null && !playerName.equals("&cNinguém")) {
                String formattedCommand = command.replace("{PLAYER}", playerName);
                plugin.debug("Executando comando: " + formattedCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
            }
        });
    }

    private void checkTopBalance() {
        plugin.debug("Verificando top balance...");
        String[] categories = {"baltop1", "baltop2", "baltop3"};

        for (int i = 1; i <= 3; i++) {
            String namePlaceholder = "%royaleeconomy_balancetop_purse_name_" + i + "%";
            String balancePlaceholder = "%royaleeconomy_balancetop_purse_balance_" + i + "%";

            String topPlayerName = balanceIntegration.getPurseNameFromPlaceholder(namePlaceholder);
            int topPlayerBalance = balanceIntegration.getPurseBalanceFromPlaceholder(balancePlaceholder);

            if (!topPlayerName.equals("&cPlaceholder inválido") && topPlayerBalance != -1) {
                plugin.debug("Posição " + i + ": Jogador - " + topPlayerName + ", Saldo - " + topPlayerBalance);
                updateTop(categories[i - 1], topPlayerName + " - " + topPlayerBalance);
            } else {
                plugin.debug("Placeholder inválido detectado para a posição " + i);
            }
        }
    }

    private void checkTopTime() {
        plugin.debug("Verificando top de tempo...");
        String topPlayerName = timeIntegration.getTopPlayer();
        long playTime = timeIntegration.getOnlineTime(Bukkit.getPlayer(topPlayerName));
        String playTimeDisplay = formatTime(playTime);

        plugin.debug("Top de tempo: Jogador - " + topPlayerName + ", Tempo - " + playTimeDisplay);
        updateTop("TopTempo", topPlayerName + " - " + playTimeDisplay);
    }

    private void checkTopKDR() {
        plugin.debug("Verificando top de KDR...");
        UUID topPlayerUUID = kdrIntegration.getTopPlayerUUID();

        if (topPlayerUUID != null) {
            String playerName = Bukkit.getOfflinePlayer(topPlayerUUID).getName();
            double kdr = kdrIntegration.getKDR(topPlayerUUID);
            String kdrDisplay = String.format("%.2f", kdr);

            plugin.debug("Top de KDR: Jogador - " + playerName + ", KDR - " + kdrDisplay);
            updateTop("TopKDR", playerName + " - " + kdrDisplay);
        } else {
            plugin.debug("UUID para o top KDR é nulo. Verifique a integração de KDR.");
            updateTop("TopKDR", "&cNinguém - 0.00");
        }
    }

    private void checkTopSkills() {
        plugin.debug("Verificando top de habilidades...");
        String topPlayerName = skillsIntegration.getTopPlayer();
        int skillPoints = skillsIntegration.getTopValue();

        if (!topPlayerName.equals("&cPlaceholder inválido")) {
            plugin.debug("Top de habilidades: Jogador - " + topPlayerName + ", Pontos - " + skillPoints);
            updateTop("TopSkills", topPlayerName + " - " + skillPoints);
        } else {
            plugin.debug("Falha ao obter o placeholder para TopSkills.");
        }
    }

    private void checkTopVotes() {
        plugin.debug("Verificando top de votos...");
        UUID topPlayerUUID = voteIntegration.getTopPlayerUUID();
        String playerName = topPlayerUUID != null ? Bukkit.getOfflinePlayer(topPlayerUUID).getName() : "&cNinguém";
        int voteCount = voteIntegration.getVotes(topPlayerUUID);

        plugin.debug("Top de votos: Jogador - " + playerName + ", Votos - " + voteCount);
        updateTop("TopVoto", playerName + " - " + voteCount);
    }

    private void updateTop(String category, String currentTopPlayerInfo) {
        String previousTopPlayerInfo = previousTopPlaceholders.getOrDefault(category, "&cNinguém");
        plugin.debug("Atualizando top para a categoria " + category + ": anterior - " + previousTopPlayerInfo + ", atual - " + currentTopPlayerInfo);

        if (!previousTopPlayerInfo.equals(currentTopPlayerInfo)) {
            String previousTopPlayerName = previousTopPlayerInfo.split(" - ")[0];
            String currentTopPlayerName = currentTopPlayerInfo.split(" - ")[0];
            executeCommands(previousTopPlayerName, category, false);
            executeCommands(currentTopPlayerName, category, true);
        }

        previousTopPlaceholders.put(category, currentTopPlayerInfo);
        plugin.debug("Top da categoria " + category + " atualizado com sucesso.");
    }

    public Map<String, String> getCurrentTopPlayers() {
        Map<String, String> topPlayers = new HashMap<>();

        topPlayers.put("baltop1", previousTopPlaceholders.getOrDefault("baltop1", "&cNinguém"));
        topPlayers.put("baltop2", previousTopPlaceholders.getOrDefault("baltop2", "&cNinguém"));
        topPlayers.put("baltop3", previousTopPlaceholders.getOrDefault("baltop3", "&cNinguém"));
        topPlayers.put("TopTempo", previousTopPlaceholders.getOrDefault("TopTempo", "&cNinguém"));
        topPlayers.put("TopKDR", previousTopPlaceholders.getOrDefault("TopKDR", "&cNinguém"));
        topPlayers.put("TopSkills", previousTopPlaceholders.getOrDefault("TopSkills", "&cNinguém"));
        topPlayers.put("TopVoto", previousTopPlaceholders.getOrDefault("TopVoto", "&cNinguém"));

        plugin.debug("Obtendo jogadores top atuais: " + topPlayers);
        return topPlayers;
    }

    public void updateTopDonate(String playerName, int donateAmount) {
        if (donateAmount > topDonateAmount) {
            if (!topDonatePlayer.equals("&cNinguém")) {
                executeCommands(topDonatePlayer, "TopDonate", false);
            }

            topDonateAmount = donateAmount;
            topDonatePlayer = playerName;
            executeCommands(topDonatePlayer, "TopDonate", true);

            previousTopPlaceholders.put("TopDonate", topDonatePlayer + " - " + topDonateAmount);
            plugin.debug("Top Donate atualizado: Jogador - " + topDonatePlayer + ", Quantidade - " + topDonateAmount);
        } else {
            plugin.debug("O valor de doação não é maior que o valor do Top Donate atual.");
        }
    }

    private String formatTime(long playTime) {
        long hours = playTime / 3600;
        long minutes = (playTime % 3600) / 60;
        long seconds = playTime % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}