package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.ItemBuilder;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Instantly toggles visibility of other players with no cooldown,
 * keeping per-session state.
 */
public final class PlayerHiderManager {

    private final NovaLobby plugin;
    private final Set<UUID> hidingPlayers = ConcurrentHashMap.newKeySet();

    public PlayerHiderManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("player-hider.enabled", true);
    }

    public boolean isHiding(Player player) {
        return hidingPlayers.contains(player.getUniqueId());
    }

    public boolean toggle(Player player) {
        if (isHiding(player)) {
            show(player);
            return false;
        }
        hide(player);
        return true;
    }

    public void hide(Player player) {
        hidingPlayers.add(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.hidePlayer(plugin, other);
            }
        }
    }

    public void show(Player player) {
        hidingPlayers.remove(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.showPlayer(plugin, other);
            }
        }
    }

    public void applyToNewPlayer(Player joined) {
        for (UUID hidingId : hidingPlayers) {
            Player hiding = Bukkit.getPlayer(hidingId);
            if (hiding != null && !hiding.equals(joined)) {
                hiding.hidePlayer(plugin, joined);
            }
        }
    }

    public void clear(Player player) {
        hidingPlayers.remove(player.getUniqueId());
    }

    public ItemStack buildToggleItem(boolean hidden) {
        String path = hidden ? "player-hider.item-hidden" : "player-hider.item-shown";
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        Material fallback = hidden ? Material.GRAY_DYE : Material.LIME_DYE;
        Material material = Material.matchMaterial(section.getString("material", fallback.name()));
        return new ItemBuilder(material == null ? fallback : material)
                .name(section.getString("name", "Player Hider"))
                .lore(section.getStringList("lore"))
                .tag(ItemKeys.key(), ItemKeys.PLAYER_HIDER)
                .build();
    }
}
