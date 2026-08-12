package dev.itsdavlooo.novalobby.config;

import dev.itsdavlooo.novalobby.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Loads messages.yml and sends parsed messages with prefix and placeholder support.
 */
public final class MessagesManager {

    private final JavaPlugin plugin;
    private final File messagesFile;
    private FileConfiguration messages;

    public MessagesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        reload();
    }

    public void reload() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getRaw(String key, String... replacements) {
        String message = messages.getString(key, key);
        message = message.replace("%prefix%", messages.getString("prefix", ""));
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    public void send(CommandSender sender, String key, String... replacements) {
        String message = getRaw(key, replacements);
        if (!message.isEmpty()) {
            sender.sendMessage(TextUtil.parse(message));
        }
    }
}
