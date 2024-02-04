// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class FarmlandBlock extends FarmlandBlockBase
{
    public static final int LIGHT_LEVEL_FOR_WEED_GROWTH = 11;

    public FarmlandBlock(int iBlockID)
    {
    	super( iBlockID );    	
        
        setUnlocalizedName( "fcBlockFarmlandNew" );
        
        setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
        
        if (world.getBlockMaterial( i, j + 1, k ).isSolid() ||
			canFallIntoBlockAtPos(world, i, j - 1, k) )
        {
            world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
        }
        else if (getWeedsGrowthLevel(world, i, j, k) > 0 &&
				 !canWeedsShareSpaceWithBlockAt(world, i, j + 1, k) )
        {
        	// the weeds we had above us are no longer possible
        	
			setWeedsGrowthLevel(world, i, j, k, 0);
        }
    }

	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
		super.updateTick( world, i, j, k, rand );
		
		if ( world.getBlockId( i, j, k ) == blockID ) // super can destroy block
		{
			if ( !checkForSnowReversion(world, i, j, k, rand) )
			{
				updateWeedGrowth(world, i, j, k, rand);
			}
		}			
    }
	
    @Override
	public void notifyOfPlantAboveRemoved(World world, int i, int j, int k, Block plantBlock)
	{
    	// don't untill on weed growth
    	
    	if ( world.getBlockId( i, j + 1, k ) != Block.tallGrass.blockID )
    	{
    		world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
    	}
	}
	
    @Override
    protected boolean isHydrated(int iMetadata)
    {
    	// stores decreasing levels of hydration from 3 to 1
    	
    	return ( iMetadata & 1 ) > 0;
    }
    
    @Override
	public int setFullyHydrated(int iMetadata)
    {
    	return iMetadata | 1;
    }
    
	@Override
	protected void dryIncrementally(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );
        int iHydrationLevel = iMetadata & 1;
        
        if ( iHydrationLevel > 0 )
        {
        	iMetadata &= (~1);
        	
            world.setBlockMetadataWithNotify( i, j, k, iMetadata );
        }
	}

	@Override
    protected boolean isFertilized(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}
    
	@Override
    protected void setFertilized(World world, int i, int j, int k)
    {
    	int iTargetBlockMetadata = world.getBlockMetadata( i, j, k );
    	
    	world.setBlockAndMetadataWithNotify( i, j, k, 
    		BTWBlocks.fertilizedFarmland.blockID, iTargetBlockMetadata );
    }
    
	@Override
	public int getWeedsGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getWeedsGrowthLevel(blockAccess.getBlockMetadata(i, j, k));
	}
	
	@Override
	public void removeWeeds(World world, int i, int j, int k)
	{
		setWeedsGrowthLevel(world, i, j, k, 0);
	}
	
	@Override
	protected void checkForSoilReversion(World world, int i, int j, int k)
	{
		// slow down soil reversion since we don't have the metadata fidelity of vanilla
		// tilledEarth
		
		if ( world.rand.nextInt( 8 ) == 0 )
		{
			super.checkForSoilReversion(world, i, j, k);
		}
	}
	
	@Override
    protected int getHorizontalHydrationRange(World world, int i, int j, int k)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords( i, k );
        
        if ( biome.getEnableSnow() || biome.canRainInBiome() )
        {
        	return 4;
        }
        
        return 2;
    }
    
    //------------- Class Specific Methods ------------//
    
	public boolean canWeedsShareSpaceWithBlockAt(World world, int i, int j, int k)
	{
		Block block = Block.blocksList[world.getBlockId( i, j, k )];
		
		if ( block != null )
		{
			return block.canWeedsGrowInBlock(world, i, j, k);
		}
		
		return false;
	}

	protected int getWeedsGrowthLevel(int iMetadata)
	{
		return ( iMetadata & 14 ) >> 1;
	}
	
	protected void setWeedsGrowthLevel(World world, int i, int j, int k, int iGrowthLevel)
	{
		int iMetadata = setWeedsGrowthLevel(world.getBlockMetadata(i, j, k), iGrowthLevel);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	protected int setWeedsGrowthLevel(int iMetadata, int iGrowthLevel)
	{
		iMetadata &= (~14); // filter out old level
		
		return iMetadata | ( iGrowthLevel << 1 );
	}

	/**
	 * Returns true if the block has been reverted to loose dirt
	 */
    public boolean checkForSnowReversion(World world, int i, int j, int k, Random rand)
    {
    	if (world.isSnowingAtPos(i, j + 1, k) && rand.nextInt(2) == 0 )
    	{
    		if ( world.getSavedLightValue( EnumSkyBlock.Block, i, j + 1, k ) < 10  )
    		{
	    		world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
	    		
	    		if ( Block.snow.canPlaceBlockAt( world, i, j + 1, k ) )
	    		{
	    			world.setBlockWithNotify( i, j + 1, k, Block.snow.blockID );
	    		}
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
    
    public void updateWeedGrowth(World world, int i, int j, int k, Random rand)
    {
		if ( world.getBlockId( i, j, k ) == blockID )
		{
			int iWeedsLevel = getWeedsGrowthLevel(world, i, j, k);
	        int iTimeOfDay = (int)( world.worldInfo.getWorldTime() % 24000L );
			
			if ( world.isAirBlock( i, j + 1, k ) )
			{
		        if ( iTimeOfDay > 14000 && iTimeOfDay < 22000 )
		        {
		        	// night
		        	
	        		if ( rand.nextInt( 20 ) == 0 &&
						 world.getBlockNaturalLightValueMaximum(i, j + 1, k) >= LIGHT_LEVEL_FOR_WEED_GROWTH)
	        		{
	        			// only start to grow on empty earth if there's potential for natural light
	        			// to avoid weirdness with weeds popping up deep underground and such
	        			
		        		world.setBlockWithNotify( i, j + 1, k, 
		        			BTWBlocks.weeds.blockID );
		        		
		        		setWeedsGrowthLevel(world, i, j, k, 1);
	        		}
		        }
			}
			else if ( canWeedsShareSpaceWithBlockAt(world, i, j + 1, k) )
			{
		        if ( iTimeOfDay > 14000 && iTimeOfDay < 22000 )
		        {
		        	// night
		        	
		        	if ( iWeedsLevel == 0  )
		        	{
		        		if ( rand.nextInt( 20 ) == 0 )
		        		{
			        		setWeedsGrowthLevel(world, i, j, k, 1);
		        		}
		        	}
		        	else if ( iWeedsLevel % 2 == 0 )
		        	{
		        		// weeds are only allowed to grow one stage per day, so this flags
		        		// them for the next day's growth.
		        		
		        		setWeedsGrowthLevel(world, i, j, k, iWeedsLevel + 1);
		        	}
		        }
		        else
		        {
		        	// day(ish)
		        	
		        	if (world.getBlockNaturalLightValue(i, j + 1, k) >= LIGHT_LEVEL_FOR_WEED_GROWTH)
		        	{
			        	if (iWeedsLevel == 7 && world.getDifficulty().canWeedsKillPlants())
			        	{
			        		setWeedsGrowthLevel(world, i, j, k, 0);
	
			        		world.setBlockAndMetadataWithNotify( i, j + 1, k, Block.tallGrass.blockID, 
			        			1 );
			        	}
			        	else if ( iWeedsLevel % 2 == 1 )
			        	{
			        		setWeedsGrowthLevel(world, i, j, k, iWeedsLevel + 1);
			        	}
		        	}
		        }
			}
			else if ( iWeedsLevel > 0 )
			{
				setWeedsGrowthLevel(world, i, j, k, 0);
			}
		}
    }
    
	//----------- Client Side Functionality -----------//
}
