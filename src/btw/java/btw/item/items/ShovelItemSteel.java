// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class ShovelItemSteel extends ShovelItem
{
    public ShovelItemSteel(int iItemID)
    {
        super( iItemID, EnumToolMaterial.SOULFORGED_STEEL );
        
        setUnlocalizedName( "fcItemShovelRefined" );        
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 0x11940;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
    	if (!entityplayer.isUsingSpecialKey())
    	{
        entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
    	}
        return itemstack;
    }


    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}