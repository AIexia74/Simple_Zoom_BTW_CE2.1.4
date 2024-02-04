// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.AxeItem;
import btw.item.items.ChiselItem;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class ChewedLogBlock extends Block {
	public final static float HARDNESS = 2F;
	
	private BlockModel[] blockModels;
	private BlockModel[] blockModelsNarrowOneSide;
	private BlockModel[] blockModelsNarrowTwoSides;
	
	private AxisAlignedBB[] boxSelectionArray;
	
	protected String sideTexture;
	protected String topTexture;
	protected String stumpTopTexture;
	
	Block logSpike;
	
	public ChewedLogBlock(int blockID, String sideTexture, String topTexture, String stumpTopTexture, Block logSpike) {
		super(blockID, BTWBlocks.logMaterial);
		
		setHardness(HARDNESS);
		setAxesEffectiveOn();
		setChiselsEffectiveOn();
		
		setBuoyant();
		
		setFireProperties(5, 5);
		
		initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
		
		initModels();
		
		Block.useNeighborBrightness[blockID] = true;
		setLightOpacity(8);
		
		setStepSound(soundWoodFootstep);
		
		this.sideTexture = sideTexture;
		this.topTexture = topTexture;
		this.stumpTopTexture = stumpTopTexture;
		
		this.logSpike = logSpike;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public float getBlockHardness(World world, int i, int j, int k) {
		float fHardness = super.getBlockHardness(world, i, j, k);
		
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (getIsStump(world, i, j, k)) {
			fHardness *= 3;
		}
		
		return fHardness;
	}
	
	@Override
	public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
		if (iFacing <= 1) {
			iMetadata = setOrientation(iMetadata, 0);
		}
		else if (iFacing <= 3) {
			iMetadata = setOrientation(iMetadata, 2);
		}
		else {
			iMetadata = setOrientation(iMetadata, 1);
		}
		
		return iMetadata;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		checkForReplaceWithSpike(world, i, j, k, iMetadata);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		int iFacing = setCurrentModelForBlock(world, i, j, k);
		
		BlockModel transformedModel = currentModel.makeTemporaryCopy();
		
		transformedModel.tiltToFacingAlongY(iFacing);
		
		return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
	}
	
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		int iOrientation = getOrientation(blockAccess, i, j, k);
		
		if (iOrientation == 0) {
			return iFacing <= 1;
		}
		else if (iOrientation == 1) {
			return iFacing >= 4;
		}
		else // 2
		{
			return iFacing == 2 || iFacing == 3;
		}
	}
	
	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
		boolean bIsStump = getIsStump(iMetadata);
		
		if (bIsStump) {
			dropBlockAsItem_do(world, i, j, k, new ItemStack(BTWItems.sawDust));
		}
		
		// last item dropped is always saw dust to encourage player to keep some chewed logs around decoratively 
		
		dropBlockAsItem_do(world, i, j, k, new ItemStack(BTWItems.sawDust));
	}
	
	@Override
	public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
		return true;
	}
	
	@Override
	public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
		int iOldMetadata = world.getBlockMetadata(i, j, k);
		int iDamageLevel = getDamageLevel(iOldMetadata);
		
		if (iDamageLevel < 3) {
			iDamageLevel++;
			
			setDamageLevel(world, i, j, k, iDamageLevel);
			
			if (!world.isRemote) {
				if (getIsStump(iOldMetadata)) {
					ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.sawDust, 1), iFromSide);
					ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.sawDust, 1), iFromSide);
				}
				else if (iDamageLevel == 1 || iDamageLevel == 3) {
					world.playAuxSFX(BTWEffectManager.SHAFT_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
					
					ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(Item.stick, 1), iFromSide);
				}
				else {
					ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.sawDust, 1), iFromSide);
				}
			}
			
			return true;
		}
		else if (!world.isRemote) {
			if (getIsStump(iOldMetadata)) {
				ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(BTWItems.sawDust, 1));
			}
			
			ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(BTWItems.sawDust, 1));
		}
		
		return false;
	}
	
	@Override
	public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k) {
		return getIsStump(blockAccess, i, j, k);
	}
	
	@Override
	public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k) {
		return getIsStump(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
		float fChanceOfPileDrop = 1.0F;
		
		if (explosion != null) {
			fChanceOfPileDrop = 1.0F / explosion.explosionSize;
		}
		
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 1, 0, fChanceOfPileDrop);
	}
	
	@Override
	public boolean getCanBlockBeIncinerated(World world, int i, int j, int k) {
		return !getIsStump(world, i, j, k);
	}
	
	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return 1000; // always convert, never harvest
	}
	
	@Override
	public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread) {
		if (getIsStump(world, i, j, k)) {
			int iNewMetadata = BTWBlocks.charredStump.setDamageLevel(0, getDamageLevel(world, i, j, k));
			
			world.setBlockAndMetadataWithNotify(i, j, k, BTWBlocks.charredStump.blockID, iNewMetadata);
		}
		else {
			super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
		}
	}
	
	//------------- Class Specific Methods ------------//
	
	private final static float RIM_WIDTH = (1F / 16F);
	
	private final static float LAYER_HEIGHT = (2F / 16F);
	private final static float FIRST_LAYER_HEIGHT = (3F / 16F);
	private final static float LAYER_WIDTH_GAP = (1F / 16F);
	
	private boolean tempPosNarrow = false;
	private boolean tempNegNarrow = false;
	
	private BlockModel currentModel;
	
	protected void initModels() {
		blockModels = new BlockModel[4];
		blockModelsNarrowOneSide = new BlockModel[4];
		blockModelsNarrowTwoSides = new BlockModel[4];
		
		boxSelectionArray = new AxisAlignedBB[4];
		
		// center colum
		
		for (int iTempIndex = 0; iTempIndex < 4; iTempIndex++) {
			BlockModel tempModel = blockModels[iTempIndex] = new BlockModel();
			BlockModel tempNarrowOneSide = blockModelsNarrowOneSide[iTempIndex] = new BlockModel();
			BlockModel tempNarrowTwoSides = blockModelsNarrowTwoSides[iTempIndex] = new BlockModel();
			
			float fCenterColumnWidthGap = RIM_WIDTH + (LAYER_WIDTH_GAP * iTempIndex);
			float fCenterColumnHeightGap = 0F;
			
			if (iTempIndex > 0) {
				fCenterColumnHeightGap = FIRST_LAYER_HEIGHT + (LAYER_HEIGHT * (iTempIndex - 1));
			}
			
			tempModel.addBox(fCenterColumnWidthGap, fCenterColumnHeightGap, fCenterColumnWidthGap, 1F - fCenterColumnWidthGap, 1F - fCenterColumnHeightGap,
					1F - fCenterColumnWidthGap);
			
			tempNarrowOneSide
					.addBox(fCenterColumnWidthGap, fCenterColumnHeightGap, fCenterColumnWidthGap, 1F - fCenterColumnWidthGap, 1F, 1F - fCenterColumnWidthGap);
			
			tempNarrowTwoSides.addBox(fCenterColumnWidthGap, 0, fCenterColumnWidthGap, 1F - fCenterColumnWidthGap, 1F, 1F - fCenterColumnWidthGap);
			
			AxisAlignedBB tempSelection = boxSelectionArray[iTempIndex] =
					new AxisAlignedBB(fCenterColumnWidthGap, 0, fCenterColumnWidthGap, 1F - fCenterColumnWidthGap, 1F, 1F - fCenterColumnWidthGap);
		}
		
		// first layer
		
		for (int iTempIndex = 1; iTempIndex < 4; iTempIndex++) {
			// bottom
			
			blockModels[iTempIndex].addBox(RIM_WIDTH, 0, RIM_WIDTH, 1F - RIM_WIDTH, FIRST_LAYER_HEIGHT, 1F - RIM_WIDTH);
			blockModelsNarrowOneSide[iTempIndex].addBox(RIM_WIDTH, 0, RIM_WIDTH, 1F - RIM_WIDTH, FIRST_LAYER_HEIGHT, 1F - RIM_WIDTH);
			
			// top
			
			blockModels[iTempIndex].addBox(RIM_WIDTH, 1F - FIRST_LAYER_HEIGHT, RIM_WIDTH, 1F - RIM_WIDTH, 1F, 1F - RIM_WIDTH);
			
		}
		
		// second layer
		
		float fWidthGap = RIM_WIDTH + LAYER_WIDTH_GAP;
		float fHeightGap = FIRST_LAYER_HEIGHT;
		
		for (int iTempIndex = 2; iTempIndex < 4; iTempIndex++) {
			// second layer bottom
			
			blockModels[iTempIndex].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
			blockModelsNarrowOneSide[iTempIndex].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
			
			// second layer top
			
			blockModels[iTempIndex].addBox(fWidthGap, 1F - fHeightGap - LAYER_HEIGHT, fWidthGap, 1F - fWidthGap, 1F - fHeightGap, 1F - fWidthGap);
		}
		
		// third layer bottom
		
		fWidthGap = RIM_WIDTH + (LAYER_WIDTH_GAP * 2);
		fHeightGap = FIRST_LAYER_HEIGHT + LAYER_HEIGHT;
		
		blockModels[3].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
		blockModelsNarrowOneSide[3].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
		
		// third layer top
		
		blockModels[3].addBox(fWidthGap, 1F - fHeightGap - LAYER_HEIGHT, fWidthGap, 1F - fWidthGap, 1F - LAYER_HEIGHT, 1F - fWidthGap);
	}
	
	public void setDamageLevel(World world, int i, int j, int k, int iDamageLevel) {
		int iMetadata = (world.getBlockMetadata(i, j, k)) & (~3);
		
		iMetadata |= iDamageLevel;
		
		if (!checkForReplaceWithSpike(world, i, j, k, iMetadata)) {
			world.setBlockMetadataWithNotify(i, j, k, iMetadata);
		}
	}
	
	private boolean checkForReplaceWithSpike(World world, int i, int j, int k, int iMetadata) {
		if (getDamageLevel(iMetadata) == 3 && !getIsStump(iMetadata)) {
			int iFacing = setConnectionFlagsForBlock(world, i, j, k, iMetadata);
			
			if (tempPosNarrow != tempNegNarrow) {
				BlockPos targetPos = new BlockPos(i, j, k);
				targetPos.addFacingAsOffset(iFacing);
				
				int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
				Block targetBlock = Block.blocksList[iTargetBlockID];
				
				if (iTargetBlockID != this.blockID || getOrientation(iMetadata) != getOrientation(world, targetPos.x, targetPos.y, targetPos.z)) {
					world.setBlockAndMetadataWithNotify(i, j, k, this.logSpike.blockID, iFacing);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getDamageLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return getDamageLevel(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getDamageLevel(int iMetadata) {
		return iMetadata & 3;
	}
	
	/**
	 * Uses same orientation as BlockLog.  0 is J aligned.  1 is I aligned.  2 is K aligned.
	 */
	public int getOrientation(IBlockAccess blockAccess, int i, int j, int k) {
		return getOrientation(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getOrientation(int iMetadata) {
		int iOrientation = (iMetadata >> 2) & 3;
		
		if (iOrientation == 3) // stump
		{
			iOrientation = 0;
		}
		
		return iOrientation;
	}
	
	public int setOrientation(int iMetadata, int iOrientation) {
		if (!getIsStump(iMetadata)) {
			iMetadata |= (iOrientation << 2);
		}
		
		return iMetadata;
	}
	
	static public boolean getIsStump(IBlockAccess blockAccess, int i, int j, int k) {
		return getIsStump(blockAccess.getBlockMetadata(i, j, k));
	}
	
	static public boolean getIsStump(int iMetadata) {
		return (iMetadata & 12) == 12;
	}
	
	public int setIsStump(int iMetadata) {
		iMetadata |= 12;
		
		return iMetadata;
	}
	
	public int setCurrentModelForBlock(IBlockAccess blockAccess, int i, int j, int k) {
		int iFacing = setConnectionFlagsForBlock(blockAccess, i, j, k);
		int iDamageLevel = getDamageLevel(blockAccess, i, j, k);
		
		if (tempPosNarrow) {
			if (tempNegNarrow) {
				currentModel = blockModelsNarrowTwoSides[iDamageLevel];
			}
			else {
				currentModel = blockModelsNarrowOneSide[iDamageLevel];
			}
		}
		else {
			currentModel = blockModels[iDamageLevel];
			
		}
		
		return iFacing;
	}
	
	public int setConnectionFlagsForBlock(IBlockAccess blockAccess, int i, int j, int k) {
		return setConnectionFlagsForBlock(blockAccess, i, j, k, blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int setConnectionFlagsForBlock(IBlockAccess blockAccess, int i, int j, int k, int iMetadata) {
		int iOrientation = getOrientation(iMetadata);
		int iFacing = 1;
		
		if (iOrientation == 1) {
			iFacing = 5;
		}
		else if (iOrientation == 2) {
			iFacing = 3;
		}
		
		tempPosNarrow = true;
		tempNegNarrow = true;
		
		BlockPos targetPos = new BlockPos(i, j, k);
		targetPos.addFacingAsOffset(iFacing);
		
		int iTargetBlockID = blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		Block targetBlock = Block.blocksList[iTargetBlockID];
		
		if (doesTargetBlockConnectToFacing(iOrientation, blockAccess, targetPos.x, targetPos.y, targetPos.z, Block.getOppositeFacing(iFacing))) {
			tempPosNarrow = false;
		}
		
		targetPos.set(i, j, k);
		targetPos.addFacingAsOffset(Block.getOppositeFacing(iFacing));
		
		iTargetBlockID = blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		targetBlock = Block.blocksList[iTargetBlockID];
		
		if (getIsStump(iMetadata) || doesTargetBlockConnectToFacing(iOrientation, blockAccess, targetPos.x, targetPos.y, targetPos.z, iFacing)) {
			tempNegNarrow = false;
		}
		
		if (tempPosNarrow == false && tempNegNarrow == true) {
			iFacing = Block.getOppositeFacing(iFacing);
			
			tempPosNarrow = true;
			tempNegNarrow = false;
		}
		
		return iFacing;
	}
	
	public boolean doesTargetBlockConnectToFacing(int iMyOrientation, IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
		int targetBlockID = blockAccess.getBlockId(i, j, k);
		Block targetBlock = Block.blocksList[targetBlockID];
		
		if (targetBlock == null) {
			return false;
		}
		
		if (targetBlock instanceof ChewedLogBlock) {
			return getDamageLevel(blockAccess, i, j, k) == 0 && iMyOrientation == getOrientation(blockAccess, i, j, k);
		}
		else if (targetBlock instanceof LogSpikeBlock) {
			return BTWBlocks.oakLogSpike.getFacing(blockAccess, i, j, k) == Block.getOppositeFacing(iFacing);
		}
		else if (targetBlock.isLog(blockAccess, i, j, k)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isItemEffectiveConversionTool(ItemStack stack, World world, int i, int j, int k) {
		if (stack != null) {
			Item item = stack.getItem();
			
			if (item instanceof ChiselItem || item instanceof AxeItem || item == BTWItems.battleaxe) {
				return true;
			}
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private Icon iconSide;
	@Environment(EnvType.CLIENT)
	private Icon iconStumpTop;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon(sideTexture);
		iconSide = register.registerIcon(topTexture);
		iconStumpTop = register.registerIcon(stumpTopTexture);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		int iOrientation = this.getOrientation(iMetadata);
		
		Icon icon = blockIcon;
		
		if (iOrientation == 0) // j aligned
		{
			if (iSide >= 2) {
				icon = iconSide;
			}
			else if (iSide == 1 && getIsStump(iMetadata)) {
				icon = iconStumpTop;
			}
		}
		else if (iOrientation == 1) // i aligned
		{
			if (iSide != 4 && iSide != 5) {
				icon = iconSide;
			}
		}
		else // k aligned
		{
			if (iSide != 2 && iSide != 3) {
				icon = iconSide;
			}
		}
		
		return icon;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
		int facing = setCurrentModelForBlock(renderBlocks.blockAccess, x, y, z);
		BlockModel transformedModel = currentModel.makeTemporaryCopy();
		transformedModel.tiltToFacingAlongY(facing);
		
		int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		
		if (metadata == 4 || metadata == 5 || metadata == 6 || metadata == 7) {
			renderBlocks.setUVRotateTop(1);
			renderBlocks.setUVRotateBottom(1);
			renderBlocks.setUVRotateWest(1);
			renderBlocks.setUVRotateEast(1);
		}
		else if (metadata == 8 || metadata == 9 || metadata == 10 || metadata == 11) {
			renderBlocks.setUVRotateNorth(1);
			renderBlocks.setUVRotateSouth(1);
		}
		
		transformedModel.renderAsBlock(renderBlocks, this, x, y, z);
		renderBlocks.clearUVRotation();
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		blockModelsNarrowOneSide[iItemDamage].renderAsItemBlock(renderBlocks, this, iItemDamage);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void getSubBlocks(int iBlockID, CreativeTabs creativeTabs, List list) {
		for (int iTempIndex = 0; iTempIndex < 4; iTempIndex++) {
			list.add(new ItemStack(iBlockID, 1, iTempIndex));
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		int iFacing = setConnectionFlagsForBlock(world, i, j, k);
		int iDamageLevel = getDamageLevel(world, i, j, k);
		
		AxisAlignedBB tempSelectionBox = boxSelectionArray[iDamageLevel].makeTemporaryCopy();
		
		tempSelectionBox.tiltToFacingAlongY(iFacing);
		
		return tempSelectionBox.offset(i, j, k);
	}
}  
