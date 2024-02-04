// FCMOD

package btw.item.blockitems;

import net.minecraft.src.World;

public class LooseSnowSlabBlockItem extends SlabBlockItem
{
    public LooseSnowSlabBlockItem(int iItemID )
    {
        super( iItemID );        
    }
    
    @Override
    public boolean canCombineWithBlock(World world, int i, int j, int k, int iItemDamage )
    {
        int iBlockID = world.getBlockId( i, j, k );
        
        if ( iBlockID == getBlockID() )
        {
        	return true;
        }
        
    	return false;
    }
}
