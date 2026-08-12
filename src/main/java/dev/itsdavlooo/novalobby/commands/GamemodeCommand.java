package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GamemodeCommand extends Command {

    private static final String OTHERS_PERMISSION = "novalobby.gamemode.others";

    private final NovaLobby plugin;
    private final GameMode gameMode;
    private final String permission;

    public GamemodeCommand(NovaLobby plugin, String name, GameMode gameMode, String permission) {
        super(name);
        this.plugin = plugin;
        this.gameMode = gameMode;
        this.permission = permission;
        setDescription("Set gamemode to " + gameMode.name().toLowerCase());
        setUsage("/" + name + " [player]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            plugin.getMessagesManager().send(sender, "no-permission");
            return true;
        }
        if (args.length >= 1) {
            if (!sender.hasPermission(OTHERS_PERMISSION)) {
                plugin.getMessagesManager().send(sender, "no-permission");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                plugin.getMessagesManager().send(sender, "player-not-found", "%target%", args[0]);
                return true;
            }
            target.setGameMode(gameMode);
            plugin.getMessagesManager().send(target, "gamemode.changed", "%gamemode%", gameMode.name().toLowerCase());
            plugin.getMessagesManager().send(sender, "gamemode.changed-other",
                    "%target%", target.getName(), "%gamemode%", gameMode.name().toLowerCase());
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessagesManager().send(sender, "player-only");
            return true;
        }
        player.setGameMode(gameMode);
        plugin.getMessagesManager().send(player, "gamemode.changed", "%gamemode%", gameMode.name().toLowerCase());
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(OTHERS_PERMISSION)) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
