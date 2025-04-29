package io.github.MoYuSOwO.moYuCustom;

import io.github.MoYuSOwO.moYuCustom.attribute.AttributeRegistry;
import io.github.MoYuSOwO.moYuCustom.entity.EntityCommandRegistrar;
import io.github.MoYuSOwO.moYuCustom.entity.EntityInitializer;
import io.github.MoYuSOwO.moYuCustom.item.ItemCommandRegistrar;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import io.github.MoYuSOwO.moYuCustom.recipe.CraftingTableRecipeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoYuCustom extends JavaPlugin {

    public static NamespacedKey registryIdKey;
    public static JavaPlugin instance;

    public MoYuCustom() {
        super();
        instance = this;
        registryIdKey = new NamespacedKey(this, "registryId");
    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        MoYuDebug.registerListener();
        AttributeRegistry.init();
        ItemRegistry.init();
        ItemCommandRegistrar.registerCommands();
        CraftingTableRecipeRegistry.init();
        EntityInitializer.init();
        EntityCommandRegistrar.registerCommands();
    }


    @Override
    public void onDisable() {
        Bukkit.resetRecipes();
    }

}