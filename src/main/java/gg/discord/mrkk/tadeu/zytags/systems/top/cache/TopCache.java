package gg.discord.mrkk.tadeu.zytags.systems.top.cache;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.storage.MySQL;

import java.util.*;

public class TopCache {

    private final Map<String, Set<String>> topHistoryCache = new HashMap<>();
    private final MySQL database;

    public TopCache(Main plugin) {
        this.database = plugin.getStorage();
    }

    public void addPlayerToHistory(String category, String playerName) {
        topHistoryCache.computeIfAbsent(category, k -> new HashSet<>()).add(playerName);
    }

    public Set<String> getCategoryHistory(String category) {
        return topHistoryCache.getOrDefault(category, Collections.emptySet());
    }

    public void saveAll() {
        topHistoryCache.forEach((category, players) -> players.forEach(playerName -> {
            try {
                database.saveTopHistory(category, playerName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}