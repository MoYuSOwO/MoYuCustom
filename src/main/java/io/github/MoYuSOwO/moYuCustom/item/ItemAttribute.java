package io.github.MoYuSOwO.moYuCustom.item;

import io.github.MoYuSOwO.moYuCustom.attribute.AttributeRegistry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemAttribute {

    private final Map<String, Object> attributeValue;

    protected ItemAttribute() {
        attributeValue = new HashMap<>();
    }

    protected void addAttribute(String name, Object value) {
        if (!AttributeRegistry.hasAttribute(name)) throw new IllegalArgumentException("You didn't register the attribute!");
        attributeValue.put(name, value);
    }

    public @Nullable <T> T getAttributeValue(NamespacedKey name) {
        if (!attributeValue.containsKey(name.asString())) return null;
        return AttributeRegistry.toAttributeValue(name, attributeValue.get(name.asString()));
    }

    public boolean isEmpty() { return attributeValue.isEmpty(); }

    public boolean hasAttribute(String name) {
        return attributeValue.containsKey(name);
    }

    public Map<String, Object> getAttributes() {
        return Map.copyOf(attributeValue);
    }
}
