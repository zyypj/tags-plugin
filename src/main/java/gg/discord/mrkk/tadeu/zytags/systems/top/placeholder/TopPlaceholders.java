package gg.discord.mrkk.tadeu.zytags.systems.top.placeholder;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.systems.top.cache.TopCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import java.util.Set;

public class TopPlaceholders extends PlaceholderExpansion {

    private final TopCache topCache;

    public TopPlaceholders(Main plugin) {
        this.topCache = plugin.getTopCache();
    }

    @Override
    public String getIdentifier() {
        return "zytags";
    }

    @Override
    public String getAuthor() {
        return "zyypj";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onRequest(Player player, String identifier) {
        if (identifier.startsWith("top_history_")) {
            String category = identifier.replace("top_history_", "");
            Set<String> history = topCache.getCategoryHistory(category);
            return history.isEmpty() ? "&cNenhum histórico" : String.join(", ", history);
        }
        return null;
    }
}