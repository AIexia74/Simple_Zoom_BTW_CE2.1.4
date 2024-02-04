// FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import net.minecraft.src.World;

public class WoolSlabBlockItem extends SlabBlockItem
{
    public WoolSlabBlockItem( int i )
    {
        super( i );
        
        setHasSubtypes( true );
        
        setUnlocalizedName( "fcBlockWoolSlab" );
    }

    @Override
    public int getMetadata( int i )
    {
    	return i;
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
        if ( iFacing == 0 || iFacing != 1 && (double)fClickY > 0.5D )
        {
			return BTWBlocks.woolSlabTop.blockID;
        }
        
		return BTWBlocks.woolSlab.blockID;
    }
    
    @Override
    public boolean canCombineWithBlock(World world, int i, int j, int k, int iItemDamage )
    {
        int iBlockID = world.getBlockId( i, j, k );
        int iBlockMetadata = world.getBlockMetadata( i, j, k );
        
        if ( ( iBlockID == BTWBlocks.woolSlab.blockID || iBlockID == BTWBlocks.woolSlabTop.blockID ) &&
    		iBlockMetadata == iItemDamage )
        {
        	return true;
        }
        
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
}
