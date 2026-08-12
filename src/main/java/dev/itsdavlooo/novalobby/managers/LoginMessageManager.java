package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.TextUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * Broadcasts a rank-based join message resolved through LuckPerms and PlaceholderAPI.
 */
public final class LoginMessageManager {

    private final NovaLobby plugin;

    public LoginMessageManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("login-messages.enabled", true);
    }

    public void broadcastLoginMessage(Player player) {
        String primaryGroup = getPrimaryGroup(player);
        if (primaryGroup == null) {
            return;
        }
        List<String> enabledRanks = plugin.getConfig().getStringList("login-messages.ranks");
        boolean rankEnabled = enabledRanks.stream().anyMatch(rank -> rank.equalsIgnoreCase(primaryGroup));
        if (!rankEnabled) {
            return;
        }
        String format = plugin.getConfig().getString("login-messages.format", "");
        if (format.isEmpty()) {
            return;
        }
        String message = format
                .replace("%player_name%", player.getName())
                .replace("%player%", player.getName())
                .replace("%luckperms_prefix%", resolvePrefix(player));
        message = TextUtil.applyPlaceholders(player, message);
        Bukkit.getServer().sendMessage(TextUtil.parse(message));
    }

    private String getPrimaryGroup(Player player) {
        if (!isLuckPermsAvailable()) {
            return null;
        }
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        return user.getPrimaryGroup().toLowerCase(Locale.ROOT);
    }

    private String resolvePrefix(Player player) {
        if (!isLuckPermsAvailable()) {
            return "";
        }
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix == null ? "" : prefix;
    }

    private boolean isLuckPermsAvailable() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }
}
