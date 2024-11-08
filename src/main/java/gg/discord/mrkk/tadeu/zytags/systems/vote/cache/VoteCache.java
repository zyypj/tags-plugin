package gg.discord.mrkk.tadeu.zytags.systems.vote.cache;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.storage.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class VoteCache {

    private final Map<UUID, Integer> cache = new HashMap<>();
    private final Main plugin;
    private final MySQL database;
    private final int saveInterval;

    public VoteCache(Main plugin) {
        this.plugin = plugin;
        this.database = plugin.getStorage();
        this.saveInterval = plugin.getConfiguration().getDatabaseSaveInterval();
        startAutoSaveTask();
    }

    public void addVote(Player player) {
        UUID playerUUID = player.getUniqueId();
        cache.put(playerUUID, cache.getOrDefault(playerUUID, 0) + 1);
    }

    private void startAutoSaveTask() {
        long intervalTicks = saveInterval * 20 * 60L;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cache.forEach((uuid, votes) -> {
                try {
                    database.updateVotes(uuid.toString(), votes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            cache.clear(); // Limpa o cache após salvar no banco
        }, intervalTicks, intervalTicks);
    }

    public void saveAll() {
        cache.forEach((uuid, votes) -> {
            try {
                database.updateVotes(uuid.toString(), votes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        cache.clear(); // Limpa o cache após salvar no banco
    }

    public int getVotes(UUID playerUUID) {
        return cache.getOrDefault(playerUUID, 0);
    }

    public Map<UUID, Integer> getAllVotes() {
        return Collections.unmodifiableMap(new HashMap<>(cache));
    }
}
