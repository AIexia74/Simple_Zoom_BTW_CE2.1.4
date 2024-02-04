// FCMOD

package btw.block.blocks;

import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.PlaceableAsItem;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class PlacedToolBlock extends BlockContainer
{
	private static final double BOUNDING_THICKNESS = (1D / 8D );
	private static final double BOUNDING_HALF_THICKNESS = (BOUNDING_THICKNESS / 2D );

    public PlacedToolBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits );
        
        setHardness( 0.05F );        
		
        setStepSound( soundWoodFootstep );        
        
        setUnlocalizedName( "fcBlockToolPlaced" );
    }
    
	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
    @Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return world.isAirBlock(x, y, z);
	}
    
	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new PlacedToolTileEntity();
    }

	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		PlacedToolTileEntity tileEntity = (PlacedToolTileEntity)world.getBlockTileEntity( i, j, k );
        
        tileEntity.ejectContents();
        
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );	        
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
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
	{
		return 0;
	}
	
	@Override
    protected boolean canSilkHarvest()
    {
        return false;
    }    

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		int iBlockFacing = getAttachedToFacing(world, i, j, k);
		
		BlockPos attachedPos = new BlockPos( i, j, k, iBlockFacing );
		
        if ( !WorldUtils.doesBlockHaveCenterHardpointToFacing(world, attachedPos.x, attachedPos.y, attachedPos.z,
															  Block.getOppositeFacing(iBlockFacing), true) )
        {
            world.setBlockWithNotify( i, j, k, 0 );
        }
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {    	
		PlacedToolTileEntity tileEntity = (PlacedToolTileEntity)world.getBlockTileEntity( i, j, k );
        
        ItemStack cookStack = tileEntity.getToolStack();
        
		if ( cookStack != null )
		{
			ItemUtils.givePlayerStackOrEjectFavorEmptyHand(player, cookStack, i, j, k);
			
			tileEntity.setToolStack(null);
			
            world.setBlockWithNotify( i, j, k, 0 );
            
			return true;
		}
		
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
		int iMetadata = blockAccess.getBlockMetadata( i, j,k );
		
		int iFacing = getFacing(iMetadata);
		int iLevel = getVerticalOrientation(iMetadata);
		
		float fHeight = 0.75F;
		float fWidth = 0.75F;
		
		PlacedToolTileEntity tileEntity = (PlacedToolTileEntity)blockAccess.getBlockTileEntity( i, j, k );
		
		if ( tileEntity != null )
		{
			ItemStack toolStack = tileEntity.getToolStack();

			if ( toolStack != null )
			{

				fHeight = ((PlaceableAsItem)toolStack.getItem()).getBlockBoundingBoxHeight();
				fWidth = ((PlaceableAsItem)toolStack.getItem()).getBlockBoundingBoxWidth();

			}
		}

		double fXMin = 0.5F - ( fWidth / 2F );
		double fXMax = 0.5F + ( fWidth / 2F );
		
		double fZMin = fXMin;
		double fZMax = fXMax;
		
		double fYMin = fXMin;
		double fYMax = fXMax;
		
		if ( iFacing < 4 )
		{
			fXMin = 0.5F - BOUNDING_HALF_THICKNESS;
			fXMax = 0.5F + BOUNDING_HALF_THICKNESS;
		}
		else
		{
			fZMin = 0.5F - BOUNDING_HALF_THICKNESS;
			fZMax = 0.5F + BOUNDING_HALF_THICKNESS;
		}
		
		if ( iLevel == 0 )
		{
			fYMin = 0F;
			fYMax = fHeight;
		}
		else if ( iLevel == 1 )
		{
			fYMin = 1F - fHeight;
			fYMax = 1F;
		}
		else
		{
			if ( iFacing == 2 )
			{
				fZMin = 0;
				fZMax = fHeight;
			}
			else if ( iFacing == 3 )
			{
				fZMin = 1F - fHeight;
				fZMax = 1F;
			}
			else if ( iFacing == 4 )
			{
				fXMin = 0;
				fXMax = fHeight;
			}
			else if ( iFacing == 5 )
			{
				fXMin = 1F - fHeight;
				fXMax = 1F;
			}
		}
		
    	return AxisAlignedBB.getAABBPool().getAABB( fXMin, fYMin, fZMin, fXMax, fYMax, fZMax );
    }
	
	@Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
		return ( iMetadata & 3 ) + 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= ~3; // filter out old facing
		
		iMetadata |= MathHelper.clamp_int( iFacing, 2, 5 ) - 2; // convert to flat facing
		
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if (getVerticalOrientation(iMetadata) == 2 )
		{
			return iFacing == getFacing(iMetadata);
		}
		
		return false;
    }
    
	@Override
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		// can't actually rotate around block due to tile entity, so pop the tool off
		
		world.setBlockToAir( i, j, k );
		
    	return false;
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
		if (iToFacing == getAttachedToFacing(world, i, j, k) )
		{
            world.setBlockWithNotify( i, j, k, 0 );
		}
	}
	
	//------------- Class Specific Methods ------------//
	
	/**
	 * 0 = down, 1 = up, 2 = side
	 */
	public int getVerticalOrientation(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getVerticalOrientation(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getVerticalOrientation(int iMetadata)
	{
		return ( iMetadata & 12 ) >> 2;
	}
	
	public void setVerticalOrientation(World world, int i, int j, int k, int iLevel)
	{
		int iMetadata = setVerticalOrientation(world.getBlockMetadata(i, j, k), iLevel);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setVerticalOrientation(int iMetadata, int iLevel)
	{
		iMetadata &= ~12; // filter out old facing
		
		iMetadata |= iLevel << 2;
		
		return iMetadata;
	}
	
	protected int getAttachedToFacing(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iFacing = getVerticalOrientation(blockAccess, i, j, k);
		
		if ( iFacing >= 2 )
		{
			iFacing = getFacing(blockAccess, i, j, k);
		}
		
		return iFacing;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" ); // for hit effects
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }
}