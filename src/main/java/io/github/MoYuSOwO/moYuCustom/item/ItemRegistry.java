package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemRegistry {

    private static final ConcurrentHashMap<String, CustomItem> registry = new ConcurrentHashMap<>();

    private ItemRegistry() {}

    public static void register() {
        File[] files = ReadUtil.readAllFiles();
        if (files != null) {
            for (File file : files) {
                if (ReadUtil.isYmlFile(file)) {
                    registerItem(YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    private static void registerItem(YamlConfiguration item) {
        String registryId = ReadUtil.getRegistryId(item);
        Material rawMaterial = getBukkitMaterial(ReadUtil.getRawMaterialString(item));
        Integer customModelData = ReadUtil.getCustomModelData(item);
        String displayName = ReadUtil.getDisplayName(item);
        List<String> lore = ReadUtil.getLore(item);
        FoodItem foodItem = ReadUtil.getFood(item);
        registry.put(registryId, new CustomItem(registryId, rawMaterial, customModelData, displayName, lore, foodItem));
    }

    public static void registerItem(String key, String Id, Material rawMaterial, int customModelData, String displayName, List<String> lore, FoodItem foodItem) {
        String registryId = key + ":" + Id;
        registry.put(registryId, new CustomItem(registryId, rawMaterial, customModelData, displayName, lore, foodItem));
    }

    public static Set<String> getAllIds() {
        return registry.keySet();
    }

    public static @NotNull String getRegistryId(@NotNull ItemStack itemStack) {
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(MoYuCustom.registryIdKey)) return "minecraft:" + itemStack.getType().toString().toLowerCase();
        return Objects.requireNonNull(itemStack.getItemMeta().getPersistentDataContainer().get(MoYuCustom.registryIdKey, PersistentDataType.STRING));
    }

    public static @NotNull ItemStack get(String registryId, int count) {
        if (!registry.containsKey(registryId)) {
            ItemStack itemStack = getMinecraftItem(registryId, count);
            if (itemStack.equals(ItemStack.empty())) throw new IllegalArgumentException("Can not find the RegistryId in registry!");
            return itemStack;
        }
        return registry.get(registryId).to(count);
    }

    public static @NotNull ItemStack get(String registryId) {
        return get(registryId, 1);
    }

    public static boolean is(ItemStack itemStack, String registryId) {
        if (!registry.containsKey(registryId)) {
            if (itemStack.getItemMeta().getPersistentDataContainer().has(MoYuCustom.registryIdKey)) {
                return false;
            } else {
                return itemStack.getType().equals(getBukkitMaterial(registryId));
            }
        }
        return registry.get(registryId).equals(itemStack);
    }

    public static int registrySize() {
        return registry.size();
    }

    private static @Nullable Material getBukkitMaterial(String registryId) {
        if (registryId.contains(":")) {
            String[] id = registryId.split(":");
            return Material.matchMaterial(id[1]);
        } else {
            return Material.matchMaterial(registryId);
        }
    }

    private static ItemStack getMinecraftItem(String registryId, int count) {
        Material material = getBukkitMaterial(registryId);
        if (material == null) return ItemStack.empty();
        else return new ItemStack(material, count);
    }
}
