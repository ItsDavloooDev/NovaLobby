package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
import dev.itsdavlooo.novalobby.utils.BungeeMessenger;
import dev.itsdavlooo.novalobby.utils.ItemBuilder;
import dev.itsdavlooo.novalobby.utils.ItemKeys;
import dev.itsdavlooo.novalobby.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the DeluxeHub-style server selector GUI (filler, base64 heads, glow)
 * and executes slot actions, including [PROXY]/[BUNGEE] server switching.
 */
public final class SelectorManager {

    private final NovaLobby plugin;
    private final BungeeMessenger bungeeMessenger;

    public SelectorManager(NovaLobby plugin) {
        this.plugin = plugin;
        this.bungeeMessenger = new BungeeMessenger(plugin);
    }

    public ItemStack buildSelectorItem() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("selector.item");
        if (section == null) {
            return null;
        }
        Material material = Material.matchMaterial(section.getString("material", "COMPASS"));
        return new ItemBuilder(material == null ? Material.COMPASS : material)
                .name(section.getString("name", "Server Selector"))
                .lore(section.getStringList("lore"))
                .tag(ItemKeys.key(), ItemKeys.SELECTOR)
                .build();
    }

    public void openSelector(Player player) {
        ConfigurationSection guiSection = plugin.getConfig().getConfigurationSection("selector.gui");
        if (guiSection == null) {
            return;
        }
        int size = resolveSize(guiSection);
        SelectorHolder holder = new SelectorHolder();
        Inventory inventory = Bukkit.createInventory(holder, size,
                TextUtil.parse(guiSection.getString("title", "Server Selector")));
        holder.inventory = inventory;

        ConfigurationSection itemsSection = guiSection.getConfigurationSection("items");
        if (itemsSection != null) {
            ConfigurationSection fillerSection = null;
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) {
                    continue;
                }
                int slot = itemSection.getInt("slot", -1);
                if (slot == -1) {
                    fillerSection = itemSection;
                    continue;
                }
                if (slot < 0 || slot >= size) {
                    continue;
                }
                inventory.setItem(slot, buildGuiItem(itemSection, key));
                holder.actionsBySlot.put(slot, itemSection.getStringList("actions"));
            }
            if (fillerSection != null) {
                ItemStack fillerItem = buildGuiItem(fillerSection, " ");
                List<String> fillerActions = fillerSection.getStringList("actions");
                for (int slot = 0; slot < size; slot++) {
                    if (inventory.getItem(slot) == null) {
                        inventory.setItem(slot, fillerItem);
                        if (!fillerActions.isEmpty()) {
                            holder.actionsBySlot.put(slot, fillerActions);
                        }
                    }
                }
            }
        }
        player.openInventory(inventory);
    }

    public void executeActions(Player player, List<String> actions) {
        for (String action : actions) {
            String resolved = action.replace("%player_name%", player.getName()).trim();
            if (resolved.startsWith("[PROXY]") || resolved.startsWith("[BUNGEE]")) {
                String server = resolved.substring(resolved.indexOf(']') + 1).trim();
                bungeeMessenger.connect(player, server);
            } else if (resolved.startsWith("[MESSAGE]")) {
                player.sendMessage(TextUtil.parse(player, resolved.substring("[MESSAGE]".length()).trim()));
            } else if (resolved.startsWith("[CONSOLE]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved.substring("[CONSOLE]".length()).trim());
            } else if (resolved.startsWith("[PLAYER]")) {
                player.performCommand(resolved.substring("[PLAYER]".length()).trim());
            } else if (resolved.equalsIgnoreCase("[CLOSE]")) {
                player.closeInventory();
            } else if (!resolved.isEmpty()) {
                player.performCommand(resolved);
            }
        }
    }

    private int resolveSize(ConfigurationSection guiSection) {
        int slots = guiSection.contains("slots")
                ? guiSection.getInt("slots", 27)
                : guiSection.getInt("rows", 3) * 9;
        int size = ((slots + 8) / 9) * 9;
        return Math.min(54, Math.max(9, size));
    }

    private ItemStack buildGuiItem(ConfigurationSection itemSection, String fallbackName) {
        Material material = Material.matchMaterial(itemSection.getString("material", "STONE"));
        return new ItemBuilder(material == null ? Material.STONE : material)
                .name(itemSection.getString("display_name", itemSection.getString("name", fallbackName)))
                .lore(itemSection.getStringList("lore"))
                .amount(itemSection.getInt("amount", 1))
                .glow(itemSection.getBoolean("glow", false))
                .headTexture(itemSection.getString("base64"))
                .hideAttributes()
                .build();
    }

    public static final class SelectorHolder implements InventoryHolder {

        private Inventory inventory;
        private final Map<Integer, List<String>> actionsBySlot = new HashMap<>();

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public List<String> getActions(int slot) {
            return actionsBySlot.get(slot);
        }
    }
}
