package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SetSpawnCommand extends Command {

    private static final String PERMISSION = "novalobby.setspawn";

    private final NovaLobby plugin;

    public SetSpawnCommand(NovaLobby plugin, String name) {
        super(name);
        this.plugin = plugin;
        setDescription("Set the lobby spawn point");
        setUsage("/" + name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            plugin.getMessagesManager().send(sender, "no-permission");
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessagesManager().send(sender, "player-only");
            return true;
        }
        plugin.getSpawnManager().setSpawn(player.getLocation());
        plugin.getMessagesManager().send(player, "spawn.set");
        return true;
    }
}
