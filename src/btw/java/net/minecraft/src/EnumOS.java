package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum EnumOS
{
    LINUX,
    SOLARIS,
    WINDOWS,
    MACOS,
    UNKNOWN;
}
