package com.github.zyypj.zytags;

import com.github.zyypj.zytags.commands.ReloadTagsCommand;
import com.github.zyypj.zytags.commands.TagsCommand;
import com.github.zyypj.zytags.commands.TopDonateCommand;
import com.github.zyypj.zytags.commands.ViewTagsCommand;
import com.github.zyypj.zytags.configuration.Configuration;
import com.github.zyypj.zytags.configuration.MessagesConfiguration;
import com.github.zyypj.zytags.inventories.listener.ViewTagsInventoryListener;
import com.github.zyypj.zytags.storage.MySQL;
import com.github.zyypj.zytags.systems.BalanceIntegration;
import com.github.zyypj.zytags.systems.SkillsIntegration;
import com.github.zyypj.zytags.systems.TimeIntegration;
import com.github.zyypj.zytags.systems.kdr.cache.KDRCache;
import com.github.zyypj.zytags.systems.kdr.listener.KDRListener;
import com.github.zyypj.zytags.systems.VoteIntegration;
import com.github.zyypj.zytags.systems.top.TopManager;
import com.github.zyypj.zytags.systems.top.cache.TopCache;
import com.github.zyypj.zytags.systems.top.placeholder.TopPlaceholders;
import com.github.zyypj.zytags.systems.vote.cache.VoteCache;
import com.github.zyypj.zytags.systems.vote.listener.VoteListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

@Getter
public class Main extends JavaPlugin {

    private Configuration configuration;
    private MessagesConfiguration messagesConfiguration;

    private MySQL storage;
    private KDRCache kdrCache;
    private VoteCache voteCache;
    private TopCache topCache;

    private BalanceIntegration balanceIntegration;
    private SkillsIntegration skillsIntegration;
    private TimeIntegration timeIntegration;
    private VoteIntegration voteIntegration;

    private TopManager topManager;

    @Override
    public void onEnable() {

        long startTime = System.currentTimeMillis();

        log(" ", false);

        mrkMessage();

        log(" ", false);
        log("&eLigando Plugin...", false);

        saveDefaultConfig();

        loadConfiguration();
        try {
            connectStorage();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loadManagers();
        registerListeners();
        registerCommands();

        registerPlaceholders();

        long endTime = System.currentTimeMillis();
        log(" ", false);
        log("&aPlugin ligado em " + (endTime - startTime) + "ms!", false);

    }

    @Override
    public void onDisable() {
        storage.close();
        kdrCache.saveAll();
        voteCache.saveAll();
        topCache.saveAll();
    }

    /**
     * Método simples para mandar informações ao console
     *
     * @param message Mensagem a ser exibida ao console
     * @param error true se for um erro
     */
    public void log(String message, boolean error) {
        if (error) {
            getLogger().severe(message.replace("&", "§"));
            return;
        }
        getLogger().info(message.replace("&", "§"));
    }

    private void loadConfiguration() {

        long startTime = System.currentTimeMillis();
        log(" ", false);
        log("&eCarregando configuração...", false);

        configuration = new Configuration(this);
        messagesConfiguration = new MessagesConfiguration(this);

        long endTime = System.currentTimeMillis();
        log("&aConfiguração carregada em " + (endTime - startTime) + "ms!", false);
    }

    private void connectStorage() throws SQLException {

        long startTime = System.currentTimeMillis();
        log(" ", false);
        log("&eConectando ao armazenamento...", false);

        storage = new MySQL(
                configuration.getDatabaseUrl(),
                configuration.getDatabaseUser(),
                configuration.getDatabasePassword()
        );
        storage.connect();
        kdrCache = new KDRCache(this);
        voteCache = new VoteCache(this);
        topCache = new TopCache(this);

        long endTime = System.currentTimeMillis();
        log("&aArmazenamento conectado em " + (endTime - startTime) + "ms!", false);
    }

    private void loadManagers() {

        long startTime = System.currentTimeMillis();
        log(" ", false);
        log("&eCarregando gerenciadores...", false);

        balanceIntegration = new BalanceIntegration();
        skillsIntegration = new SkillsIntegration();
        timeIntegration = new TimeIntegration();
        voteIntegration = new VoteIntegration(this);

        topManager = new TopManager(this);

        long endTime = System.currentTimeMillis();
        log("&aGerenciadores carregados em " + (endTime - startTime) + "ms!", false);
    }

    private void registerListeners() {

        Bukkit.getPluginManager().registerEvents(new KDRListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VoteListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ViewTagsInventoryListener(), this);

    }

    private void registerCommands() {

        getCommand("viewtags").setExecutor(new ViewTagsCommand(this));
        getCommand("reloadtags").setExecutor(new ReloadTagsCommand(this));
        getCommand("zytags").setExecutor(new TagsCommand(this));
        getCommand("settopdonate").setExecutor(new TopDonateCommand(this));

    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TopPlaceholders(this).register();
            log("&aPlaceholderAPI detectado e placeholders registrados!", false);
        } else {
            log("&cPlaceholderAPI não encontrado. Placeholders desativados.", true);
        }
    }

    private void mrkMessage() {
        log("███    ███ ██████  ██   ██", false);
        log("████  ████ ██   ██ ██  ██", false);
        log("██ ████ ██ ██████  █████", false);
        log("██  ██  ██ ██   ██ ██  ██", false);
        log("██      ██ ██   ██ ██   ██", false);
        log("", false);
        log("Feito por tadeu @zypj", false);
        log("discord.gg/mrkk", false);
    }
}