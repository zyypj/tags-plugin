package gg.discord.mrkk.tadeu.zytags.systems;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.systems.kdr.PlayerKDR;
import gg.discord.mrkk.tadeu.zytags.systems.kdr.cache.KDRCache;

import java.util.Map;
import java.util.UUID;

public class KDRIntegration {

    private final KDRCache kdrCache;

    public KDRIntegration(Main plugin) {
        this.kdrCache = plugin.getKdrCache();
    }

    public double getKDR(UUID playerUUID) {
        PlayerKDR combinedKDR = kdrCache.getKDR(playerUUID);

        int kills = combinedKDR.getKills();
        int deaths = combinedKDR.getDeaths();

        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public UUID getTopPlayerUUID() {
        UUID topPlayerUUID = null;
        double highestKDR = -1;

        for (Map.Entry<UUID, PlayerKDR> entry : kdrCache.getCache().entrySet()) {
            UUID playerUUID = entry.getKey();
            double kdr = getKDR(playerUUID);

            if (kdr > highestKDR) {
                highestKDR = kdr;
                topPlayerUUID = playerUUID;
            }
        }

        return topPlayerUUID;
    }
}
