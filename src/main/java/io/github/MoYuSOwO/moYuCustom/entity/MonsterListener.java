package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class MonsterListener implements Listener {
    private static final NamespacedKey monsterIdKey = new NamespacedKey(MoYuCustom.instance, "monster_id");

    private MonsterListener() {}

    public static void registerListener() {
        MoYuCustom.instance.getServer().getPluginManager().registerEvents(new MonsterListener(), MoYuCustom.instance);
    }

    @EventHandler
    private static void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        MonsterConfig config = MonsterManager.checkNaturalSpawn(event.getEntityType(), event.getEntity().getWorld(), event.getLocation());
        if (config != null) {
            event.setCancelled(true);
            MonsterManager.spawnMonster(config.getId(), event.getEntity().getWorld(), event.getLocation());
        }
    }

    @EventHandler
    private static void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        String monsterIdStr = entity.getPersistentDataContainer().get(monsterIdKey, PersistentDataType.STRING);
        if (monsterIdStr != null) {
            NamespacedKey monsterId = NamespacedKey.fromString(monsterIdStr, MoYuCustom.instance);
            if (monsterId == null) {
                MoYuCustom.instance.getLogger().warning("无效生物ID:" + monsterIdStr);
                return;
            }
            MonsterConfig config = MonsterManager.getMonsterConfig(monsterId);
            if (config != null) {
                event.getDrops().clear();
                MonsterManager.handleDrops(entity, config);
                if (config.getExplosion() != null) {
                    entity.getWorld().createExplosion(
                            entity.getLocation(),
                            (float) config.getExplosion().getPower(),
                            false,
                            config.getExplosion().isBreakBlocks()
                    );
                }
            } else {
                MoYuCustom.instance.getLogger().warning("无法在配置文件找到怪物ID:" + monsterId);
            }
        }

    }
}