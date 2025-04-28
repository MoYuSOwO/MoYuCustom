package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class EntityInitializer {

    private EntityInitializer() {}

    public static void init() {
        // 加载怪物配置
        MonsterManager.loadMonsters();
        // 注册事件监听器
        MonsterListener.registerListener();
    }

    public void shutdown() {
        // 清理逻辑（如果需要）
    }

}