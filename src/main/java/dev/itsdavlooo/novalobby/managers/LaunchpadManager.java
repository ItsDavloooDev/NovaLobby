package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.EffectUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Launches players stepping on a configured pressure plate placed on top of
 * a configured block type.
 */
public final class LaunchpadManager {

    private final NovaLobby plugin;

    public LaunchpadManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("launchpads.enabled", true);
    }

    public boolean isLaunchpad(Block plateBlock) {
        Material plateMaterial = Material.matchMaterial(
                plugin.getConfig().getString("launchpads.plate-material", "HEAVY_WEIGHTED_PRESSURE_PLATE"));
        Material blockBelowMaterial = Material.matchMaterial(
                plugin.getConfig().getString("launchpads.block-below", "EMERALD_BLOCK"));
        if (plateMaterial == null || blockBelowMaterial == null) {
            return false;
        }
        return plateBlock.getType() == plateMaterial
                && plateBlock.getRelative(BlockFace.DOWN).getType() == blockBelowMaterial;
    }

    public void launch(Player player) {
        double horizontalPower = plugin.getConfig().getDouble("launchpads.horizontal-power", 2.5);
        double verticalPower = plugin.getConfig().getDouble("launchpads.vertical-power", 1.0);
        Vector direction = player.getLocation().getDirection().setY(0);
        if (direction.lengthSquared() > 0) {
            direction.normalize().multiply(horizontalPower);
        }
        direction.setY(verticalPower);
        player.setVelocity(direction);

        if (plugin.getConfig().getBoolean("launchpads.no-fall-damage", true)) {
            plugin.getFallProtectionTracker().protect(player.getUniqueId());
        }
        playEffects(player.getLocation());
    }

    private void playEffects(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        if (plugin.getConfig().getBoolean("launchpads.particles.enabled", true)) {
            Particle particle = EffectUtil.parseParticle(
                    plugin.getConfig().getString("launchpads.particles.type", "FLAME"), Particle.FLAME);
            world.spawnParticle(particle, location, plugin.getConfig().getInt("launchpads.particles.count", 30),
                    0.3, 0.3, 0.3, 0.05);
        }
        if (plugin.getConfig().getBoolean("launchpads.sound.enabled", true)) {
            Sound sound = EffectUtil.parseSound(
                    plugin.getConfig().getString("launchpads.sound.type", "ENTITY_FIREWORK_ROCKET_LAUNCH"),
                    Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
            world.playSound(location, sound,
                    (float) plugin.getConfig().getDouble("launchpads.sound.volume", 1.0),
                    (float) plugin.getConfig().getDouble("launchpads.sound.pitch", 1.0));
        }
    }
}
