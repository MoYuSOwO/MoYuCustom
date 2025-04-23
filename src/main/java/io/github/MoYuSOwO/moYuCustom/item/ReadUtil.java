package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public final class ReadUtil {

    private ReadUtil() {}

    public static File[] readAllFiles() {
        File dataFolder = MoYuCustom.instance.getDataFolder();
        File itemFolder = new File(dataFolder, "item");
        if (!itemFolder.exists()) {
            itemFolder.mkdirs();
            saveDefaultIfNotExists("item/magic_bread.yml");
            saveDefaultIfNotExists("item/magic_diamond.yml");
            saveDefaultIfNotExists("item/magic_sword.yml");
            saveDefaultIfNotExists("item/broken_stick.yml");
        }
        return itemFolder.listFiles();
    }

    private static void saveDefaultIfNotExists(String resourcePath) {
        String targetPath = resourcePath.replace('/', File.separatorChar);
        File targetFile = new File(MoYuCustom.instance.getDataFolder(), targetPath);
        if (targetFile.exists()) {
            return;
        }
        MoYuCustom.instance.saveResource(resourcePath, false);
    }

    public static boolean isYmlFile(@NotNull File file) {
        if (!file.isFile()) return false;
        String name = file.getName().toLowerCase();
        return name.endsWith(".yml") || name.endsWith(".yaml");
    }

    public static String getRegistryId(YamlConfiguration item) {
        String registryId = item.getString("registryId");
        if (registryId != null) return "moyuitem:" + registryId;
        else throw new IllegalArgumentException("You must provide a registryId!");
    }

    public static String getRawMaterialString(YamlConfiguration item) {
        String rawMaterialString = item.getString("rawMaterial");
        if (rawMaterialString == null) throw new IllegalArgumentException("You must provide a raw material!");
        else return rawMaterialString;
    }

    public static @Nullable Integer getCustomModelData(YamlConfiguration item) {
        int customModelData = item.getInt("customModelData");
        if (customModelData <= 0) return null;
        else return customModelData;
    }

    public static String getDisplayName(YamlConfiguration item) {
        String itemName = item.getString("displayName");
        if (itemName == null) throw new IllegalArgumentException("You must provide a name!");
        else return itemName;
    }

    public static @NotNull List<String> getLore(YamlConfiguration item) {
        return item.getStringList("lore");
    }

    public static @NotNull FoodItem getFood(YamlConfiguration item) {
        FoodItem foodItem = FoodItem.EMPTY;
        ConfigurationSection food = item.getConfigurationSection("food");
        if (food != null) {
            int nutrition = food.getInt("nutrition");
            int saturation = food.getInt("saturation");
            boolean canAlwaysEat = food.getBoolean("canAlwaysEat");
            if (nutrition == 0 || saturation == 0) throw new IllegalArgumentException("You must provide a effective food value!");
            foodItem = new FoodItem(nutrition, saturation, canAlwaysEat);
        }
        return foodItem;
    }

    public static boolean getOriginalCraft(YamlConfiguration item) {
        return item.getBoolean("hasOriginalCraft");
    }
}
