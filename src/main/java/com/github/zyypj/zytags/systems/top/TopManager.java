package com.github.zyypj.zytags.systems.top;

import com.github.zyypj.zytags.Main;
import com.github.zyypj.zytags.configuration.Configuration;
import com.github.zyypj.zytags.systems.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TopManager {

    private final Main plugin;
    private final Configuration config;
    private final BalanceIntegration balanceIntegration;
    private final TimeIntegration timeIntegration;
    private final KDRIntegration kdrIntegration;
    private final SkillsIntegration skillsIntegration;
    private final VoteIntegration voteIntegration;

    private final Map<String, String> previousTopPlaceholders = new HashMap<>();
    private final Logger logger;

    private int topDonateAmount = 0;
    private String topDonatePlayer = "&cNinguém";

    public TopManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.logger = plugin.getLogger();
        this.balanceIntegration = new BalanceIntegration();
        this.timeIntegration = new TimeIntegration();
        this.kdrIntegration = new KDRIntegration(plugin);
        this.skillsIntegration = new SkillsIntegration();
        this.voteIntegration = new VoteIntegration(plugin);

        logger.info("Inicializando TopManager e iniciando a tarefa de verificação de tops.");
        startTopCheckTask();
    }

    private void startTopCheckTask() {
        long intervalTicks = config.getTopCheckInterval() * 20 * 60L;
        logger.info("Tarefa de verificação de tops configurada para rodar a cada " + config.getTopCheckInterval() + " minutos.");
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateTopPlayers, 0L, intervalTicks);
    }

    public void updateTopPlayers() {
        logger.info("Iniciando atualização dos tops...");
        checkTopBalance();
        checkTopTime();
        checkTopKDR();
        checkTopSkills();
        checkTopVotes();
        logger.info("Atualização dos tops concluída.");
    }

    private void executeCommands(String playerName, String category, boolean isNewTop) {
        String commandPath = isNewTop ? "commands-to-new-top" : "command-to-old-top";
        logger.info("Executando comandos para " + (isNewTop ? "novo" : "antigo") + " top na categoria: " + category);

        config.getPlugin().getConfig().getStringList("tags." + category + "." + commandPath).forEach(command -> {
            if (playerName != null && !playerName.equals("&cNinguém")) {
                String formattedCommand = command.replace("{PLAYER}", playerName);
                logger.info("Executando comando: " + formattedCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
            }
        });
    }

    private void checkTopBalance() {
        logger.info("Verificando top balance...");
        String[] categories = {"baltop1", "baltop2", "baltop3"};

        for (int i = 1; i <= 3; i++) {
            String namePlaceholder = "%royaleeconomy_balancetop_purse_name_" + i + "%";
            String balancePlaceholder = "%royaleeconomy_balancetop_purse_balance_" + i + "%";

            String topPlayerName = balanceIntegration.getPurseBalanceDisplayFromPlaceholder(namePlaceholder);
            String topPlayerBalance = balanceIntegration.getPurseNameFromPlaceholder(balancePlaceholder);

            logger.info("Posição " + i + ": Jogador - " + topPlayerName + ", Saldo - " + topPlayerBalance);
            updateTop(categories[i - 1], topPlayerName + " - " + topPlayerBalance);
        }
    }

    private void checkTopTime() {
        logger.info("Verificando top de tempo...");
        String topPlayerName = timeIntegration.getTopPlayer();
        long playTime = timeIntegration.getOnlineTime(Bukkit.getPlayer(topPlayerName));
        String playTimeDisplay = formatTime(playTime); // Formate o tempo de jogo como uma string legível

        logger.info("Top de tempo: Jogador - " + topPlayerName + ", Tempo - " + playTimeDisplay);
        updateTop("TopTempo", topPlayerName + " - " + playTimeDisplay);
    }

    private void checkTopKDR() {
        logger.info("Verificando top de KDR...");
        UUID topPlayerUUID = kdrIntegration.getTopPlayerUUID();

        if (topPlayerUUID != null) {
            String playerName = Bukkit.getOfflinePlayer(topPlayerUUID).getName();
            double kdr = kdrIntegration.getKDR(topPlayerUUID);
            String kdrDisplay = String.format("%.2f", kdr);

            logger.info("Top de KDR: Jogador - " + playerName + ", KDR - " + kdrDisplay);
            updateTop("TopKDR", playerName + " - " + kdrDisplay);
        } else {
            logger.warning("UUID para o top KDR é nulo. Verifique a integração de KDR.");
            updateTop("TopKDR", "&cNinguém - 0.00");
        }
    }


    private void checkTopSkills() {
        logger.info("Verificando top de habilidades...");
        String topPlayerName = skillsIntegration.getTopPlayer();
        int skillPoints = skillsIntegration.getTopValue();

        logger.info("Top de habilidades: Jogador - " + topPlayerName + ", Pontos - " + skillPoints);
        updateTop("TopSkills", topPlayerName + " - " + skillPoints);
    }

    private void checkTopVotes() {
        logger.info("Verificando top de votos...");
        UUID topPlayerUUID = voteIntegration.getTopPlayerUUID();
        String playerName = topPlayerUUID != null ? Bukkit.getOfflinePlayer(topPlayerUUID).getName() : "&cNinguém";
        int voteCount = voteIntegration.getVotes(topPlayerUUID); // Supondo que getVotes retorne o número de votos

        logger.info("Top de votos: Jogador - " + playerName + ", Votos - " + voteCount);
        updateTop("TopVoto", playerName + " - " + voteCount);
    }

    private void updateTop(String category, String currentTopPlayerInfo) {
        String previousTopPlayerInfo = previousTopPlaceholders.getOrDefault(category, "&cNinguém");
        logger.info("Atualizando top para a categoria " + category + ": anterior - " + previousTopPlayerInfo + ", atual - " + currentTopPlayerInfo);

        if (!previousTopPlayerInfo.equals(currentTopPlayerInfo)) {
            String previousTopPlayerName = previousTopPlayerInfo.split(" - ")[0];
            String currentTopPlayerName = currentTopPlayerInfo.split(" - ")[0];
            executeCommands(previousTopPlayerName, category, false);
            executeCommands(currentTopPlayerName, category, true);
        }

        previousTopPlaceholders.put(category, currentTopPlayerInfo);
        logger.info("Top da categoria " + category + " atualizado com sucesso.");
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

        logger.info("Obtendo jogadores top atuais: " + topPlayers);
        return topPlayers;
    }

    public void updateTopDonate(String playerName, int donateAmount) {
        // Verifica se o novo valor é maior que o topo atual
        if (donateAmount > topDonateAmount) {
            // Executa o comando de saída para o antigo Top Donate
            if (!topDonatePlayer.equals("&cNinguém")) {
                executeCommands(topDonatePlayer, "TopDonate", false);
            }

            // Atualiza o novo Top Donate
            topDonateAmount = donateAmount;
            topDonatePlayer = playerName;

            // Executa o comando de entrada para o novo Top Donate
            executeCommands(topDonatePlayer, "TopDonate", true);

            // Atualiza a entrada de "TopDonate" em previousTopPlaceholders para manter o formato consistente
            previousTopPlaceholders.put("TopDonate", topDonatePlayer + " - " + topDonateAmount);

            logger.info("Top Donate atualizado: Jogador - " + topDonatePlayer + ", Quantidade - " + topDonateAmount);
        } else {
            logger.info("O valor de doação não é maior que o valor do Top Donate atual.");
        }
    }

    private String formatTime(long playTime) {
        long hours = playTime / 3600;
        long minutes = (playTime % 3600) / 60;
        long seconds = playTime % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}