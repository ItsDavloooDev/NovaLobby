package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LaunchpadListener implements Listener {

    private final NovaLobby plugin;

    public LaunchpadListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlateStep(PlayerInteractEvent event) {
        if (!plugin.getLaunchpadManager().isEnabled()) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null) {
            return;
        }
        if (!plugin.getLaunchpadManager().isLaunchpad(event.getClickedBlock())) {
            return;
        }
        event.setCancelled(true);
        plugin.getLaunchpadManager().launch(event.getPlayer());
    }
}
