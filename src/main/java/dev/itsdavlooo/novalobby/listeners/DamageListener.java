package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamageListener implements Listener {

    private static final String FLY_DAMAGE_BYPASS_PERMISSION = "novalobby.fly.bypass.damage";

    private final NovaLobby plugin;

    public DamageListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            plugin.getFallProtectionTracker().consumeProtection(player.getUniqueId());
            event.setCancelled(true);
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID
                && plugin.getSpawnManager().isVoidTeleportEnabled()) {
            event.setCancelled(true);
            return;
        }
        disableFlightIfConfigured(player);
    }

    private void disableFlightIfConfigured(Player player) {
        if (!plugin.getConfig().getBoolean("fly.disable-on-damage", true)) {
            return;
        }
        if (!player.getAllowFlight()
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.hasPermission(FLY_DAMAGE_BYPASS_PERMISSION)) {
            return;
        }
        player.setAllowFlight(false);
        player.setFlying(false);
        plugin.getMessagesManager().send(player, "fly.disabled-by-damage");
    }
}
