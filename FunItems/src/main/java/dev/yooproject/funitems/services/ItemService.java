package dev.yooproject.funitems.services;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.configuration.impl.ItemsConfiguration;
import dev.yooproject.funitems.item.Type;
import dev.yooproject.funitems.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemService {

    private final ItemsConfiguration configuration;

    public ItemService(ItemsConfiguration itemsConfig) {
        this.configuration = itemsConfig;
    }

    public ItemStack getItem(String id, int amount) {
        ConfigurationSection section = configuration.getConfig().getConfigurationSection("items." + id);
        if (section == null) return null;

        Material mat = Material.matchMaterial(section.getString("material"));
        if (mat == null) mat = Material.GRAY_DYE;

        String name = ColorUtil.translate(section.getString("name"));
        List<String> lore = section.getStringList("lore").stream().map(ColorUtil::translate).collect(Collectors.toList());

        boolean enchanted = section.getBoolean("enchanted");
        boolean hideAttributes = section.getBoolean("hide-attributes");
        int cmd = section.getInt("custom-model-data");
        Map<Attribute, AttributeModifier> attributes = new HashMap<>();
        ConfigurationSection attrSec = section.getConfigurationSection("attributes");
        if (attrSec != null) {
            for (String a : attrSec.getKeys(false)) {
                double amountAttr = attrSec.getDouble(a + ".amount");
                String operation = attrSec.getString(a + ".operation");
                UUID uuid = UUID.randomUUID();

                attributes.put(Attribute.valueOf(a), new AttributeModifier(uuid, a, amountAttr, AttributeModifier.Operation.valueOf(operation)));
            }
        }
        Map<NamespacedKey, String> pdc = new HashMap<>();
        ConfigurationSection pdcSec = section.getConfigurationSection("pdc");
        if (pdcSec != null) {
            for (String key : pdcSec.getKeys(false)) {
                pdc.put(new NamespacedKey(FunItems.getInstance(), key.replace("funitems:", "")),
                        pdcSec.getString(key));
            }
        }
        ItemStack itemStack = new ItemStack(mat, amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            if (cmd != 0) meta.setCustomModelData(cmd);
            if (enchanted) meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            if (hideAttributes) meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
                meta.addAttributeModifier(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<NamespacedKey, String> entry : pdc.entrySet()) {
                meta.getPersistentDataContainer().set(entry.getKey(), PersistentDataType.STRING, entry.getValue());
            }

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public Type isItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(FunItems.getInstance(), "type");

        String typeName = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (typeName == null) return null;

        try {
            return Type.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
