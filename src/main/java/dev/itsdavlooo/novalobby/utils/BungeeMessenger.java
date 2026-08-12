package dev.itsdavlooo.novalobby.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class BungeeMessenger {

    public static final String CHANNEL = "BungeeCord";

    private final Plugin plugin;

    public BungeeMessenger(Plugin plugin) {
        this.plugin = plugin;
    }

    public void connect(Player player, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        player.sendPluginMessage(plugin, CHANNEL, output.toByteArray());
    }
}
