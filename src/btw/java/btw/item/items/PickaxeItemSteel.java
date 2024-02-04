// FCMOD

package btw.item.items;

import btw.BTWMod;
import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class PickaxeItemSteel extends PickaxeItem
{
    public PickaxeItemSteel(int i)
    {
        super( i, EnumToolMaterial.SOULFORGED_STEEL );
        
        setUnlocalizedName( "fcItemPickAxeRefined" );
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

    @Override
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	if ( block != null && block.blockMaterial == BTWBlocks.soulforgedSteelMaterial)
    	{
    		return true;
    	}
    	
    	return super.canHarvestBlock( stack, world, block, i, j, k );
    }
    
    @Override
    public float getStrVsBlock( ItemStack toolItemStack, World world, Block block, int i, int j, int k ) 
    {
    	if ( block != null && block.blockMaterial == BTWBlocks.soulforgedSteelMaterial)
    	{
            return efficiencyOnProperMaterial;
    	}
    	
    	return super.getStrVsBlock( toolItemStack, world, block, i, j, k );
    }
       
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}