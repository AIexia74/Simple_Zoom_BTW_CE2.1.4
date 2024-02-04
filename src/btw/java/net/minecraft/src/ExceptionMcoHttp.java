package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ExceptionMcoHttp extends RuntimeException
{
    public ExceptionMcoHttp(String par1Str, Exception par2Exception)
    {
        super(par1Str, par2Exception);
    }
}
