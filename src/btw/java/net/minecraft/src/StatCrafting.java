package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class StatCrafting extends StatBase
{
    private final int itemID;

    public StatCrafting(int par1, String par2Str, int par3)
    {
        super(par1, par2Str);
        this.itemID = par3;
    }

    @Environment(EnvType.CLIENT)
    public int getItemID()
    {
        return this.itemID;
    }
}
