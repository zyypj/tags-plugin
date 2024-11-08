package com.github.zyypj.zytags.configuration;

import com.github.zyypj.zytags.Main;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Configuration {

    private final Main plugin;
    private int databaseSaveInterval;
    private int topCheckInterval;
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    public Configuration(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        this.databaseUrl = config.getString("database.url", "jdbc:mysql://localhost:3306/minecraft");
        this.databaseUser = config.getString("database.user", "root");
        this.databasePassword = config.getString("database.password", "password");

        this.databaseSaveInterval = config.getInt("database-save-interval", 20);
        this.topCheckInterval = config.getInt("top-check-interval", 5);
    }
}
