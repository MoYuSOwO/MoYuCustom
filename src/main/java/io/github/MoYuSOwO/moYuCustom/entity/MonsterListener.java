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
    private final MonsterManager manager;
    private final NamespacedKey monsterIdKey;

    public MonsterListener(MonsterManager manager, SkillSystem skillSystem) {
        this.manager = manager;
        this.monsterIdKey = new NamespacedKey(MoYuCustom.instance, "monster_id");
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        MonsterConfig config = manager.checkNaturalSpawn(event.getEntityType(), event.getEntity().getWorld(), event.getLocation());
        if (config != null) {
            event.setCancelled(true);
            manager.spawnMonster(config.getId(), event.getEntity().getWorld(), event.getLocation());
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        String monsterIdStr = entity.getPersistentDataContainer().get(monsterIdKey, PersistentDataType.STRING);
        if (monsterIdStr != null) {
            NamespacedKey monsterId = NamespacedKey.fromString(monsterIdStr, manager.getPlugin());
            if (monsterId == null) {
                manager.getPlugin().getLogger().warning("无效生物ID:" + monsterIdStr);
                return;
            }
            MonsterConfig config = manager.getMonsterConfig(monsterId);
            if (config != null) {
                event.getDrops().clear();
                manager.handleDrops(entity, config);
                if (config.getExplosion() != null) {
                    entity.getWorld().createExplosion(
                            entity.getLocation(),
                            (float) config.getExplosion().getPower(),
                            false,
                            config.getExplosion().isBreakBlocks()
                    );
                }
            } else {
                manager.getPlugin().getLogger().warning("无法在配置文件找到怪物ID:" + monsterId);
            }
        }

    }
}