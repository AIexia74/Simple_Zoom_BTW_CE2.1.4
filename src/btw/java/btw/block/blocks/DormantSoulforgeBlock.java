// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.SoulforgeModel;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class DormantSoulforgeBlock extends Block
{
    private final BlockModel blockModel = new SoulforgeModel();
    
    public DormantSoulforgeBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 3F ); // same as gold storage
        
        setStepSound( soundMetalFootstep );        
        
        setUnlocalizedName( "fcBlockSoulforgeDormant" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	// flat facing only
    	
    	if ( iFacing < 2 )
    	{
    		iFacing = 2;
    	}
    	else
    	{
        	iFacing = Block.getOppositeFacing(iFacing);
    	}
    	
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
	}
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        if ( iFacing == 2 || iFacing == 3 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.5D - SoulforgeModel.ANVIL_HALF_BASE_WIDTH, 0D, 0D,
				0.5D + SoulforgeModel.ANVIL_HALF_BASE_WIDTH, 1D, 1D);
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0.5D - SoulforgeModel.ANVIL_HALF_BASE_WIDTH,
        		1D, 1D, 0.5D + SoulforgeModel.ANVIL_HALF_BASE_WIDTH);
        }    	
    }
	
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iFacing = getFacing(world, i, j, k);
    	
    	BlockModel transformedModel = blockModel.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
		return iMetadata;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return iFacing;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
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
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	int iFacing = getFacing(renderBlocks.blockAccess, i, j, k);
    	
    	BlockModel transformedModel = blockModel.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	blockModel.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}