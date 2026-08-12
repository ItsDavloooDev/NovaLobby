package dev.itsdavlooo.novalobby.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class ItemKeys {

    public static final String SELECTOR = "selector";
    public static final String GRAPPLING_HOOK = "grappling-hook";
    public static final String ENDERBUTT = "enderbutt";
    public static final String PLAYER_HIDER = "player-hider";
    public static final String TIME_SELECTOR = "time-selector";

    private static NamespacedKey itemKey;

    private ItemKeys() {
    }

    public static void init(Plugin plugin) {
        itemKey = new NamespacedKey(plugin, "novalobby_item");
    }

    public static NamespacedKey key() {
        return itemKey;
    }

    public static boolean isTagged(ItemStack itemStack, String value) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        String tag = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        return value.equals(tag);
    }
}
