package io.github.MoYuSOwO.moYuCustom.recipe;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomShapedRecipe {
    private final Map<Character, String> toRegistryId;
    private final char[] recipe;
    private String result;
    private int count;
    private boolean built;

    public static String toRegistryKey(ItemStack[] matrix) {
        StringBuilder builtRecipe = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (matrix[i] != null) {
                builtRecipe.append(ItemRegistry.getRegistryId(matrix[i]));
            }
            builtRecipe.append(",");
        }
        return builtRecipe.toString();
    }

    public CustomShapedRecipe(@NotNull String line1, @NotNull String line2, @NotNull String line3) {
        if (line1.isEmpty()) line1 = "   ";
        if (line2.isEmpty()) line2 = "   ";
        if (line3.isEmpty()) line3 = "   ";
        if (line1.length() != 3 || line2.length() != 3 || line3.length() != 3) {
            throw new IllegalArgumentException("You must input a String that length is 3!");
        }
        this.recipe = new char[]{
                line1.charAt(0), line1.charAt(1), line1.charAt(2),
                line2.charAt(0), line2.charAt(1), line2.charAt(2),
                line3.charAt(0), line3.charAt(1), line3.charAt(2)
        };
        toRegistryId = new HashMap<>();
        this.built = false;
    }

    public void add(char c, ItemStack itemStack) {
        try {
            if (built) throw new IllegalAccessException("It's already registered!");
        } catch (IllegalAccessException e) {
            MoYuCustom.instance.getLogger().severe(e.getLocalizedMessage());
        }
        toRegistryId.put(c, ItemRegistry.getRegistryId(itemStack).asString());
    }

    public void setResult(ItemStack itemStack, int count) {
        try {
            if (built) throw new IllegalAccessException("It's already registered!");
        } catch (IllegalAccessException e) {
            MoYuCustom.instance.getLogger().severe(e.getLocalizedMessage());
        }
        result = ItemRegistry.getRegistryId(itemStack).asString();
        this.count = count;
    }

    public void build() {
        try {
            if (built) throw new IllegalAccessException("It's already registered!");
        } catch (IllegalAccessException e) {
            MoYuCustom.instance.getLogger().severe(e.getLocalizedMessage());
        }
        StringBuilder builtRecipe = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (recipe[i] != ' ') builtRecipe.append(toRegistryId.get(recipe[i]));
            builtRecipe.append(",");
        }
        CraftingTableRecipeRegistry.register(builtRecipe.toString(), this);
        this.built = true;
    }

    protected String getResult() {
        return result;
    }

    protected int getCount() {
        return count;
    }
}
