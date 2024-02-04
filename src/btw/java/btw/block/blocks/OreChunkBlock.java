// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.OreChunkModel;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class OreChunkBlock extends Block
{
	protected static OreChunkModel model = new OreChunkModel();
	
    protected OreChunkBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits  );
        
        setHardness( 0F );
        setPicksEffectiveOn(true);
        
    	initBlockBounds(0.5D - model.BOUNDING_BOX_HALF_WIDTH,
                        model.BOUNDING_BOX_VERTICAL_OFFSET,
                        0.5D - model.BOUNDING_BOX_HALF_WIDTH,
                        0.5D + model.BOUNDING_BOX_HALF_WIDTH,
                        model.BOUNDING_BOX_VERTICAL_OFFSET + model.BOUNDING_BOX_HEIGHT,
                        0.5D + model.BOUNDING_BOX_HALF_WIDTH);
    	
        setStepSound( soundStoneFootstep );
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
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		int iBlockFacing = world.rand.nextInt( 4 ) + 2;

        return setFacing(iMetadata, iBlockFacing);
    }
	
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        if ( WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
		{
            return super.canPlaceBlockAt( world, i, j, k );
		}
        
		return false;		
    }
    
	@Override
	public boolean isBlockRestingOnThatBelow(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
        }
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return null;
	}
	
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, 
    	Vec3 startRay, Vec3 endRay )
    {
    	BlockModel m_modelTransformed = model.makeTemporaryCopy();
    	
    	int iFacing = getFacing(world, i, j, k);
    	
    	m_modelTransformed.rotateAroundYToFacing(iFacing);
    	
    	return m_modelTransformed.collisionRayTrace(world, i, j, k, startRay, endRay);
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
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {    	
    	BlockModel transformedModel = model.makeTemporaryCopy();
		
		transformedModel.rotateAroundYToFacing(getFacing(renderBlocks.blockAccess, i, j, k));
		
		return transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }
}