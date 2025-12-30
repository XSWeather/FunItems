package dev.yooproject.funitems.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class Item {

    private String name;
    private List<String> lore;

    private Material material;
    private int amount;

    private boolean enchanted;
    private int customModelData;

    private Map<Attribute, AttributeModifier> attributes;
    private boolean hideAttributes;

    private Map<NamespacedKey, String> pdc;

    private Item() {}

    public static Builder builder() {
        return new Builder();
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        if (name != null) meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        if (customModelData > 0) meta.setCustomModelData(customModelData);

        if (enchanted) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (attributes != null) attributes.forEach(meta::addAttributeModifier);

        if (hideAttributes)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);

        if (pdc != null)
            pdc.forEach((key, value) -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value));
        stack.setItemMeta(meta);
        return stack;
    }

    public static class Builder {
        private final Item item = new Item();

        public Builder name(String name) {
            item.name = name;
            return this;
        }

        public Builder lore(List<String> lore) {
            item.lore = lore;
            return this;
        }

        public Builder material(Material material) {
            item.material = material;
            return this;
        }

        public Builder amount(int amount) {
            item.amount = amount;
            return this;
        }

        public Builder enchanted(boolean enchanted) {
            item.enchanted = enchanted;
            return this;
        }

        public Builder customModelData(int data) {
            item.customModelData = data;
            return this;
        }

        public Builder attributes(Map<Attribute, AttributeModifier> attributes) {
            item.attributes = attributes;
            return this;
        }

        public Builder hideAttributes(boolean hide) {
            item.hideAttributes = hide;
            return this;
        }

        public Builder pdc(Map<NamespacedKey, String> pdc) {
            item.pdc = pdc;
            return this;
        }

        public Item build() {
            return item;
        }
    }
}