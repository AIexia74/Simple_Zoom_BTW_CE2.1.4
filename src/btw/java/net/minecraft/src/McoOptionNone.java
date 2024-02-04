package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class McoOptionNone extends McoOption
{
    public Object func_98155_a()
    {
        throw new RuntimeException("None has no value");
    }
}
