package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class McoOption
{
    public abstract Object func_98155_a();

    public static McoOptionSome func_98153_a(Object par0Obj)
    {
        return new McoOptionSome(par0Obj);
    }

    public static McoOptionNone func_98154_b()
    {
        return new McoOptionNone();
    }
}
