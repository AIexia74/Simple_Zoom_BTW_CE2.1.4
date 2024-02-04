package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StatPlaceholder extends StatBase
{
    public StatPlaceholder(int par1)
    {
        super(par1, "Unknown stat");
    }
}
