package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

final class StatTypeSimple implements IStatType
{
    /**
     * Formats a given stat for human consumption.
     */
    @Environment(EnvType.CLIENT)
    public String format(int par1)
    {
        return StatBase.getNumberFormat().format((long)par1);
    }
}
