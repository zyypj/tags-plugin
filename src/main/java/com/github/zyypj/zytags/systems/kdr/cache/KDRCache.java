package com.github.zyypj.zytags.systems.kdr.cache;

import com.github.zyypj.zytags.Main;
import com.github.zyypj.zytags.storage.MySQL;
import com.github.zyypj.zytags.systems.kdr.PlayerKDR;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class KDRCache {

    private final Map<UUID, PlayerKDR> cache = new HashMap<>();
    private final Main plugin;
    private final MySQL database;
    private final int saveInterval;

    public KDRCache(Main plugin) {
        this.plugin = plugin;
        this.database = plugin.getStorage();
        this.saveInterval = plugin.getConfiguration().getDatabaseSaveInterval();
        startAutoSaveTask();
    }

    public void addKill(Player player) {
        UUID playerUUID = player.getUniqueId();
        cache.computeIfAbsent(playerUUID, k -> new PlayerKDR()).addKill();
    }

    public void addDeath(Player player) {
        UUID playerUUID = player.getUniqueId();
        cache.computeIfAbsent(playerUUID, k -> new PlayerKDR()).addDeath();
    }

    private void startAutoSaveTask() {
        // Converte o intervalo para ticks (1 minuto = 20 * 60 ticks)
        long intervalTicks = saveInterval * 20 * 60L;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cache.forEach((uuid, kdr) -> {
                try {
                    database.updateKDR(uuid.toString(), kdr.getKills(), kdr.getDeaths());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }, 0L, intervalTicks);
    }

    public void saveAll() {
        cache.forEach((uuid, kdr) -> {
            try {
                database.updateKDR(uuid.toString(), kdr.getKills(), kdr.getDeaths());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retorna o KDR do jogador combinando dados do cache e da database.
     *
     * @param playerUUID UUID do jogador
     * @return Instância de PlayerKDR com os dados combinados
     */
    public PlayerKDR getKDR(UUID playerUUID) {
        // Busca o KDR do cache, se existir
        PlayerKDR kdrFromCache = cache.getOrDefault(playerUUID, new PlayerKDR());

        // Busca o KDR do banco de dados
        PlayerKDR kdrFromDatabase = getKDRFromDatabase(playerUUID);

        // Combina os valores de kills e deaths
        PlayerKDR combinedKDR = new PlayerKDR();
        combinedKDR.addKills(kdrFromCache.getKills() + kdrFromDatabase.getKills());
        combinedKDR.addDeaths(kdrFromCache.getDeaths() + kdrFromDatabase.getDeaths());

        return combinedKDR;
    }

    /**
     * Consulta o banco de dados para obter o KDR de um jogador.
     *
     * @param playerUUID UUID do jogador
     * @return Instância de PlayerKDR com dados do banco de dados
     */
    private PlayerKDR getKDRFromDatabase(UUID playerUUID) {
        String sql = "SELECT kills, deaths FROM player_kdr WHERE player_uuid = ?";
        PlayerKDR playerKDR = new PlayerKDR();

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                playerKDR.addKills(rs.getInt("kills"));
                playerKDR.addDeaths(rs.getInt("deaths"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerKDR;
    }
}
