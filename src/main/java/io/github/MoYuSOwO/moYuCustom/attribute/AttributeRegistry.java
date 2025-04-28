package io.github.MoYuSOwO.moYuCustom.attribute;

import io.github.MoYuSOwO.moYuCustom.MoYuCustom;
import io.github.MoYuSOwO.moYuCustom.recipe.CraftingTableRecipeRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class AttributeRegistry {

    private static final Map<String, Class<?>> TYPE_MAP = Map.of(
            "int", Integer.class,
            "integer", Integer.class,
            "double", Double.class,
            "float", Float.class,
            "string", String.class,
            "boolean", Boolean.class
    );

    private static final Map<String, PersistentDataType<?, ?>> attributePDCType = Map.of(
            "int", PersistentDataType.INTEGER,
            "integer", PersistentDataType.INTEGER,
            "double", PersistentDataType.DOUBLE,
            "float", PersistentDataType.FLOAT,
            "string", PersistentDataType.STRING,
            "boolean", PersistentDataType.BOOLEAN
    );

    private static final Map<String, NamespacedKey> attributeKey = new HashMap<>();

    private static final Map<String, String> attributeRegistry = new HashMap<>();

    private AttributeRegistry() {}

    public static void init() {
        registerFromFile();
        MoYuCustom.instance.getLogger().info("successfully register " + attributeRegistry.size() + " custom shaped attributes");;
    }

    private static void registerFromFile() {
        File file = ReadUtil.readAttributeFiles();
        if (file.isFile() && ReadUtil.isYmlFile(file)) {
            YamlConfiguration attribute = YamlConfiguration.loadConfiguration(file);
            for (String key : attribute.getKeys(false)) {
                String value = attribute.getString(key);
                if (!TYPE_MAP.containsKey(value)) throw new IllegalArgumentException("You must provide a legal type name!");
                attributeRegistry.put("moyuattribute:" + key, value);
            }
        }
    }

    public static void register(NamespacedKey key, String type) {
        if (!TYPE_MAP.containsKey(type)) throw new IllegalArgumentException("You must provide a legal type name!");
        attributeRegistry.put(key.asString(), type);
    }

    public static boolean hasAttribute(String name) {
        return attributeRegistry.containsKey(name);
    }

    public static @NotNull Class<?> getAttributeType(String name) {
        if (!attributeRegistry.containsKey(name)) throw new IllegalArgumentException("You must check if attribute exists before get!");
        return TYPE_MAP.get(attributeRegistry.get(name));
    }

    public static @NotNull PersistentDataType<?, ?> getAttributePDCType(String name) {
        if (!attributeRegistry.containsKey(name)) throw new IllegalArgumentException("You must check if attribute exists before get!");
        return attributePDCType.get(attributeRegistry.get(name));
    }

    /**
     * 根据属性名自动返回正确类型的值
     * 基本类型不能取消装箱
     * @param name 属性名
     * @param object 属性值的Object对象
     * @return 类型转换后的对应的属性类型，如果不存在或类型不匹配返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T toAttributeValue(String name, Object object) {
        if (!attributeRegistry.containsKey(name)) return null;
        Class<T> type = (Class<T>) getAttributeType(name);
        try {
            return type.cast(object);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static @NotNull NamespacedKey getKey(String name) {
        if (!name.contains(":")) name = "moyuattribute:" + name;
        if (attributeKey.containsKey(name)) return attributeKey.get(name);
        NamespacedKey key = NamespacedKey.fromString(name);
        if ((!attributeRegistry.containsKey(name)) || (key == null)) throw new IllegalArgumentException("You must check if attribute exists before get!");
        attributeKey.put(name, key);
        return key;
    }

}