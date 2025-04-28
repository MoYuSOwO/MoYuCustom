package io.github.MoYuSOwO.moYuCustom.attribute;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ReadUtil {

    private ReadUtil() {}

    public static File readAttributeFiles() {
        File dataFolder = MoYuCustom.instance.getDataFolder();
        File recipeFolder = new File(dataFolder, "attribute");
        if (!recipeFolder.exists()) {
            recipeFolder.mkdirs();
            saveDefaultIfNotExists("attribute/attribute.yml");
        }
        return new File(recipeFolder, "attribute.yml");
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
}
