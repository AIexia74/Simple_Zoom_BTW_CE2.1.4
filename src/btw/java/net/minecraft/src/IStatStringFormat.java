package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IStatStringFormat
{
    /**
     * Formats the strings based on 'IStatStringFormat' interface.
     */
    String formatString(String var1);
}
