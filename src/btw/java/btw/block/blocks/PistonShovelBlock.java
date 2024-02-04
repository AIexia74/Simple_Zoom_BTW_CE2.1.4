// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.PistonShovelModel;
import btw.util.MiscUtils;
import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class PistonShovelBlock extends Block
{
    protected PistonShovelModel model = new PistonShovelModel();

    public PistonShovelBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 5F );     
        setPicksEffectiveOn();
        
    	initBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
    	
        setStepSound( soundMetalFootstep );        
        
        setUnlocalizedName( "fcBlockShovel" );
        
        setCreativeTab( CreativeTabs.tabRedstone );
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
    public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB boundingBox, List list, Entity entity )
    {
		BlockModel transformedModel = getTransformedModelForMetadata(model.collisionModel,
																	 world.getBlockMetadata( i, j, k ));
		
		transformedModel.addIntersectingBoxesToCollisionList(world, i, j, k, boundingBox, list);
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		int iVerticalOrientation = 1;
		int iBlockFacing = 2;
		
		if ( iFacing >= 2 )
		{
			if ( fClickY > 0.5F )
			{
				iVerticalOrientation = 0;
			}
			
			iBlockFacing = iFacing;
		}
		else
		{
			if ( iFacing == 0 )
			{
				iVerticalOrientation = 0; 
			}
		}
			
		iMetadata = setFacing(iMetadata, iBlockFacing);
		iMetadata = setVerticalOrientation(iMetadata, iVerticalOrientation);
		
        return iMetadata;        
    }
    
	@Override
    public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata, EntityLiving entityBy)
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityBy);

		return setFacing(iMetadata, iFacing);
	}
	
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );
    	
		BlockModel transformedModel = getTransformedModelForMetadata(model.rayTraceModel,
																	 world.getBlockMetadata( i, j, k ));
		
		transformedModel.addToRayTrace(rayTrace);
		
        return rayTrace.getFirstIntersection();
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
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
    public int getPistonShovelEjectDirection(World world, int i, int j, int k, int iToFacing)
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if ( iToFacing >= 2 )
		{
			if (iToFacing == getFacing(iMetadata) )
			{
				return getVerticalOrientation(iMetadata);
			}
		}
		else if (iToFacing == getVerticalOrientation(iMetadata) )
		{			
			return getFacing(iMetadata);
		}
		
		return -1;
    }
    
	//------------- Class Specific Methods ------------//
	
	/**
	 * 0 = down, 1 = up, 2 = side
	 */
	public int getVerticalOrientation(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getVerticalOrientation(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getVerticalOrientation(int iMetadata)
	{
		return ( iMetadata & 12 ) >> 2;
	}
	
	public void setVerticalOrientation(World world, int i, int j, int k, int iLevel)
	{
		int iMetadata = setVerticalOrientation(world.getBlockMetadata(i, j, k), iLevel);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setVerticalOrientation(int iMetadata, int iLevel)
	{
		iMetadata &= ~12; // filter out old facing
		
		iMetadata |= iLevel << 2;
		
		return iMetadata;
	}
	
    private BlockModel getTransformedModelForMetadata(BlockModel model, int iMetadata)
    {
		BlockModel transformedModel = model.makeTemporaryCopy();
		
    	if (getVerticalOrientation(iMetadata) == 0 )
    	{
    		transformedModel.tiltToFacingAlongY(0);
    	}
    		 
		transformedModel.rotateAroundYToFacing(getFacing(iMetadata));
		
		return transformedModel;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconEdge;
    @Environment(EnvType.CLIENT)
    private Icon iconEdgeBack;
    @Environment(EnvType.CLIENT)
    private Icon iconEdgeMiddle;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconEdge = register.registerIcon("fcBlockShovel_edge");
		iconEdgeBack = register.registerIcon("fcBlockShovel_edge_back");
		iconEdgeMiddle = register.registerIcon("fcBlockShovel_edge_middle");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconByIndex(int iIndex)
    {
    	if ( iIndex == 1 )
    	{
    		return iconEdge;
    	}
    	else if ( iIndex == 2 )
    	{
    		return iconEdgeBack;
    	}
    	else if ( iIndex == 3 )
    	{
    		return iconEdgeMiddle;
    	}
    	
    	return blockIcon;
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
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {  
		BlockModel transformedModel = getTransformedModelForMetadata(model,
																	 renderBlocks.blockAccess.getBlockMetadata( i, j, k ));
		
		return transformedModel.renderAsBlockWithColorMultiplier(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		BlockModel transformedModel = model.makeTemporaryCopy();
		
		transformedModel.rotateAroundYToFacing(3);
		
    	transformedModel.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}