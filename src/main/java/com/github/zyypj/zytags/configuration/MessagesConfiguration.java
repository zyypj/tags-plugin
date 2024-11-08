package com.github.zyypj.zytags.configuration;

import com.github.zyypj.zytags.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MessagesConfiguration {

    private final Main plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessagesConfiguration(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    // Carrega o arquivo messages.yml
    private void loadConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    // Método para recarregar a configuração
    public void reloadConfig() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        plugin.getLogger().info("Configuração de mensagens recarregada.");
    }

    // Retorna uma mensagem simples com suporte a cores
    public String getMessage(String path) {
        if (messagesConfig.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(path, ""));
        } else {
            plugin.getLogger().warning("Mensagem não encontrada em messages.yml: " + path);
            return ChatColor.RED + "Mensagem não encontrada: " + path;
        }
    }

    // Retorna uma lista de mensagens com suporte a cores
    public List<String> getListMessage(String path) {
        if (messagesConfig.contains(path)) {
            List<String> messages = messagesConfig.getStringList(path);
            messages.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
            return messages;
        } else {
            plugin.getLogger().warning("Lista de mensagens não encontrada em messages.yml: " + path);
            return Collections.singletonList(ChatColor.RED + "Lista de mensagens não encontrada: " + path);
        }
    }
}
