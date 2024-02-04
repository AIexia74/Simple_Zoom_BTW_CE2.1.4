// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class UnfiredPotteryBlock extends Block
{
    public final static int NUM_SUBTYPES = 14;
    public static final int NUM_SPUN_POTTERY_TYPES = 4;
    
    public static final int SUBTYPE_CRUCIBLE = 0;
    public static final int SUBTYPE_PLANTER = 1;
    public static final int SUBTYPE_VASE = 2;
    public static final int SUBTYPE_URN = 3;
    public static final int SUBTYPE_MOULD = 4;
    public static final int SUBTYPE_CLAY_BRICK = 5;
    public static final int SUBTYPE_CLAY_BRICK_X_ALIGNED = 6;
    public static final int SUBTYPE_NETHER_BRICK = 7;
    public static final int SUBTYPE_NETHER_BRICK_X_ALIGNED = 8;
    public static final int SUBTYPE_UNCOOKED_CAKE = 9;
    public static final int SUBTYPE_UNCOOKED_COOKIES = 10;
    public static final int SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED = 11;
    public static final int SUBTYPE_UNCOOKED_PUMPKIN_PIE = 12;
    public static final int SUBTYPE_UNCOOKED_BREAD = 13;
    public static final int SUBTYPE_UNCOOKED_BREAD_I_ALIGNED = 14;
    
    public final static int ROTATIONS_ON_TURNTABLE_TO_CHANG_STATE = 8;
    
	public static final float UNFIRED_POTTERY_URN_BASE_WIDTH = (4.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_BASE_HALF_WIDTH = UNFIRED_POTTERY_URN_BASE_WIDTH / 2.0F;
	public static final float UNFIRED_POTTERY_URN_BASE_HEIGHT = (1.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_BODY_WIDTH = (6.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_BODY_HALF_WIDTH = UNFIRED_POTTERY_URN_BODY_WIDTH / 2.0F;
	public static final float UNFIRED_POTTERY_URN_BODY_HEIGHT = (6.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_NECK_WIDTH = (4.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_NECK_HALF_WIDTH = UNFIRED_POTTERY_URN_NECK_WIDTH / 2.0F;
	public static final float UNFIRED_POTTERY_URN_NECK_HEIGHT = (1.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_TOP_WIDTH = (6.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_TOP_HALF_WIDTH = UNFIRED_POTTERY_URN_TOP_WIDTH / 2.0F;
	public static final float UNFIRED_POTTERY_URN_TOP_HEIGHT = (1.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_LID_WIDTH = (4.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_LID_HALF_WIDTH = UNFIRED_POTTERY_URN_LID_WIDTH / 2.0F;
	public static final float UNFIRED_POTTERY_URN_LID_HEIGHT = (1.0F / 16F );
	public static final float UNFIRED_POTTERY_URN_HEIGHT = (UNFIRED_POTTERY_URN_BASE_HEIGHT + UNFIRED_POTTERY_URN_BODY_HEIGHT +
															UNFIRED_POTTERY_URN_NECK_HEIGHT + UNFIRED_POTTERY_URN_TOP_HEIGHT +
															UNFIRED_POTTERY_URN_LID_HEIGHT);
	
	public static final float UNFIRED_POTTERY_MOULD_HEIGHT = (2F / 16F );
	public static final float UNFIRED_POTTERY_MOULD_WIDTH = (6F / 16F );
	public static final float UNFIRED_POTTERY_MOULD_HALF_WIDTH = UNFIRED_POTTERY_MOULD_WIDTH / 2F;
	
	public static final float UNFIRED_POTTERY_BRICK_HEIGHT = (4F / 16F );
	public static final float UNFIRED_POTTERY_BRICK_WIDTH = (6F / 16F );
	public static final float UNFIRED_POTTERY_BRICK_HALF_WIDTH = (UNFIRED_POTTERY_BRICK_WIDTH / 2F );
	public static final float UNFIRED_POTTERY_BRICK_LENGTH = (12F / 16F );
	public static final float UNFIRED_POTTERY_BRICK_HALF_LENGTH = (UNFIRED_POTTERY_BRICK_LENGTH / 2F );
	
	public static final float UNFIRED_POTTERY_UNCOOKED_CAKE_HEIGHT = (8F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_CAKE_WIDTH = (14F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_WIDTH = (UNFIRED_POTTERY_UNCOOKED_CAKE_WIDTH / 2F );
	public static final float UNFIRED_POTTERY_UNCOOKED_CAKE_LENGTH = (14F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_LENGTH = (UNFIRED_POTTERY_UNCOOKED_CAKE_LENGTH / 2F );
	
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_HEIGHT = (1F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_WIDTH = (6F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_WIDTH = (UNFIRED_POTTERY_UNCOOKED_COOKIES_WIDTH / 2F );
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_LENGTH = (14F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_LENGTH = (UNFIRED_POTTERY_UNCOOKED_COOKIES_LENGTH / 2F );
	
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH = (2F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_HALF_WIDTH = (UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH / 2F );
	
	public static final float UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HEIGHT = (4F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_WIDTH = (12F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_WIDTH = (UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_WIDTH / 2F );
	public static final float UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_LENGTH = (12F / 16F );
	public static final float UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_LENGTH = (UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_LENGTH / 2F );
	
    public UnfiredPotteryBlock(int iBlockID )
    {
        super( iBlockID, Material.clay );
        
        setHardness( 0.6F );
        setShovelsEffectiveOn(true);
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockUnfiredPottery" );
        
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }
    
	@Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
		// check beneath for valid block due to piston pushing not sending a notify
		if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
		{
	        dropBlockAsItem(world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
	        world.setBlockWithNotify(i, j, k, 0);
		}
    }
	
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		if ( iFacing == 4 || iFacing == 5 )
		{
			if (iMetadata == SUBTYPE_CLAY_BRICK)
			{
				iMetadata = SUBTYPE_CLAY_BRICK_X_ALIGNED;
			}
			else if (iMetadata == SUBTYPE_NETHER_BRICK)
			{
				iMetadata = SUBTYPE_NETHER_BRICK_X_ALIGNED;
			}
			else if (iMetadata == SUBTYPE_UNCOOKED_COOKIES)
			{
				iMetadata = SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED;
			}
			else if (iMetadata == SUBTYPE_UNCOOKED_BREAD)
			{
				iMetadata = SUBTYPE_UNCOOKED_BREAD_I_ALIGNED;
			}
		}
		
		return iMetadata;
    }
	
	@Override
    public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving placingEntity, ItemStack stack )
    {
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(placingEntity);

		if ( iFacing == 4 || iFacing == 5 )
		{
			int iMetadata = world.getBlockMetadata( i, j, k );
			
			if (iMetadata == SUBTYPE_CLAY_BRICK)
			{
				world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_CLAY_BRICK_X_ALIGNED);
			}
			else if (iMetadata == SUBTYPE_NETHER_BRICK)
			{
				world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_NETHER_BRICK_X_ALIGNED);
			}
			else if (iMetadata == SUBTYPE_UNCOOKED_COOKIES)
			{
				world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED);
			}
			else if (iMetadata == SUBTYPE_UNCOOKED_BREAD)
			{
				world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_UNCOOKED_BREAD_I_ALIGNED);
			}
		}			
    }
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
		if (iMetadata == SUBTYPE_CLAY_BRICK || iMetadata == SUBTYPE_CLAY_BRICK_X_ALIGNED)
		{
			return Item.clay.itemID;
		}
		else if (iMetadata == SUBTYPE_NETHER_BRICK || iMetadata == SUBTYPE_NETHER_BRICK_X_ALIGNED)
		{
			return BTWItems.netherSludge.itemID;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_CAKE)
		{
			return BTWItems.unbakedCake.itemID;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_COOKIES || iMetadata == SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED)
		{
			return BTWItems.unbakedCookies.itemID;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_PUMPKIN_PIE)
		{
			return BTWItems.unbakedPumpkinPie.itemID;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_BREAD || iMetadata == SUBTYPE_UNCOOKED_BREAD_I_ALIGNED)
		{
			return BTWItems.breadDough.itemID;
		}
	    
	    return blockID;
    }
	
	@Override
    public int damageDropped( int iMetadata )
    {
		if (iMetadata < NUM_SPUN_POTTERY_TYPES)
		{
	        return iMetadata; 
		}
		
		return 0;
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
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iMetaData = blockAccess.getBlockMetadata( i, j, k );
    	
    	switch ( iMetaData )
    	{
			case SUBTYPE_CRUCIBLE:
			case SUBTYPE_PLANTER:
				
				break;
				
			case SUBTYPE_VASE:
    			
	        	return AxisAlignedBB.getAABBPool().getAABB(
						( 0.5F - VaseBlock.VASE_BODY_HALF_WIDTH), 0.0F,
						( 0.5F - VaseBlock.VASE_BODY_HALF_WIDTH),
						( 0.5F + VaseBlock.VASE_BODY_HALF_WIDTH), 1.0F,
						( 0.5F + VaseBlock.VASE_BODY_HALF_WIDTH));
    			
			case SUBTYPE_URN:
    			
	        	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_URN_BODY_HALF_WIDTH),
						(0.5F + UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT,
						(0.5F + UNFIRED_POTTERY_URN_BODY_HALF_WIDTH));
    	    	
    		case SUBTYPE_MOULD:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_MOULD_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_MOULD_HALF_WIDTH),
						(0.5F + UNFIRED_POTTERY_MOULD_HALF_WIDTH), UNFIRED_POTTERY_MOULD_HEIGHT,
						(0.5F + UNFIRED_POTTERY_MOULD_HALF_WIDTH));
        	    	
    		case SUBTYPE_CLAY_BRICK:
    		case SUBTYPE_NETHER_BRICK:
    		case SUBTYPE_UNCOOKED_BREAD:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_BRICK_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_BRICK_HALF_LENGTH),
						(0.5F + UNFIRED_POTTERY_BRICK_HALF_WIDTH), UNFIRED_POTTERY_BRICK_HEIGHT,
						(0.5F + UNFIRED_POTTERY_BRICK_HALF_LENGTH));
    	    	
    		case SUBTYPE_CLAY_BRICK_X_ALIGNED:
    		case SUBTYPE_NETHER_BRICK_X_ALIGNED:
    		case SUBTYPE_UNCOOKED_BREAD_I_ALIGNED:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_BRICK_HALF_LENGTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_BRICK_HALF_WIDTH),
						(0.5F + UNFIRED_POTTERY_BRICK_HALF_LENGTH), UNFIRED_POTTERY_BRICK_HEIGHT,
						(0.5F + UNFIRED_POTTERY_BRICK_HALF_WIDTH));
    	    	
    		case SUBTYPE_UNCOOKED_CAKE:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_LENGTH),
						(0.5F + UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_WIDTH), UNFIRED_POTTERY_UNCOOKED_CAKE_HEIGHT,
						(0.5F + UNFIRED_POTTERY_UNCOOKED_CAKE_HALF_LENGTH));
    	    	
    		case SUBTYPE_UNCOOKED_COOKIES:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_LENGTH),
						(0.5F + UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_WIDTH), UNFIRED_POTTERY_UNCOOKED_COOKIES_HEIGHT,
						(0.5F + UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_LENGTH));
    			
    		case SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_LENGTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_WIDTH),
						(0.5F + UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_LENGTH), UNFIRED_POTTERY_UNCOOKED_COOKIES_HEIGHT,
						(0.5F + UNFIRED_POTTERY_UNCOOKED_COOKIES_HALF_WIDTH));
    	    	
    		case SUBTYPE_UNCOOKED_PUMPKIN_PIE:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						(0.5F - UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_WIDTH), 0.0F,
						(0.5F - UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_LENGTH),
						(0.5F + UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_WIDTH), UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HEIGHT,
						(0.5F + UNFIRED_POTTERY_UNCOOKED_PUMPKIN_PIE_HALF_LENGTH));
    	}
    	
    	return AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, 1D, 1D );
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true);
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
    	if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
    	{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
    	}
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
    	{
    		// Unfired pottery can both be pushed by pistons and needs to rest on a block, which can create weird
    		// interactions if the block below is pushed at the same time as this one, 
    		// creating a ghost block on the client. Delay the popping of the block to next tick prevent this.

    		if( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    		{
    			world.scheduleBlockUpdate( i, j, k, blockID, 1 );
    		}
    	}
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	if (iMetadata == SUBTYPE_VASE)
    	{
    		return BTWBlocks.vase.collisionRayTrace( world, i, j, k, startRay, endRay );
    	}
    	else if (iMetadata == SUBTYPE_PLANTER)
    	{
    		return BTWBlocks.planter.collisionRayTrace( world, i, j, k, startRay, endRay );
    	}
    	
    	return super.collisionRayTrace( world, i, j, k, startRay, endRay );    	
    }
    
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		if (iMetadata == SUBTYPE_CLAY_BRICK)
		{
			iMetadata = SUBTYPE_CLAY_BRICK_X_ALIGNED;
		}
		else if (iMetadata == SUBTYPE_CLAY_BRICK_X_ALIGNED)
		{
			iMetadata = SUBTYPE_CLAY_BRICK;
		}
		else if (iMetadata == SUBTYPE_NETHER_BRICK)
		{
			iMetadata = SUBTYPE_NETHER_BRICK_X_ALIGNED;
		}
		else if (iMetadata == SUBTYPE_NETHER_BRICK_X_ALIGNED)
		{
			iMetadata = SUBTYPE_NETHER_BRICK;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_COOKIES)
		{
			iMetadata = SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED)
		{
			iMetadata = SUBTYPE_UNCOOKED_COOKIES;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_BREAD)
		{
			iMetadata = SUBTYPE_UNCOOKED_BREAD_I_ALIGNED;
		}
		else if (iMetadata == SUBTYPE_UNCOOKED_BREAD_I_ALIGNED)
		{
			iMetadata = SUBTYPE_UNCOOKED_BREAD;
		}
		
		return iMetadata;			
	}

	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	int iSubtype = world.getBlockMetadata( i, j, k );
    	
		if (iSubtype == SUBTYPE_VASE || iSubtype == SUBTYPE_URN)
		{
			return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
		}
		
		return super.canGroundCoverRestOnBlock(world, i, j, k);
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iSubtype = blockAccess.getBlockMetadata( i, j, k );
    	
		if (iSubtype == SUBTYPE_VASE || iSubtype == SUBTYPE_URN)
		{
			return -1F;
		}
		
		return super.groundCoverRestingOnVisualOffset(blockAccess, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconNetherSludge;
    @Environment(EnvType.CLIENT)
    private Icon iconUncookedPastry;
    @Environment(EnvType.CLIENT)
    private Icon iconUncookedPumpkinPieTop;

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
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconNetherSludge = register.registerIcon("fcBlockNetherSludge");
		iconUncookedPastry = register.registerIcon("fcBlockPastryUncooked");
		iconUncookedPumpkinPieTop = register.registerIcon("fcBlockUncookedPumpkinPie_top");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if (iMetadata == SUBTYPE_NETHER_BRICK || iMetadata == SUBTYPE_NETHER_BRICK_X_ALIGNED)
    	{
    		return iconNetherSludge;
    	}
    	else if (iMetadata >= SUBTYPE_UNCOOKED_CAKE)
    	{
    		if ( iSide == 1 )
    		{
	    		if (iMetadata == SUBTYPE_UNCOOKED_PUMPKIN_PIE)
	    		{
	    			return iconUncookedPumpkinPieTop;
	    		}
    		}
    		
    		return iconUncookedPastry;
    	}
    	
    	return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_CRUCIBLE));
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_PLANTER));
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_VASE));
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_URN));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		
		if (metadata == SUBTYPE_NETHER_BRICK || metadata == SUBTYPE_NETHER_BRICK_X_ALIGNED) {
			return BTWItems.unfiredNetherBrick.itemID;
		}
		else {
			return this.idDropped(metadata, world.rand, 0);
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	int iMetaData = blockAccess.getBlockMetadata( i, j, k );
    	
    	switch ( iMetaData )
    	{
    		case SUBTYPE_CRUCIBLE:
    			
    			Icon crucibleTexture = getBlockTexture( blockAccess, i, j, k, 0 );
    			
    			renderUnfiredCrucible(renderBlocks, blockAccess, i, j, k, this, crucibleTexture);
    			
    			break;
    			
    		case SUBTYPE_PLANTER:
    			
    			PlanterBlock.renderEmptyPlanterBlock(renderBlocks, blockAccess, i, j, k, this);
    			
    			break;
    			
    		case SUBTYPE_VASE:
    			
    			VaseBlock.renderVaseBlock(renderBlocks, blockAccess, i, j, k, this);
    			
    			break;
    			
    		case SUBTYPE_URN:

    			Icon urnTexture = getBlockTexture( blockAccess, i, j, k, 0 );
    			
    			renderUnfiredUrn(renderBlocks, blockAccess, i, j, k, this, urnTexture, 0.0F);
    			
    			break;    			
    			
			case SUBTYPE_UNCOOKED_COOKIES:
			case SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED:
				
				renderUncookedCookies(renderBlocks, blockAccess, i, j, k, this, (iMetaData == SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED));
				
    			break;    			
    			
    		//case m_iSubtypeMould:
    		//case m_iSubtypeClayBrick:
    		//case m_iSubtypeClayBrickIAligned:
    		//case m_iSubtypeNetherBrick:
        	//case m_iSubtypeNetherBrickIAligned:
			//case m_iSubtypeUncookedCake:
			//case m_iSubtypeUncookedPumpkinPie:
    		//case m_iSubtypeUncookedBread:
    		//case m_iSubtypeUncookedBreadIAligned:
    		    
			default:
				
				super.renderBlock(renderBlocks, i, j, k);
		    	
		    	break;    		        
    	}
		
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }

    @Environment(EnvType.CLIENT)
    static public boolean renderUnfiredUrn
    ( 
    	RenderBlocks renderBlocks, 
    	IBlockAccess blockAccess, 
    	int i, int j, int k, 
    	Block block,
    	Icon texture,
    	float fVerticalOffset
    )
    {
    	// render base 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_BASE_HALF_WIDTH), 0.0F + fVerticalOffset, (0.5F - UNFIRED_POTTERY_URN_BASE_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_BASE_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT + fVerticalOffset, (0.5F +
																																	   UNFIRED_POTTERY_URN_BASE_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	// render body 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT + fVerticalOffset, (0.5F -
																																	   UNFIRED_POTTERY_URN_BODY_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT + UNFIRED_POTTERY_URN_BODY_HEIGHT + fVerticalOffset, (0.5F +
																																		   UNFIRED_POTTERY_URN_BODY_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	// render neck 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_NECK_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - (UNFIRED_POTTERY_URN_TOP_HEIGHT +
																												 UNFIRED_POTTERY_URN_NECK_HEIGHT +
																												 UNFIRED_POTTERY_URN_LID_HEIGHT) + fVerticalOffset, (0.5F -
																																										 UNFIRED_POTTERY_URN_NECK_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_NECK_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_TOP_HEIGHT -
																				   UNFIRED_POTTERY_URN_LID_HEIGHT + fVerticalOffset, (0.5F +
																																		  UNFIRED_POTTERY_URN_NECK_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	// render top 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_TOP_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_TOP_HEIGHT -
																				  UNFIRED_POTTERY_URN_LID_HEIGHT + fVerticalOffset, (0.5F -
																																		 UNFIRED_POTTERY_URN_TOP_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_TOP_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_LID_HEIGHT + fVerticalOffset, (0.5F +
																																								  UNFIRED_POTTERY_URN_TOP_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	// render lid 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_LID_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_LID_HEIGHT + fVerticalOffset, (0.5F -
																																								  UNFIRED_POTTERY_URN_LID_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_LID_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT + fVerticalOffset, (0.5F +
																																 UNFIRED_POTTERY_URN_LID_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	return true;
    }

    @Environment(EnvType.CLIENT)
    static public boolean renderUnfiredCrucible
    ( 
    	RenderBlocks renderBlocks, 
    	IBlockAccess blockAccess, 
    	int i, int j, int k, 
    	Block block,
    	Icon texture
    )
    {
    	// render sides
    	
        renderBlocks.setRenderBounds(
				( 0.5F - CookingVesselBlock.MODEL_HALF_WIDTH), 0.0F, (0.5F - CookingVesselBlock.MODEL_HALF_WIDTH),
				( 0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F, CookingVesselBlock.MODEL_HEIGHT, (0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				( 0.5F - CookingVesselBlock.MODEL_HALF_WIDTH), 0.0F, (0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F,
				( 0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F, CookingVesselBlock.MODEL_HEIGHT, (0.5F + CookingVesselBlock.MODEL_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				( 0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F, 0.0F, (0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F,
				( 0.5F + CookingVesselBlock.MODEL_HALF_WIDTH), CookingVesselBlock.MODEL_HEIGHT, (0.5F + CookingVesselBlock.MODEL_HALF_WIDTH));
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				( 0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - CookingVesselBlock.MODEL_HALF_WIDTH),
				( 0.5F + CookingVesselBlock.MODEL_HALF_WIDTH), CookingVesselBlock.MODEL_HEIGHT, (0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        // render bottom
        
        renderBlocks.setRenderBounds(
				( 0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - CookingVesselBlock.MODEL_HALF_WIDTH) + 0.125F,
				( 0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F, 2.0F / 16F, (0.5F + CookingVesselBlock.MODEL_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
    	// render band around centre
    	
        renderBlocks.setRenderBounds(
				0.0F, 0.5F - CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.0F,
				0.125F, 0.5F + CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.875F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				0.0F, 0.5F - CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.875F,
				0.875F, 0.5F + CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 1.0F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				0.875F, 0.5F - CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.125F,
				1.0F, 0.5F + CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 1.0F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
        
        renderBlocks.setRenderBounds(
				0.125F, 0.5F - CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.0F,
				1.0F, 0.5F + CookingVesselBlock.MODEL_BAND_HALF_HEIGHT, 0.125F);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, texture);
    	
    	return true;
    }

    @Environment(EnvType.CLIENT)
    static public boolean renderUncookedCookies
    ( 
    	RenderBlocks renderBlocks, 
    	IBlockAccess blockAccess, 
    	int i, int j, int k, 
    	Block block,
    	boolean bIAligned
    )
    {
    	int iCookiesAlongI;
    	int iCookiesAlongK;
    	
    	float fStartCenterX;
    	float fStartCenterZ;
    	
    	if ( !bIAligned )
    	{
        	iCookiesAlongI = 2;
        	iCookiesAlongK = 4;
        	
        	fStartCenterX = 0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH;
        	fStartCenterZ = 0.5F -  (3 * UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH);
    	}
    	else
    	{
    		iCookiesAlongI = 4;
    		iCookiesAlongK = 2;
    		
        	fStartCenterX = 0.5F -  (3 * UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH);
        	fStartCenterZ = 0.5F - UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH;
    	}

    	for ( int iCountAlongI = 0; iCountAlongI < iCookiesAlongI; iCountAlongI++ )
    	{
        	for ( int iCountAlongK = 0; iCountAlongK < iCookiesAlongK; iCountAlongK++ )
        	{
        		float fCenterCookieX = fStartCenterX + ((float)iCountAlongI * UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH * 2F );
        		float fCenterCookieZ = fStartCenterZ + ((float)iCountAlongK * UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_WIDTH * 2F );
        		
                renderBlocks.setRenderBounds(
						fCenterCookieX - UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_HALF_WIDTH, 0.0F, fCenterCookieZ -
																									   UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_HALF_WIDTH,
						fCenterCookieX + UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_HALF_WIDTH, UNFIRED_POTTERY_UNCOOKED_COOKIES_HEIGHT, fCenterCookieZ +
																																		  UNFIRED_POTTERY_UNCOOKED_COOKIES_INDIVIDUAL_HALF_WIDTH);
            
                renderBlocks.renderStandardBlock( block, i, j, k );                
        	}
    	}
    	
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	switch ( iItemDamage )
    	{
			case SUBTYPE_CRUCIBLE:

				renderUnfiredCrucibleInvBlock(renderBlocks, this, iItemDamage, blockIcon);
    			
    			break;
    			
			case SUBTYPE_PLANTER:
    			
				PlanterBlock.renderEmptyPlanterInvBlock(renderBlocks, this, iItemDamage);
    			
    			break;
    			
			case SUBTYPE_VASE:
    			
				VaseBlock.renderInvBlock(renderBlocks, this, iItemDamage);
    			
    			break;
    			
			case SUBTYPE_URN:
    			
				renderUnfiredUrnInvBlock(renderBlocks, this, iItemDamage, blockIcon);
    			
    			break;
    			
			case SUBTYPE_MOULD:
				
    	    	renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_MOULD_HALF_WIDTH), 0.0F, (0.5F - UNFIRED_POTTERY_MOULD_HALF_WIDTH),
											 (0.5F + UNFIRED_POTTERY_MOULD_HALF_WIDTH), UNFIRED_POTTERY_MOULD_HEIGHT, (0.5F + UNFIRED_POTTERY_MOULD_HALF_WIDTH));
    	    	
    	    	// fall through
    	    	
			default:
				
		        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, iItemDamage);
		        
		    	break;    		        
    	}    	
    }

    @Environment(EnvType.CLIENT)
    static public void renderUnfiredUrnInvBlock
    ( 
		RenderBlocks renderBlocks, 
		Block block, 
		int iItemDamage, 
		Icon texture
	)
    {
    	// render base 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_BASE_HALF_WIDTH), 0.0F, (0.5F - UNFIRED_POTTERY_URN_BASE_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_BASE_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT, (0.5F +
																													 UNFIRED_POTTERY_URN_BASE_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
        
    	// render body 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT, (0.5F - UNFIRED_POTTERY_URN_BODY_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), UNFIRED_POTTERY_URN_BASE_HEIGHT + UNFIRED_POTTERY_URN_BODY_HEIGHT, (0.5F +
																																					   UNFIRED_POTTERY_URN_BODY_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
        
    	// render neck 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_NECK_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - (UNFIRED_POTTERY_URN_TOP_HEIGHT +
																												 UNFIRED_POTTERY_URN_NECK_HEIGHT +
																												 UNFIRED_POTTERY_URN_LID_HEIGHT), (0.5F -
																																					   UNFIRED_POTTERY_URN_NECK_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_NECK_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_TOP_HEIGHT -
																				   UNFIRED_POTTERY_URN_LID_HEIGHT, (0.5F +
																													UNFIRED_POTTERY_URN_NECK_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
        
    	// render top 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_TOP_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_TOP_HEIGHT -
																				  UNFIRED_POTTERY_URN_LID_HEIGHT, (0.5F -
																												   UNFIRED_POTTERY_URN_TOP_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_TOP_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_LID_HEIGHT, (0.5F +
																																				UNFIRED_POTTERY_URN_TOP_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
        
    	// render lid 
    	
        renderBlocks.setRenderBounds((0.5F - UNFIRED_POTTERY_URN_LID_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT - UNFIRED_POTTERY_URN_LID_HEIGHT, (0.5F -
																																				UNFIRED_POTTERY_URN_LID_HALF_WIDTH),
									 (0.5F + UNFIRED_POTTERY_URN_LID_HALF_WIDTH), UNFIRED_POTTERY_URN_HEIGHT, (0.5F + UNFIRED_POTTERY_URN_LID_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
    }

    @Environment(EnvType.CLIENT)
    static public void renderUnfiredCrucibleInvBlock
    ( 
		RenderBlocks renderBlocks, 
		Block block, 
		int iItemDamage, 
		Icon texture
	)
    {
    	// render bin sides
    	
        renderBlocks.setRenderBounds(( 0.5F - VesselBlock.MODEL_HALF_WIDTH), 0.0F, (0.5F - VesselBlock.MODEL_HALF_WIDTH),
									 ( 0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F, VesselBlock.MODEL_HEIGHT, (0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(( 0.5F - VesselBlock.MODEL_HALF_WIDTH), 0.0F, (0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F,
									 ( 0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F, VesselBlock.MODEL_HEIGHT, (0.5F + VesselBlock.MODEL_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(( 0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F, 0.0F, (0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F,
									 ( 0.5F + VesselBlock.MODEL_HALF_WIDTH), VesselBlock.MODEL_HEIGHT, (0.5F + VesselBlock.MODEL_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(( 0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - VesselBlock.MODEL_HALF_WIDTH),
									 ( 0.5F + VesselBlock.MODEL_HALF_WIDTH), VesselBlock.MODEL_HEIGHT, (0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        // render bin bottom
        
        renderBlocks.setRenderBounds(( 0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - VesselBlock.MODEL_HALF_WIDTH) + 0.125F,
									 ( 0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F, 2.0F / 16F, (0.5F + VesselBlock.MODEL_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
    	// render band around centre
    	
        renderBlocks.setRenderBounds(0.0F, 0.5F - VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.0F,
									 0.125F, 0.5F + VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.875F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(0.0F, 0.5F - VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.875F,
									 0.875F, 0.5F + VesselBlock.MODEL_BAND_HALF_HEIGHT, 1.0F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(0.875F, 0.5F - VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.125F,
									 1.0F, 0.5F + VesselBlock.MODEL_BAND_HALF_HEIGHT, 1.0F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
        
        renderBlocks.setRenderBounds(0.125F, 0.5F - VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.0F,
									 1.0F, 0.5F + VesselBlock.MODEL_BAND_HALF_HEIGHT, 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 0);
    }
}