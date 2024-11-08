package gg.discord.mrkk.tadeu.zytags.systems;

import me.qKing12.RoyaleEconomy.API.APIHandler;
import me.qKing12.RoyaleEconomy.RoyaleEconomy;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceIntegration {

    private final APIHandler api;

    public BalanceIntegration() {
        this.api = RoyaleEconomy.apiHandler;
    }

    public String getTopBalanceName(int position) {
        return api.balanceTop.getTopPurse(position).getName();
    }

    public String getDisplayCoins(int position) {
        return api.balanceTop.getTopPurse(position).getCoinsDisplay();
    }

    public double getPlayerBalance(Player player) {
        UUID uuid = player.getUniqueId();
        return api.balance.getBalance(uuid.toString());
    }

    // Método para processar o placeholder e retornar o nome do jogador no top purse
    public String getPurseNameFromPlaceholder(String placeholder) {
        int position = extractPositionFromPlaceholder(placeholder, "purse_name");
        if (position == -1) return "&cPlaceholder inválido";

        try {
            return api.balanceTop.getTopPurse(position).getName();
        } catch (Exception e) {
            return "&cN/A"; // Retorna valor padrão em caso de erro
        }
    }

    // Método para processar o placeholder e retornar o saldo exibido do jogador no top purse como int
    public int getPurseBalanceFromPlaceholder(String placeholder) {
        int position = extractPositionFromPlaceholder(placeholder, "purse_balance");
        if (position == -1) return -1; // Retorna -1 para indicar erro

        try {
            String coinsDisplay = api.balanceTop.getTopPurse(position).getCoinsDisplay();
            // Converte o valor de coinsDisplay para int
            return Integer.parseInt(coinsDisplay.replaceAll("\\D", "")); // Remove caracteres não numéricos
        } catch (Exception e) {
            return -1; // Retorna -1 em caso de erro
        }
    }

    // Método auxiliar para extrair a posição do placeholder
    private int extractPositionFromPlaceholder(String placeholder, String type) {
        String expectedPrefix = "%royaleeconomy_balancetop_" + type + "_";

        if (placeholder.startsWith(expectedPrefix) && placeholder.endsWith("%")) {
            try {
                String positionString = placeholder.substring(expectedPrefix.length(), placeholder.length() - 1);
                int position = Integer.parseInt(positionString);

                if (position >= 1 && position <= 3) {
                    return position;
                }
            } catch (NumberFormatException e) {
                // Retorna -1 em caso de formato inválido
            }
        }
        return -1; // Retorna -1 se o placeholder for inválido
    }
}