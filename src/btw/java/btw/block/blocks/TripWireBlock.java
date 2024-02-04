// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class TripWireBlock extends BlockTripWire
{
    public TripWireBlock(int iBlockID )
    {
        super( iBlockID );
        
        initBlockBounds(0D, 0D, 0D, 1D, 0.15625D, 1D);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity )
    {
    	if ( entity.canEntityTriggerTripwire() )
    	{
    		super.onEntityCollidedWithBlock( world, i, j, k, entity );
    	}
    }
    
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int var5 = blockAccess.getBlockMetadata(i, j, k);
        
        boolean var6 = (var5 & 4) == 4;
        boolean var7 = (var5 & 2) == 2;

        if (!var7)
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0D, 1D, 0.09375D, 1D );
        }
        else if (!var6)
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0D, 1D, 0.5D, 1D );
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0.0625D, 0D, 1D, 0.15625D, 1D );
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockTripWire( this, i, j, k );
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
