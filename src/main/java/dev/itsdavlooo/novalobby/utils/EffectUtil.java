package dev.itsdavlooo.novalobby.utils;

import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.Locale;

public final class EffectUtil {

    private EffectUtil() {
    }

    public static Particle parseParticle(String name, Particle fallback) {
        try {
            return Particle.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return fallback;
        }
    }

    @SuppressWarnings("deprecation")
    public static Sound parseSound(String name, Sound fallback) {
        try {
            return Sound.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return fallback;
        }
    }
}
