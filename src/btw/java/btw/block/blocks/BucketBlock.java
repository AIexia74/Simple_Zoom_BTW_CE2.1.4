// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.block.model.BucketModel;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BucketBlock extends FallingBlock
{
	protected BucketModel model;

	protected BlockModel modelTransformed;
    	
    public BucketBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.miscMaterial);
        
        setHardness( 0F );
        setResistance( 0F );
        
    	initBlockBounds(
    		0.5D - BucketModel.BODY_HALF_WIDTH, BucketModel.BASE_HEIGHT,
    		0.5D - BucketModel.BODY_HALF_WIDTH,
    		0.5D + BucketModel.BODY_HALF_WIDTH, BucketModel.HEIGHT,
    		0.5D + BucketModel.BODY_HALF_WIDTH);
    	
        setStepSound( Block.soundMetalFootstep );
        
        setUnlocalizedName( "bucket" );
        
        initModels();
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
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing,
                             float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		iMetadata = setFacing(iMetadata, iFacing);
		
		return iMetadata;
    }
	
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		//always let player place bucket straight, as any sane person would. :P
		setFacing(world, i, j, k, 1);
	}
	

		
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneMod )
    {
		return Item.bucketEmpty.itemID;
    }
	
	@Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChance)
    {
		// always an empty bucket on bad break, regardless of contents
		
		dropItemsIndividually(world, i, j, k, Item.bucketEmpty.itemID, 1, 0, fChance);
		
    	return true;
	}
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
        return null;
    }

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
		modelTransformed = model.makeTemporaryCopy();
    	
    	int iFacing = getFacing(world, i, j, k);
    	
    	modelTransformed.tiltToFacingAlongY(iFacing);
    	
    	BucketModel.offsetModelForFacing(modelTransformed, iFacing);
    	
    	return modelTransformed.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
	@Override
	public int getFacing(int iMetadata)
	{		
    	return iMetadata & 7;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= ~7;
		
        return iMetadata | iFacing;
	}

	@Override
    public int onPreBlockPlacedByPiston(World world, int i, int j, int k, int iMetadata, int iDirectionMoved)
    {
		if ( !WorldUtils.doesBlockHaveCenterHardpointToFacing(world, i, j - 1, k,
															  1, true) )
		{
			// handle bucket being pushed over ledge, and tilting in the appropriate direction
			
			if ( iDirectionMoved >= 2 )
			{ 
				int iFacing = getFacing(iMetadata);
				
				if ( iFacing == 0 )
				{
					iFacing = Block.getOppositeFacing(iDirectionMoved);
				}
				else if ( iFacing == 1 )
				{				
					iFacing = iDirectionMoved;
				}
				else if ( iFacing == iDirectionMoved )
				{
					iFacing = 0;
				}
				else if ( iFacing == Block.getOppositeFacing(iDirectionMoved) )
				{
					iFacing = 1;
				}
				else
				{
					// orientations perpendicular to direction of travel do not change facing
				}
				
				iMetadata = setFacing(iMetadata, iFacing);
			}
		}
		
    	return iMetadata;
    }
	
	@Override
	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
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
    
	//------------- Class Specific Methods ------------//
	
	protected void initModels()
	{
		model = new BucketModel();
	    
		// must initialize transformed model due to weird vanilla getIcon() calls that 
		// occur outside of regular rendering

		modelTransformed = model;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconOpenTop;
    @Environment(EnvType.CLIENT)
    private Icon iconOpenSide;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockBucketEmpty" );

		iconOpenTop = register.registerIcon("fcBlockBucketEmpty_top");
		iconOpenSide = register.registerIcon("fcBlockBucketEmpty_top_side");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if (modelTransformed.getActivePrimitiveID() == model.ASSEMBLY_ID_RIM)
		{
			int iFacing = getFacing(iMetadata);
			
			// backface must use texture with hole in it, as backfaces get rendered when pushed by 
			// pistons, causing them to flicker with the contents texture
			
			if ( iFacing == iSide || iFacing == Block.getOppositeFacing(iSide) )
			{
				if ( iFacing < 2 )
				{
					return iconOpenTop;
				}
				else
				{
					return iconOpenSide;
				}
			}
		}
		
		return super.getIcon( iSide, iMetadata );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		BlockPos myPos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK,
			Block.getOppositeFacing(iSide) );
		
		int iMetadata = blockAccess.getBlockMetadata(myPos.x, myPos.y, myPos.z);
		
		return shouldSideBeRenderedOnFallingBlock(iSide, iMetadata);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRenderedOnFallingBlock(int iSide, int iMetadata)
    {
    	int iFacing = getFacing(iMetadata);
		
		int iActiveID = modelTransformed.getActivePrimitiveID();
		
		if ( iActiveID == BucketModel.ASSEMBLY_ID_INTERIOR)
		{
			return iSide != Block.getOppositeFacing(iFacing);
		}
		else if ( iSide == iFacing )
		{
			return iActiveID == BucketModel.ASSEMBLY_ID_RIM;
		}
		
		return true;		
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
		modelTransformed = model.makeTemporaryCopy();
    	
    	int iFacing = getFacing(renderBlocks.blockAccess, i, j, k);
    	
    	modelTransformed.tiltToFacingAlongY(iFacing);

    	BucketModel.offsetModelForFacing(modelTransformed, iFacing);
		
    	return modelTransformed.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		modelTransformed = model;
    	
    	modelTransformed.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
    {
		modelTransformed = model.makeTemporaryCopy();
    	
    	int iFacing = getFacing(iMetadata);
    	
    	modelTransformed.tiltToFacingAlongY(iFacing);

    	BucketModel.offsetModelForFacing(modelTransformed, iFacing);
    	
    	modelTransformed.renderAsFallingBlock(renderBlocks, this, i, j, k, iMetadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	// override to bypass rendering of all faces normally done with blocks moved by pistons
    	// since the bucket model relies on certain faces being culled
    	
        renderBlock(renderBlocks, i, j, k);
    }

    @Environment(EnvType.CLIENT)
    private static AxisAlignedBB selectionBox = new AxisAlignedBB(
		0.5D - BucketModel.BODY_HALF_WIDTH, BucketModel.BASE_HEIGHT,
		0.5D - BucketModel.BODY_HALF_WIDTH,
		0.5D + BucketModel.BODY_HALF_WIDTH, BucketModel.HEIGHT,
		0.5D + BucketModel.BODY_HALF_WIDTH);

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, MovingObjectPosition rayTraceHit )
    {
		int i = rayTraceHit.blockX;
		int j = rayTraceHit.blockY;
		int k = rayTraceHit.blockZ;
		
		int iFacing = getFacing(world, i, j, k);
		
		AxisAlignedBB tempBox = selectionBox.makeTemporaryCopy();
		
		if ( iFacing != 1 )
		{
	    	tempBox.tiltToFacingAlongY(iFacing);
	    	
			Vec3 offset = BucketModel.getOffsetForFacing(iFacing);
			
			tempBox.translate(offset.xCoord, offset.yCoord, offset.zCoord);
		}
		
		return tempBox.offset( i, j, k );
    }
}
