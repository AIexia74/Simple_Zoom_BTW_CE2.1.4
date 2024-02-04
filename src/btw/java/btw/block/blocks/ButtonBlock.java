// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ButtonBlock extends BlockButton
{
    protected ButtonBlock(int iBlockID, boolean bSensitive )
    {
        super( iBlockID, bSensitive );
    }

	@Override
	public int getFacing(int iMetadata)
    {
    	// facing is opposite side attached to 
    	
    	return MathHelper.clamp_int( 6 - ( iMetadata & 7 ), 2, 5 );
    }
    
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		return iFacing == Block.getOppositeFacing(getFacing(world, i, j, k));
    }
	
	@Override
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
		
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
    public void setBlockBoundsBasedOnState( IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
	@Override
    public void setBlockBoundsForItemRender()
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	// FCNOTE: This doesn't handle the interactions between arrows and wood buttons in 
    	// super.func_82535_o(), which means there may be innaccuracies between client and server.
    	// However, I doubt it'll ever even be noticed, so I'm leaving it as is for now. 
    	
    	int iMetadata = blockAccess.getBlockMetadata( i, j, k );
    	
        int iDirection = iMetadata & 7;
        boolean bDepressed = ( iMetadata & 8 ) > 0;
        
        float fMinY = 0.375F;
        float fMaxY = 0.625F;
        float fHalfWidth = 0.1875F;
        
        float fThickness = 0.125F;

        if (bDepressed)
        {
            fThickness = 0.0625F;
        }

        if ( iDirection == 1 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
        		0.0F, fMinY, 0.5F - fHalfWidth, 
        		fThickness, fMaxY, 0.5F + fHalfWidth );
        }
        else if ( iDirection == 2 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
        		1.0F - fThickness, fMinY, 0.5F - fHalfWidth, 
        		1.0F, fMaxY, 0.5F + fHalfWidth );
        }
        else if ( iDirection == 3 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
        		0.5F - fHalfWidth, fMinY, 0.0F, 
        		0.5F + fHalfWidth, fMaxY, fThickness );
        }
        else // iDirection == 4
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
        		0.5F - fHalfWidth, fMinY, 1.0F - fThickness, 
        		0.5F + fHalfWidth, fMaxY, 1.0F );
        }
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private static final double ITEM_DEPTH = (6D / 16D );
    @Environment(EnvType.CLIENT)
    private static final double ITEM_HALF_DEPTH = (ITEM_DEPTH / 2D );

    @Environment(EnvType.CLIENT)
    private static final double ITEM_WIDTH = (4D / 16D );
    @Environment(EnvType.CLIENT)
    private static final double ITEM_HALF_WIDTH = (ITEM_WIDTH / 2D );

    @Environment(EnvType.CLIENT)
    private static final double ITEM_HEIGHT = (4D / 16D );
    @Environment(EnvType.CLIENT)
    private static final double ITEM_HALF_HEIGHT = (ITEM_HEIGHT / 2D );

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int iItemDamage)
    {
    	return AxisAlignedBB.getAABBPool().getAABB(
                0.5F - ITEM_HALF_DEPTH, 0.5D - ITEM_HALF_HEIGHT, 0.5D - ITEM_HALF_WIDTH,
                0.5F + ITEM_HALF_DEPTH, 0.5D + ITEM_HALF_HEIGHT, 0.5D + ITEM_HALF_WIDTH);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }
}