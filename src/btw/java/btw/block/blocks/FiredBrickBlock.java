// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class FiredBrickBlock extends Block
{
	public static final double BRICK_HEIGHT = (4D / 16D );
	public static final double BRICK_WIDTH = (6D / 16D );
	public static final double BRICK_HALF_WIDTH = (BRICK_WIDTH / 2D );
	public static final double BRICK_LENGTH = (12D / 16D );
	public static final double BRICK_HALF_LENGTH = (BRICK_LENGTH / 2D );

    public FiredBrickBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits );
        
        setHardness( 0F );
        setPicksEffectiveOn(true);
        
        setStepSound( soundStoneFootstep );        
        
        setUnlocalizedName( "fcBlockCookedBrick" );
    }
    
	@Override
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setIAligned(iMetadata, isFacingIAligned(iFacing));
    }
    
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityLiving);
		
		setIAligned(world, i, j, k, isFacingIAligned(iFacing));
	}	
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
		return Item.brick.itemID;
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return null;
	}
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( getIsIAligned(blockAccess, i, j, k) )
		{
        	return AxisAlignedBB.getAABBPool().getAABB(
					0.5D - BRICK_HALF_LENGTH, 0D, 0.5D - BRICK_HALF_WIDTH,
					0.5D + BRICK_HALF_LENGTH, BRICK_HEIGHT, 0.5D + BRICK_HALF_WIDTH);
		}
		
    	return AxisAlignedBB.getAABBPool().getAABB(
				0.5D - BRICK_HALF_WIDTH, 0D, 0.5D - BRICK_HALF_LENGTH,
				0.5D + BRICK_HALF_WIDTH, BRICK_HEIGHT, 0.5D + BRICK_HALF_LENGTH);
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true);
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
    	{
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify(i, j, k, 0);
    	}
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
	public int getFacing(int iMetadata)
	{
		if ( getIsIAligned(iMetadata) )
		{
			return 4;
		}
		
		return 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return setIAligned(iMetadata, isFacingIAligned(iFacing));
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		return setIAligned(iMetadata, !getIsIAligned(iMetadata));
	}

	//------------- Class Specific Methods ------------//
	
	public void setIAligned(World world, int i, int j, int k, boolean bIAligned)
	{
		int iMetadata = setIAligned(world.getBlockMetadata(i, j, k), bIAligned);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setIAligned(int iMetadata, boolean bIAligned)
	{
		if ( bIAligned )
		{
			iMetadata |= 1;
		}
		else
		{
			iMetadata &= (~1);
		}
		
		return iMetadata;
	}
	
	public boolean getIsIAligned(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsIAligned(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getIsIAligned(int iMetadata)
	{
		return ( iMetadata & 1 ) != 0;
	}
	
	public boolean isFacingIAligned(int iFacing)
	{
		return iFacing >= 4;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		if ( iSide == 0 )
		{
			return RenderUtils.shouldRenderNeighborFullFaceSide(blockAccess,
                                                                iNeighborI, iNeighborJ, iNeighborK, iSide);
		}
		
		return true;
    }
}