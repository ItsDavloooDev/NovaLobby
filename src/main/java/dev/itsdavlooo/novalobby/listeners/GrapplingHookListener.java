package dev.itsdavlooo.novalobby.listeners;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class GrapplingHookListener implements Listener {

    private final NovaLobby plugin;

    public GrapplingHookListener(NovaLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!plugin.getGrapplingHookManager().isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!isHoldingRod(player)) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.FISHING
                && plugin.getConfig().getBoolean("grappling-hook.stick-to-walls", true)
                && event.getHook() != null) {
            new ImpactMonitorTask(plugin, event.getHook()).runTaskTimer(plugin, 1L, 1L);
            return;
        }

        if (event.getState() != PlayerFishEvent.State.REEL_IN
                && event.getState() != PlayerFishEvent.State.IN_GROUND) {
            return;
        }
        plugin.getGrapplingHookManager().pull(player, event.getHook().getLocation());
    }

    private boolean isHoldingRod(Player player) {
        return ItemKeys.isTagged(player.getInventory().getItemInMainHand(), ItemKeys.GRAPPLING_HOOK)
                || ItemKeys.isTagged(player.getInventory().getItemInOffHand(), ItemKeys.GRAPPLING_HOOK);
    }

    /**
     * FishHook's ProjectileHitEvent is unreliable against vertical block faces
     * on Paper/Canvas, so impacts are detected manually via block ray tracing.
     */
    private static final class ImpactMonitorTask extends BukkitRunnable {

        private final NovaLobby plugin;
        private final FishHook hook;
        private Location previousLocation;

        private ImpactMonitorTask(NovaLobby plugin, FishHook hook) {
            this.plugin = plugin;
            this.hook = hook;
            this.previousLocation = hook.getLocation();
        }

        @Override
        public void run() {
            if (!hook.isValid() || hook.isDead()) {
                cancel();
                return;
            }
            Location currentLocation = hook.getLocation();
            Vector delta = currentLocation.toVector().subtract(previousLocation.toVector());
            double distance = delta.length();
            if (distance > 0.0001) {
                RayTraceResult result = hook.getWorld().rayTraceBlocks(
                        previousLocation, delta.normalize(), distance, FluidCollisionMode.NEVER, true);
                if (result != null && result.getHitBlock() != null) {
                    Location pinnedLocation = result.getHitPosition().toLocation(hook.getWorld());
                    hook.setGravity(false);
                    hook.setVelocity(new Vector(0, 0, 0));
                    hook.teleport(pinnedLocation);
                    new PinTask(hook, pinnedLocation).runTaskTimer(plugin, 1L, 1L);
                    cancel();
                    return;
                }
            }
            previousLocation = currentLocation;
        }
    }

    private static final class PinTask extends BukkitRunnable {

        private final FishHook hook;
        private final Location pinnedLocation;

        private PinTask(FishHook hook, Location pinnedLocation) {
            this.hook = hook;
            this.pinnedLocation = pinnedLocation;
        }

        @Override
        public void run() {
            if (!hook.isValid() || hook.isDead()) {
                cancel();
                return;
            }
            hook.setVelocity(new Vector(0, 0, 0));
            if (hook.getLocation().distanceSquared(pinnedLocation) > 0.01) {
                hook.teleport(pinnedLocation);
            }
        }
    }
}
