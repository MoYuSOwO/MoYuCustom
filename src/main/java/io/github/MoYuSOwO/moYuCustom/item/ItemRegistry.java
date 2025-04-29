package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    public static void init() {
        ItemRegistry.register();
        MoYuCustom.instance.getLogger().info("成功注册 " + ItemRegistry.registrySize() + " 个自定义物品");
    }

    public static void register() {
        File[] files = ReadUtil.readAllFiles();
        if (files != null) {
            for (File file : files) {
                if (ReadUtil.isYmlFile(file)) {
                    registerItemFromFile(YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    private static void registerItemFromFile(YamlConfiguration item) {
        String registryId = ReadUtil.getRegistryId(item);
        Material rawMaterial = getBukkitMaterial(ReadUtil.getRawMaterialString(item));
        boolean hasOriginalCraft = ReadUtil.getOriginalCraft(item);
        Integer customModelData = ReadUtil.getCustomModelData(item);
        String displayName = ReadUtil.getDisplayName(item);
        List<String> lore = ReadUtil.getLore(item);
        FoodItem foodItem = ReadUtil.getFood(item);
        ItemAttribute itemAttribute = ReadUtil.getAttribute(item);
        registry.put(registryId, new CustomItem(registryId, rawMaterial, hasOriginalCraft, customModelData, displayName, lore, foodItem, itemAttribute));
    }

    @SuppressWarnings("unused")
    public static void registerItem(String key, String Id, Material rawMaterial, boolean hasOriginalCraft, int customModelData, String displayName, List<String> lore, FoodItem foodItem, ItemAttribute itemAttribute) {
        String registryId = key + ":" + Id;
        registry.put(registryId, new CustomItem(registryId, rawMaterial, hasOriginalCraft, customModelData, displayName, lore, foodItem, itemAttribute));
    }

    public static Set<String> getAllIds() {
        return registry.keySet();
    }

    public static @NotNull NamespacedKey getRegistryId(@NotNull ItemStack itemStack) {
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(MoYuCustom.registryIdKey)) return itemStack.getType().getKey();
        String registryIdString = itemStack.getItemMeta().getPersistentDataContainer().get(MoYuCustom.registryIdKey, PersistentDataType.STRING);
        NamespacedKey registryId = NamespacedKey.fromString(Objects.requireNonNull(registryIdString));
        return Objects.requireNonNull(registryId);
    }

    public static boolean hasAttribute(NamespacedKey registryId, NamespacedKey attributeId) {
        if (!registry.containsKey(registryId.asString())) return false;
        return registry.get(registryId.asString()).hasAttribute(attributeId);
    }

    /**
     * 根据物品ID和属性名自动返回正确类型的值
     * 基本类型不能取消装箱
     * @param registryId 物品ID
     * @param attributeId 属性名称
     * @return 类型转换后的值，如果不存在或类型不匹配返回null
     */
    public static @Nullable <T> T getAttributeValue(NamespacedKey registryId, NamespacedKey attributeId) {
        if (!registry.containsKey(registryId.asString())) throw new IllegalArgumentException("Can not find the RegistryId in registry!");
        return registry.get(registryId.asString()).getAttributeValue(attributeId);
    }

    public static @NotNull ItemStack get(NamespacedKey registryId, int count) {
        if (!registry.containsKey(registryId.asString())) {
            ItemStack itemStack = getMinecraftItem(registryId.asString(), count);
            if (itemStack.equals(ItemStack.empty())) throw new IllegalArgumentException("Can not find the RegistryId in registry!");
            return itemStack;
        }
        return registry.get(registryId.asString()).to(count);
    }

    public static @NotNull ItemStack get(NamespacedKey registryId) {
        return get(registryId, 1);
    }

    @SuppressWarnings("unused")
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

    public static boolean hasOriginalCraft(NamespacedKey registryId) {
        return registry.get(registryId.asString()).hasOriginalCraft();
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
