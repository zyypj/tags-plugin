package com.github.zyypj.zytags.systems.kdr.listener;

import com.github.zyypj.zytags.Main;
import com.github.zyypj.zytags.systems.kdr.cache.KDRCache;
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