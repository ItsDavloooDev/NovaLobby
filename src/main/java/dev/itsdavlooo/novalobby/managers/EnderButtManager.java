package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.EffectUtil;
import dev.itsdavlooo.novalobby.utils.ItemBuilder;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Builds the enderbutt pearl and applies dash or vertical launch velocity
 * based on the player's pitch.
 */
public final class EnderButtManager {

    private final NovaLobby plugin;

    public EnderButtManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("enderbutt.enabled", true);
    }

    public ItemStack buildPearlItem() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("enderbutt.item");
        if (section == null) {
            return null;
        }
        Material material = Material.matchMaterial(section.getString("material", "ENDER_PEARL"));
        return new ItemBuilder(material == null ? Material.ENDER_PEARL : material)
                .name(section.getString("name", "EnderButt"))
                .lore(section.getStringList("lore"))
                .tag(ItemKeys.key(), ItemKeys.ENDERBUTT)
                .build();
    }

    public void launch(Player player) {
        float pitch = player.getLocation().getPitch();
        double downThreshold = plugin.getConfig().getDouble("enderbutt.pitch-down-threshold", 60);
        double upThreshold = plugin.getConfig().getDouble("enderbutt.pitch-up-threshold", -60);
        double verticalLaunchPower = plugin.getConfig().getDouble("enderbutt.vertical-launch-power", 2.0);

        Vector velocity;
        if (pitch >= downThreshold) {
            velocity = new Vector(0, -verticalLaunchPower, 0);
        } else if (pitch <= upThreshold) {
            velocity = new Vector(0, verticalLaunchPower, 0);
        } else {
            double horizontalPower = plugin.getConfig().getDouble("enderbutt.dash-horizontal-power", 2.6);
            double verticalPower = plugin.getConfig().getDouble("enderbutt.dash-vertical-power", 0.0);
            Vector direction = player.getLocation().getDirection().setY(0);
            if (direction.lengthSquared() > 0) {
                direction.normalize().multiply(horizontalPower);
            }
            velocity = direction.setY(verticalPower);
        }
        player.setVelocity(velocity);

        if (plugin.getConfig().getBoolean("enderbutt.no-fall-damage", true)) {
            plugin.getFallProtectionTracker().protect(player.getUniqueId());
        }
        playEffects(player);
    }

    private void playEffects(Player player) {
        if (!plugin.getConfig().getBoolean("enderbutt.effects.enabled", true)) {
            return;
        }
        World world = player.getWorld();
        Location location = player.getLocation();

        Particle burst = EffectUtil.parseParticle(
                plugin.getConfig().getString("enderbutt.effects.burst-particle", "REVERSE_PORTAL"),
                Particle.REVERSE_PORTAL);
        world.spawnParticle(burst, location.clone().add(0, 0.8, 0), 45, 0.35, 0.55, 0.35, 0.08);

        Sound sound = EffectUtil.parseSound(
                plugin.getConfig().getString("enderbutt.effects.sound", "ENTITY_ENDERMAN_TELEPORT"),
                Sound.ENTITY_ENDERMAN_TELEPORT);
        world.playSound(location, sound,
                (float) plugin.getConfig().getDouble("enderbutt.effects.sound-volume", 1.0),
                (float) plugin.getConfig().getDouble("enderbutt.effects.sound-pitch", 1.4));

        Particle trail = EffectUtil.parseParticle(
                plugin.getConfig().getString("enderbutt.effects.trail-particle", "PORTAL"),
                Particle.PORTAL);
        int trailTicks = plugin.getConfig().getInt("enderbutt.effects.trail-ticks", 12);
        new BukkitRunnable() {
            private int elapsedTicks;

            @Override
            public void run() {
                if (!player.isOnline() || elapsedTicks++ >= trailTicks) {
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(trail,
                        player.getLocation().add(0, 0.6, 0), 10, 0.15, 0.25, 0.15, 0.02);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
