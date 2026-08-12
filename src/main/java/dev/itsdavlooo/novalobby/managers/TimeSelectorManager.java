package dev.itsdavlooo.novalobby.managers;

import dev.itsdavlooo.novalobby.NovaLobby;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Personal time selector: lets each player pick a client-side day time
 * without affecting the rest of the server.
 */
public final class TimeSelectorManager {

    private final NovaLobby plugin;

    public TimeSelectorManager(NovaLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("time-selector.enabled", true);
    }

    public ItemStack buildSelectorItem() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("time-selector.item");
        Material material = Material.CLOCK;
        String name = "<gradient:#FFEA00:#FF6D00>Time Selector</gradient> &7(Right Click)";
        List<String> lore = List.of("&7Choose the time only you see!");
        if (section != null) {
            Material configured = Material.matchMaterial(section.getString("material", "CLOCK"));
            material = configured == null ? Material.CLOCK : configured;
            name = section.getString("name", name);
            lore = section.getStringList("lore").isEmpty() ? lore : section.getStringList("lore");
        }
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .tag(ItemKeys.key(), ItemKeys.TIME_SELECTOR)
                .build();
    }

    public void openGui(Player player) {
        ConfigurationSection guiSection = plugin.getConfig().getConfigurationSection("time-selector.gui");
        String title = guiSection == null ? "&8Select the time" : guiSection.getString("title", "&8Select the time");
        int configuredSlots = guiSection == null ? 9 : guiSection.getInt("slots", 9);
        int size = Math.min(54, Math.max(9, ((configuredSlots + 8) / 9) * 9));

        TimeSelectorHolder holder = new TimeSelectorHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, TextUtil.parse(title));
        holder.inventory = inventory;

        for (Map.Entry<Integer, GuiOption> entry : resolveOptions(guiSection, size).entrySet()) {
            GuiOption option = entry.getValue();
            inventory.setItem(entry.getKey(), new ItemBuilder(option.material())
                    .name(option.displayName())
                    .lore(option.lore())
                    .hideAttributes()
                    .build());
            holder.optionsBySlot.put(entry.getKey(), new TimeOption(option.time(), option.displayName()));
        }
        player.openInventory(inventory);
    }

    private Map<Integer, GuiOption> resolveOptions(ConfigurationSection guiSection, int size) {
        Map<Integer, GuiOption> options = new LinkedHashMap<>();
        ConfigurationSection optionsSection = guiSection == null ? null : guiSection.getConfigurationSection("options");
        if (optionsSection != null) {
            for (String key : optionsSection.getKeys(false)) {
                ConfigurationSection optionSection = optionsSection.getConfigurationSection(key);
                if (optionSection == null) {
                    continue;
                }
                int slot = optionSection.getInt("slot", -1);
                if (slot < 0 || slot >= size) {
                    continue;
                }
                Material material = Material.matchMaterial(optionSection.getString("material", "CLOCK"));
                options.put(slot, new GuiOption(material == null ? Material.CLOCK : material,
                        optionSection.getString("display_name", key),
                        optionSection.getStringList("lore"),
                        optionSection.getLong("time", -1)));
            }
        }
        if (options.isEmpty()) {
            options.put(3, new GuiOption(Material.GLOWSTONE, "&eDay", List.of(), 6000));
            options.put(4, new GuiOption(Material.ORANGE_STAINED_GLASS_PANE, "&6Sunset", List.of(), 12500));
            options.put(5, new GuiOption(Material.BLACK_STAINED_GLASS_PANE, "&9Night", List.of(), 18000));
        }
        return options;
    }

    public void applyTime(Player player, TimeOption option) {
        if (option.time() < 0) {
            player.resetPlayerTime();
        } else {
            player.setPlayerTime(option.time(), false);
        }
        plugin.getMessagesManager().send(player, "time-selector.set", "%option%", option.displayName());
    }

    private record GuiOption(Material material, String displayName, List<String> lore, long time) {
    }

    public record TimeOption(long time, String displayName) {
    }

    public static final class TimeSelectorHolder implements InventoryHolder {

        private Inventory inventory;
        private final Map<Integer, TimeOption> optionsBySlot = new HashMap<>();

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public TimeOption getOption(int slot) {
            return optionsBySlot.get(slot);
        }
    }
}
