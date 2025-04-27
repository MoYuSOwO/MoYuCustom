package io.github.MoYuSOwO.moYuCustom.entity;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistrar {
    private final JavaPlugin plugin;
    private final MonsterManager monsterManager;

    public CommandRegistrar(JavaPlugin plugin, MonsterManager monsterManager) {
        this.plugin = plugin;
        this.monsterManager = monsterManager;
    }
    public void registerCommands() {
        CommandMap commandMap = plugin.getServer().getCommandMap();
        CustomCommand customCommand = new CustomCommand(monsterManager);
        commandMap.register("moyucustom", customCommand);
        plugin.getLogger().info("已重载指令:/custom");
    }
}