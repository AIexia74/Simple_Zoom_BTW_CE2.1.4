// FCMOD

package btw.block.blocks.legacy;

import net.minecraft.src.BlockCarrot;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class LegacyCarrotBlock extends BlockCarrot
{
    public LegacyCarrotBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public float getBaseGrowthChance(World world, int i, int j, int k)
    {
    	return 0.1F;
    }
    
    @Override
    protected void incrementGrowthLevel(World world, int i, int j, int k)
    {
    	int iGrowthLevel = getGrowthLevel(world, i, j, k) + 1;

    	if ( iGrowthLevel == 7 || ( iGrowthLevel & 1 ) == 0 )
    	{
    		super.incrementGrowthLevel(world, i, j, k);
    	}
    	else
    	{    	
    		// only notify surrounding blocks and the client on visible change
    		
            setGrowthLevelNoNotify(world, i, j, k, iGrowthLevel);
    	}    
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
