// FCMOD

package btw.block.blocks.legacy;

import net.minecraft.src.BlockCrops;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class LegacyWheatBlock extends BlockCrops
{
    public LegacyWheatBlock(int iBlockID)
    {
    	super( iBlockID );    	
    }
    
	@Override
    public boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k)
    {
		return true;
    }
	
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {
		// only retrieve if fully grown
    	
		if ( world.getBlockMetadata( i, j, k ) >= 7 )
		{
			return super.getStackRetrievedByBlockDispenser(world, i, j, k);
		}
    	
    	return null;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
