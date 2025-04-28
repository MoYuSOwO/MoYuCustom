package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.attribute.AttributeRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CustomItem {

    private final String registryId;
    private final Material rawMaterial;
    private final boolean hasOriginalCraft;
    private final Integer customModelData;
    private final Component displayName;
    private final List<Component> lore;
    private final FoodItem foodItem;
    private final ItemAttribute itemAttribute;
    private final ItemStack itemStack;

    protected CustomItem(String registryId, Material rawMaterial, boolean hasOriginalCraft, Integer customModelData, Component displayName, List<Component> lore, FoodItem foodItem, ItemAttribute itemAttribute) {
        this.registryId = registryId;
        this.rawMaterial = rawMaterial;
        this.hasOriginalCraft = hasOriginalCraft;
        this.customModelData = customModelData;
        this.displayName = displayName;
        this.lore = lore;
        this.foodItem = foodItem;
        this.itemAttribute = itemAttribute;
        this.itemStack = createNewItemStack(1);
    }

    protected CustomItem(String registryId, Material rawMaterial, boolean hasOriginalCraft, Integer customModelData, String displayName, List<String> lore, FoodItem foodItem, ItemAttribute itemAttribute) {
        this(registryId, rawMaterial, hasOriginalCraft, customModelData, to(displayName), to(lore), foodItem, itemAttribute);
    }

    private ItemStack createNewItemStack(int count) {
        ItemStack itemStack = new ItemStack(this.rawMaterial, count);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.customName(this.displayName);
        itemMeta.lore(this.lore);
        if (this.customModelData != null) {
            itemMeta.setCustomModelData(this.customModelData);
        }
        itemMeta.getPersistentDataContainer().set(MoYuCustom.registryIdKey, PersistentDataType.STRING, this.registryId);
        if (this.foodItem != FoodItem.EMPTY) {
            FoodComponent foodComponent = itemMeta.getFood();
            foodComponent.setNutrition(this.foodItem.nutrition());
            foodComponent.setSaturation(this.foodItem.saturation());
            foodComponent.setCanAlwaysEat(this.foodItem.canAlwaysEat());
            itemMeta.setFood(foodComponent);
        }
        if (!this.itemAttribute.isEmpty()) {
            for (Map.Entry<String, Object> entry : this.itemAttribute.getAttributes().entrySet()) {
                itemMeta.getPersistentDataContainer().set(
                        AttributeRegistry.getKey(entry.getKey()),
                        AttributeRegistry.getAttributePDCType(entry.getKey()),
                        this.itemAttribute.getAttributeValue(entry.getKey())
                );
            }
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected ItemStack to(int count) {
        ItemStack itemStack = this.itemStack.clone();
        itemStack.setAmount(count);
        return itemStack;
    }

    protected boolean equals(@NotNull ItemStack itemStack) {
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(MoYuCustom.registryIdKey)) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().get(MoYuCustom.registryIdKey, PersistentDataType.STRING).equals(this.registryId);
    }

    protected boolean hasOriginalCraft() {
        return this.hasOriginalCraft;
    }

    private static Component to(String s) {
        s = "<white><italic:false>" + s;
        return MiniMessage.miniMessage().deserialize(s);
    }

    private static List<Component> to(List<String> list) {
        List<Component> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(
                    MiniMessage.miniMessage().deserialize(
                            "<gray><italic:false>" + s
                    )
            );
        }
        return newList;
    }
}
