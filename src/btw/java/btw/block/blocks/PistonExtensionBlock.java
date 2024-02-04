// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class PistonExtensionBlock extends BlockPistonExtension
{
    public PistonExtensionBlock(int iBlockID )
    {
        super( iBlockID );
        
        setPicksEffectiveOn(true);
    }

    @Override
    public AxisAlignedBB getAsPistonMovingBoundingBox(World world, int i, int j, int k)
    {
    	// override to return full block bounding box to fix problem with piston heads 
    	// moving through items

    	return getFixedBlockBoundsFromPool().offset(i, j, k);
    }
    
    @Override
    public boolean canContainPistonPackingToFacing(World world, int i, int j, int k, int iFacing)
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		return BlockPistonExtension.getDirectionMeta( iMetadata ) == iFacing;
    }
    
    @Override
    public void addCollisionBoxesToList( World world, int i, int j, int k, 
    	AxisAlignedBB intersectingBox, List list, Entity entity )
    {
        int iFacing = getDirectionMeta( world.getBlockMetadata( i, j, k ) );

        AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB( 0D, 0.75D, 0D, 1D, 1D, 1D );        
        tempBox.tiltToFacingAlongY(iFacing);
        tempBox.offset( i, j, k );
        tempBox.addToListIfIntersects(intersectingBox, list);
    		
        tempBox = AxisAlignedBB.getAABBPool().getAABB( 0.375D, 0D, 0.375D, 0.625D, 0.75D, 0.625D );        
        tempBox.tiltToFacingAlongY(iFacing);
        tempBox.offset( i, j, k );
        tempBox.addToListIfIntersects(intersectingBox, list);
    }
    
	@Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getDirectionMeta( blockAccess.getBlockMetadata( i, j, k ) );

        AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB( 0D, 0.75F, 0D, 1D, 1D, 1D );
        tempBox.tiltToFacingAlongY(iFacing);
        
        return tempBox;
    }
    
    @Override
    public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getDirectionMeta( blockAccess.getBlockMetadata( i, j, k ) ) == 1;    	
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderPistonExtension( this, i, j, k, true );
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
