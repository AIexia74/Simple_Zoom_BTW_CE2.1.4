// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.block.model.LogSpikeModel;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LogSpikeBlock extends Block {
	public final static float HARDNESS = 2F;
	
	private BlockModel modelBlock = new LogSpikeModel();
	
	protected String sideTexture;
	protected String topTexture;
	
	public LogSpikeBlock(int blockID, String sideTexture, String topTexture) {
		super(blockID, BTWBlocks.logMaterial);
		
		setHardness(HARDNESS);
		setAxesEffectiveOn();
		setChiselsEffectiveOn();
		
		setBuoyant();
		
		setFireProperties(5, 5);
		
		Block.useNeighborBrightness[blockID] = true;
		setLightOpacity(4);
		
		setStepSound(soundWoodFootstep);
		
		this.sideTexture = sideTexture;
		this.topTexture = topTexture;
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
	public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
		iMetadata = setFacing(iMetadata, iFacing);
		
		return iMetadata;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		int iFacing = getFacing(world, i, j, k);
		
		BlockModel transformedModel = modelBlock.makeTemporaryCopy();
		
		transformedModel.tiltToFacingAlongY(iFacing);
		
		return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		return iFacing == Block.getOppositeFacing(iFacing);
	}
	
	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
		// last item dropped is always saw dust to encourage player to keep some chewed logs around decoratively 
		
		dropBlockAsItem_do(world, i, j, k, new ItemStack(BTWItems.sawDust));
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
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return 1000; // always convert, never harvest
	}
	
	@Override
	public int getFacing(int iMetadata) {
		int iFacing = (iMetadata) & 7;
		
		return iFacing;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing) {
		iMetadata &= (~7);
		
		return iMetadata | iFacing;
	}
	
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private Icon iconSide;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon(this.sideTexture);
		iconSide = register.registerIcon(this.topTexture);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		int iFacing = getFacing(iMetadata);
		
		if (iSide == iFacing || iSide == Block.getOppositeFacing(iFacing)) {
			return blockIcon;
		}
		
		return iconSide;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
		int facing = this.getFacing(renderBlocks.blockAccess, x, y, z);
		BlockModel model = this.modelBlock.makeTemporaryCopy();
		model.tiltToFacingAlongY(facing);
		
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
		
		model.renderAsBlock(renderBlocks, this, x, y, z);
		renderBlocks.clearUVRotation();
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		modelBlock.renderAsItemBlock(renderBlocks, this, iItemDamage);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		int iFacing = getFacing(world, i, j, k);
		
		AxisAlignedBB tempSelectionBox = LogSpikeModel.boxSelection.makeTemporaryCopy();
		
		tempSelectionBox.tiltToFacingAlongY(iFacing);
		
		return tempSelectionBox.offset(i, j, k);
	}
}
