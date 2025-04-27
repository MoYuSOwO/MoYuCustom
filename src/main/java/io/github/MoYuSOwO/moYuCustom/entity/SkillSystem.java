package io.github.MoYuSOwO.moYuCustom.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillSystem {
    private final JavaPlugin plugin;
    private final SkillHandler skillHandler;
    private final Map<UUID, BukkitRunnable> tasks;

    public SkillSystem(JavaPlugin plugin) {
        this.plugin = plugin;
        this.skillHandler = new SkillHandler();
        this.tasks = new HashMap<>();
    }
    public void loadSkills() {
        // 占位方法，技能从 MonsterConfig 获取
    }
    public void registerSkills(LivingEntity entity, MonsterConfig config) {
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
                        skillHandler.executeSkill(entity, skillConfig);
                    }
                };
                task.runTaskTimer(plugin, 20L, skillConfig.getInterval() * 20L);
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