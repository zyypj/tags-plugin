package gg.discord.mrkk.tadeu.zytags.systems.kdr.listener;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.systems.kdr.cache.KDRCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KDRListener implements Listener {

    private final KDRCache kdrCache;

    public KDRListener(Main plugin) {
        this.kdrCache = plugin.getKdrCache();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        kdrCache.addDeath(player);

        if (killer != null) {
            kdrCache.addKill(killer);
        }
    }
}