package dev.itsdavlooo.novalobby.utils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FallProtectionTracker {

    private final Set<UUID> protectedPlayers = ConcurrentHashMap.newKeySet();

    public void protect(UUID playerId) {
        protectedPlayers.add(playerId);
    }

    public boolean consumeProtection(UUID playerId) {
        return protectedPlayers.remove(playerId);
    }

    public void clear(UUID playerId) {
        protectedPlayers.remove(playerId);
    }
}
