// FCMOD

package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class EndPortalFrameBlock extends BlockEndPortalFrame
{
    public EndPortalFrameBlock(int iBlockID )
    {
        super( iBlockID );
        
        initBlockBounds(0F, 0F, 0F, 1F, 0.8125F, 1F);
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k,
                                        AxisAlignedBB intersectingBox, List list, Entity entity )
    {
        AxisAlignedBB tempBox = getCollisionBoundingBoxFromPool( world, i, j, k );

    	tempBox.addToListIfIntersects(intersectingBox, list);
    	
        if ( isEnderEyeInserted( world.getBlockMetadata( i, j, k ) ) )
        {
        	tempBox = AxisAlignedBB.getAABBPool().getAABB( 
        		0.3125F, 0.8125F, 0.3125F, 
        		0.6875F, 1.0F, 0.6875F ).
        		offset( i, j, k );
            
        	tempBox.addToListIfIntersects(intersectingBox, list);
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils(
    		world, i, j, k, startRay, endRay );
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(getFixedBlockBoundsFromPool());
        
        if ( isEnderEyeInserted( world.getBlockMetadata( i, j, k ) ) )
        {
	    	rayTrace.addBoxWithLocalCoordsToIntersectionList(
	    		0.25F, 0.8125F, 0.25F, 
	    		0.75F, 1.0F, 0.75F);
        }
    	
    	return rayTrace.getFirstIntersection();
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
	
	@Override
	public int getMobilityFlag()
	{
		return 2; // cannot be pushed
	}
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	return renderer.renderBlockEndPortalFrame(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	if ( iSide != 1 )
    	{
            return !blockAccess.isBlockOpaqueCube( i, j, k );    		
    	}
    	
    	return true;
    }
}
