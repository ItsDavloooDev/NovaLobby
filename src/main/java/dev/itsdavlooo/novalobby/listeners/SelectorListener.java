package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.managers.SelectorManager;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public final class SelectorListener implements Listener {

    private final NovaLobby plugin;

    public SelectorListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!ItemKeys.isTagged(event.getItem(), ItemKeys.SELECTOR)) {
            return;
        }
        event.setCancelled(true);
        plugin.getSelectorManager().openSelector(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SelectorManager.SelectorHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        List<String> actions = holder.getActions(event.getRawSlot());
        if (actions != null) {
            plugin.getSelectorManager().executeActions(player, actions);
        }
    }
}
