// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.MetalSpikeModel;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class MetalSpikeBlock extends Block
{
	protected static MetalSpikeModel model = new MetalSpikeModel();
	
    public MetalSpikeBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 2F );     
        setPicksEffectiveOn();
        
		initBlockBounds(model.boxCollisionCenter);
		
        setStepSound( soundMetalFootstep );        
        
        setUnlocalizedName( "fcBlockSpikeIron" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
		// computes the bounds strictly from metadata, since we can't rely on neighboring blocks
		// to determine shape during chunk load
		
		int iFacing = getFacing(world, i, j, k);
		
		if ( iFacing >= 2 )
		{
			AxisAlignedBB tempBox = model.boxCollisionStrut.makeTemporaryCopy();
			
			tempBox.rotateAroundYToFacing(iFacing);
			
			tempBox.offset( i, j, k );
			
			return tempBox;
		}
		
    	return model.boxCollisionCenter.makeTemporaryCopy().offset(i, j, k);
	}
	
    @Override
    public MovingObjectPosition collisionRayTrace( World world, 
    	int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	BlockModel m_modelTransformed = new BlockModel();

    	// use the center collision area instead of just the raw model to give the player
    	// a larger surface to click on the top and bottom
    	
    	m_modelTransformed.addPrimitive(model.boxCollisionCenter.makeTemporaryCopy());
    	
    	BlockModel transformedSupportsModel =
    		getSideSupportsTemporaryModel(world, i, j, k);
    	
    	if ( transformedSupportsModel != null )
    	{
    		transformedSupportsModel.makeTemporaryCopyOfPrimitiveList(m_modelTransformed);
    	}
    	
    	return m_modelTransformed.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
    @Override
    public boolean canPlaceBlockOnSide( World world, int i, int j, int k, int iSide )
    {
    	return canConnectToBlockToFacing(world, i, j, k, Block.getOppositeFacing(iSide));
    }

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		return setFacing(iMetadata, Block.getOppositeFacing(iFacing));
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
        
		if ( !canConnectToBlockToFacing(world, i, j, k, getFacing(world, i, j, k)) )
		{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
		}
    }
	
	@Override
	public int getFacing(int iMetadata)
	{		
    	return iMetadata & 7;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= ~7;
		
        return iMetadata | iFacing;
	}
	
	@Override
	public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k,
												   int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing < 2;
	}
	
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
	//------------- Class Specific Methods ------------//
	
	protected boolean canConnectToBlockToFacing(IBlockAccess blockAccess,
												int i, int j, int k, int iFacing)
	{
		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
		
		return WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(blockAccess,
																	targetPos.x, targetPos.y, targetPos.z, Block.getOppositeFacing(iFacing)) ||
			   blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z) == blockID;
	}
	
	protected boolean isConnectedSpikeToFacing(IBlockAccess blockAccess,
											   int i, int j, int k, int iFacing)
	{
		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
		
		return blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z) == blockID &&
			   getFacing(blockAccess, targetPos.x, targetPos.y, targetPos.z) ==
			   Block.getOppositeFacing(iFacing);
	}
	
	/**
	 * Relies on horizontally neighboring blocks, so may not be accurate during chunk load
	 * and thus should not be used for collision volume
	 */
	BlockModel getSideSupportsTemporaryModel(IBlockAccess blockAccess, int i, int j, int k)
	{
    	BlockModel supportsModel = null;
    	int iBlockFacing = getFacing(blockAccess, i, j, k);
    		
    	for ( int iTempFacing = 2; iTempFacing <= 5; iTempFacing++ )
    	{
    		if ( iTempFacing == iBlockFacing ||
				 isConnectedSpikeToFacing(blockAccess, i, j, k, iTempFacing) )
    		{
    			BlockModel tempSupportsModel = model.modelSideSupport.makeTemporaryCopy();
    			
    			tempSupportsModel.rotateAroundYToFacing(iTempFacing);
	    		
    			if ( supportsModel == null )
    			{
    				supportsModel = tempSupportsModel;
    			}
    			else
    			{
    				tempSupportsModel.makeTemporaryCopyOfPrimitiveList(supportsModel);
    			}
    		}
    	}
    	
		return supportsModel;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	int iFacing = getFacing(blockAccess, i, j, k);

    	// side supports

    	BlockModel transformedSupportsModel =
    		getSideSupportsTemporaryModel(blockAccess, i, j, k);
    	
    	BlockModel transformedCenterModel = model.makeTemporaryCopy();
    	
    	// base
    	
    	if ( ( iFacing == 0 && blockAccess.getBlockId( i, j - 1, k ) != blockID ) ||
    		( iFacing == 1 && blockAccess.getBlockId( i, j + 1, k ) != blockID ) )
    	{
        	if ( !WorldUtils.isGroundCoverOnBlock(renderBlocks.blockAccess, i, j, k) )
        	{
        		model.modelBase.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
        	}
    	}

    	// ball on top or holding platform
    	
    	if ( iFacing == 1 )
    	{
			int iBlockBelowID = blockAccess.getBlockId( i, j - 1, k );
			
			if ( iBlockBelowID != blockID && transformedSupportsModel == null )
			{
				model.modelBall.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
			}
    	}
    	else
    	{
			int iBlockAboveID = blockAccess.getBlockId( i, j + 1, k );
			
			if ( iBlockAboveID != blockID )
			{
				if ( WorldUtils.isBlockRestingOnThatBelow(blockAccess, i, j + 1, k) )
				{
					Block block = Block.blocksList[blockAccess.getBlockId(i, j + 1, k)];

					// TODO: Should probably have a hook in Block
					if (block instanceof CandleBlock && ((CandleBlock) block).getCandleCount(blockAccess, i, j + 1, k) > 1) {
						model.holderModelLarge.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
					}
					else {
						model.modelHolder.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
					}
				}
				else if ( transformedSupportsModel == null )
				{
					model.modelBall.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
				}
			}
    	}
    	
    	if ( iFacing == 1 )
    	{
    		transformedCenterModel.tiltToFacingAlongY(0);
    	}
    	
    	if ( transformedSupportsModel != null )
    	{
    		transformedSupportsModel.renderAsBlock(renderBlocks, this, i, j, k);
    		
			model.modelCenterBrace.makeTemporaryCopyOfPrimitiveList(transformedCenterModel);
    	}
		
		return transformedCenterModel.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}
