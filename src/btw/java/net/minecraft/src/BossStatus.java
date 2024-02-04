package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class BossStatus
{
    public static float healthScale;
    public static int statusBarLength;
    public static String bossName;
    public static boolean field_82825_d;

    public static void func_82824_a(IBossDisplayData par0IBossDisplayData, boolean par1)
    {
        healthScale = (float)par0IBossDisplayData.getBossHealth() / (float)par0IBossDisplayData.getMaxHealth();
        statusBarLength = 100;
        bossName = par0IBossDisplayData.getEntityName();
        field_82825_d = par1;
    }
}
