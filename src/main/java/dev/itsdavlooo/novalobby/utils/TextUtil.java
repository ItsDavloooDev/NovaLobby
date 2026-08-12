package dev.itsdavlooo.novalobby.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses legacy color codes, hex colors and MiniMessage tags into Adventure components.
 */
public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("[&§]#([0-9a-fA-F]{6})");
    private static final Pattern BUNGEE_HEX_PATTERN = Pattern.compile("[&§]x([&§][0-9a-fA-F]){6}");
    private static final Map<Character, String> LEGACY_TO_MINI = Map.ofEntries(
            Map.entry('0', "<black>"),
            Map.entry('1', "<dark_blue>"),
            Map.entry('2', "<dark_green>"),
            Map.entry('3', "<dark_aqua>"),
            Map.entry('4', "<dark_red>"),
            Map.entry('5', "<dark_purple>"),
            Map.entry('6', "<gold>"),
            Map.entry('7', "<gray>"),
            Map.entry('8', "<dark_gray>"),
            Map.entry('9', "<blue>"),
            Map.entry('a', "<green>"),
            Map.entry('b', "<aqua>"),
            Map.entry('c', "<red>"),
            Map.entry('d', "<light_purple>"),
            Map.entry('e', "<yellow>"),
            Map.entry('f', "<white>"),
            Map.entry('k', "<obfuscated>"),
            Map.entry('l', "<bold>"),
            Map.entry('m', "<strikethrough>"),
            Map.entry('n', "<underlined>"),
            Map.entry('o', "<italic>"),
            Map.entry('r', "<reset>")
    );

    private TextUtil() {
    }

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(toMiniMessageFormat(input));
    }

    public static Component parse(Player player, String input) {
        return parse(applyPlaceholders(player, input));
    }

    public static String applyPlaceholders(Player player, String input) {
        if (input == null) {
            return "";
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }

    private static String toMiniMessageFormat(String input) {
        String result = convertBungeeHex(input);
        Matcher hexMatcher = HEX_PATTERN.matcher(result);
        result = hexMatcher.replaceAll("<#$1>");
        StringBuilder builder = new StringBuilder(result.length());
        for (int i = 0; i < result.length(); i++) {
            char current = result.charAt(i);
            if ((current == '&' || current == '§') && i + 1 < result.length()) {
                String replacement = LEGACY_TO_MINI.get(Character.toLowerCase(result.charAt(i + 1)));
                if (replacement != null) {
                    builder.append(replacement);
                    i++;
                    continue;
                }
            }
            builder.append(current);
        }
        return builder.toString();
    }

    private static String convertBungeeHex(String input) {
        Matcher matcher = BUNGEE_HEX_PATTERN.matcher(input);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group().replaceAll("[&§x]", "");
            matcher.appendReplacement(builder, "<#" + hex + ">");
        }
        matcher.appendTail(builder);
        return builder.toString();
    }
}
