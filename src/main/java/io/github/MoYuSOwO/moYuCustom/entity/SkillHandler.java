package io.github.MoYuSOwO.moYuCustom.entity;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SkillHandler {
    private final Map<String, BiConsumer<LivingEntity, MonsterConfig.SkillConfig>> skillExecutors;

    public SkillHandler() {
        skillExecutors = new HashMap<>();
        skillExecutors.put("FIREBALL", this::executeFireball);
        skillExecutors.put("ARROW", this::executeArrow);
        skillExecutors.put("EXPLOSION", this::executeExplosion);
    }
    //获取需要使用的技能方法
    public void executeSkill(LivingEntity entity, MonsterConfig.SkillConfig config) {
        BiConsumer<LivingEntity, MonsterConfig.SkillConfig> executor = skillExecutors.get(config.getType().toUpperCase());
        if (executor != null) {
            executor.accept(entity, config);
        }
    }
    //获取攻击目标位置
    private Entity findTarget(LivingEntity entity, double range) {
        for (Entity nearby : entity.getNearbyEntities(range, range, range)) {
            if (nearby instanceof org.bukkit.entity.Player player) {
                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    return nearby;
                }
            }
        }
        return null;
    }
    //技能实现方法(一个方法一个技能)
    private void executeFireball(LivingEntity entity, MonsterConfig.SkillConfig config) {
        Entity target = findTarget(entity, config.getRange());
        if (target == null) return;
        Location eyeLoc = entity.getEyeLocation();
        Vector direction = target.getLocation().toVector().subtract(eyeLoc.toVector()).normalize();
        Fireball fireball = entity.getWorld().spawn(eyeLoc, Fireball.class);
        fireball.setShooter(entity);
        fireball.setDirection(direction.multiply(config.getSpeed()));
        fireball.setYield((float) config.getPower());
    }
    private void executeArrow(LivingEntity entity, MonsterConfig.SkillConfig config) {
        Entity target = findTarget(entity, config.getRange());
        if (target == null) return;
        Location eyeLoc = entity.getEyeLocation();
        Vector direction = target.getLocation().toVector().subtract(eyeLoc.toVector()).normalize();
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        Arrow arrow = entity.getWorld().spawnArrow(eyeLoc, direction, (float) config.getSpeed(), 12);
        arrow.setShooter(entity);
        arrow.setDamage(config.getPower());
    }
    private void executeExplosion(LivingEntity entity, MonsterConfig.SkillConfig config) {
        entity.getWorld().createExplosion(
                entity.getLocation(),
                (float) config.getPower(),
                false,
                false
        );
    }
}