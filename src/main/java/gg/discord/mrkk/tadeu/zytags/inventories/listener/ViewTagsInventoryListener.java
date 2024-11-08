package gg.discord.mrkk.tadeu.zytags.inventories.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class ViewTagsInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8Top Tags") && event.getInventory().getType() == InventoryType.CHEST) {
            event.setCancelled(true);
        }
    }
}
