package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Stores the lobby spawn point set with /setspawn and teleports players to it
 * on join and on void fall.
 */
public final class SpawnManager {

    private final NovaLobby plugin;

    public SpawnManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public void setSpawn(Location location) {
        String serialized = String.format(Locale.ROOT, "%s;%.2f;%.2f;%.2f;%.2f;%.2f",
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        plugin.getConfig().set("spawn.location", serialized);
        plugin.saveConfig();
    }

    public Location getSpawn() {
        String serialized = plugin.getConfig().getString("spawn.location", "");
        if (serialized.isEmpty()) {
            return null;
        }
        String[] parts = serialized.split(";");
        if (parts.length != 6) {
            return null;
        }
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }
        try {
            return new Location(world,
                    Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                    Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public boolean teleportToSpawn(Player player) {
        Location spawn = getSpawn();
        if (spawn == null) {
            return false;
        }
        player.setFallDistance(0);
        player.teleport(spawn);
        return true;
    }

    public boolean isTeleportOnJoinEnabled() {
        return plugin.getConfig().getBoolean("spawn.teleport-on-join", true);
    }

    public boolean isVoidTeleportEnabled() {
        return plugin.getConfig().getBoolean("spawn.void-teleport", true);
    }

    public double getVoidY() {
        return plugin.getConfig().getDouble("spawn.void-y", -64);
    }
}
