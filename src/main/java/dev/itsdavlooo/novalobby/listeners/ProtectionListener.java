package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ProtectionListener implements Listener {

    private final NovaLobby plugin;

    public ProtectionListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!plugin.getConfig().getBoolean("disable-hunger", true)) {
            return;
        }
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (!plugin.getConfig().getBoolean("disable-item-drop", true)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getSpawnManager().isVoidTeleportEnabled()) {
            return;
        }
        if (event.getTo().getY() >= plugin.getSpawnManager().getVoidY()) {
            return;
        }
        Player player = event.getPlayer();
        if (!plugin.getSpawnManager().teleportToSpawn(player)) {
            player.setFallDistance(0);
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }
}
