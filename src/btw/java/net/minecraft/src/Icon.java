package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Icon
{
    /**
     * Returns the X position of this icon on its texture sheet, in pixels.
     */
    @Environment(EnvType.CLIENT)
    int getOriginX();

    /**
     * Returns the Y position of this icon on its texture sheet, in pixels.
     */
    @Environment(EnvType.CLIENT)
    int getOriginY();

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    @Environment(EnvType.CLIENT)
    float getMinU();

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    @Environment(EnvType.CLIENT)
    float getMaxU();

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    @Environment(EnvType.CLIENT)
    float getInterpolatedU(double var1);

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    @Environment(EnvType.CLIENT)
    float getMinV();

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    @Environment(EnvType.CLIENT)
    float getMaxV();

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    @Environment(EnvType.CLIENT)
    float getInterpolatedV(double var1);

    @Environment(EnvType.CLIENT)
    String getIconName();

    /**
     * Returns the width of the texture sheet this icon is on, in pixels.
     */
    @Environment(EnvType.CLIENT)
    int getSheetWidth();

    /**
     * Returns the height of the texture sheet this icon is on, in pixels.
     */
    @Environment(EnvType.CLIENT)
    int getSheetHeight();
}
