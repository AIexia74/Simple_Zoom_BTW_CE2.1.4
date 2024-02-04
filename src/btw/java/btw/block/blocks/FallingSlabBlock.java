// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class FallingSlabBlock extends SlabBlock
{
    public FallingSlabBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );
    }
    
	@Override
    public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		if ( entity.blockID == blockID && !getIsUpsideDown(world, i, j, k) )
		{
			convertToFullBlock(world, i, j, k);
					
			return true;
		}
		
		return false;
	}

    @Override
    public boolean isFallingBlock()
    {
    	return true;
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k ) 
    {
    	scheduleCheckForFall(world, i, j, k);
    }
    
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID ) 
    {    	
    	scheduleCheckForFall(world, i, j, k);
    }

    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) 
    {    	
        if ( !checkForFall(world, i, j, k) )
        {
        	if ( getIsUpsideDown(world, i, j, k) )
        	{
        		setIsUpsideDown(world, i, j, k, false);
        	}
        }
    }
    
    @Override
    public int tickRate( World world )
    {
		return FallingBlock.FALLING_BLOCK_TICK_RATE;
    }
    
    @Override
    protected void onStartFalling( EntityFallingSand entity ) 
    {
    	if (getIsUpsideDown(entity.metadata)) {
    		entity.posY += 0.5;
    	}
    	
    	entity.metadata = setIsUpsideDown(entity.metadata, false);
    }
    
    @Override
    public boolean canBePlacedUpsideDownAtLocation(World world, int i, int j, int k)
    {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderWhileFalling(World world, EntityFallingSand entity)
    {
        int iCurrentBlockI = MathHelper.floor_double( entity.posX );
        int iCurrentBlockJ = MathHelper.floor_double( entity.posY );
        int iCurrentBlockK = MathHelper.floor_double( entity.posZ );
        
        int iBlockIDAtLocation = world.getBlockId( iCurrentBlockI, iCurrentBlockJ, iCurrentBlockK );
        
        Block fallingBlock = Block.blocksList[entity.blockID];
        
		if ( iBlockIDAtLocation == entity.blockID )
		{
			if ( entity.posY - (double)iCurrentBlockJ < 0.4D )
			{
				return false;
			}
		}
		else
		{
        	FallingSlabBlock fallingSlab = (FallingSlabBlock)fallingBlock;
        	
        	if (fallingSlab.getCombinedBlockID(entity.metadata) == iBlockIDAtLocation )
        	{
        		return false;
        	}
		}
		
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
    {
        renderBlocks.setRenderBounds(getBlockBoundsFromPoolFromMetadata(iMetadata));
        
        renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
    }
}