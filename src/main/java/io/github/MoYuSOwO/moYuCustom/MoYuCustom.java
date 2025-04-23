package io.github.MoYuSOwO.moYuCustom;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
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

    private static final SuggestionProvider<CommandSourceStack> REGISTRY_ID_SUGGESTIONS =
            (ctx, builder) -> {
                String currentInput = builder.getRemaining().toLowerCase();
                ItemRegistry.getAllIds().stream()
                        .filter(id -> id.toLowerCase().startsWith(currentInput))
                        .forEach(builder::suggest);
                return builder.buildFuture();
            };

    private static final LiteralArgumentBuilder<CommandSourceStack> command =
            Commands.literal("item").requires(ctx -> ctx.getSender().isOp()).then(
                    Commands.literal("get").then(
                            Commands.argument("registryId", ArgumentTypes.namespacedKey())
                                    .suggests(REGISTRY_ID_SUGGESTIONS)
                                    .executes(
                                    ctx -> {
                                        if (ctx.getSource().getSender() instanceof Player player) {
                                            String registryId = ctx.getArgument("registryId", NamespacedKey.class).asString();
                                            player.give(ItemRegistry.get(registryId, 1));
                                            return 1;
                                        }
                                        ctx.getSource().getSender().sendMessage(
                                                (ComponentLike) Component.literal("你必须是一名玩家！").withStyle(ChatFormatting.RED)
                                        );
                                        return 0;
                                    }
                            )
                    ).then(
                            Commands.argument("registryId", ArgumentTypes.namespacedKey()).then(
                                    Commands.argument("count", IntegerArgumentType.integer(1))
                                            .executes(
                                            ctx -> {
                                                if (ctx.getSource().getSender() instanceof Player player) {
                                                    String registryId = ctx.getArgument("registryId", NamespacedKey.class).asString();
                                                    int count = IntegerArgumentType.getInteger(ctx, "count");
                                                    player.give(ItemRegistry.get(registryId, count));
                                                    return 1;
                                                }
                                                ctx.getSource().getSender().sendMessage(
                                                        (ComponentLike) Component.literal("你必须是一名玩家！").withStyle(ChatFormatting.RED)
                                                );
                                                return 0;
                                            }
                                    )
                            )
                    )
            );
    private static final LiteralCommandNode<CommandSourceStack> buildCommand = command.build();

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
        ItemRegistry.register();
        getLogger().info("Successfully register " + ItemRegistry.registrySize() + " custom items");
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        CraftingTableRecipeRegistry.registerListener();
        CraftingTableRecipeRegistry.register();
        getLogger().info("Successfully register " + CraftingTableRecipeRegistry.getShapedRegistrySize() + " custom shaped recipes");
        getLogger().info("Successfully register " + CraftingTableRecipeRegistry.getShapelessRegistrySize() + " custom shapeless recipes");
    }

    @Override
    public void onDisable() {
        Bukkit.resetRecipes();
    }
}
