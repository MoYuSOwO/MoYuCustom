package io.github.MoYuSOwO.moYuCustom.item;

public record FoodItem(int nutrition, float saturation, boolean canAlwaysEat) {
    public static final FoodItem EMPTY = new FoodItem(-1, -1, false);
}
