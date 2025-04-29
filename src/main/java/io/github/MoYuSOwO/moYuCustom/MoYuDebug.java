package io.github.MoYuSOwO.moYuCustom;

import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class MoYuDebug implements Listener {

    private MoYuDebug() {}

    public static void registerListener() {
        MoYuCustom.instance.getServer().getPluginManager().registerEvents(new MoYuDebug(), MoYuCustom.instance);
    }

    @EventHandler
    public static void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        MoYuCustom.instance.getLogger().info(event.getEventName());
        if (event.getDamager() instanceof Player player) {
            NamespacedKey registryId = ItemRegistry.getRegistryId(player.getInventory().getItemInMainHand());
            NamespacedKey attributeId = NamespacedKey.fromString("moyuattribute:test2");
            if (!ItemRegistry.hasAttribute(registryId, attributeId)) return;
            Double damage = ItemRegistry.getAttributeValue(registryId, attributeId);
            MoYuCustom.instance.getLogger().info(String.valueOf(damage));
            if (damage != null) {
                event.setDamage(damage);
            }
        }
    }
}
