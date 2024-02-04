// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class AxeItemSteel extends AxeItem
{
    public AxeItemSteel(int i)
    {
        super( i, EnumToolMaterial.SOULFORGED_STEEL );
        
        setUnlocalizedName( "fcItemHatchetRefined" );
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 72000;
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

    @Override
    public boolean getCanBePlacedAsBlock()
    {
    	return true;
    }    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}