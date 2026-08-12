package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.projectiles.ProjectileSource;

public final class EnderButtListener implements Listener {

    private final NovaLobby plugin;

    public EnderButtListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.getEnderButtManager().isEnabled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!ItemKeys.isTagged(event.getItem(), ItemKeys.ENDERBUTT)) {
            return;
        }
        event.setCancelled(true);
        plugin.getEnderButtManager().launch(event.getPlayer());
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Player player)) {
            return;
        }
        if (ItemKeys.isTagged(player.getInventory().getItemInMainHand(), ItemKeys.ENDERBUTT)
                || ItemKeys.isTagged(player.getInventory().getItemInOffHand(), ItemKeys.ENDERBUTT)) {
            event.setCancelled(true);
        }
    }
}
