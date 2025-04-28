package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.attribute.AttributeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemAttribute {

    private final Map<String, Object> attributeValue;

    public ItemAttribute() {
        attributeValue = new HashMap<>();
    }

    public void addAttribute(String name, Object value) {
        if (!AttributeRegistry.hasAttribute(name)) throw new IllegalArgumentException("You didn't register the attribute!");
        attributeValue.put(name, value);
    }

    /**
     * 根据属性名自动返回正确类型的值
     * 基本类型不能取消装箱
     * @param name 属性名
     * @return 类型转换后的值，如果不存在或类型不匹配返回null
     */
    public @Nullable <T> T getAttributeValue(String name) {
        if (!attributeValue.containsKey(name)) return null;
        return AttributeRegistry.toAttributeValue(name, attributeValue.get(name));
    }

    public boolean isEmpty() { return attributeValue.isEmpty(); }

    public Map<String, Object> getAttributes() {
        return Map.copyOf(attributeValue);
    }
}
