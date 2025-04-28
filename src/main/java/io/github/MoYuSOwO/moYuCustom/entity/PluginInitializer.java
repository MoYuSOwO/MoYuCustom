package io.github.MoYuSOwO.moYuCustom.entity;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class PluginInitializer {
    private final JavaPlugin plugin;
    private final MonsterManager monsterManager;
    private final MonsterListener monsterListener;

    public PluginInitializer(JavaPlugin plugin) {
        this.plugin = plugin;
        this.monsterManager = new MonsterManager(plugin);
        SkillSystem skillSystem = new SkillSystem(plugin);
        this.monsterListener = new MonsterListener(monsterManager, skillSystem);
    }
    public void initialize() {
        // 加载怪物配置
        monsterManager.loadMonsters();
        // 注册事件监听器
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(monsterListener, plugin);
    }
    public void shutdown() {
        // 清理逻辑（如果需要）
    }
    public MonsterManager getMonsterManager() {
        return monsterManager;
    }
}