package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registers feature commands at runtime through the CommandMap.
 */
public final class CommandRegistrar {

    private static final String FALLBACK_PREFIX = "novalobby";

    private final NovaLobby plugin;
    private final List<Command> registeredCommands = new ArrayList<>();

    public CommandRegistrar(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        unregisterAll();
        register(new GamemodeCommand(plugin, "gmc", GameMode.CREATIVE, "novalobby.gamemode.creative"));
        register(new GamemodeCommand(plugin, "gms", GameMode.SURVIVAL, "novalobby.gamemode.survival"));
        register(new GamemodeCommand(plugin, "gmsp", GameMode.SPECTATOR, "novalobby.gamemode.spectator"));
        register(new GamemodeCommand(plugin, "gma", GameMode.ADVENTURE, "novalobby.gamemode.adventure"));
        register(new FlyCommand(plugin, "fly"));
        register(new SetSpawnCommand(plugin, "setspawn"));
        register(new PlaytimeCommand(plugin, "playtime"));

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    private void register(Command command) {
        Bukkit.getCommandMap().register(FALLBACK_PREFIX, command);
        registeredCommands.add(command);
    }

    public void unregisterAll() {
        CommandMap commandMap = Bukkit.getCommandMap();
        Map<String, Command> knownCommands = commandMap.getKnownCommands();
        for (Command command : registeredCommands) {
            knownCommands.remove(command.getName(), command);
            knownCommands.remove(FALLBACK_PREFIX + ":" + command.getName(), command);
            command.unregister(commandMap);
        }
        registeredCommands.clear();
    }
}
