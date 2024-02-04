// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WickerBasketBlock extends BasketBlock
{
	private static final float BASKET_OPEN_HEIGHT = 0.75F;
	
	private static final float BASKET_HEIGHT = 0.5F;
	private static final float BASKET_RIM_WIDTH = (1F / 16F );
	private static final float BASKET_WIDTH_LIP = (0F / 16F );
	private static final float BASKET_DEPTH_LIP = (1F / 16F );
	
	private static final float BASKET_LID_HEIGHT = (2F / 16F );
	private static final float BASKET_LID_LAYER_HEIGHT = (1F / 16F );
	private static final float BASKET_LID_LAYER_WIDTH_GAP = (1F / 16F );
	
	private static final float BASKET_HANDLE_HEIGHT = (1F / 16F );
	private static final float BASKET_HANDLE_WIDTH = (2F / 16F );
	private static final float BASKET_HANDLE_HALF_WIDTH = (BASKET_HANDLE_WIDTH / 2F );
	
	private static final float BASKET_HANDLE_LENGTH = (4F / 16F );
	private static final float BASKET_HANDLE_HALF_LENGTH = (BASKET_HANDLE_LENGTH / 2F );
	
	private static final float BASKET_INTERIOR_WALL_THICKNESS = (1F / 16F );
	private static final float MIND_THE_GAP = 0.001F;
	
	private static final double LID_OPEN_LIP_HEIGHT = (1D / 16D );
	private static final double LID_OPEN_LIP_Y_POS = (1D - LID_OPEN_LIP_HEIGHT);
	private static final double LID_OPEN_LIP_WIDTH = (2D / 16D );
	private static final double LID_OPEN_LIP_LENGTH = ( 1D );
	private static final double LID_OPEN_LIP_HALF_LENGTH = (LID_OPEN_LIP_LENGTH / 2D );
	private static final double LID_OPEN_LIP_HORIZONTAL_OFFSET = (5D / 16D );
	
    public BlockModel blockModelBase;
    public BlockModel blockModelBaseOpenCollision;
    public BlockModel blockModelLid;
    public BlockModel blockModelLidFull;
    public BlockModel blockModelInterior;
    
    private static AxisAlignedBB boxCollisionLidOpenLip =
    	new AxisAlignedBB(0.5D - LID_OPEN_LIP_HALF_LENGTH, LID_OPEN_LIP_Y_POS, LID_OPEN_LIP_HORIZONTAL_OFFSET,
						  0.5D + LID_OPEN_LIP_HALF_LENGTH, LID_OPEN_LIP_Y_POS + LID_OPEN_LIP_HEIGHT, LID_OPEN_LIP_HORIZONTAL_OFFSET +
																									 LID_OPEN_LIP_WIDTH);
    
    private static final Vec3 lidRotationPoint = Vec3.createVectorHelper(8F / 16F, 6F / 16F, 14F / 16F);
    
    public WickerBasketBlock(int iBlockID )
    {
        super( iBlockID );

    	initBlockBounds(0D, 0D, 0D, 1D, BASKET_HEIGHT, 1D);
        
        initModelBase();
        initModelBaseOpenCollison();
        initModelLid();
        initModelLidFull();
        initModelInterior();
        
        setUnlocalizedName( "fcBlockBasketWicker" );
    }
    
	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new WickerBasketTileEntity();
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if ( !getIsOpen(iMetadata) )
		{
			if ( !world.isRemote )
			{
				setIsOpen(world, i, j, k, true);
			}
			else
			{
				player.playSound( "step.gravel", 
		    		0.25F + ( world.rand.nextFloat() * 0.1F ), 
		    		0.5F + ( world.rand.nextFloat() * 0.1F ) );
			}
			
			return true;
		}
		else if ( isClickingOnLid(world, i, j, k, iFacing, fXClick, fYClick, fZClick) )
		{
	        WickerBasketTileEntity tileEntity = (WickerBasketTileEntity)world.getBlockTileEntity( i, j, k );
	        
			if ( !tileEntity.closing)
			{
				if ( !world.isRemote )
				{
					tileEntity.startClosingServerSide();
				}
				
				return true;
			}
		}
		else if ( getHasContents(iMetadata) )
		{
			if ( world.isRemote )
			{
				player.playSound( "step.gravel", 
		    		0.5F + ( world.rand.nextFloat() * 0.25F ), 
		    		1F + ( world.rand.nextFloat() * 0.25F ) );
			}
			else
			{
				ejectStorageStack(world, i, j, k);
			}
			
			setHasContents(world, i, j, k, false);
			
			return true;
		}
		else 
    	{
	    	ItemStack heldStack = player.getCurrentEquippedItem();
	    	
			if ( heldStack != null )
			{
				if ( world.isRemote )
				{
					player.playSound( "step.gravel", 
			    		0.5F + ( world.rand.nextFloat() * 0.25F ), 
			    		0.5F + ( world.rand.nextFloat() * 0.25F ) );
				}
				else
				{				
			        WickerBasketTileEntity tileEntity = (WickerBasketTileEntity)world.getBlockTileEntity( i, j, k );
			        
		        	tileEntity.setStorageStack(heldStack);
				}
				
    			heldStack.stackSize = 0;
    			
    			setHasContents(world, i, j, k, true);
    			
    			return true;
			}    		
    	}
		
		return false;
    }
	
    private void ejectStorageStack(World world, int i, int j, int k)
    {    	
        WickerBasketTileEntity tileEntity = (WickerBasketTileEntity)world.getBlockTileEntity( i, j, k );
        
        ItemStack storageStack = tileEntity.getStorageStack();

        if ( storageStack != null )
        {
	        float xOffset = 0.5F;
	        float yOffset = 0.4F;
	        float zOffset = 0.5F;
	        
	        double xPos = (float)i + xOffset;
	        double yPos = (float)j + yOffset;
	        double zPos = (float)k + zOffset;
	        
            EntityItem entityitem = 
            		(EntityItem) EntityList.createEntityOfType(EntityItem.class, world, xPos, yPos, zPos, storageStack );

            entityitem.motionY = 0.2D;
            
            double fFacingFactor = 0.15D;
            double fRandomFactor = 0.05D;
            
            int iFacing = getFacing(world, i, j, k);

            if ( iFacing <= 3 )
            {
                entityitem.motionX = ( world.rand.nextDouble() * 2D - 1D ) * fRandomFactor;
                
                if ( iFacing == 2 )
                {
                	entityitem.motionZ = -fFacingFactor;
                }
                else // 3
                {
                	entityitem.motionZ = fFacingFactor;
                }
            }
            else
            {
                entityitem.motionZ = ( world.rand.nextDouble() * 2D - 1D )  * fRandomFactor;
                
	        	if ( iFacing == 4 )
	            {
	            	entityitem.motionX = -fFacingFactor;
	            }
	            else // 5
	            {
	            	entityitem.motionX = fFacingFactor;
	            }
            }
            
            entityitem.delayBeforeCanPickup = 10;
            
            world.spawnEntityInWorld( entityitem );
            
			tileEntity.setStorageStack(null);
        }
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	return getFixedBlockBoundsFromPool().offset(i, j, k);
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	if ( !getIsOpen(blockAccess, i, j, k) )
		{ 
        	return AxisAlignedBB.getAABBPool().getAABB(
					0F, 0F, 0F, 1F, BASKET_HEIGHT, 1F);
		}
    	else
    	{
        	return AxisAlignedBB.getAABBPool().getAABB(
					0F, 0F, 0F, 1F, BASKET_OPEN_HEIGHT, 1F);
    	}
    }

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );
    	
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	int iFacing = getFacing(iMetadata);
    	
    	BlockModel tempBaseModel;
    	
    	if ( !getIsOpen(iMetadata) )
		{ 
        	tempBaseModel = blockModelBase.makeTemporaryCopy();
        	
	    	BlockModel tempLidModel;
	    	
	    	if ( getHasContents(iMetadata) )
	    	{
	    		tempLidModel = blockModelLidFull.makeTemporaryCopy();
	    	}
	    	else
	    	{
	    		tempLidModel = blockModelLid.makeTemporaryCopy();
	    	}
	    	
	    	tempLidModel.rotateAroundYToFacing(iFacing);
	    	
	    	tempLidModel.addToRayTrace(rayTrace);
		}
    	else
    	{
        	tempBaseModel = blockModelBaseOpenCollision.makeTemporaryCopy();
        	
            WickerBasketTileEntity tileEntity = (WickerBasketTileEntity)world.getBlockTileEntity( i, j, k );
            
            if (tileEntity.lidOpenRatio > 0.95F )
            {
    	    	AxisAlignedBB tempLidBox = boxCollisionLidOpenLip.makeTemporaryCopy();
    	    	
    	    	tempLidBox.rotateAroundYToFacing(iFacing);
    	    	
    	    	rayTrace.addBoxWithLocalCoordsToIntersectionList(tempLidBox);
            }	            
    	}
		
    	tempBaseModel.rotateAroundYToFacing(iFacing);
    	
    	tempBaseModel.addToRayTrace(rayTrace);
		
        return rayTrace.getFirstIntersection();
    }
    
    @Override
    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	if ( !world.isRemote )
    	{
    		dropItemsIndividually(world, i, j, k, BTWItems.wickerPane.itemID, 1, 0, 0.75F);
    	}
    }
    
    @Override
	public BlockModel getLidModel(int iMetadata)
    {
    	if ( getHasContents(iMetadata) )
    	{
    		return blockModelLidFull;
    	}
    	
		return blockModelLid;
    }
	
    @Override
	public Vec3 getLidRotationPoint()
	{
    	return lidRotationPoint;
	}
    
    @Override
    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
    {
    	return -(1F - BASKET_HEIGHT);
    }
    
	//------------- Class Specific Methods ------------//
	
	private void initModelBase()
	{
		blockModelBase = new BlockModel();
		
		// base of basket
		
		blockModelBase.addBox(0D + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP, 0D, 0D + BASKET_RIM_WIDTH + BASKET_DEPTH_LIP,
							  1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_HEIGHT, 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP);
	}
	
    private void initModelBaseOpenCollison()
    {
		blockModelBaseOpenCollision = new BlockModel();
		
		// base of basket
		
		blockModelBaseOpenCollision.addBox(0D + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP, 0D, 0D + BASKET_RIM_WIDTH + BASKET_DEPTH_LIP,
										   1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP, BASKET_OPEN_HEIGHT, 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP);
    }
    
	private void initModelLid()
	{
		blockModelLid = new BlockModel();
		
		// lid
		
		blockModelLid.addBox(0D + BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_HEIGHT, 0D + BASKET_DEPTH_LIP,
							 1D - BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_HEIGHT + BASKET_LID_LAYER_HEIGHT, 1D - BASKET_DEPTH_LIP);
		
		blockModelLid.addBox(0D + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_LAYER_HEIGHT, 0D + BASKET_RIM_WIDTH + BASKET_DEPTH_LIP,
							 1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP, BASKET_HEIGHT, 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP);
		
		// handle
		
		blockModelLid.addBox(0.5D - BASKET_HANDLE_HALF_LENGTH, BASKET_HEIGHT, 0.5D - BASKET_HANDLE_HALF_WIDTH,
							 0.5D + BASKET_HANDLE_HALF_LENGTH, BASKET_HEIGHT + BASKET_HANDLE_HEIGHT, 0.5D + BASKET_HANDLE_HALF_WIDTH);
	}
	
	private void initModelLidFull()
	{
		blockModelLidFull = new BlockModel();
		
		// lid
		
		blockModelLidFull.addBox(0D + BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_HEIGHT, 0D + BASKET_DEPTH_LIP,
								 1D - BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_HEIGHT + BASKET_LID_LAYER_HEIGHT, 1D - BASKET_DEPTH_LIP);
		
		blockModelLidFull.addBox(0D + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP, BASKET_HEIGHT - BASKET_LID_LAYER_HEIGHT, 0D + BASKET_RIM_WIDTH +
																													BASKET_DEPTH_LIP,
								 1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP, BASKET_HEIGHT, 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP);
		
		// top layer that replaces handle
		
		blockModelLidFull.addBox(0D + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP + BASKET_LID_LAYER_WIDTH_GAP, BASKET_HEIGHT, 0D + BASKET_RIM_WIDTH +
																													   BASKET_DEPTH_LIP +
																													   BASKET_LID_LAYER_WIDTH_GAP,
								 1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP - BASKET_LID_LAYER_WIDTH_GAP, BASKET_HEIGHT + BASKET_LID_LAYER_HEIGHT, 1D -
																																				 BASKET_RIM_WIDTH -
																																				 BASKET_DEPTH_LIP -
																																				 BASKET_LID_LAYER_WIDTH_GAP);
	}

	private void initModelInterior()
	{
		blockModelInterior = new BlockModel();
		
		// inverted interior
		
		blockModelInterior.addBox(
				1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP - BASKET_INTERIOR_WALL_THICKNESS + MIND_THE_GAP,
				BASKET_HEIGHT - BASKET_LID_HEIGHT,
				1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP - BASKET_INTERIOR_WALL_THICKNESS + MIND_THE_GAP,
				BASKET_RIM_WIDTH + BASKET_WIDTH_LIP + BASKET_INTERIOR_WALL_THICKNESS - MIND_THE_GAP, BASKET_INTERIOR_WALL_THICKNESS,
				BASKET_RIM_WIDTH + BASKET_DEPTH_LIP + BASKET_INTERIOR_WALL_THICKNESS - MIND_THE_GAP);
	}
	
	private boolean isClickingOnLid(World world, int i, int j, int k, int iSideClicked, float fXClick, float fYClick, float fZClick)
	{
		if (fYClick > BASKET_OPEN_HEIGHT)
		{
			// the only time this should be true is if the player clicks on the rim of the basket
			
			return true;
		}
        
		return false;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconBaseOpenTop;
    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconBottom;

    @Environment(EnvType.CLIENT)
    private boolean renderingBase = false;
    @Environment(EnvType.CLIENT)
    private boolean renderingInterior = false;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconBaseOpenTop = register.registerIcon("fcBlockBasketWicker_open_top");
		iconFront = register.registerIcon("fcBlockBasketWicker_front");
		iconTop = register.registerIcon("fcBlockBasketWicker_top");
		iconBottom = register.registerIcon("fcBlockBasketWicker_bottom");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide == 1 && renderingBase)
		{
			return iconBaseOpenTop;
		}		
		else if ( !renderingInterior)
		{
			if ( iSide == 1 )
			{
				return iconTop;
			}
			else if ( iSide == 0 )
			{
				return iconBottom;
			}
			else
			{				
				int iFacing = this.getFacing(iMetadata);
				
				if ( iSide == iFacing )
				{
					return iconFront;
				}
			}
		}
		
		return super.getIcon( iSide, iMetadata );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if ( iSide == 0 )
    	{
    		if (renderingInterior)
    		{
    			return false;
    		}
    		
    		return !renderingBase || super.shouldSideBeRendered(
    			blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
    	}
    	
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {    	
    	BlockModel transformedModel;
    	int iMetadata = renderer.blockAccess.getBlockMetadata( i, j, k );
    	
		int iFacing = getFacing(iMetadata);

		renderingBase = true;
		transformedModel = blockModelBase.makeTemporaryCopy();
		
		transformedModel.rotateAroundYToFacing(getFacing(renderer.blockAccess, i, j, k));

		renderer.setUVRotateTop(convertFacingToTopTextureRotation(iFacing));
		renderer.setUVRotateBottom(convertFacingToBottomTextureRotation(iFacing));
		
		boolean bReturnValue = transformedModel.renderAsBlock(renderer, this, i, j, k);

		renderingBase = false;

    	if ( !getIsOpen(iMetadata) )
    	{
	    	if ( getHasContents(iMetadata) )
	    	{
	    		transformedModel = blockModelLidFull.makeTemporaryCopy();
	    	}
	    	else
	    	{
	    		transformedModel = blockModelLid.makeTemporaryCopy();
	    	}
	    	
			transformedModel.rotateAroundYToFacing(getFacing(renderer.blockAccess, i, j, k));
	    	
			transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);
    	}
    	else
    	{
	    	transformedModel = blockModelInterior.makeTemporaryCopy();
	    	
	    	transformedModel.rotateAroundYToFacing(iFacing);

			renderingInterior = true;
	    	
	        transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);

			renderingInterior = false;
    	}		
    	
		renderer.clearUVRotation();
		
		return bReturnValue;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	blockModelLid.renderAsItemBlock(renderBlocks, this, iItemDamage);
    	blockModelBase.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, 
    	MovingObjectPosition rayTraceHit )
    {
		int i = rayTraceHit.blockX;
		int j = rayTraceHit.blockY;
		int k = rayTraceHit.blockZ;
		
		int iMetadata = world.getBlockMetadata( i, j, k );
		int iFacing = getFacing(iMetadata);
		
		double minYBox = j;		
		double maxYBox = j + BASKET_HEIGHT;
		
		if ( getIsOpen(iMetadata) )
		{
            if ( rayTraceHit.hitVec.yCoord - minYBox >= 1D - MIND_THE_GAP - LID_OPEN_LIP_HEIGHT) // check if the lid lip has been clicked
            {
            	AxisAlignedBB tempLidBox = boxCollisionLidOpenLip.makeTemporaryCopy();
		    	
            	tempLidBox.rotateAroundYToFacing(iFacing);
		    	
		        return tempLidBox.offset( i, j, k );
            }
            
			maxYBox -= BASKET_LID_HEIGHT;
		}
		
		double minXBox, maxXBox, minZBox, maxZBox;
		
		if ( iFacing == 2 || iFacing == 3 )
		{
			minXBox = (double)i + +BASKET_RIM_WIDTH + BASKET_WIDTH_LIP;
			maxXBox = (double)i + 1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP;
			
			minZBox = (double)k + +BASKET_RIM_WIDTH + BASKET_DEPTH_LIP;
			maxZBox = (double)k + 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP;
		}
		else
		{	
			minXBox = (double)i + BASKET_RIM_WIDTH + BASKET_DEPTH_LIP;
			maxXBox = (double)i + 1D - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP;
			
			minZBox = (double)k + BASKET_RIM_WIDTH + BASKET_WIDTH_LIP;
			maxZBox = (double)k + 1D - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP;
		}
		
        return AxisAlignedBB.getAABBPool().getAABB( minXBox, minYBox, minZBox, maxXBox, maxYBox, maxZBox );
    }
}