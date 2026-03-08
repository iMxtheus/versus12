package me.robomonkey.versus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samjakob.spigui.SpiGUI;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.arena.command.RootArenaCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.command.OneVOneCommand;
import me.robomonkey.versus.duel.command.Root1v1Command;
import me.robomonkey.versus.duel.command.RootSpectateCommand;
import me.robomonkey.versus.duel.eventlisteners.BlockBreakListener;
import me.robomonkey.versus.duel.eventlisteners.BlockPlaceListener;
import me.robomonkey.versus.duel.eventlisteners.CommandListener;
import me.robomonkey.versus.duel.eventlisteners.DamageEventListener;
import me.robomonkey.versus.duel.eventlisteners.DuelInteractListener;
import me.robomonkey.versus.duel.eventlisteners.FireworkExplosionListener;
import me.robomonkey.versus.duel.eventlisteners.InteractEventListener;
import me.robomonkey.versus.duel.eventlisteners.JoinEventListener;
import me.robomonkey.versus.duel.eventlisteners.MoveEventListener;
import me.robomonkey.versus.duel.eventlisteners.PlayerDeathListener;
import me.robomonkey.versus.duel.eventlisteners.QuitEventListener;
import me.robomonkey.versus.duel.eventlisteners.RespawnEventListener;
import me.robomonkey.versus.duel.playerdata.adapter.ConfigurationSerializableAdapter;
import me.robomonkey.versus.duel.playerdata.adapter.ItemStackAdapter;
import me.robomonkey.versus.duel.playerdata.adapter.ItemStackArrayAdapter;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Versus extends JavaPlugin {

    private static Gson gson;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private static Versus instance;
    private final static String prefix = "[Versus]";
    private static final int pluginId = 23279;
    public static SpiGUI spiGUI;

    public static void log(String message) {
        Bukkit.getServer().getLogger().info(prefix + " " + message);
    }

    public static void error(String message) {
        log("Error: " + message);
    }

    public static Gson getGSON() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                    .registerTypeAdapter(ItemStack[].class, new ItemStackArrayAdapter());
            gson = builder.create();
        }
        return gson;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Versus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        log("Versus has been enabled!");

        Settings.getInstance().registerConfig();
        spiGUI = new SpiGUI(this);

        duelManager = DuelManager.getInstance();
        arenaManager = ArenaManager.getInstance();
        arenaManager.loadArenas();

        registerCommands();
        registerListeners();
        registerMetrics();
    }

    @Override
    public void onDisable() {
        log("Versus has been disabled!");
        arenaManager.saveAllArenas();
    }

    public void registerCommands() {
        new RootArenaCommand();
        new RootVersusCommand();
        new Root1v1Command();
        new RootSpectateCommand();
        new OneVOneCommand();
    }

    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new BlockPlaceListener(), this);
        pluginManager.registerEvents(new CommandListener(), this);
        pluginManager.registerEvents(new DamageEventListener(), this);
        pluginManager.registerEvents(new DuelInteractListener(), this);
        pluginManager.registerEvents(new FireworkExplosionListener(), this);
        pluginManager.registerEvents(new InteractEventListener(), this);
        pluginManager.registerEvents(new JoinEventListener(), this);
        pluginManager.registerEvents(new MoveEventListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new QuitEventListener(), this);
        pluginManager.registerEvents(new RespawnEventListener(), this);
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, pluginId);
        List<Setting> noted = List.of(
                Setting.FIGHT_MUSIC_ENABLED,
                Setting.VICTORY_MUSIC_ENABLED,
                Setting.RETURN_WINNERS,
                Setting.RETURN_LOSERS,
                Setting.ANNOUNCE_DUELS,
                Setting.FIREWORKS_ENABLED,
                Setting.VICTORY_EFFECTS_ENABLED
        );

        noted.forEach(setting ->
                metrics.addCustomChart(
                        new SimplePie(setting.toString().toLowerCase(),
                                () -> Settings.getStringVersion(setting)
                        )
                )
        );
    }
}