// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class ChiselItemStone extends ChiselItem
{
    public ChiselItemStone(int iItemID )
    {
        super( iItemID, EnumToolMaterial.STONE, 8 );
        
        setFilterableProperties(Item.FILTERABLE_SMALL);
        
        efficiencyOnProperMaterial /= 2;
        
        setUnlocalizedName( "fcItemChiselStone" );        
    }
    
    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	float fStrength = super.getStrVsBlock( stack, world, block, i, j, k );
    	
    	if ( block.blockID == Block.web.blockID || block.blockID == BTWBlocks.web.blockID )
    	{
    		// boost stone chisels against webs so that it reads a little better
    		
    		fStrength *= 2;
    	}
    	
    	return fStrength;
    }
}
