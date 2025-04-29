package io.github.MoYuSOwO.moYuCustom.recipe;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CustomShapelessRecipe {
    private final String[] recipe;
    private String result;
    private int count;
    private int i;
    private boolean built;

    public static String toRegistryKey(ItemStack[] matrix) {
        StringBuilder builtRecipe = new StringBuilder();
        String[] inv = new String[9];
        Arrays.fill(inv, "");
        for (int i = 0; i < 9; i++) {
            if (matrix[i] != null) {
                inv[i] = ItemRegistry.getRegistryId(matrix[i]).asString();
            }
        }
        Arrays.sort(inv);
        for (int i = 0; i < 9; i++) {
            if (inv[i] != null) {
                builtRecipe.append(inv[i]);
            }
            builtRecipe.append(",");
        }
        return builtRecipe.toString();
    }

    public CustomShapelessRecipe() {
        this.recipe = new String[9];
        this.i = 0;
        this.built = false;
        Arrays.fill(recipe, "");
    }

    public CustomShapelessRecipe(ItemStack result, int count) {
        this();
        this.result = ItemRegistry.getRegistryId(result).asString();
        this.count = count;
    }

    public void add(ItemStack itemStack) {
        try {
            if (built) throw new IllegalAccessException("It's already registered!");
        } catch (IllegalAccessException e) {
            MoYuCustom.instance.getLogger().severe(e.getLocalizedMessage());
        }
        if (i == recipe.length) throw new ArrayIndexOutOfBoundsException("You can no longer add!");
        recipe[i++] = ItemRegistry.getRegistryId(itemStack).asString();
    }

    @SuppressWarnings("unused")
    public void add(ItemStack... itemStacks) {
        try {
            if (built) throw new IllegalAccessException("It's already registered!");
        } catch (IllegalAccessException e) {
            MoYuCustom.instance.getLogger().severe(e.getLocalizedMessage());
        }
        if (itemStacks.length + i > recipe.length) throw new ArrayIndexOutOfBoundsException("You can no long add!");
        for (ItemStack itemStack : itemStacks) {
            recipe[i++] = ItemRegistry.getRegistryId(itemStack).asString();
        }
    }

    @SuppressWarnings("unused")
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
        Arrays.sort(recipe);
        StringBuilder builtRecipe = new StringBuilder();
        for (String s : recipe) {
            if (!s.isEmpty()) builtRecipe.append(s);
            builtRecipe.append(",");
        }
        CraftingTableRecipeRegistry.register(builtRecipe.toString(), this);
        built = true;
    }

    protected String getResult() {
        return result;
    }

    protected int getCount() {
        return count;
    }
}
