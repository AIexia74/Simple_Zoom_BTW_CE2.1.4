// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.item.items.ToolItem;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class PlacedShaftBlock extends Block
{
	protected static final double SHAFT_WIDTH = 0.125D;
	protected static final double SHAFT_HALF_WIDTH = (SHAFT_WIDTH / 2D );
	protected static final double SHAFT_HEIGHT = 0.75D;

	protected static final double SELECTION_BOX_WIDTH = (SHAFT_WIDTH + (2D / 16D ) );
	protected static final double SELECTION_BOX_HALF_WIDTH = (SELECTION_BOX_WIDTH / 2D );
	protected static final double SELECTION_BOX_HEIGHT = SHAFT_HEIGHT + (1D / 16D );
	
    private static final AxisAlignedBB boxShaft = new AxisAlignedBB(
			0.5F - SHAFT_HALF_WIDTH, 1F - SHAFT_HEIGHT, 0.5F - SHAFT_HALF_WIDTH,
			0.5F + SHAFT_HALF_WIDTH, 1F, 0.5F + SHAFT_HALF_WIDTH);
    
    private static final AxisAlignedBB boxShaftSupporting = new AxisAlignedBB(
			0.5F - SHAFT_HALF_WIDTH, 0F, 0.5F - SHAFT_HALF_WIDTH,
			0.5F + SHAFT_HALF_WIDTH, 1F, 0.5F + SHAFT_HALF_WIDTH);
	
    private static final AxisAlignedBB boxSelection = new AxisAlignedBB(
			0.5F - SELECTION_BOX_HALF_WIDTH, 1F - SELECTION_BOX_HEIGHT, 0.5F - SELECTION_BOX_HALF_WIDTH,
			0.5F + SELECTION_BOX_HALF_WIDTH, 1F, 0.5F + SELECTION_BOX_HALF_WIDTH);
    
    private static final AxisAlignedBB boxSelectionSupporting = new AxisAlignedBB(
			0.5F - SELECTION_BOX_HALF_WIDTH, 0F, 0.5F - SELECTION_BOX_HALF_WIDTH,
			0.5F + SELECTION_BOX_HALF_WIDTH, 1F, 0.5F + SELECTION_BOX_HALF_WIDTH);
	
    public PlacedShaftBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits );

        setHardness( 0F );
        setResistance( 0F );

        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockShaft" );
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
    public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int iSide )
    {
		BlockPos targetPos = new BlockPos( i, j, k, Block.getOppositeFacing(iSide) );
		
		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z, iSide) )
		{
			int iTargetID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
			
			if ( canStickInBlockType(iTargetID) )
			{
				return true;
			}
		}
    	
        return false;
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		iMetadata = setFacing(iMetadata, Block.getOppositeFacing(iFacing));
		
		BlockPos anchorPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
		int iAnchorID = world.getBlockId(anchorPos.x, anchorPos.y, anchorPos.z);
		Block anchorBlock = Block.blocksList[iAnchorID];
		
		if ( anchorBlock != null )
		{		
			world.playSoundEffect( (float)i + 0.5F, (float)j - 0.5F, (float)k + 0.5F, anchorBlock.stepSound.getPlaceSound(), 
				anchorBlock.stepSound.getPlaceVolume() / 2.0F, anchorBlock.stepSound.getStepPitch() * 0.8F );
			
			if ( !world.isRemote )
			{
				anchorBlock.onPlayerWalksOnBlock(world, anchorPos.x, anchorPos.y, anchorPos.z, null);
			}				
		}
	    
		return iMetadata;
    }
	
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return Item.stick.itemID;
    }
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
        return null;
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	AxisAlignedBB transformedBox;
        
    	if ( isSupportingOtherBlock(blockAccess, i, j, k) )
    	{
    		transformedBox = boxShaftSupporting.makeTemporaryCopy();
    	}
    	else
    	{
    		transformedBox = boxShaft.makeTemporaryCopy();
    	}
    	
    	transformedBox.tiltToFacingAlongY(getFacing(blockAccess, i, j, k));
		
		return transformedBox;		
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
		int iFacing = getFacing(world, i, j, k);
		
		if ( !canPlaceBlockOnSide( world, i, j, k, Block.getOppositeFacing(iFacing)) )
		{
	    	// pop the block off if it no longer has a valid anchor point
	    	
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify( i, j, k, 0 );
		}
    }
	
	@Override
	public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		// only has upwards facing hardpoint for tiki torches
		
		return iFacing == 1 && getFacing(blockAccess, i, j, k) == 0;
	}
	
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	if ( !world.isRemote )
    	{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );    		
    	}
    }
    
	@Override
	public int getFacing(int iMetadata)
	{		
		// shaft facing is in the direction of attachment
		
    	return iMetadata & 7;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= ~7;
		
        return iMetadata | iFacing;
	}
	
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		return iFacing == getFacing(world, i, j, k);
    }
    
	@Override
    public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iInitialFacing, int iRotatedFacing)
    {
		int iOldMetadata = world.getBlockMetadata( i, j, k );
		
		return setFacing(iOldMetadata, iRotatedFacing);
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
    
	@Override
	public void onNeighborDisrupted(World world, int i, int j, int k, int iToFacing)
	{
		if (iToFacing == getFacing(world, i, j, k) )
		{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
		}
	}
		
    //------------- Class Specific Methods ------------//    
	
    public boolean canStickInBlockType(int iBlockID)
    {
    	Block block = Block.blocksList[iBlockID];
    	
    	return block != null && ((ToolItem)(Item.shovelWood)).isToolTypeEfficientVsBlockType(block);
    }   
    
    public boolean isSupportingOtherBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	if (getFacing(blockAccess, i, j, k) == 0 && WorldUtils.isBlockRestingOnThatBelow(blockAccess, i, j + 1, k) )
    	{
			return true;
    	}
    	
    	return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return Item.stick.itemID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		BlockPos myPos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK,
			Block.getOppositeFacing(iSide) );
		
    	int iFacing = getFacing(blockAccess, myPos.x, myPos.y, myPos.z);
    	
    	if ( iSide == iFacing )
    	{
	   		return !RenderUtils.shouldRenderNeighborFullFaceSide(
	   			blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
		}
    	
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {    	
    	AxisAlignedBB transformedBox;
        
    	if ( isSupportingOtherBlock(renderBlocks.blockAccess, i, j, k) )
    	{
    		transformedBox = boxShaftSupporting.makeTemporaryCopy();
    	}
    	else
    	{
    		transformedBox = boxShaft.makeTemporaryCopy();
    	}
    	
    	transformedBox.tiltToFacingAlongY(getFacing(renderBlocks.blockAccess, i, j, k));
		
    	
		return transformedBox.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	AxisAlignedBB transformedBox;
        
    	if ( isSupportingOtherBlock(world, i, j, k) )
    	{
    		transformedBox = boxSelectionSupporting.makeTemporaryCopy();
    	}
    	else
    	{
    		transformedBox = boxSelection.makeTemporaryCopy();
    	}
    	
    	transformedBox.tiltToFacingAlongY(getFacing(world, i, j, k));
		
		return transformedBox.offset( i, j, k );		
    }
}