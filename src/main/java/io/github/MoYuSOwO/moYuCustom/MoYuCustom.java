package io.github.MoYuSOwO.moYuCustom;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.MoYuSOwO.moYuCustom.entity.CommandRegistrar;
import io.github.MoYuSOwO.moYuCustom.entity.PluginInitializer;
import io.github.MoYuSOwO.moYuCustom.item.ItemCommand;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import io.github.MoYuSOwO.moYuCustom.recipe.CraftingTableRecipeRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.ComponentLike;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoYuCustom extends JavaPlugin {

    public static NamespacedKey registryIdKey;
    public static JavaPlugin instance;
    private PluginInitializer initializer;

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
        ItemRegistry.init();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ItemCommand.buildCommand));
        CraftingTableRecipeRegistry.init();
        initializer = new PluginInitializer(this);
        initializer.initialize();
        new CommandRegistrar(this, initializer.getMonsterManager()).registerCommands();

    }


    @Override
    public void onDisable () {
        Bukkit.resetRecipes();
    }

}