package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class PlayerHiderListener implements Listener {

    private final NovaLobby plugin;

    public PlayerHiderListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.getPlayerHiderManager().isEnabled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!ItemKeys.isTagged(event.getItem(), ItemKeys.PLAYER_HIDER)) {
            return;
        }
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        boolean nowHidden = plugin.getPlayerHiderManager().toggle(player);
        ItemStack updatedItem = plugin.getPlayerHiderManager().buildToggleItem(nowHidden);
        if (updatedItem != null) {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), updatedItem);
        }
        plugin.getMessagesManager().send(player, nowHidden ? "player-hider.hidden" : "player-hider.shown");
    }
}
