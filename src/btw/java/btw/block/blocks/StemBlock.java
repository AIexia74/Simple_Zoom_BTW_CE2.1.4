// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class StemBlock extends BlockStem
{
	private final static int STEM_TICK_RATE = 2;
	
	private static final double WIDTH = 0.25D;
	private static final double HALF_WIDTH = (WIDTH / 2D );
	
    public StemBlock(int iBlockID, Block fruitBlock)
    {
    	super( iBlockID, fruitBlock );
    	
    	setHardness( 0F );
    	
    	setBuoyant();
    	
        initBlockBounds(0.5F - HALF_WIDTH, 0F, 0.5F - HALF_WIDTH,
						0.5F + HALF_WIDTH, 0.25F, 0.5F + HALF_WIDTH);
        
        setStepSound( soundGrassFootstep );
        
        setUnlocalizedName( "pumpkinStem" );        
    }
    
	@Override
    public int tickRate( World world )
    {
    	return STEM_TICK_RATE;
    }
	
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {    	
        checkFlowerChange( world, i, j, k );  // replaces call to super function two levels up
        
        // necessary because checkFlowerChange() may destroy the stem
        if ( world.getBlockId( i, j, k ) == blockID ) 
        {
        	validateFruitState(world, i, j, k, rand);
        }
    }
    
    @Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
    	updateTick( world, i, j, k, rand );
    	
    	 // necessary to check blockID because udateTick() may destroy the stem
    	
        if ( world.getBlockId( i, j, k ) == blockID && world.provider.dimensionId != 1 )
        {
        	checkForGrowth(world, i, j, k, rand);
        }        
    }
    
	@Override
    public boolean onBlockSawed(World world, int i, int j, int k)
    {
		return false;
    }
	
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
		// override to prevent stems from dropping seeds
    }
	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k )
	{
		// override to deprecate parent method
	}
	
	@Override
    public void setBlockBoundsForItemRender()
    {
		// override to deprecate parent method
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		
		if ( iMetadata == 15 && !hasConnectedFruit(blockAccess, i, j, k) )
		{
			// necessary to avoid brief visual glitch on pumpkin harvest
			
			iMetadata = 7;
		}
		
        double dMaxY = (double)( iMetadata + 1 ) / 16D;
        
        if ( dMaxY < 0.125 )
        {
        	dMaxY = 0.125;
        }
        
        double dHalfWidth = HALF_WIDTH;
    	int iWeedsGrowthLevel = getWeedsGrowthLevel(blockAccess, i, j, k);
    	
    	if ( iWeedsGrowthLevel > 0 )
    	{
    		dMaxY = Math.max( dMaxY, 
    			WeedsBlock.getWeedsBoundsHeight(iWeedsGrowthLevel));
    		
    		dHalfWidth = WeedsBlock.WEEDS_BOUNDS_HALF_WIDTH;
    	}
    	
    	return AxisAlignedBB.getAABBPool().getAABB(         	
    		0.5D - dHalfWidth, 0D, 0.5D - dHalfWidth, 
        	0.5D + dHalfWidth, dMaxY, 0.5D + dHalfWidth );        
    }

	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		super.onNeighborBlockChange( world, i, j, k, iBlockID );
		
		// necessary because super.onNeighborBlockChange() may destroy the stem
        if ( world.getBlockId( i, j, k ) == blockID ) 
        {
        	validateFruitState(world, i, j, k, world.rand);
        }
    }
	
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
								 EntityAnimal animal)
    {
		return true;
    }
    
    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	Block blockOn = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	return blockOn != null && blockOn.canDomesticatedCropsGrowOnBlock(world, i, j, k);
    }
    
	@Override
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}	
	
    //------------- Class Specific Methods ------------//
    
	public void setFruitBlock(Block block)
	{
		fruitType = block;
	}
	
	private boolean hasConnectedFruit(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getConnectedFruitDirection(blockAccess, i, j, k) > 0;
	}
	
	private int getConnectedFruitDirection(IBlockAccess blockAccess, int i, int j, int k)
	{
		for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
		{
    		BlockPos targetPos = new BlockPos( i, j, k );
    		
    		targetPos.addFacingAsOffset(iTempFacing);
    		
    		int iTempBlockID = blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    		
    		if ( iTempBlockID == fruitType.blockID && 
    			fruitType.isBlockAttachedToFacing(blockAccess, targetPos.x, targetPos.y, targetPos.z, Block.getOppositeFacing(iTempFacing)) )
    		{
    			return iTempFacing;
    		}
		}
        
        return -1;
	}
	
	private void validateFruitState(World world, int i, int j, int k, Random rand)
	{
        int iMetadata = world.getBlockMetadata( i, j, k );
        
		if ( iMetadata == 15 && !hasConnectedFruit(world, i, j, k) )
		{
			// reset to earlier growth stage
			
            world.setBlockMetadataWithNotify( i, j, k, 7 );
		}
	}
	
	private void checkForGrowth(World world, int i, int j, int k, Random rand)
	{
        if (getWeedsGrowthLevel(world, i, j, k) == 0 &&
        	world.getBlockLightValue( i, j + 1, k ) >= 9 )
        {
	        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
	        
	        if ( blockBelow != null && 
	        	blockBelow.isBlockHydratedForPlantGrowthOn(world, i, j - 1, k) )
	        {
	    		float fGrowthChance = 0.2F * 
	    			blockBelow.getPlantGrowthOnMultiplier(world, i, j - 1, k, this);
	    		
	            if ( rand.nextFloat() <= fGrowthChance )
	            {
	                int iMetadata = world.getBlockMetadata( i, j, k );
	
	                if ( iMetadata < 14 )
	                {
	                    ++iMetadata;
	                    
	                    world.setBlockMetadataWithNotify( i, j, k, iMetadata );                    
	                }
	                else if ( iMetadata == 14 )
	                {
	                    BlockPos targetPos = new BlockPos( i, j, k );
	                    int iTargetFacing = 0;
	                    
	                    if ( hasSpaceToGrow(world, i, j, k) )
	                    {
	                    	// if the plant doesn't have space around it to grow, 
	                    	// the fruit will crush its own stem
	                    	
		                    iTargetFacing = rand.nextInt( 4 ) + 2;
		                	
		                    targetPos.addFacingAsOffset(iTargetFacing);
	                    }
	                    
	                    if ( canGrowFruitAt(world, targetPos.x, targetPos.y, targetPos.z) )
	                    {	
	                    	blockBelow.notifyOfFullStagePlantGrowthOn(world, i, j - 1, k, this);
	                    	
	                    	world.setBlockWithNotify(targetPos.x, targetPos.y, targetPos.z,
													 fruitType.blockID );
	                    	
	                    	if ( iTargetFacing != 0 )
	                    	{
	                    		fruitType.attachToFacing(world,
														 targetPos.x, targetPos.y, targetPos.z,
														 Block.getOppositeFacing(iTargetFacing));
	                    		
	                            world.setBlockMetadataWithNotify( i, j, k, 15 );
	                    	}   
	                    }
	                }
	            }
	        }
        }
	}
    
    protected boolean hasSpaceToGrow(World world, int i, int j, int k)
    {
        for ( int iTargetFacing = 2; iTargetFacing <= 5; iTargetFacing++ )
        {
        	BlockPos targetPos = new BlockPos( i, j, k );
        	
            targetPos.addFacingAsOffset(iTargetFacing);
            
            if ( canGrowFruitAt(world, targetPos.x, targetPos.y, targetPos.z) )
            {
            	return true;
            }
        }
        
        return false;
    }
    
    protected boolean canGrowFruitAt(World world, int i, int j, int k)
    {
		int iBlockID = world.getBlockId( i, j, k );		
		Block block = Block.blocksList[iBlockID];
		
        if (WorldUtils.isReplaceableBlock(world, i, j, k) ||
			( block != null && block.blockMaterial == Material.plants &&
    		iBlockID != Block.cocoaPlant.blockID ) )
        {
			// CanGrowOnBlock() to allow fruit to grow on tilled earth and such
			if (world.doesBlockHaveSolidTopSurface( i, j - 1, k ) ||
				canGrowOnBlock(world, i, j - 1, k) )
            {				
				return true;
            }
        }
        
        return false;
    }    
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	renderer.renderBlockStem( this, i, j, k );
    	
    	BTWBlocks.weeds.renderWeeds(this, renderer, i, j, k);
		
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getRenderColor( int iMetadata )
    {
        int iRed = iMetadata * 16;
        int iGreen = 255 - iMetadata * 4;
        int iBlue = iMetadata * 2;
        
        return iRed << 16 | iGreen << 8 | iBlue;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier( IBlockAccess blockAccess, int i, int j, int k )
    {
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		
		if ( iMetadata == 15 && !hasConnectedFruit(blockAccess, i, j, k) )
		{
			// necessary to avoid brief visual glitch on pumpkin harvest
			
			iMetadata = 7;
		}
		
        return getRenderColor( iMetadata );
    }    
    
	/**
     * Returns the current state of the stem. Returns -1 if the stem is not fully grown, or a value between 0 and 3
     * based on the direction the stem is facing.  Only used in rendering on client.
     */
    @Override
    @Environment(EnvType.CLIENT)
    public int getState( IBlockAccess blockAccess, int i, int j, int k )
	{
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		 
		if ( iMetadata == 15 )
		{
	 		int iFruitDirection = getConnectedFruitDirection(blockAccess, i, j, k);
	 		
	 		if ( iFruitDirection > 0 )
	 		{
	 			if ( iFruitDirection == 2 )
	 			{
	 				return 2;
	 			}
	 			else if ( iFruitDirection == 3 )
	 			{
	 				return 3;
	 			}
	 			else if ( iFruitDirection == 4 )
	 			{
	 				return 0;
	 			}
	 			else if ( iFruitDirection == 5 )
	 			{
	 				return 1;
	 			}
	 		}	 		
		}
 
		return -1;
	}
}