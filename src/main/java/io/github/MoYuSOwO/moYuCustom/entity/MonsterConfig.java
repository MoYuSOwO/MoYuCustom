package io.github.MoYuSOwO.moYuCustom.entity;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MonsterConfig {
    private final NamespacedKey id; // 怪物ID
    private final EntityType type; // 实体类型
    private final String name; // 显示名称
    private final int levelMin; // 最低等级
    private final int levelMax; // 最高等级
    private final double baseHealth; // 基础血量
    private final double baseArmor; // 基础护甲
    private final double baseAttackDamage; // 基础伤害
    private final double speed; // 移动速度
    private final boolean randomSpawn; // 是否随机生成
    private final double spawnChance; // 生成几率
    private final List<String> spawnBiomes; // 生物群系
    private final List<String> spawnWorlds; // 世界
    private final List<Drop> drops; // 掉落物
    private final ExplosionEffect explosion; // 爆炸效果
    private final LevelScaling scaling; // 等级缩放
    private final List<SkillConfig> skills; // 技能列表

    public MonsterConfig(NamespacedKey id, ConfigurationSection section, ConfigurationSection globalConfig, JavaPlugin plugin) {
        this.id = id;
        // 解析类型
        String typeStr = section.getString("type");
        if (typeStr == null) {
            throw new IllegalArgumentException("生物" + id + "配置文件中包含错误的 'type'");
        }
        try {
            this.type = EntityType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("类型无效'" + typeStr + "' 对于生物: " + id);
        }
        this.name = section.getString("name", id.getKey());
        this.levelMin = section.getInt("level_min", 1);
        this.levelMax = section.getInt("level_max", 10);
        this.baseHealth = section.getDouble("health", 20.0);
        this.baseArmor = section.getDouble("armor", 0.0);
        this.baseAttackDamage = section.getDouble("attack_damage", 3.0);
        this.speed = section.getDouble("speed", 0.23);
        this.randomSpawn = section.getBoolean("spawn.random", true);
        this.spawnChance = section.getDouble("spawn.chance", 0.0);
        this.spawnBiomes = section.getStringList("spawn.biomes");
        this.spawnWorlds = section.getStringList("spawn.worlds");
        // 解析掉落物
        this.drops = new ArrayList<>();
        List<?> dropList = section.getList("drops", new ArrayList<>());
        for (Object obj : dropList) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dropMap = (Map<String, Object>) obj;
                MemoryConfiguration dropConfig = new MemoryConfiguration();
                dropMap.forEach(dropConfig::set);
                ConfigurationSection drop = dropConfig;

                String itemId = drop.getString("item", "minecraft:stone");
                NamespacedKey itemKey;
                try {
                    itemKey = NamespacedKey.fromString(itemId);
                    if (itemKey == null) {
                        throw new IllegalArgumentException("Invalid NamespacedKey: " + itemId);
                    }
                } catch (IllegalArgumentException e) {
                    MoYuCustom.instance.getLogger().warning("Invalid item ID '" + itemId + "' for monster " + id + ", skipping drop.");
                    continue;
                }
                int minAmount = 1;
                int maxAmount = 1;
                String amountStr = drop.getString("amount", "1");
                String[] amountRange = amountStr.split("-");
                try {
                    if (amountRange.length == 2) {
                        minAmount = Integer.parseInt(amountRange[0]);
                        maxAmount = Integer.parseInt(amountRange[1]);
                    } else {
                        minAmount = maxAmount = Integer.parseInt(amountStr);
                    }
                } catch (NumberFormatException e) {
                    MoYuCustom.instance.getLogger().warning("自定义生物:" + id + "无效数量配置" + amountStr + "将使用默认配置'1'");
                }
                double chance = Math.clamp(drop.getDouble("chance", 1.0), 0.0, 1.0); // 限制 chance 在 0-1
                drops.add(new Drop(itemKey, minAmount, maxAmount, chance));
            }
        }
        // 解析爆炸
        ConfigurationSection explosionSection = section.getConfigurationSection("on_death.explosion");
        this.explosion = explosionSection != null ?
                new ExplosionEffect(explosionSection.getDouble("power", 0.0), explosionSection.getBoolean("break_blocks", false)) :
                null;
        // 解析等级缩放
        ConfigurationSection scalingSection = globalConfig.getConfigurationSection("level_scaling");
        this.scaling = scalingSection != null ?
                new LevelScaling(
                        scalingSection.getDouble("health_multiplier", 0.2),
                        scalingSection.getDouble("armor_increment", 2.0),
                        scalingSection.getDouble("damage_multiplier", 0.15)
                ) :
                new LevelScaling(0.2, 2.0, 0.15);
        // 解析技能
        this.skills = new ArrayList<>();
        List<?> skillList = section.getList("skills", new ArrayList<>());
        for (Object obj : skillList) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> skillMap = (Map<String, Object>) obj;
                MemoryConfiguration skillConfig = new MemoryConfiguration();
                skillMap.forEach(skillConfig::set); // 将 Map 转换为 ConfigurationSection
                ConfigurationSection skill = skillConfig;

                String skillType = skill.getString("type", "NONE");
                int interval = skill.getInt("interval", 5);
                double range = skill.getDouble("range", 10.0);
                double power = skill.getDouble("power", 1.0);
                double speed = skill.getDouble("speed", 1.0);
                skills.add(new SkillConfig(skillType, interval, range, power, speed));
            }
        }
    }
    public boolean isValidLevel(int level) {
        return level >= levelMin && level <= levelMax;
    }
    public double getHealth(int level) {
        return baseHealth * (1 + (level - 1) * scaling.healthMultiplier);
    }
    public double getArmor(int level) {
        return baseArmor + (level - 1) * scaling.armorIncrement;
    }
    public double getAttackDamage(int level) {
        return baseAttackDamage * (1 + (level - 1) * scaling.damageMultiplier);
    }
    public int getRandomLevel() {
        return levelMin + new Random().nextInt(levelMax - levelMin + 1);
    }
    // Getter 方法
    public NamespacedKey getId() { return id; }
    public EntityType getType() { return type; }
    public String getName() { return name; }
    public int getLevelMin() { return levelMin; }
    public int getLevelMax() { return levelMax; }
    public double getSpeed() { return speed; }
    public boolean isRandomSpawn() { return randomSpawn; }
    public double getSpawnChance() { return spawnChance; }
    public List<String> getSpawnBiomes() { return spawnBiomes; }
    public List<String> getSpawnWorlds() { return spawnWorlds; }
    public List<Drop> getDrops() { return drops; }
    public ExplosionEffect getExplosion() { return explosion; }
    public List<SkillConfig> getSkills() { return skills; }

    public static class Drop {
        private final NamespacedKey itemKey; // 使用 NamespacedKey
        private final int minAmount;
        private final int maxAmount;
        private final double chance;

        public Drop(NamespacedKey itemKey, int minAmount, int maxAmount, double chance) {
            this.itemKey = itemKey;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
        }

        public NamespacedKey getItemKey() { return itemKey; }
        public int getMinAmount() { return minAmount; }
        public int getMaxAmount() { return maxAmount; }
        public double getChance() { return chance; }
    }

    public static class ExplosionEffect {
        private final double power;
        private final boolean breakBlocks;

        public ExplosionEffect(double power, boolean breakBlocks) {
            this.power = power;
            this.breakBlocks = breakBlocks;
        }

        public double getPower() { return power; }
        public boolean isBreakBlocks() { return breakBlocks; }
    }

    public static class LevelScaling {
        private final double healthMultiplier;
        private final double armorIncrement;
        private final double damageMultiplier;

        public LevelScaling(double healthMultiplier, double armorIncrement, double damageMultiplier) {
            this.healthMultiplier = healthMultiplier;
            this.armorIncrement = armorIncrement;
            this.damageMultiplier = damageMultiplier;
        }
    }

    public static class SkillConfig {
        private final String type;
        private final int interval;
        private final double range;
        private final double power;
        private final double speed;

        public SkillConfig(String type, int interval, double range, double power, double speed) {
            this.type = type;
            this.interval = interval;
            this.range = range;
            this.power = power;
            this.speed = speed;
        }

        public String getType() { return type; }
        public int getInterval() { return interval; }
        public double getRange() { return range; }
        public double getPower() { return power; }
        public double getSpeed() { return speed; }
    }
}