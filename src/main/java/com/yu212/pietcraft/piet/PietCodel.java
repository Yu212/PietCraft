package com.yu212.pietcraft.piet;

import org.bukkit.Color;
import org.bukkit.Material;

public enum PietCodel {
    LIGHT_RED(0, 0,   0xFFC0C0, Material.RED_WOOL),
    RED(0, 1, 0xFF0000, Material.RED_CONCRETE),
    DARK_RED(0, 2, 0xC00000, Material.RED_TERRACOTTA),
    LIGHT_YELLOW(1, 0, 0xFFFFC0, Material.YELLOW_WOOL),
    YELLOW(1, 1, 0xFFFF00, Material.YELLOW_CONCRETE),
    DARK_YELLOW(1, 2, 0xC0C000, Material.YELLOW_TERRACOTTA),
    LIGHT_GREEN(2, 0, 0xC0FFC0, Material.LIME_WOOL),
    GREEN(2, 1, 0x00FF00, Material.LIME_CONCRETE),
    DARK_GREEN(2, 2, 0x00C000, Material.LIME_TERRACOTTA),
    LIGHT_CYAN(3, 0, 0xC0FFFF, Material.LIGHT_BLUE_WOOL),
    CYAN(3, 1, 0x00FFFF, Material.LIGHT_BLUE_CONCRETE),
    DARK_CYAN(3, 2, 0x00C0C0, Material.LIGHT_BLUE_TERRACOTTA),
    LIGHT_BLUE(4, 0, 0xC0C0FF, Material.BLUE_WOOL),
    BLUE(4, 1, 0x0000FF, Material.BLUE_CONCRETE),
    DARK_BLUE(4, 2, 0x0000C0, Material.BLUE_TERRACOTTA),
    LIGHT_MAGENTA(5, 0, 0xFFC0FF, Material.MAGENTA_WOOL),
    MAGENTA(5, 1, 0xFF00FF, Material.MAGENTA_CONCRETE),
    DARK_MAGENTA(5, 2, 0xC000C0, Material.MAGENTA_TERRACOTTA),
    WHITE(-1, -1, 0xFFFFFF, Material.WHITE_CONCRETE),
    BLACK(-2, -2, 0x000000, Material.BLACK_CONCRETE);

    public final int h;
    public final int v;
    public final Color color;
    public final Material material;

    PietCodel(int h, int v, int rgb, Material material) {
        this.h = h;
        this.v = v;
        this.color = Color.fromRGB(rgb);
        this.material = material;
    }

    public static PietCodel of(Material material) {
        for (PietCodel codel : values()) {
            if (codel.material == material) {
                return codel;
            }
        }
        return null;
    }

    public static PietCodel getClosest(int rgb) {
        Color color = Color.fromRGB(rgb);
        PietCodel closest = PietCodel.BLACK;
        int minDiff = Integer.MAX_VALUE;
        for (PietCodel codel : values()) {
            int diff = 0;
            diff += Math.abs(codel.color.getRed() - color.getRed());
            diff += Math.abs(codel.color.getGreen() - color.getGreen());
            diff += Math.abs(codel.color.getBlue() - color.getBlue());
            if (minDiff > diff) {
                minDiff = diff;
                closest = codel;
            }
        }
        return closest;
    }
}
