package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PlaytimeCommand extends Command {

    private static final String BASE_PERMISSION = "novalobby.playtime";
    private static final String OTHERS_PERMISSION = "novalobby.playtime.others";

    private final NovaLobby plugin;

    public PlaytimeCommand(NovaLobby plugin, String name) {
        super(name);
        this.plugin = plugin;
        setDescription("Show time spent in the lobby this session");
        setUsage("/" + name + " [player]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!sender.hasPermission(BASE_PERMISSION)) {
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
            plugin.getMessagesManager().send(sender, "playtime.other",
                    "%target%", target.getName(),
                    "%playtime%", plugin.getPlaytimeManager().getFormattedPlaytime(target));
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessagesManager().send(sender, "player-only");
            return true;
        }
        plugin.getMessagesManager().send(player, "playtime.self",
                "%playtime%", plugin.getPlaytimeManager().getFormattedPlaytime(player));
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
