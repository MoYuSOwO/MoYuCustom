package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.item.ItemRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class MonsterManager {
    private final JavaPlugin plugin;
    private final SkillSystem skillSystem;
    private final Map<NamespacedKey, MonsterConfig> monsters;
    private final Random random;

    public MonsterManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.skillSystem = new SkillSystem(plugin);
        this.monsters = new HashMap<>();
        this.random = new Random();
    }
    public void loadMonsters() {
        monsters.clear();
        File entity = new File(plugin.getDataFolder(), "entity");
        if (!entity.exists()) {
            entity.mkdirs();
            String[] defaultMonsterFiles = {"blaze_zombie.yml"};
            for (String fileName : defaultMonsterFiles) {
                File monsterFile = new File(entity, fileName);
                if (!monsterFile.exists()) {
                    try {
                        plugin.saveResource("entity/" + fileName, false);
                        plugin.getLogger().info("已保存默认生物配置文件:" + fileName);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("无法保存默认生物配置文件" + fileName + ":" + e.getMessage());
                    }
                }
            }
        }

        File[] monsterFiles = entity.listFiles((dir, name) -> name.endsWith(".yml"));
        if (monsterFiles == null || monsterFiles.length == 0) {
            plugin.getLogger().warning("在entity目录中没有找到任何生物的配置文件");
            return;
        }

        // 加载全局配置文件
        File configFile = new File(plugin.getDataFolder(), "levelConfig.yml");
        if (!configFile.exists()) {
            plugin.saveResource("levelConfig.yml", false);
            plugin.getLogger().info("已保存默认配置文件:levelConfig.yml");
        }
        YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(configFile);
        if (!globalConfig.contains("level_scaling")) {
            plugin.getLogger().warning("levelConfig.yml中不包含'level_scaling'将使用默认值.");
        }

        Logger logger = plugin.getLogger();
        for (File monsterFile : monsterFiles) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(monsterFile);
                NamespacedKey id = new NamespacedKey(plugin, monsterFile.getName().replace(".yml", ""));
                logger.info("已加载生物配置:" + monsterFile.getName());
                monsters.put(id, new MonsterConfig(id, config, globalConfig,MoYuCustom.instance));
            } catch (Exception e) {
                logger.warning("无法加载生物配置文件: " + monsterFile.getName() + ": " + e.getMessage());
            }
        }
        logger.info("已加载" + monsters.size() + "个自定义生物配置");
    }

    public int getMonsterCount() {
        return monsters.size();
    }

    public Set<NamespacedKey> getMonsterIds() {
        return monsters.keySet();
    }
    //用于命令生成
    public boolean spawnMonster(NamespacedKey id, int level, double health, double armor, double damage, World world, Location location) {
        MonsterConfig config = monsters.get(id);
        if (config == null || !config.isValidLevel(level)) {
            plugin.getLogger().warning("无法生成生物: " + id + ": 错误的ID或者等级: " + level);
            return false;
        }
        Entity entity = world.spawnEntity(location, config.getType());
        if (entity instanceof LivingEntity) {
            setupEntity((LivingEntity) entity, config, level, health, armor, damage);
            return true;
        }
        plugin.getLogger().warning("无法生成生物: " + id );
        return false;
    }
    //用于世界自然生成
    public boolean spawnMonster(NamespacedKey id, World world, Location location) {
        MonsterConfig config = monsters.get(id);
        if (config == null) {
            plugin.getLogger().warning("无法生成生物: " + id);
            return false;
        }
        int level = config.getRandomLevel();
        return spawnMonster(id, level, config.getHealth(level), config.getArmor(level), config.getAttackDamage(level), world, location);
    }
    //指定等级生成
    public boolean spawnMonster(NamespacedKey id, int level, World world, Location location) {
        MonsterConfig config = monsters.get(id);
        if (config == null || !config.isValidLevel(level)) return false;
        return spawnMonster(id, level, config.getHealth(level), config.getArmor(level), config.getAttackDamage(level), world, location);
    }

    public void setupEntity(LivingEntity entity, MonsterConfig config, int level, double health, double armor, double damage) {
        entity.setCustomName("§e[Lv." + level + "] §r" + config.getName());
        entity.setCustomNameVisible(true);
        Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(health);
        entity.setHealth(health);
        Objects.requireNonNull(entity.getAttribute(Attribute.ARMOR)).setBaseValue(armor);
        Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(config.getSpeed());
        Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(damage);

        skillSystem.registerSkills(entity, config);
        NamespacedKey monsterIdKey = new NamespacedKey(plugin, "monster_id");
        NamespacedKey monsterLevelKey = new NamespacedKey(plugin, "monster_level");

        entity.getPersistentDataContainer().set(monsterIdKey, PersistentDataType.STRING, config.getId().toString());
        entity.getPersistentDataContainer().set(monsterLevelKey, PersistentDataType.INTEGER, level);
    }

    public MonsterConfig checkNaturalSpawn(EntityType type, World world, Location location) {
        for (MonsterConfig config : monsters.values()) {
            if (config.isRandomSpawn() &&
                    config.getType() == type &&
                    config.getSpawnChance() > 0 &&
                    config.getSpawnWorlds().contains(world.getName()) &&
                    config.getSpawnBiomes().contains(world.getBiome(location).getKey().getKey().toUpperCase()) &&
                    random.nextDouble() < config.getSpawnChance()) {
                return config;
            }
        }
        return null;
    }
    public void handleDrops(LivingEntity entity, MonsterConfig config) {
        entity.getEquipment().clear();
        if (config.getDrops().isEmpty()) {
            plugin.getLogger().info("没有为: " + config.getId() + "配置掉落物");
            return;
        }
        for (MonsterConfig.Drop drop : config.getDrops()) {
            if (random.nextDouble() < drop.getChance()) {
                int amount = random.nextInt(drop.getMaxAmount() - drop.getMinAmount() + 1) + drop.getMinAmount();
                ItemStack itemStack = getItemFromNamespacedKey(drop.getItemKey(), amount);
                if (itemStack != null) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                } else {
                    plugin.getLogger().warning("无法为:"+ config.getId() + " 生成掉落物 " + drop.getItemKey() );
                }
            }
        }
    }
    private ItemStack getItemFromNamespacedKey(NamespacedKey key, int amount) {
        try {
            ItemStack customItem = ItemRegistry.get(key.toString(), amount);
            if (customItem != null && !customItem.getType().isAir()) {
                return customItem;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("无法通过 ItemRegistry 获取物品:" + key + ": " + e.getMessage());
        }
        Material material = Material.matchMaterial(key.toString());
        if (material != null && material.isItem()) {
            return new ItemStack(material, amount);
        }
        plugin.getLogger().warning("未找到有效物品: " + key);
        return null;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }
    public MonsterConfig getMonsterConfig(NamespacedKey id) {
        return monsters.get(id);
    }
}