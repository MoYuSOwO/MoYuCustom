package io.github.MoYuSOwO.moYuCustom.recipe;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CraftingTableRecipeRegistry implements Listener {

    public static void registerListener() {
        Bukkit.getPluginManager().registerEvents(new CraftingTableRecipeRegistry(), MoYuCustom.instance);
    }

    private CraftingTableRecipeRegistry() {}

    private static final ConcurrentHashMap<String, CustomShapedRecipe> shapedRegistry = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CustomShapelessRecipe> shapelessRegistry = new ConcurrentHashMap<>();

    public static void register() {
        File[] files = ReadUtil.readAllFiles();
        if (files != null) {
            for (File file : files) {
                if (ReadUtil.isYmlFile(file)) {
                    registerRecipes(YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    public static int getShapedRegistrySize() { return shapedRegistry.size(); }

    public static int getShapelessRegistrySize() { return shapelessRegistry.size(); }

    private static void registerRecipes(YamlConfiguration yml) {
        String recipeType = ReadUtil.getRecipeType(yml);
        if (recipeType.equals("shaped")) registerShapedRecipe(yml);
        else if (recipeType.equals("shapeless")) registerShapelessRecipe(yml);
    }

    private static void registerShapedRecipe(YamlConfiguration yml) {
        List<String> shape = ReadUtil.getShaped(yml);
        CustomShapedRecipe r = new CustomShapedRecipe(shape.get(0), shape.get(1), shape.get(2));
        for (Map.Entry<Character, String> entry : ReadUtil.getShapedMappings(yml).entrySet()) {
            r.add(entry.getKey(), ItemRegistry.get(entry.getValue()));
        }
        r.setResult(ItemRegistry.get(ReadUtil.getResult(yml)), ReadUtil.getCount(yml));
        r.build();
    }

    private static void registerShapelessRecipe(YamlConfiguration yml) {
        List<String> items = ReadUtil.getShapelessItems(yml);
        CustomShapelessRecipe r = new CustomShapelessRecipe(ItemRegistry.get(ReadUtil.getResult(yml)), ReadUtil.getCount(yml));
        for (String item : items) {
            r.add(ItemRegistry.get(item));
        }
        r.build();
    }

    static void register(String identifier, CustomShapedRecipe r) {
        shapedRegistry.put(identifier, r);
    }

    static void register(String identifier, CustomShapelessRecipe r) {
        shapelessRegistry.put(identifier, r);
    }

    @EventHandler
    private static void onPrepareCraftAtCraftingTable(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();
        if (event.getRecipe() != null) {
            for (int i = 0; i < 9; i++) {
                if (matrix[i] == null) continue;
                if (!ItemRegistry.getRegistryId(matrix[i]).contains("minecraft:")) {
                    event.getInventory().setResult(null);
                    break;
                }
            }
        }
        String shapedRegistryKey = CustomShapedRecipe.toRegistryKey(matrix);
        if (shapedRegistry.containsKey(shapedRegistryKey)) {
            CustomShapedRecipe r = shapedRegistry.get(shapedRegistryKey);
            event.getInventory().setResult(ItemRegistry.get(r.getResult(), r.getCount()));
        } else {
            String shapelessRegistryKey = CustomShapelessRecipe.toRegistryKey(matrix);
            if (shapelessRegistry.containsKey(shapelessRegistryKey)) {
                CustomShapelessRecipe r = shapelessRegistry.get(shapelessRegistryKey);
                event.getInventory().setResult(ItemRegistry.get(r.getResult(), r.getCount()));
            }
        }
    }
}
