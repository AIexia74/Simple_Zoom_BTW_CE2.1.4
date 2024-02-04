// FCMOD

package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

import java.util.Random;

public abstract class DailyGrowthCropsBlock extends CropsBlock
{
    protected DailyGrowthCropsBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public float getBaseGrowthChance(World world, int i, int j, int k)
    {
    	return 0.4F;
    }
    
    @Override
    protected void attemptToGrow(World world, int x, int y, int z, Random rand) {
        int timeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L);
        
        if (timeOfDay > 14000 && timeOfDay < 22000) {
        	// night
        	if (getHasGrownToday(world, x, y, z)) {
        		setHasGrownToday(world, x, y, z, false);
        	}
        }
        else {
	    	if (!getHasGrownToday(world, x, y, z) && getWeedsGrowthLevel(world, x, y, z) == 0 && canGrowAtCurrentLightLevel(world, x, y, z)) {
		        Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
		        
		        if (blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z)) {
		    		float growthChance = getBaseGrowthChance(world, x, y, z);
		    		
			    	if (blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) {
			    		growthChance *= 2F;
			    	}
			    	
		            if (rand.nextFloat() <= growthChance) {
		            	incrementGrowthLevel(world, x, y, z);
		            	updateFlagForGrownToday(world, x, y, z);
		            }
		        }
		    }
        }
    }
    
    //------------- Class Specific Methods ------------//
    
	protected void updateFlagForGrownToday(World world, int i, int j, int k)
	{
    	// fertilized crops can grow twice in a day
		
        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
        
        if ( blockBelow != null )
        {
	    	if (!blockBelow.getIsFertilizedForPlantGrowth(world, i, j - 1, k) ||
				getGrowthLevel(world, i, j, k) % 2 == 0 )
	    	{
	    		setHasGrownToday(world, i, j, k, true);
	    	}
        }
	}
	
    protected boolean getHasGrownToday(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getHasGrownToday(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected boolean getHasGrownToday(int iMetadata)
    {
    	return ( iMetadata & 8 ) != 0;
    }
    
    protected void setHasGrownToday(World world, int i, int j, int k, boolean bHasGrown)
    {
    	int iMetadata = setHasGrownToday(world.getBlockMetadata(i, j, k), bHasGrown);
    	
    	// intentionally no notify as this does not affect the visual state and should
    	// not trigger Buddy
    	
    	world.setBlockMetadata( i, j, k, iMetadata );
    }
    
    protected int setHasGrownToday(int iMetadata, boolean bHasGrown)
    {
    	if ( bHasGrown )
    	{
    		iMetadata |= 8;
    	}
    	else
    	{
    		iMetadata &= (~8);
    	}
    	
    	return iMetadata;
    }
    
	//----------- Client Side Functionality -----------//
}
