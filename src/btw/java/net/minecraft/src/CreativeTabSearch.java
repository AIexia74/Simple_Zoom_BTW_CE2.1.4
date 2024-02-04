package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

final class CreativeTabSearch extends CreativeTabs
{
    CreativeTabSearch(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    /**
     * the itemID for the item to be displayed on the tab
     */
    @Environment(EnvType.CLIENT)
    public int getTabIconItemIndex()
    {
        return Item.compass.itemID;
    }
}
