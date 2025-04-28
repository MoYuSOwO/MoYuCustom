package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.command.CommandMap;

public class EntityCommandRegistrar {

    private EntityCommandRegistrar() {}

    public static void registerCommands() {
        CommandMap commandMap = MoYuCustom.instance.getServer().getCommandMap();
        commandMap.register("moyucustom", new CustomCommand());
        MoYuCustom.instance.getLogger().info("已重载指令:/custom");
    }
}