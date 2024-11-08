package com.github.zyypj.zytags.inventories;

import com.github.zyypj.zytags.Main;
import com.github.zyypj.zytags.configuration.MessagesConfiguration;
import com.github.zyypj.zytags.systems.top.TopManager;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewTagsInventory extends SimpleInventory {

    private final Main plugin;
    private final MessagesConfiguration messageManager;

    public ViewTagsInventory(Main plugin) {
        super("top.tags.menu", "&8Top Tags", 9 * 3);
        this.plugin = plugin;
        this.messageManager = plugin.getMessagesConfiguration();
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        TopManager topManager = plugin.getTopManager();
        Map<String, String> topPlayers = topManager.getCurrentTopPlayers();
        int itemCount = topPlayers.size();

        // Configura a borda do inventário
        Border border = Border.of(1);  // Define uma borda de 1 slot em todas as direções

        // Calcula os slots centralizados
        int[] centeredSlots = getCenteredSlots(itemCount, border);

        int index = 0;
        for (Map.Entry<String, String> entry : topPlayers.entrySet()) {
            String category = entry.getKey();
            String playerName = entry.getValue();

            // Busca o nome e a lore do item no MessageManager, substituindo as variáveis
            String itemName = messageManager.getMessage("view-tags-item-name")
                    .replace("{CATEGORY}", category)
                    .replace("{PLAYER}", playerName);

            List<String> itemLore = messageManager.getListMessage("view-tags-item-lore").stream()
                    .map(line -> line.replace("{CATEGORY}", category).replace("{PLAYER}", playerName))
                    .collect(Collectors.toList());

            // Cria o item de exibição com as mensagens configuráveis
            ItemStack itemStack = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(itemName);
            meta.setLore(itemLore);
            itemStack.setItemMeta(meta);

            // Adiciona o item ao inventário em um slot centralizado
            InventoryItem item = InventoryItem.of(itemStack);
            if (index < centeredSlots.length) {
                editor.setItem(centeredSlots[index], item);
            }
            index++;
        }
    }

    private int[] getCenteredSlots(int itemCount, Border border) {
        int totalSlots = getSize();
        int slotsPerRow = 9;
        int usableSlots = totalSlots - (border.getLeft() + border.getRight() + slotsPerRow * (border.getTop() + border.getBottom()));

        int startSlot = border.getLeft() + border.getTop() * slotsPerRow;
        int[] centeredSlots = new int[itemCount];

        // Calcular os slots centralizados
        for (int i = 0; i < itemCount; i++) {
            centeredSlots[i] = startSlot + (i % slotsPerRow) + ((i / slotsPerRow) * slotsPerRow);
        }

        return centeredSlots;
    }
}