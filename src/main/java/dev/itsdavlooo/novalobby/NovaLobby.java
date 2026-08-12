package dev.itsdavlooo.novalobby;

import dev.itsdavlooo.novalobby.commands.CommandRegistrar;
import dev.itsdavlooo.novalobby.commands.NovaLobbyCommand;
import dev.itsdavlooo.novalobby.config.ConfigManager;
import dev.itsdavlooo.novalobby.config.MessagesManager;
import dev.itsdavlooo.novalobby.listeners.DamageListener;
import dev.itsdavlooo.novalobby.listeners.EnderButtListener;
import dev.itsdavlooo.novalobby.listeners.GrapplingHookListener;
import dev.itsdavlooo.novalobby.listeners.LaunchpadListener;
import dev.itsdavlooo.novalobby.listeners.PlayerHiderListener;
import dev.itsdavlooo.novalobby.listeners.PlayerJoinListener;
import dev.itsdavlooo.novalobby.listeners.ProtectionListener;
import dev.itsdavlooo.novalobby.listeners.SelectorListener;
import dev.itsdavlooo.novalobby.listeners.TimeSelectorListener;
import dev.itsdavlooo.novalobby.managers.EnderButtManager;
import dev.itsdavlooo.novalobby.managers.GrapplingHookManager;
import dev.itsdavlooo.novalobby.managers.LaunchpadManager;
import dev.itsdavlooo.novalobby.managers.LoginMessageManager;
import dev.itsdavlooo.novalobby.managers.PlayerHiderManager;
import dev.itsdavlooo.novalobby.managers.PlaytimeManager;
import dev.itsdavlooo.novalobby.managers.SelectorManager;
import dev.itsdavlooo.novalobby.managers.SpawnManager;
import dev.itsdavlooo.novalobby.managers.TimeSelectorManager;
import dev.itsdavlooo.novalobby.utils.BungeeMessenger;
import dev.itsdavlooo.novalobby.utils.FallProtectionTracker;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * NovaLobby main class: wires managers, listeners and runtime command registration.
 */
public final class NovaLobby extends JavaPlugin {

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private SelectorManager selectorManager;
    private LaunchpadManager launchpadManager;
    private GrapplingHookManager grapplingHookManager;
    private EnderButtManager enderButtManager;
    private PlayerHiderManager playerHiderManager;
    private PlaytimeManager playtimeManager;
    private LoginMessageManager loginMessageManager;
    private SpawnManager spawnManager;
    private TimeSelectorManager timeSelectorManager;
    private FallProtectionTracker fallProtectionTracker;
    private CommandRegistrar commandRegistrar;

    @Override
    public void onEnable() {
        ItemKeys.init(this);
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        fallProtectionTracker = new FallProtectionTracker();
        selectorManager = new SelectorManager(this);
        launchpadManager = new LaunchpadManager(this);
        grapplingHookManager = new GrapplingHookManager(this);
        enderButtManager = new EnderButtManager(this);
        playerHiderManager = new PlayerHiderManager(this);
        playtimeManager = new PlaytimeManager();
        loginMessageManager = new LoginMessageManager(this);
        spawnManager = new SpawnManager(this);
        timeSelectorManager = new TimeSelectorManager(this);
        commandRegistrar = new CommandRegistrar(this);

        registerListeners();
        registerCommands();
        freezeWorldTime();
        getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessenger.CHANNEL);
    }

    private void freezeWorldTime() {
        if (!getConfig().getBoolean("world.freeze-time", true)) {
            return;
        }
        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    @Override
    public void onDisable() {
        commandRegistrar.unregisterAll();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void reloadPlugin() {
        configManager.reload();
        messagesManager.reload();
        commandRegistrar.registerAll();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new SelectorListener(this), this);
        pluginManager.registerEvents(new LaunchpadListener(this), this);
        pluginManager.registerEvents(new GrapplingHookListener(this), this);
        pluginManager.registerEvents(new EnderButtListener(this), this);
        pluginManager.registerEvents(new PlayerHiderListener(this), this);
        pluginManager.registerEvents(new DamageListener(this), this);
        pluginManager.registerEvents(new ProtectionListener(this), this);
        pluginManager.registerEvents(new TimeSelectorListener(this), this);
    }

    private void registerCommands() {
        PluginCommand novaLobbyCommand = getCommand("novalobby");
        if (novaLobbyCommand != null) {
            NovaLobbyCommand executor = new NovaLobbyCommand(this);
            novaLobbyCommand.setExecutor(executor);
            novaLobbyCommand.setTabCompleter(executor);
        }
        commandRegistrar.registerAll();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public SelectorManager getSelectorManager() {
        return selectorManager;
    }

    public LaunchpadManager getLaunchpadManager() {
        return launchpadManager;
    }

    public GrapplingHookManager getGrapplingHookManager() {
        return grapplingHookManager;
    }

    public EnderButtManager getEnderButtManager() {
        return enderButtManager;
    }

    public PlayerHiderManager getPlayerHiderManager() {
        return playerHiderManager;
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }

    public LoginMessageManager getLoginMessageManager() {
        return loginMessageManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public TimeSelectorManager getTimeSelectorManager() {
        return timeSelectorManager;
    }

    public FallProtectionTracker getFallProtectionTracker() {
        return fallProtectionTracker;
    }
}
