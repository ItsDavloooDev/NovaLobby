package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerJoinListener implements Listener {

    private final NovaLobby plugin;

    public PlayerJoinListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(null);
        if (plugin.getSpawnManager().isTeleportOnJoinEnabled()) {
            plugin.getSpawnManager().teleportToSpawn(player);
        }
        if (plugin.getLoginMessageManager().isEnabled()) {
            plugin.getLoginMessageManager().broadcastLoginMessage(player);
        }

        plugin.getPlayerHiderManager().applyToNewPlayer(player);
        plugin.getPlaytimeManager().track(player);
        giveJoinItems(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        plugin.getPlayerHiderManager().clear(player);
        plugin.getPlaytimeManager().clear(player);
        plugin.getFallProtectionTracker().clear(player.getUniqueId());
    }

    private void giveJoinItems(Player player) {
        if (plugin.getConfig().getBoolean("selector.item.give-on-join", true)) {
            setItem(player, plugin.getConfig().getInt("selector.item.slot", 5),
                    plugin.getSelectorManager().buildSelectorItem());
        }
        if (plugin.getGrapplingHookManager().isEnabled()
                && plugin.getConfig().getBoolean("grappling-hook.item.give-on-join", true)) {
            setItem(player, plugin.getConfig().getInt("grappling-hook.item.slot", 2),
                    plugin.getGrapplingHookManager().buildRodItem());
        }
        if (plugin.getEnderButtManager().isEnabled()
                && plugin.getConfig().getBoolean("enderbutt.item.give-on-join", true)) {
            setItem(player, plugin.getConfig().getInt("enderbutt.item.slot", 1),
                    plugin.getEnderButtManager().buildPearlItem());
        }
        if (plugin.getTimeSelectorManager().isEnabled()
                && plugin.getConfig().getBoolean("time-selector.item.give-on-join", true)) {
            setItem(player, plugin.getConfig().getInt("time-selector.item.slot", 7),
                    plugin.getTimeSelectorManager().buildSelectorItem());
        }
        if (plugin.getPlayerHiderManager().isEnabled()
                && plugin.getConfig().getBoolean("player-hider.give-on-join", true)) {
            setItem(player, plugin.getConfig().getInt("player-hider.slot", 8),
                    plugin.getPlayerHiderManager().buildToggleItem(false));
        }
    }

    private void setItem(Player player, int slot, ItemStack itemStack) {
        if (itemStack != null && slot >= 0 && slot <= 8) {
            player.getInventory().setItem(slot, itemStack);
        }
    }
}
