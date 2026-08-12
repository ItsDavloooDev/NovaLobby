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
import org.bukkit.util.Vector;

/**
 * Builds the grappling hook rod and pulls players toward their hook location.
 */
public final class GrapplingHookManager {

    private final NovaLobby plugin;

    public GrapplingHookManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("grappling-hook.enabled", true);
    }

    public ItemStack buildRodItem() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("grappling-hook.item");
        if (section == null) {
            return null;
        }
        Material material = Material.matchMaterial(section.getString("material", "FISHING_ROD"));
        return new ItemBuilder(material == null ? Material.FISHING_ROD : material)
                .name(section.getString("name", "Grappling Hook"))
                .lore(section.getStringList("lore"))
                .unbreakable()
                .tag(ItemKeys.key(), ItemKeys.GRAPPLING_HOOK)
                .build();
    }

    public void pull(Player player, Location hookLocation) {
        double horizontalMultiplier = plugin.getConfig().getDouble("grappling-hook.horizontal-multiplier", 1.6);
        double verticalMultiplier = plugin.getConfig().getDouble("grappling-hook.vertical-multiplier", 1.2);

        Vector direction = hookLocation.toVector().subtract(player.getLocation().toVector());
        if (direction.lengthSquared() == 0) {
            return;
        }
        direction.normalize();
        Vector velocity = new Vector(
                direction.getX() * horizontalMultiplier,
                Math.max(0.4, direction.getY() * verticalMultiplier),
                direction.getZ() * horizontalMultiplier);
        player.setVelocity(velocity);

        if (plugin.getConfig().getBoolean("grappling-hook.no-fall-damage", true)) {
            plugin.getFallProtectionTracker().protect(player.getUniqueId());
        }
        playEffects(player, hookLocation);
    }

    private void playEffects(Player player, Location hookLocation) {
        if (!plugin.getConfig().getBoolean("grappling-hook.effects.enabled", true)) {
            return;
        }
        World world = player.getWorld();
        if (!world.equals(hookLocation.getWorld())) {
            return;
        }

        Particle lineParticle = EffectUtil.parseParticle(
                plugin.getConfig().getString("grappling-hook.effects.line-particle", "END_ROD"),
                Particle.END_ROD);
        Location start = player.getEyeLocation();
        Vector direction = hookLocation.toVector().subtract(start.toVector());
        double distance = Math.min(direction.length(), 40);
        if (distance > 0.1) {
            direction.normalize();
            for (double travelled = 0; travelled <= distance; travelled += 0.5) {
                Location point = start.clone().add(direction.clone().multiply(travelled));
                world.spawnParticle(lineParticle, point, 1, 0, 0, 0, 0);
            }
        }
        world.spawnParticle(Particle.CLOUD, hookLocation, 8, 0.2, 0.2, 0.2, 0.02);

        Sound sound = EffectUtil.parseSound(
                plugin.getConfig().getString("grappling-hook.effects.sound", "ITEM_TRIDENT_RIPTIDE_1"),
                Sound.ITEM_TRIDENT_RIPTIDE_1);
        world.playSound(player.getLocation(), sound,
                (float) plugin.getConfig().getDouble("grappling-hook.effects.sound-volume", 1.0),
                (float) plugin.getConfig().getDouble("grappling-hook.effects.sound-pitch", 1.2));
    }
}
