// FCMOD

package btw.block.blocks;

import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public abstract class AttachedSlabBlock extends SlabBlock
{
    protected AttachedSlabBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int iSide )
    {
    	if ( iSide == 0 || iSide == 1 )
    	{
    		if ( hasValidAnchorToFacing(world, i, j, k, Block.getOppositeFacing(iSide)) )
			{
		        return super.canPlaceBlockOnSide( world, i, j, k, iSide );
			}
    	}
    	else if (hasValidAnchorToFacing(world, i, j, k, 0) ||
				 hasValidAnchorToFacing(world, i, j, k, 1) )
		{
	        return super.canPlaceBlockOnSide( world, i, j, k, iSide );
		}
    	
		return false;
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		if ( iFacing == 0 )
		{
        	iMetadata = setIsUpsideDown(iMetadata, true);
		}
		else if ( iFacing != 1 )
        {
			if ( (double)fClickY > 0.5D )
			{
				if ( hasValidAnchorToFacing(world, i, j, k, 1) )
				{
		        	iMetadata = setIsUpsideDown(iMetadata, true);
				}
			}
			else
			{
				if ( !hasValidAnchorToFacing(world, i, j, k, 0) )
				{
		        	iMetadata = setIsUpsideDown(iMetadata, true);
				}
			}
        }
        
        return iMetadata;
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
		int iAnchorSide = 0;
		
		if ( getIsUpsideDown(world.getBlockMetadata(i, j, k)) )
		{
			iAnchorSide = 1;			
		}
		
		if ( !hasValidAnchorToFacing(world, i, j, k, iAnchorSide) )
		{
			onAnchorBlockLost(world, i, j, k);
		}
    }
	
    //------------- Class Specific Methods ------------//    
    
	protected boolean hasValidAnchorToFacing(World world, int i, int j, int k, int iFacing)
	{
		BlockPos attachedPos = new BlockPos( i, j, k, iFacing );
		
		return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, attachedPos.x, attachedPos.y, attachedPos.z,
																	Block.getOppositeFacing(iFacing), true);
	}
	
	abstract protected void onAnchorBlockLost(World world, int i, int j, int k);
	
	//----------- Client Side Functionality -----------//
}
