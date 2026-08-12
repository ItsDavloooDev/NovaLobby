package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.managers.TimeSelectorManager;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class TimeSelectorListener implements Listener {

    private final NovaLobby plugin;

    public TimeSelectorListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.getTimeSelectorManager().isEnabled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!ItemKeys.isTagged(event.getItem(), ItemKeys.TIME_SELECTOR)) {
            return;
        }
        event.setCancelled(true);
        plugin.getTimeSelectorManager().openGui(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TimeSelectorManager.TimeSelectorHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        TimeSelectorManager.TimeOption option = holder.getOption(event.getRawSlot());
        if (option != null) {
            player.closeInventory();
            plugin.getTimeSelectorManager().applyTime(player, option);
        }
    }
}
