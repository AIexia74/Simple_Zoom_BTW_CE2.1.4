package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IStatType
{
    /**
     * Formats a given stat for human consumption.
     */
    @Environment(EnvType.CLIENT)
    String format(int var1);
}
