package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillSystem {
    private static final Map<UUID, BukkitRunnable> tasks = new HashMap<>();

    private SkillSystem() {}

    public static void loadSkills() {
        // 占位方法，技能从 MonsterConfig 获取
    }

    public static void registerSkills(LivingEntity entity, MonsterConfig config) {
        for (MonsterConfig.SkillConfig skillConfig : config.getSkills()) {
            if (!skillConfig.getType().equals("NONE")) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!entity.isValid() || entity.isDead()) {
                            cancel();
                            tasks.remove(entity.getUniqueId());
                            return;
                        }
                        SkillHandler.executeSkill(entity, skillConfig);
                    }
                };
                task.runTaskTimer(MoYuCustom.instance, 20L, skillConfig.getInterval() * 20L);
                tasks.put(entity.getUniqueId(), task);
            }
        }
    }

    public void stopAllTasks() {
        //终止所有技能任务
        for (BukkitRunnable task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }
}