// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class PistonBlockMoving extends BlockPistonMoving
{
    public PistonBlockMoving(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public AxisAlignedBB getAxisAlignedBB(World world, int i, int j, int k, int iBlockID, float fPistonPushProgress, int iOrientation )
    {
    	// override to fix problem with piston heads moving through items
    	
        if ( iBlockID != 0 && iBlockID != this.blockID )
        {
            AxisAlignedBB boundingBox = Block.blocksList[iBlockID].getAsPistonMovingBoundingBox(
            	world, i, j, k);

            if (boundingBox == null)
            {
                return null;
            }
            else
            {
                if (Facing.offsetsXForSide[iOrientation] < 0)
                {
                    boundingBox.minX -= (double)((float)Facing.offsetsXForSide[iOrientation] * fPistonPushProgress);
                }
                else
                {
                    boundingBox.maxX -= (double)((float)Facing.offsetsXForSide[iOrientation] * fPistonPushProgress);
                }

                if (Facing.offsetsYForSide[iOrientation] < 0)
                {
                    boundingBox.minY -= (double)((float)Facing.offsetsYForSide[iOrientation] * fPistonPushProgress);
                }
                else
                {
                    boundingBox.maxY -= (double)((float)Facing.offsetsYForSide[iOrientation] * fPistonPushProgress);
                }

                if (Facing.offsetsZForSide[iOrientation] < 0)
                {
                    boundingBox.minZ -= (double)((float)Facing.offsetsZForSide[iOrientation] * fPistonPushProgress);
                }
                else
                {
                    boundingBox.maxZ -= (double)((float)Facing.offsetsZForSide[iOrientation] * fPistonPushProgress);
                }

                return boundingBox;
            }
        }
        else
        {
            return null;
        }
    }
    
	@Override
    public void setBlockBoundsBasedOnState( IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        TileEntity tileEntity = blockAccess.getBlockTileEntity( i, j, k );

        if ( tileEntity != null && tileEntity instanceof TileEntityPiston )
        {
        	TileEntityPiston pistonEntity = (TileEntityPiston)tileEntity;
        	
            Block block = Block.blocksList[pistonEntity.getStoredBlockID()];

            if ( block != null && block != this )
            {
	            AxisAlignedBB bounds = block.getBlockBoundsFromPoolBasedOnState(
	            	blockAccess, i, j, k);
	            
	            float fExtensionRatio = pistonEntity.getProgress(0.0F);
	
	            if (pistonEntity.isExtending())
	            {
	                fExtensionRatio = 1.0F - fExtensionRatio;
	            }
	
	            int iFacing = pistonEntity.getPistonOrientation();
	            
	            bounds.minX -= Facing.offsetsXForSide[iFacing] * fExtensionRatio;
	            bounds.minY -= Facing.offsetsYForSide[iFacing] * fExtensionRatio;
	            bounds.minZ -= Facing.offsetsZForSide[iFacing] * fExtensionRatio;
	            
	            bounds.maxX -= Facing.offsetsXForSide[iFacing] * fExtensionRatio;
	            bounds.maxY -= Facing.offsetsYForSide[iFacing] * fExtensionRatio;
	            bounds.maxZ -= Facing.offsetsZForSide[iFacing] * fExtensionRatio;
	            
	            return bounds;
            }
        }
        
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
    }

    @Override
    public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k)
    {
    	// wait until after the piston stops moving to evaluate falling blocks
    	
    	return true;    	
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
    
	//------------- Class Specific Methods ------------//
	
    public static TileEntity getShoveledTileEntity(int iBlockID, int iMetadata, int iFacing)
    {
        return new TileEntityPiston( iBlockID, iMetadata, iFacing, true, false, true );
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	// rendering handled by tile entity
    	
    	return false;
    }
}
