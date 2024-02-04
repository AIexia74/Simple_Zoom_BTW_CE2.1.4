// FCMOD

package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class BrewingStandBlock extends BlockBrewingStand
{
	protected static final double BASE_HEIGHT = 0.125D;
	protected static final double BASE_WIDTH = (1D - (2D / 16D ) );
	protected static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
	
	protected static final double CENTER_COLUMN_WIDTH = (2D / 16D );
	protected static final double CENTER_COLUMN_HALF_WIDTH = (CENTER_COLUMN_WIDTH / 2D );
	
	protected static final double CENTER_ASSEMBLY_WIDTH = (10D / 16D );
	protected static final double CENTER_ASSEMBLY_HALF_WIDTH = (CENTER_ASSEMBLY_WIDTH / 2D );
	
    public BrewingStandBlock(int iBlockID )
    {
    	super( iBlockID );
    }
    
    @Override
    public boolean doesBlockHopperInsert(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k,
                                        AxisAlignedBB boundingBox, List list, Entity entity )
    {
    	// base
    	
    	AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(
				0D, 0D, 0D, 1D, BASE_HEIGHT, 1D).
    		offset( i, j, k );
    	
    	tempBox.addToListIfIntersects(boundingBox, list);

    	// center
    	
    	tempBox = AxisAlignedBB.getAABBPool().getAABB(
				0.5D - CENTER_COLUMN_HALF_WIDTH, 0D, 0.5D - CENTER_COLUMN_HALF_WIDTH,
				0.5D + CENTER_COLUMN_HALF_WIDTH, 1D, 0.5D + CENTER_COLUMN_HALF_WIDTH).
    		offset( i, j, k );
    	
    	tempBox.addToListIfIntersects(boundingBox, list);
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils(
    		world, i, j, k, startRay, endRay );
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(
				0.5D - BASE_HALF_WIDTH, 0D, 0.5D - BASE_HALF_WIDTH,
				0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_WIDTH);
        
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(
				0.5D - CENTER_ASSEMBLY_HALF_WIDTH, BASE_HEIGHT, 0.5D - CENTER_ASSEMBLY_HALF_WIDTH,
				0.5D + CENTER_ASSEMBLY_HALF_WIDTH, 1D, 0.5D + CENTER_ASSEMBLY_HALF_WIDTH);
    	
    	return rayTrace.getFirstIntersection();
    }
    
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	return renderer.renderBlockBrewingStand(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k)
    {
    	return AxisAlignedBB.getAABBPool().getAABB(
				0.5D - BASE_HALF_WIDTH, 0, 0.5D - BASE_HALF_WIDTH,
				0.5D + BASE_HALF_WIDTH, 1D, 0.5D + BASE_HALF_WIDTH).
    		offset( i, j, k );
    }
}
