package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

final class CreativeTabCombat extends CreativeTabs
{
    CreativeTabCombat(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    /**
     * the itemID for the item to be displayed on the tab
     */
    @Environment(EnvType.CLIENT)
    public int getTabIconItemIndex()
    {
        return Item.swordGold.itemID;
    }

    /**
     * only shows items which have tabToDisplayOn == this
     */
    @Environment(EnvType.CLIENT)
    public void displayAllReleventItems(List par1List)
    {
        super.displayAllReleventItems(par1List);
        this.func_92116_a(par1List, new EnumEnchantmentType[] {EnumEnchantmentType.armor, EnumEnchantmentType.armor_feet, EnumEnchantmentType.armor_head, EnumEnchantmentType.armor_legs, EnumEnchantmentType.armor_torso, EnumEnchantmentType.bow, EnumEnchantmentType.weapon});
    }
}
