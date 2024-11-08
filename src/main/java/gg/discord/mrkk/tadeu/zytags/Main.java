package gg.discord.mrkk.tadeu.zytags;

import gg.discord.mrkk.tadeu.zytags.commands.ReloadTagsCommand;
import gg.discord.mrkk.tadeu.zytags.commands.TagsCommand;
import gg.discord.mrkk.tadeu.zytags.commands.TopDonateCommand;
import gg.discord.mrkk.tadeu.zytags.commands.ViewTagsCommand;
import gg.discord.mrkk.tadeu.zytags.configuration.Configuration;
import gg.discord.mrkk.tadeu.zytags.configuration.MessagesConfiguration;
import gg.discord.mrkk.tadeu.zytags.inventories.listener.ViewTagsInventoryListener;
import gg.discord.mrkk.tadeu.zytags.storage.MySQL;
import gg.discord.mrkk.tadeu.zytags.systems.BalanceIntegration;
import gg.discord.mrkk.tadeu.zytags.systems.SkillsIntegration;
import gg.discord.mrkk.tadeu.zytags.systems.TimeIntegration;
import gg.discord.mrkk.tadeu.zytags.systems.kdr.cache.KDRCache;
import gg.discord.mrkk.tadeu.zytags.systems.kdr.listener.KDRListener;
import gg.discord.mrkk.tadeu.zytags.systems.VoteIntegration;
import gg.discord.mrkk.tadeu.zytags.systems.top.TopManager;
import gg.discord.mrkk.tadeu.zytags.systems.top.cache.TopCache;
import gg.discord.mrkk.tadeu.zytags.systems.top.placeholder.TopPlaceholders;
import gg.discord.mrkk.tadeu.zytags.systems.vote.cache.VoteCache;
import gg.discord.mrkk.tadeu.zytags.systems.vote.listener.VoteListener;
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