package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FlyCommand extends Command {

    private static final String BASE_PERMISSION = "novalobby.fly";
    private static final String OTHERS_PERMISSION = "novalobby.fly.others";

    private final NovaLobby plugin;

    public FlyCommand(NovaLobby plugin, String name) {
        super(name);
        this.plugin = plugin;
        setDescription("Toggle flight");
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
            boolean enabled = toggleFlight(target);
            plugin.getMessagesManager().send(target, enabled ? "fly.enabled" : "fly.disabled");
            plugin.getMessagesManager().send(sender, enabled ? "fly.enabled-other" : "fly.disabled-other",
                    "%target%", target.getName());
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessagesManager().send(sender, "player-only");
            return true;
        }
        boolean enabled = toggleFlight(player);
        plugin.getMessagesManager().send(player, enabled ? "fly.enabled" : "fly.disabled");
        return true;
    }

    private boolean toggleFlight(Player player) {
        boolean enabled = !player.getAllowFlight();
        player.setAllowFlight(enabled);
        if (!enabled) {
            player.setFlying(false);
        }
        return enabled;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(OTHERS_PERMISSION)) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
