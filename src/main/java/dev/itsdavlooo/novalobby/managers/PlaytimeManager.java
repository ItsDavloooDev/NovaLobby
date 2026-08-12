package dev.itsdavlooo.novalobby.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the join timestamp of each player and formats the elapsed session time
 * as a simple millisecond difference (no repeating task needed).
 */
public final class PlaytimeManager {

    private final Map<UUID, Long> joinTimestamps = new ConcurrentHashMap<>();

    public void track(Player player) {
        joinTimestamps.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void clear(Player player) {
        joinTimestamps.remove(player.getUniqueId());
    }

    /**
     * Returns the session time formatted as "1h 23m 45s" (units with value 0 are
     * omitted, except seconds which are always shown).
     */
    public String getFormattedPlaytime(Player player) {
        Long joinedAt = joinTimestamps.get(player.getUniqueId());
        if (joinedAt == null) {
            return "0s";
        }
        return format(System.currentTimeMillis() - joinedAt);
    }

    private String format(long millis) {
        long totalSeconds = millis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            builder.append(minutes).append("m ");
        }
        builder.append(seconds).append("s");
        return builder.toString();
    }
}
