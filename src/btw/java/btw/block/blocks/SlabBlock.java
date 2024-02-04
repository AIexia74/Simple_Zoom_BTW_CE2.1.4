// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public abstract class SlabBlock extends Block
{
    public SlabBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );
        
        initBlockBounds(0F, 0F, 0F, 1F, 0.5F, 1F);
        
        setLightOpacity( 255 );
        
        Block.useNeighborBrightness[iBlockID] = true;
    }

	@Override
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        if ( iFacing == 0 || ( iFacing != 1 && fClickY > 0.5F ) )
        {
    		if ( canBePlacedUpsideDownAtLocation(world, i, j, k) )
    		{
    			iMetadata = setIsUpsideDown(iMetadata, true);
    		}
        }
        
        return iMetadata;
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getBlockBoundsFromPoolFromMetadata(blockAccess.getBlockMetadata(i, j, k));
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
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
    	
    	if ( bIsUpsideDown )
    	{
    		return iFacing == 1;
    	}
    	else
    	{
    		return iFacing == 0;
    	}
	}
    
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
        if ( !getIsUpsideDown(blockAccess, i, j, k) )
        {
        	return -0.5F;
        }
        
    	return 0F;
    }
    
    @Override
    public boolean isSnowCoveringTopSurface(IBlockAccess blockAccess, int i, int j, int k)
    {
        if ( !getIsUpsideDown(blockAccess, i, j, k) )
        {
        	// bottom half slabs are only covered by offset snow ground cover
        	
			return blockAccess.getBlockId( i, j + 1, k ) == Block.snow.blockID;
        }
        
    	return super.isSnowCoveringTopSurface(blockAccess, i, j, k);
    }
    
    @Override
	public boolean hasContactPointToFullFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
    	if ( iFacing < 2 )
    	{
        	boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
        	
        	return bIsUpsideDown == ( iFacing == 1 );
    	}    	
    		
		return true;
	}
	
    @Override
	public boolean hasContactPointToSlabSideFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIsSlabUpsideDown)
	{
		return bIsSlabUpsideDown == getIsUpsideDown(blockAccess, i, j, k);
	}
	
    @Override
    public boolean hasNeighborWithMortarInContact(World world, int i, int j, int k)
    {
    	boolean bIsUpsideDown = getIsUpsideDown(world, i, j, k);
    	
    	return hasNeighborWithMortarInContact(world, i, j, k, bIsUpsideDown);
    }
    
    @Override
    public boolean hasStickySnowNeighborInContact(World world, int i, int j, int k)
    {
    	boolean bIsUpsideDown = getIsUpsideDown(world, i, j, k);
    	
    	return hasStickySnowNeighborInContact(world, i, j, k, bIsUpsideDown);
    }
    
    @Override
    protected ItemStack createStackedBlock( int iMetadata )
    {
    	iMetadata = setIsUpsideDown(iMetadata, false);
    	
    	return super.createStackedBlock( iMetadata );
    }
    
    @Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
        return blockMaterial.getMobsCanSpawnOn(world.provider.dimensionId);
    }

    @Override
    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
    {
        if ( !getIsUpsideDown(world, i, j, k) )
        {
        	return -0.5F;
        }
        
    	return 0F;
    }
    
    //------------- Class Specific Methods ------------//
    
    protected boolean hasNeighborWithMortarInContact(World world, int i, int j, int k, boolean bIsUpsideDown)
    {
    	if ( bIsUpsideDown )
    	{
    		if ( WorldUtils.hasNeighborWithMortarInFullFaceContactToFacing(world, i, j, k, 1) )
			{
				return true;
			}
    	}
    	else
    	{
    		if ( WorldUtils.hasNeighborWithMortarInFullFaceContactToFacing(world, i, j, k, 0) )
			{
				return true;
			}
    	}
    	
    	for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
    	{
    		if ( WorldUtils.hasNeighborWithMortarInSlabSideContactToFacing(world, i, j, k, iTempFacing, bIsUpsideDown) )
			{
				return true;
			}
    	}
    	
    	return false;
    }
    
    protected boolean hasStickySnowNeighborInContact(World world, int i, int j, int k, boolean bIsUpsideDown)
    {
    	if ( bIsUpsideDown )
    	{
    		if ( WorldUtils.hasStickySnowNeighborInFullFaceContactToFacing(world, i, j, k, 1) )
			{
				return true;
			}
    	}
    	else
    	{
    		if ( WorldUtils.hasStickySnowNeighborInFullFaceContactToFacing(world, i, j, k, 0) )
			{
				return true;
			}
    	}
    	
    	for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
    	{
    		if ( WorldUtils.hasStickySnowNeighborInSlabSideContactToFacing(world, i, j, k, iTempFacing, bIsUpsideDown) )
			{
				return true;
			}
    	}
    	
    	return false;
    }
    
    public boolean canBePlacedUpsideDownAtLocation(World world, int i, int j, int k)
    {
    	return true;
    }
    
    public boolean getIsUpsideDown(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsUpsideDown(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public boolean getIsUpsideDown(int iMetadata)
    {
    	return ( iMetadata & 1 ) > 0;
    }
    
    public void setIsUpsideDown(World world, int i, int j, int k, boolean bUpsideDown)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	world.setBlockMetadataWithNotify(i, j, k, setIsUpsideDown(iMetadata, bUpsideDown));
    }
    
    public int setIsUpsideDown(int iMetadata, boolean bUpsideDown)
	{
    	if ( bUpsideDown )
    	{
    		iMetadata |= 1;
    	}
    	else
    	{
    		iMetadata &= (~1);    		
    	}
    	
    	return iMetadata;    	
	}
        
    public boolean convertToFullBlock(World world, int i, int j, int k)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	return world.setBlockAndMetadataWithNotify(i, j, k, getCombinedBlockID(iMetadata), getCombinedMetadata(iMetadata));
    }
    
	public abstract int getCombinedBlockID(int iMetadata);
	
	public int getCombinedMetadata(int iMetadata)
	{
		return 0;
	}
	
    protected AxisAlignedBB getBlockBoundsFromPoolFromMetadata(int iMetadata)
    {
        if ( getIsUpsideDown(iMetadata) )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(             
        		0F, 0.5F, 0F, 1F, 1F, 1F );
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(             
        		0F, 0F, 0F, 1F, 0.5F, 1F );
        }
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	BlockPos myPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK,
                                      getOppositeFacing(iSide) );
    	
    	boolean bUpsideDown = getIsUpsideDown(blockAccess, myPos.x, myPos.y, myPos.z);
    	
    	if ( iSide < 2 )
    	{
	    	if ( iSide == 0 )
	    	{
	    		return bUpsideDown || !blockAccess.isBlockOpaqueCube( iNeighborI, iNeighborJ, iNeighborK );
	    	}
	    	else // iSide == 1
	    	{
	    		return !bUpsideDown || !blockAccess.isBlockOpaqueCube( iNeighborI, iNeighborJ, iNeighborK );
	    	}
    	}

        return RenderUtils.shouldRenderNeighborHalfSlabSide(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide, bUpsideDown);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
    {
		return getIsUpsideDown(blockAccess, i, j, k) != bNeighborUpsideDown;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
    {
    	if ( iNeighborSide < 2 )
    	{
    		boolean bUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
    		
    		if ( iNeighborSide == 0 )
    		{
    			return !bUpsideDown;
    		}
    		else // iNeighborSide == 1
    		{
    			return bUpsideDown;
    		}    			
    	}
    	
		return true;
    }    
}
