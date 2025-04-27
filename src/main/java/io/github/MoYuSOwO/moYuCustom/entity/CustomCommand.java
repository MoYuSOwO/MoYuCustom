package io.github.MoYuSOwO.moYuCustom.entity;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCommand extends Command {
    private final MonsterManager monsterManager;

    public CustomCommand(MonsterManager monsterManager) {
        super("custom");
        this.monsterManager = monsterManager;
        this.setPermission("monsterframework.admin");
        this.setPermissionMessage("§c你没有权限使用此指令");
    }
    @Override
    public boolean execute(CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!sender.hasPermission("monsterframework.admin")) {
            sender.sendMessage("§c你没有权限使用此指令!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§c请使用:/custom <summon|reload>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "summon":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c此指令只可用于玩家!");
                    return true;
                }
                if (args.length < 6) {
                    sender.sendMessage("§c请使用:/custom summon <id> <level> <health> <armor> <damage>");
                    return true;
                }
                String idStr = args[1];
                NamespacedKey id = NamespacedKey.fromString(idStr, monsterManager.getPlugin());
                if (id == null) {
                    sender.sendMessage("§c无效怪物ID:" + idStr);
                    return true;
                }
                int level;
                double health, armor, damage;
                try {
                    level = Integer.parseInt(args[2].replace("level", ""));
                    health = Double.parseDouble(args[3]);
                    armor = Double.parseDouble(args[4]);
                    damage = Double.parseDouble(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c无效数据格式:" + e.getMessage());
                    return true;
                }
                Player player = (Player) sender;
                Location location = player.getLocation();
                World world = location.getWorld();
                boolean spawned = monsterManager.spawnMonster(id, level, health, armor, damage, world, location);
                if (spawned) {
                    sender.sendMessage("§a已成功生成生物:" + id + ",等级为:" + level+"级");
                } else {
                    sender.sendMessage("§c无法生成:" + id);
                }
                return true;

            case "reload":
                monsterManager.loadMonsters();
                sender.sendMessage("§a配置文件重载成功!");
                return true;

            default:
                sender.sendMessage("§c请使用:/custom <summon|reload> [args]");
                return true;
        }
    }
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("summon".startsWith(args[0].toLowerCase())) {
                completions.add("summon");
            }
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("summon")) {
            String input = args[1].toLowerCase();
            completions.addAll(monsterManager.getMonsterIds().stream()
                    .filter(id -> id.getKey().toLowerCase().startsWith(input))
                    .map(NamespacedKey::toString)
                    .collect(Collectors.toList()));
        }
        return completions;
    }
}