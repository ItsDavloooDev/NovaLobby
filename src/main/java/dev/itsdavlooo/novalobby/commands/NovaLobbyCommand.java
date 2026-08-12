package dev.itsdavlooo.novalobby.commands;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/**
 * Handles /novalobby with the reload subcommand.
 */
public final class NovaLobbyCommand implements CommandExecutor, TabCompleter {

    private final NovaLobby plugin;

    public NovaLobbyCommand(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("novalobby.admin")) {
            plugin.getMessagesManager().send(sender, "no-permission");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            plugin.getMessagesManager().send(sender, "reload-success");
            return true;
        }
        sender.sendMessage("Usage: /" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
