// FCMOD

package btw.block.blocks;

import java.util.Random;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.block.MechanicalBlock;
import btw.block.tileentity.HopperTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.inventory.BTWContainers;
import btw.inventory.container.HopperContainer;
import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.HopperFilteringCraftingManager;
import btw.crafting.recipe.types.HopperFilterRecipe;
import btw.block.util.MechPowerUtils;
import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11; //client only

public class HopperBlock extends BlockContainer
implements MechanicalBlock
{
	protected static final int TICK_RATE = 10;

	protected static final float SPOUT_HEIGHT = 0.25F;
	protected static final float SPOUT_WIDTH = (6F / 16F );
	protected static final float SPOUT_HALF_WIDTH = (SPOUT_WIDTH / 2F );

	public HopperBlock(int iBlockID)
	{
		super( iBlockID, BTWBlocks.plankMaterial);

		setHardness( 2F );

		setAxesEffectiveOn(true);

		setBuoyancy(1F);

		setFireProperties(Flammability.PLANKS);

		initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

		setStepSound( soundWoodFootstep );

		setUnlocalizedName( "fcBlockHopper" );

		setTickRandomly( true );

		setCreativeTab( CreativeTabs.tabRedstone );
	}	

	@Override
	public int tickRate( World world )
	{
		return TICK_RATE;
	}

	@Override
	public void onBlockAdded( World world, int i, int j, int k )
	{
		super.onBlockAdded( world, i, j, k );

		world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
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
	public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
	{
		boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);

		if (isBlockOn(world, i, j, k) != bReceivingPower &&
			!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{    		
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}

		// the change in state may unblock the hopper, and this forces a recheck

		((HopperTileEntity)world.getBlockTileEntity( i, j, k )).outputBlocked = false;;
	}

	@Override
	public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
	{
		if ( !world.isRemote )
		{
			HopperTileEntity tileEntityHopper = (HopperTileEntity)world.getBlockTileEntity( i, j, k );

			if ( player instanceof EntityPlayerMP ) // should always be true
			{
				HopperContainer container = new HopperContainer( player.inventory, tileEntityHopper );

				BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.hopperContainerID);
			}
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity( World world )
	{
		return new HopperTileEntity();
	}

	@Override
	public void updateTick( World world, int i, int j, int k, Random random )
	{
		boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
		boolean bOn = isBlockOn(world, i, j, k);

		if ( bOn != bReceivingPower )
		{
			world.playAuxSFX( BTWEffectManager.HOPPER_CLOSE_EFFECT_ID, i, j, k, 0 );

			setBlockOn(world, i, j, k, bReceivingPower);
		}
	}

	@Override
	public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
	{
		InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)world.getBlockTileEntity(i, j, k));

		super.breakBlock( world, i, j, k, iBlockID, iMetadata );
	}

	@Override
	public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
	{
		// don't collect items on the client, as it's dependent on the state of the inventory

		if ( !world.isRemote )
		{
			if ( entity instanceof EntityItem )
			{
				onEntityItemCollidedWithBlock(world, i, j, k, (EntityItem)entity);
			}
			else if ( entity instanceof EntityXPOrb )
			{
				onEntityXPOrbCollidedWithBlock(world, i, j, k, (EntityXPOrb)entity);
			}
		}		
	}

	@Override
	public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
	{
		RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );

		// spout

		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5D - SPOUT_HALF_WIDTH, 0D, 0.5D - SPOUT_HALF_WIDTH,
														 0.5D + SPOUT_HALF_WIDTH, SPOUT_HEIGHT, 0.5D + SPOUT_HALF_WIDTH);

		// body

		rayTrace.addBoxWithLocalCoordsToIntersectionList(0D, SPOUT_HEIGHT, 0D, 1D, 1D, 1D);

		return rayTrace.getFirstIntersection();
	}

	@Override
	public void onArrowCollide(World world, int i, int j, int k, EntityArrow arrow)
	{
		if ( !world.isRemote && !arrow.isDead && arrow.canHopperCollect() )
		{    	
			// check if arrow is within the collection zone       

			Vec3 arrowPos = Vec3.createVectorHelper( arrow.posX, arrow.posY, arrow.posZ );

			AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB( (float)i, (float)j + 0.9F, (float)k, 
					(float)(i + 1), (float)j + 1.1F, (float)(k + 1) );

			if ( collectionZone.isVecInside( arrowPos ) )
			{
				HopperTileEntity tileEntityHopper = (HopperTileEntity)world.getBlockTileEntity( i, j, k );

				ItemStack newItemStack = new ItemStack(arrow.getCorrespondingItem(), 1, 0 );

				if ( tileEntityHopper.canCurrentFilterProcessItem(newItemStack) )
				{
					if ( InventoryUtils.addItemStackToInventoryInSlotRange(tileEntityHopper,
                                                                           newItemStack, 0, 17) )
					{        				
						arrow.setDead();

						world.playAuxSFX( BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID,
								i, j, k, 0 );							        
					}        				
				}
			}
		}
	}

	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 2; // iron or better
	}

	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.gear.itemID, 1, 0, fChanceOfDrop);

		return true;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
	{
		return Container.calcRedstoneFromInventory(((IInventory) par1World.getBlockTileEntity(par2, par3, par4)));
	}

	//------------- FCIBlockMechanical -------------//

	@Override
	public boolean canOutputMechanicalPower()
	{
		return false;
	}

	@Override
	public boolean canInputMechanicalPower()
	{
		return true;
	}

	@Override
	public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
	{
		return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, this) ||
			   MechPowerUtils.isBlockPoweredByHandCrank(world, i, j, k);
	}    

	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		return iFacing >= 2;
	}

	@Override
	public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
	{
		return false;
	}

	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakHopper(world, i, j, k);
	}

	//------------- Class Specific Methods ------------//

	public boolean isBlockOn(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return ( iBlockAccess.getBlockMetadata( i, j, k ) & 1 ) > 0;    
	}

	public void setBlockOn(World world, int i, int j, int k, boolean bOn)
	{
		int iMetaData = world.getBlockMetadata( i, j, k ); // filter out old on state

		if ( bOn )
		{
			iMetaData |= 1;
		}
		else
		{
			iMetaData &= ~1;
		}

		world.setBlockMetadataWithNotifyNoClient( i, j, k, iMetaData );
	}

	public boolean hasFilter(IBlockAccess iBlockAccess, int i, int j, int k)
	{	
		return ( iBlockAccess.getBlockMetadata( i, j, k ) & 8 ) > 0;
	}

	public void setHasFilter(World world, int i, int j, int k, boolean bOn)
	{
		int iMetaData = world.getBlockMetadata( i, j, k );

		if ( bOn )
		{
			iMetaData |= 8;
		}
		else
		{
			iMetaData &= ~8;
		}

		world.setBlockMetadataWithNotifyNoClient( i, j, k, iMetaData );
	}

	public void breakHopper(World world, int i, int j, int k)
	{
		dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);

		world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );

		world.setBlockWithNotify( i, j, k, 0 );
	}

	private void onEntityItemCollidedWithBlock(World world, int x, int y, int z, EntityItem entityItem) {
		// check if item is within the collection zone       
		float hopperHeight = 1F;

		AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB( 
				x, y + hopperHeight, z, 
				x + 1, y + hopperHeight + 0.05F, z + 1
				);

		if (entityItem.boundingBox.intersectsWith(collectionZone)) {    	
			if (!entityItem.isDead) {
				Item targetItem = Item.itemsList[entityItem.getEntityItem().itemID];
				ItemStack targetStack = entityItem.getEntityItem();

				HopperTileEntity tileEntityHopper = (HopperTileEntity)world.getBlockTileEntity(x, y, z);

				if (tileEntityHopper.canCurrentFilterProcessItem(targetStack)) {
					Item filterItem = tileEntityHopper.getFilterItem();
					HopperFilterRecipe recipe = filterItem == null ?
							null : HopperFilteringCraftingManager.instance.getRecipe(targetStack, new ItemStack(filterItem));

					if (recipe != null) {
						if (recipe.getContainsSouls()) {
							ItemStack filteredOutput = recipe.getFilteredOutput();

							convertItemAndIncrementSouls(world, x, y, z, entityItem, filteredOutput.itemID, filteredOutput.getItemDamage());
						}
						else {
							ItemStack hopperOutput = recipe.getHopperOutput().copy();

							int quantityToAttemptToSwallow = targetStack.stackSize * hopperOutput.stackSize;
							int quantityToSwallow = 0;

							int availableSpots = InventoryUtils.getMaxNumberOfItemsForTransferInRange(tileEntityHopper, hopperOutput, quantityToAttemptToSwallow, 0, 17);

							if (availableSpots == 0) {
								return;
							}

							if (quantityToAttemptToSwallow <= availableSpots) {
								quantityToSwallow = quantityToAttemptToSwallow;
								entityItem.setDead();
							}
							else {
								quantityToSwallow = availableSpots;

								//Makes sure the output is a multiple of the input
								quantityToSwallow /= hopperOutput.stackSize;
								quantityToSwallow *= hopperOutput.stackSize;
							}

							int quantityProcessed = quantityToSwallow / hopperOutput.stackSize;

							if (!entityItem.isDead) {
								entityItem.getEntityItem().stackSize -= quantityProcessed;
							}

							if (quantityProcessed > 0) {
								while (quantityToSwallow > 0) {
									ItemStack stackToTransfer = hopperOutput.copy();

									if (quantityToSwallow >= stackToTransfer.getMaxStackSize()) {
										stackToTransfer.stackSize = stackToTransfer.getMaxStackSize();
									}
									else {
										stackToTransfer.stackSize = quantityToSwallow;
									}

									quantityToSwallow -= stackToTransfer.stackSize;

									InventoryUtils.addItemStackToInventoryInSlotRange(tileEntityHopper, stackToTransfer, 0, 17);
								}
								
								world.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, x, y, z, 0);

								if (recipe.getFilteredOutput() != null) {
									ItemStack filteredOutput = recipe.getFilteredOutput().copy();
									int outputCount = filteredOutput.stackSize;

									for (int i = 0; i < outputCount; i++) {
										filteredOutput.stackSize = quantityProcessed;

										EntityItem filteredEntity = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world,
												entityItem.posX, entityItem.posY, entityItem.posZ,
												filteredOutput.copy());

										filteredEntity.delayBeforeCanPickup = 10;

										world.spawnEntityInWorld(filteredEntity);
									}
								}
							}
						}
					}
					else if (InventoryUtils.addItemStackToInventoryInSlotRange(tileEntityHopper, entityItem.getEntityItem(), 0, 17)) {
						world.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, x, y, z, 0);

						entityItem.setDead();
					}
				}		        
			}
		}
	}

	private void convertItemAndIncrementSouls(World world, int i, int j, int k, EntityItem inputEntityItem, int iOutputItemID, int iOutputItemDamage)
	{
		HopperTileEntity tileEntityHopper = (HopperTileEntity)world.getBlockTileEntity( i, j, k );

		ItemStack outputItemStack = new ItemStack(  iOutputItemID, 
				inputEntityItem.getEntityItem().stackSize, iOutputItemDamage );

		EntityItem outputEntityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world,
				inputEntityItem.posX, inputEntityItem.posY, inputEntityItem.posZ,
				outputItemStack );

		outputEntityItem.delayBeforeCanPickup = 10;

		tileEntityHopper.incrementContainedSoulCount(outputItemStack.stackSize);

		world.spawnEntityInWorld( outputEntityItem );

		world.playAuxSFX( BTWEffectManager.GHAST_MOAN_EFFECT_ID, i, j, k, 0 );

		inputEntityItem.setDead();
	}

	private void onEntityXPOrbCollidedWithBlock(World world, int i, int j, int k, EntityXPOrb entityXPOrb)
	{
		if ( !entityXPOrb.isDead )
		{
			// check if item is within the collection zone

			float fHopperHeight = 1F;

			AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB( (float)i, (float)j + fHopperHeight, (float)k, 
					(float)(i + 1), (float)j + fHopperHeight + 0.05F, (float)(k + 1) );

			if ( entityXPOrb.boundingBox.intersectsWith( collectionZone ) )
			{    	
				HopperTileEntity tileEntityHopper = (HopperTileEntity)world.getBlockTileEntity( i, j, k );

				Item filterItem = tileEntityHopper.getFilterItem();

				if ( filterItem != null && filterItem.itemID == Block.slowSand.blockID )
				{
					if ( tileEntityHopper.attemptToSwallowXPOrb(world, i, j, k, entityXPOrb) )
					{
						world.playAuxSFX( BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, i, j, k, 0 );
					}
				}	    		
			}
		}
	}	

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBinBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon[] iconNozzleBySideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon iconInteriorSide;
    @Environment(EnvType.CLIENT)
    private Icon iconInteriorTop;

    @Environment(EnvType.CLIENT)
    private Icon iconContents;

    @Environment(EnvType.CLIENT)
    private boolean isRenderingNozzle = false;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
	{
		iconBinBySideArray[0] = register.registerIcon("fcBlockHopperBin_bottom");
		iconBinBySideArray[1] = register.registerIcon("fcBlockHopperBin_top");

		Icon sideIcon = register.registerIcon( "fcBlockHopperBin_side" );

		iconBinBySideArray[2] = sideIcon;
		iconBinBySideArray[3] = sideIcon;
		iconBinBySideArray[4] = sideIcon;
		iconBinBySideArray[5] = sideIcon;

		blockIcon = sideIcon; // for hit effects

		iconNozzleBySideArray[0] = register.registerIcon("fcBlockHopperNozzle_bottom");
		iconNozzleBySideArray[1] = iconBinBySideArray[1]; // the nozzle doesn't have a visible top

		sideIcon = register.registerIcon( "fcBlockHopperNozzle_side" );

		iconNozzleBySideArray[2] = sideIcon;
		iconNozzleBySideArray[3] = sideIcon;
		iconNozzleBySideArray[4] = sideIcon;
		iconNozzleBySideArray[5] = sideIcon;

		iconInteriorSide = register.registerIcon("fcBlockHopperInterior_side");
		iconInteriorTop = register.registerIcon("fcBlockHopperInterior_top");

		iconContents = register.registerIcon("fcBlockHopper_contents");
	}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
	{
		if (isRenderingNozzle)
		{
			return iconNozzleBySideArray[iSide];
		}
		else
		{
			return iconBinBySideArray[iSide];
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
	{
		// determines the size of the selection box around the block

		return AxisAlignedBB.getAABBPool().getAABB(0D, SPOUT_HEIGHT, 0D,
												   1D, 1D, 1D ).offset( i, j, k );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
	{
		return true;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{	
		IBlockAccess blockAccess = renderer.blockAccess;

		// render the bin

		isRenderingNozzle = false;

		renderer.setRenderBounds(0F, SPOUT_HEIGHT, 0F, 1F, 1F, 1F);

		renderer.renderStandardBlock( this, i, j, k );

		// render the interior of the bin

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness( getMixedBrightnessForBlock( blockAccess, i, j, k ) );

		final float fInteriorBrightnessMultiplier = 0.66F;

		int iColorMultiplier = colorMultiplier( blockAccess, i, j, k );

		float iColorRed = (float)( iColorMultiplier >> 16 & 255 ) / 255F;
		float iColorGreen = (float)( iColorMultiplier >> 8 & 255 ) / 255F;
		float iColorBlue = (float)( iColorMultiplier & 255 ) / 255F;

		tesselator.setColorOpaque_F( fInteriorBrightnessMultiplier * iColorRed, fInteriorBrightnessMultiplier * iColorGreen, fInteriorBrightnessMultiplier * iColorBlue );

		final double dInteriorOffset = ( 2D / 16D ) - 0.001D; // slight extra bit to avoid texture glitches around edges

		renderer.renderFaceXPos(this, i - 1D + dInteriorOffset, j, k, iconInteriorSide);
		renderer.renderFaceXNeg(this, i + 1D - dInteriorOffset, j, k, iconInteriorSide);
		renderer.renderFaceZPos(this, i, j, k - 1D + dInteriorOffset, iconInteriorSide);
		renderer.renderFaceZNeg(this, i, j, k + 1D - dInteriorOffset, iconInteriorSide);

		renderer.renderFaceYPos(this, i, j - 1D + 0.25D, k, iconInteriorTop);

		// render the nozzle

		isRenderingNozzle = true;

		renderer.setRenderBounds(
				0.5D - SPOUT_HALF_WIDTH, 0F, 0.5D - SPOUT_HALF_WIDTH,
				0.5D + SPOUT_HALF_WIDTH, SPOUT_HEIGHT, 0.5D + SPOUT_HALF_WIDTH);

		renderer.renderStandardBlock( this, i, j, k );

		// render the contents

		TileEntity tileEntity = blockAccess.getBlockTileEntity(i, j, k );
		
		if (tileEntity instanceof HopperTileEntity) {
			HopperTileEntity tileEntityHopper = (HopperTileEntity) tileEntity;
			
			short iItemCount = ((HopperTileEntity) tileEntity).storageSlotsOccupied;
			
			if (iItemCount > 0) {
				float fHeightRatio = (float) iItemCount / 18F;
				
				float fBottom = 6F / 16F;
				
				float fTop = fBottom + (1F / 16F) + (((1F - (2F / 16F)) - (fBottom + (1F / 16F))) * fHeightRatio);
				
				renderer.setRenderBounds(2F / 16F, fBottom, 2F / 16F, 1F - (2F / 16F), fTop, 1F - (2F / 16F));
				
				RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconContents);
			}
			
			// render the filter
			
			Item filterItem = tileEntityHopper.getCurrentFilterItem();
			
			if (filterItem != null) {
				Icon filterIcon = filterItem.getHopperFilterIcon();
				
				if (filterIcon != null) {
					renderer.setRenderBounds(2F / 16F, 1F - (2F / 16F), 2F / 16F, 1F - (2F / 16F), 1F - (1F / 16F), 1F - (2F / 16F));
					
					RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, filterIcon);
				}
			}
		}
		
		return true;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
	{
		IBlockAccess blockAccess = renderBlocks.blockAccess;

		// render the bin

		isRenderingNozzle = false;

		renderBlocks.setRenderBounds(0D, SPOUT_HEIGHT, 0D, 1D, 1D, 1D);

		RenderUtils.renderInvBlockWithMetadata(renderBlocks, this,
											   -0.5F, -0.5F, -0.5F, 0);

		// render the interior of the bin

		Tessellator tessellator = Tessellator.instance;

		final double dInteriorOffset = ( 2D / 16D ) - 0.001D; // slight extra bit to avoid texture glitches around edges

		GL11.glTranslatef( -0.5F, -0.5F, -0.5F );

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderBlocks.renderFaceYPos(this, 0.0D, -0.75F, 0.0D, iconInteriorTop);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderBlocks.renderFaceZNeg(this, 0.0D, 0.0D, 1.0F - dInteriorOffset, iconInteriorSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderBlocks.renderFaceZPos(this, 0.0D, 0.0D, -1.0F + dInteriorOffset, iconInteriorSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderBlocks.renderFaceXNeg(this, 1.0F - dInteriorOffset, 0.0D, 0.0D, iconInteriorSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderBlocks.renderFaceXPos(this, -1.0D + dInteriorOffset, 0.0D, 0.0D, iconInteriorSide);
		tessellator.draw();

		GL11.glTranslatef( 0.5F, 0.5F, 0.5F );        

		// render the nozzle

		isRenderingNozzle = true;

		renderBlocks.setRenderBounds(
				0.5D - SPOUT_HALF_WIDTH, 0F, 0.5D - SPOUT_HALF_WIDTH,
				0.5D + SPOUT_HALF_WIDTH, SPOUT_HEIGHT, 0.5D + SPOUT_HALF_WIDTH);

		RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);
	}    
}
