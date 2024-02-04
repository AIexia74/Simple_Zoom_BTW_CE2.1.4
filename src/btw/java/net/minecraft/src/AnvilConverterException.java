package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnvilConverterException extends Exception
{
    public AnvilConverterException(String par1Str)
    {
        super(par1Str);
    }
}
