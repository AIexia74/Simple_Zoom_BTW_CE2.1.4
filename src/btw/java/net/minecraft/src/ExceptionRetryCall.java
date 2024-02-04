package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ExceptionRetryCall extends ExceptionMcoService
{
    public final int field_96393_c;

    public ExceptionRetryCall(int par1)
    {
        super(503, "Retry operation");
        this.field_96393_c = par1;
    }
}
