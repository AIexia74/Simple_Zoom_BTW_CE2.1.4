package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StitcherException extends RuntimeException
{
    private final StitchHolder field_98149_a;

    public StitcherException(StitchHolder par1StitchHolder)
    {
        this.field_98149_a = par1StitchHolder;
    }
}
