// FCMOD

package btw.block.blocks;

import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class TorchBlockBase extends Block
{
    protected TorchBlockBase(int iBlockID )
    {
        super( iBlockID, Material.circuits );
    	
    	setHardness( 0F );
    	
    	setBuoyant();
		setFilterableProperties(Item.FILTERABLE_NARROW);
    	
    	setStepSound( soundWoodFootstep );
    	
        setTickRandomly( true );
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
	{
		return null;
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
    public int getRenderType()
    {
        return 2;
    }

    @Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
    	for ( int iFacing = 2; iFacing < 6; iFacing++ )
    	{
    		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    		
    		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z,
																 Block.getOppositeFacing(iFacing)) )
    		{
    			return true;
    		}
    	}
    	
        return canPlaceTorchOn( world, i, j - 1, k );
    }
    
    @Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        if ( iFacing == 1  )
        {
        	if ( canPlaceTorchOn( world, i, j - 1, k ) )
    		{
        		iMetadata = setOrientation(iMetadata, 5);
    		}
        }
        else if ( iFacing != 0 )
        {
    		BlockPos targetPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
    		
    		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z, iFacing) )
    		{
    			iMetadata = setOrientation(iMetadata, 6 - iFacing);
    		}
    	}
    	
    	return iMetadata;
    }

    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        super.updateTick(world, i, j, k, rand);

        if (getOrientation(world, i, j, k) == 0 )
        {
            onBlockAdded( world, i, j, k );
        }
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        if (getOrientation(world, i, j, k) == 0 )
        {
        	for ( int iFacing = 1; iFacing < 6; iFacing++ )
        	{
        		BlockPos targetPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
        		
        		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z,
																	 iFacing) )
        		{
                    setOrientation(world, i, j, k, 6 - iFacing);
                    
                    return;
        		}
        	}
        	
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
        }
    }
    
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborID )
    {
        validateState(world, i, j, k, iNeighborID);
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iOrientation = getOrientation(blockAccess, i, j, k);
        
        float fTorchWidth = 0.15F;

        if ( iOrientation == 1 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0F, 0.2F, 0.5F - fTorchWidth, 
        		fTorchWidth * 2F, 0.8F, 0.5F + fTorchWidth );
        }
        else if ( iOrientation == 2 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		1F - fTorchWidth * 2F, 0.2F, 0.5F - fTorchWidth, 
        		1F, 0.8F, 0.5F + fTorchWidth );
        }
        else if (iOrientation == 3)
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.5F - fTorchWidth, 0.2F, 0.0F, 
        		0.5F + fTorchWidth, 0.8F, fTorchWidth * 2.0F );
        }
        else if (iOrientation == 4)
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.5F - fTorchWidth, 0.2F, 1.0F - fTorchWidth * 2.0F, 
        		0.5F + fTorchWidth, 0.8F, 1.0F );
        }
        else // 5
        {
            fTorchWidth = 0.1F;
            
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.5F - fTorchWidth, 0.0F, 0.5F - fTorchWidth, 
        		0.5F + fTorchWidth, 0.6F, 0.5F + fTorchWidth );
        }
    }
    
    @Override
	public boolean isBlockRestingOnThatBelow(IBlockAccess blockAccess, int i, int j, int k)
	{
        return getOrientation(blockAccess, i, j, k) == 5;
	}

    @Override
    public int getFacing(int iMetadata)
    {
    	// facing is opposite side attached to    	
    	
    	return MathHelper.clamp_int(6 - getOrientation(iMetadata), 1, 5);
    }
    
    @Override
    public int setFacing(int iMetadata, int iFacing)
    {
    	iFacing = MathHelper.clamp_int( iFacing, 1, 5 );
    	
    	return setOrientation(iMetadata, 6 - iFacing);
    }
    
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		return iFacing == Block.getOppositeFacing(getFacing(world, i, j, k));
    }
    
	@Override
    public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iInitialFacing, int iRotatedFacing)
    {
		int iOldMetadata = world.getBlockMetadata( i, j, k );
		
		return setFacing(iOldMetadata, Block.getOppositeFacing(iRotatedFacing));
    }
	
	@Override
	public void onNeighborDisrupted(World world, int i, int j, int k, int iToFacing)
	{
		if ( iToFacing == Block.getOppositeFacing(getFacing(world, i, j, k)) )
		{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
		}
	}
	
    //------------- Class Specific Methods ------------//    
	
    protected boolean canPlaceTorchOn( World world, int i, int j, int k)
    {
    	return WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, i, j, k, 1, true);
    }
    
    protected boolean validateState(World world, int i, int j, int k, int iNeighborID)
    {
    	// checks for drop on a neighbor block change
    	
        int iOrientation = getOrientation(world, i, j, k);
        int iFacing = 0;
        
        if ( iOrientation != 0 ) // only do further tests if the torch has already been initialized
        {
			iFacing = 6 - iOrientation;
			
	        boolean bShouldDrop = false;
	        
	        if ( iFacing == 1  )
	        {
	        	if ( !canPlaceTorchOn( world, i, j - 1, k ) )
	    		{
	        		bShouldDrop = true;
	    		}
	        }
	        else
	        {
	    		BlockPos targetPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
	    		
	    		if ( !WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z, iFacing) )
	    		{
	    			bShouldDrop = true;
	    		}
	    	}
	            
	        if ( bShouldDrop )
	        {
	            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
	            world.setBlockWithNotify( i, j, k, 0 );
	            
	            return true;
	        }
        }      
        
        return false;
    }
    
    public int getOrientation(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getOrientation(blockAccess.getBlockMetadata(i, j, k));
    }
    
    static public int getOrientation(int iMetadata)
    {
    	return iMetadata & 7;
    }
    
    public void setOrientation(World world, int i, int j, int k, int iOrientation)
    {
		int iMetadata = setOrientation(world.getBlockMetadata(i, j, k), iOrientation);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    static public int setOrientation(int iMetadata, int iOrientation)
    {
    	iMetadata &= (~7);
    	
    	return iMetadata | iOrientation;
    }
    
    public boolean isRainingOnTorch(World world, int i, int j, int k)
    {
    	return world.isRaining() && world.isRainingAtPos(i, j, k);
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return renderBlocks.renderBlockTorch( this, i, j, k );
    }
}
