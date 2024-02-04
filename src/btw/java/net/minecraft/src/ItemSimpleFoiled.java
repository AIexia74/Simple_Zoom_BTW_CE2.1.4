package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ItemSimpleFoiled extends Item
{
    public ItemSimpleFoiled(int par1)
    {
        super(par1);
    }

    @Environment(EnvType.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }
}
