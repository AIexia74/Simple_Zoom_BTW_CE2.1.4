package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class McoOptionSome extends McoOption
{
    private final Object field_98156_a;

    public McoOptionSome(Object par1Obj)
    {
        this.field_98156_a = par1Obj;
    }

    public Object func_98155_a()
    {
        return this.field_98156_a;
    }
}
