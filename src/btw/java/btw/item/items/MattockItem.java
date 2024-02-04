// FCMOD

package btw.item.items;

import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class MattockItem extends PickaxeItemSteel
{
    public MattockItem(int i)
    {
        super( i );
        
        setUnlocalizedName( "fcItemMattock" );
    }
    
    @Override
    public float getStrVsBlock(ItemStack itemStack, World world, Block block, int i, int j, int k )
    {
    	float pickStr = super.getStrVsBlock( itemStack, world, block, i, j, k );
    	float shovelStr = ((ShovelItemSteel)(BTWItems.steelShovel)).getStrVsBlock( itemStack, world, block, i, j, k );
    	
    	if ( shovelStr > pickStr )
    	{
    		return shovelStr;
    	}
    	else
    	{
    		return pickStr;
    	}
    }

    @Override
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	return super.canHarvestBlock( stack, world, block, i, j, k ) || 
    		((ShovelItemSteel)(BTWItems.steelShovel)).canHarvestBlock( stack, world, block, i, j, k );
    }
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
    	return super.isEfficientVsBlock(stack, world, block, i, j, k) ||
               ((ShovelItemSteel)(BTWItems.steelShovel)).isEfficientVsBlock(stack, world, block, i, j, k);
    }    
}