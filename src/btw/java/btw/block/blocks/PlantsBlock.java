// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public abstract class PlantsBlock extends Block
{
    protected PlantsBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );
    }
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
	{
		return null;
	}
	
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
        
		Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
		
		if ( blockBelow != null )
		{
			blockBelow.notifyOfPlantAboveRemoved(world, i, j - 1, k, this);
		}
    }
    
    @Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
    	// don't slow down movement in air as it affects the ability of entities to jump up blocks
    	
    	if (entity.isAffectedByMovementModifiers() && entity.onGround )
    	{
	        entity.motionX *= 0.8D;
	        entity.motionZ *= 0.8D;
    	}
    }
    
    @Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        return super.canPlaceBlockAt( world, i, j, k ) && canGrowOnBlock(world, i, j - 1, k);
    }
    
    @Override
    public boolean canBlockStay( World world, int i, int j, int k )
    {
        return canGrowOnBlock(world, i, j - 1, k);
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, 
    	int iFacing, float fXClick, float fYClick, float fZClick )
    {
		if (getWeedsGrowthLevel(world, i, j, k) > 0 )
		{
            if ( !world.isRemote )
            {
            	removeWeeds(world, i, j, k);
            	
            	// block break FX            	
                world.playAuxSFX( 2001, i, j, k, Block.crops.blockID + ( 6 << 12 ) );
            }
            
        	return true;
		}
		
		return false;
    }
	
	@Override
	public int getWeedsGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iBlockBelowID = blockAccess.getBlockId( i, j - 1, k );
		Block blockBelow = Block.blocksList[iBlockBelowID];
		
		if ( blockBelow != null && iBlockBelowID != blockID )
		{
			return blockBelow.getWeedsGrowthLevel(blockAccess, i, j - 1, k);
		}
		
		return 0;
	}
	
	@Override
	public void removeWeeds(World world, int i, int j, int k)
	{
		Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
		
		if ( blockBelow != null )
		{
			blockBelow.removeWeeds(world, i, j - 1, k);
		}
	}
	
	@Override
	public boolean attemptToApplyFertilizerTo(World world, int i, int j, int k)
	{
		// relay to the block below so that applying fertilizer to the plant will
		// fertilize the soil
		
		Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
		
		if ( blockBelow != null )
		{
			return blockBelow.attemptToApplyFertilizerTo(world, i, j - 1, k);
		}
		
		return false;
	}
	
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k ) && 
    		blockID != Block.waterlily.blockID;
    }
    
	@Override
    public void clientBreakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata)
    {
		// destroy any ground cover on this block as well to prevent it momentarily "popping" and appearing above the block 
		
		WorldUtils.clearAnyGroundCoverOnBlock(world, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	Block blockOn = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	return blockOn != null && blockOn.canWildVegetationGrowOnBlock(world, i, j, k);
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
    		renderer.blockAccess, i, j, k) );
        
    	renderer.renderCrossedSquares( this, i, j, k );
    	
    	BTWBlocks.weeds.renderWeeds(this, renderer, i, j, k);
		
		return true;
    }    
}
