// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class MouldingAndDecorativeBlock extends MouldingBlock {
	public static final int SUBTYPE_COLUMN = 12;
	public static final int SUBTYPE_PEDESTAL_UP = 13;
	public static final int SUBTYPE_PEDESTAL_DOWN = 14;
	public static final int SUBTYPE_TABLE = 15;
	
	protected static final double COLUM_WIDTH = (10D / 16D);
	protected static final double COLUM_HALF_WIDTH = (COLUM_WIDTH / 2D);
	
	protected static final double PEDESTAL_BASE_HEIGHT = (12D / 16D);
	protected static final double PEDESTAL_MIDDLE_HEIGHT = (2D / 16D);
	protected static final double PEDESTAL_MIDDLE_WIDTH = (14D / 16D);
	protected static final double PEDESTAL_MIDDLE_HALF_WIDTH = (PEDESTAL_MIDDLE_WIDTH / 2D);
	protected static final double PEDESTAL_TOP_HEIGHT = (2D / 16D);
	protected static final double PEDESTAL_TOP_WIDTH = (12D / 16D);
	protected static final double PEDESTAL_TOP_HALF_WIDTH = (PEDESTAL_TOP_WIDTH / 2D);
	
	protected static final double TABLE_TOP_HEIGHT = (2D / 16D);
	protected static final double TABLE_LEG_HEIGHT = (1D - TABLE_TOP_HEIGHT);
	protected static final double TABLE_LEG_WIDTH = (4D / 16D);
	protected static final double TABLE_LEG_HALF_WIDTH = (TABLE_LEG_WIDTH / 2D);
	
	String columnSideTextureName;
	String columnTopAndBottomTextureName;
	String pedestalSideTextureName;
	String pedestalTopAndBottomTextureName;
	
	public MouldingAndDecorativeBlock(int iBlockID, Material material, String sTextureName, String sColumnSideTextureName, int iMatchingCornerBlockID,
			float fHardness, float fResistance, StepSound stepSound, String name) {
		super(iBlockID, material, sTextureName, iMatchingCornerBlockID, fHardness, fResistance, stepSound, name);
		
		columnSideTextureName = sColumnSideTextureName;
		columnTopAndBottomTextureName = sTextureName;
		pedestalSideTextureName = sTextureName;
		pedestalTopAndBottomTextureName = sTextureName;
	}
	
	public MouldingAndDecorativeBlock(int iBlockID, Material material, String sTextureName, String sColumnSideTextureName,
			String sColumnTopAndBottomTextureName, String sPedestalSideTextureName, String sPedestalTopAndBottomTextureName, int iMatchingCornerBlockID,
			float fHardness, float fResistance, StepSound stepSound, String name) {
		super(iBlockID, material, sTextureName, iMatchingCornerBlockID, fHardness, fResistance, stepSound, name);
		
		columnSideTextureName = sColumnSideTextureName;
		columnTopAndBottomTextureName = sColumnTopAndBottomTextureName;
		pedestalSideTextureName = sPedestalSideTextureName;
		pedestalTopAndBottomTextureName = sPedestalTopAndBottomTextureName;
	}
	
	@Override
	public int damageDropped(int iMetadata) {
		if (!isDecorative(iMetadata)) {
			return super.damageDropped(iMetadata);
		}
		
		if (iMetadata == SUBTYPE_PEDESTAL_DOWN) {
			iMetadata = SUBTYPE_PEDESTAL_UP;
		}
		
		return iMetadata;
	}
	
	@Override
	public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
		int iSubtype = iMetadata;
		
		if (iSubtype == SUBTYPE_PEDESTAL_UP) {
			if (iFacing == 0 || iFacing != 1 && (double) fClickY > 0.5D) {
				return SUBTYPE_PEDESTAL_DOWN;
			}
		}
		else if (!isDecorative(iMetadata)) {
			return super.onBlockPlaced(world, i, j, k, iFacing, fClickX, fClickY, fClickZ, iMetadata);
		}
		
		return iMetadata;
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (isDecorative(iMetadata)) {
			switch (iMetadata) {
				case SUBTYPE_COLUMN:
					
					return AxisAlignedBB.getAABBPool()
							.getAABB(0.5D - COLUM_HALF_WIDTH, 0D, 0.5D - COLUM_HALF_WIDTH, 0.5D + COLUM_HALF_WIDTH, 1D, 0.5D + COLUM_HALF_WIDTH);
				
				case SUBTYPE_TABLE:
					
					if (!doesTableHaveLeg(blockAccess, i, j, k)) {
						return AxisAlignedBB.getAABBPool().getAABB(0D, 1D - TABLE_TOP_HEIGHT, 0D, 1D, 1D, 1D);
					}
					
					return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 1D, 1D, 1D);
				
				case SUBTYPE_PEDESTAL_UP:
				case SUBTYPE_PEDESTAL_DOWN:
					
					return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 1D, 1D, 1D);
				
				default:
					
					return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 1D, 1D, 1D);
			}
		}
		
		return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		if (isDecorative(world, i, j, k)) {
			if (isBlockTable(world, i, j, k) && doesTableHaveLeg(world, i, j, k)) {
				return collisionRayTraceTableWithLeg(world, i, j, k, startRay, endRay);
			}
			else {
				return collisionRayTraceVsBlockBounds(world, i, j, k, startRay, endRay);
			}
		}
		
		return super.collisionRayTrace(world, i, j, k, startRay, endRay);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB intersectingBox, List list, Entity entity) {
		if (isDecorative(world, i, j, k)) {
			getCollisionBoundingBoxFromPool(world, i, j, k).addToListIfIntersects(intersectingBox, list);
		}
		else {
			super.addCollisionBoxesToList(world, i, j, k, intersectingBox, list, entity);
		}
	}
	
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		switch (iSubtype) {
			case SUBTYPE_COLUMN:
				
				return iFacing == 0 || iFacing == 1;
			
			case SUBTYPE_PEDESTAL_UP:
			case SUBTYPE_PEDESTAL_DOWN:
				
				return true;
		}
		
		return super.hasCenterHardPointToFacing(blockAccess, i, j, k, iFacing, bIgnoreTransparency);
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		switch (iSubtype) {
			case SUBTYPE_PEDESTAL_UP:
				
				return iFacing == 0;
			
			case SUBTYPE_PEDESTAL_DOWN:
				
				return iFacing == 1;
			
			case SUBTYPE_TABLE:
				
				return iFacing == 1;
		}
		
		return super.hasLargeCenterHardPointToFacing(blockAccess, i, j, k, iFacing, bIgnoreTransparency);
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse) {
		if (!isDecorative(iMetadata)) {
			return super.rotateMetadataAroundJAxis(iMetadata, bReverse);
		}
		
		return iMetadata;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse) {
		if (!isDecorative(world, i, j, k)) {
			return super.toggleFacing(world, i, j, k, bReverse);
		}
		
		return false;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (isDecorative(iMetadata)) {
			if (iMetadata == SUBTYPE_PEDESTAL_UP || iMetadata == SUBTYPE_PEDESTAL_DOWN) {
				return true;
			}
			
			return false;
		}
		
		return super.canTransmitRotationHorizontallyOnTurntable(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (isDecorative(iMetadata)) {
			if (iMetadata != SUBTYPE_TABLE) {
				return true;
			}
			
			return false;
		}
		
		return super.canTransmitRotationVerticallyOnTurntable(blockAccess, i, j, k);
	}
	
	@Override
	public float mobSpawnOnVerticalOffset(World world, int i, int j, int k) {
		int iSubtype = world.getBlockMetadata(i, j, k);
		
		if (iSubtype < 12) {
			return super.mobSpawnOnVerticalOffset(world, i, j, k);
		}
		
		return 0F;
	}
	
	@Override
	public boolean isBenchOrTable(int metadata) {
		return metadata == SUBTYPE_TABLE;
	}
	
	@Override
	public boolean shouldWallFormPostBelowThisBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return blockAccess.getBlockMetadata(x, y, z) == SUBTYPE_TABLE && doesTableHaveLeg(blockAccess, x, y, z);
	}
	
	//------------- Class Specific Methods ------------//
	
	@Override
	protected boolean isMouldingOfSameType(IBlockAccess blockAccess, int i, int j, int k) {
		return blockAccess.getBlockId(i, j, k) == blockID && !isDecorative(blockAccess, i, j, k);
	}
	
	public boolean isDecorative(IBlockAccess blockAccess, int i, int j, int k) {
		return isDecorative(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean isDecorative(int iMetadata) {
		return iMetadata >= 12;
	}
	
	public boolean isBlockTable(IBlockAccess blockAccess, int i, int j, int k) {
		if (blockAccess.getBlockId(i, j, k) == blockID) {
			if (blockAccess.getBlockMetadata(i, j, k) == SUBTYPE_TABLE) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean doesTableHaveLeg(IBlockAccess blockAccess, int i, int j, int k) {
		int iBlockBelowID = blockAccess.getBlockId(i, j - 1, k);
		
		if (blockID == BTWBlocks.netherBrickMouldingAndDecorative.blockID) {
			if (iBlockBelowID == Block.netherFence.blockID) {
				return true;
			}
		}
		else if (iBlockBelowID == matchingCornerBlockID) {
			int iBlockBelowMetadata = blockAccess.getBlockMetadata(i, j - 1, k);
			
			if (iBlockBelowMetadata == SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE) {
				return true;
			}
		}
		
		boolean positiveITable = isBlockTable(blockAccess, i + 1, j, k);
		boolean negativeITable = isBlockTable(blockAccess, i - 1, j, k);
		boolean positiveKTable = isBlockTable(blockAccess, i, j, k + 1);
		boolean negativeKTable = isBlockTable(blockAccess, i, j, k - 1);
		
		if ((!positiveITable && (!positiveKTable || !negativeKTable)) || (!negativeITable && (!positiveKTable || !negativeKTable))) {
			return true;
		}
		
		return false;
	}
	
	public MovingObjectPosition collisionRayTraceTableWithLeg(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		RayTraceUtils rayTrace = new RayTraceUtils(world, i, j, k, startRay, endRay);
		
		// top
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
		
		// leg
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH,
				TABLE_LEG_HEIGHT, 0.5F + TABLE_LEG_HALF_WIDTH);
		
		return rayTrace.getFirstIntersection();
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private Icon iconColumnSide;
	@Environment(EnvType.CLIENT)
	private Icon iconColumnTopAndBottom;
	@Environment(EnvType.CLIENT)
	private Icon iconPedestalSide;
	@Environment(EnvType.CLIENT)
	private Icon iconPedestalTopAndBottom;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		super.registerIcons(register);
		
		iconColumnSide = register.registerIcon(columnSideTextureName);
		iconColumnTopAndBottom = register.registerIcon(columnTopAndBottomTextureName);
		iconPedestalSide = register.registerIcon(pedestalSideTextureName);
		iconPedestalTopAndBottom = register.registerIcon(pedestalTopAndBottomTextureName);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		if (iMetadata == SUBTYPE_COLUMN) {
			if (iSide < 2) {
				return iconColumnTopAndBottom;
			}
			else {
				return iconColumnSide;
			}
		}
		else if (iMetadata == SUBTYPE_PEDESTAL_UP || iMetadata == SUBTYPE_PEDESTAL_DOWN) {
			if (iSide < 2) {
				return iconPedestalTopAndBottom;
			}
			else {
				return iconPedestalSide;
			}
		}
		
		return super.getIcon(iSide, iMetadata);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k) {
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (!isDecorative(iMetadata)) {
			return super.renderBlock(renderBlocks, i, j, k);
		}
		
		switch (iMetadata) {
			case SUBTYPE_PEDESTAL_UP:
				
				return renderPedestalUp(renderBlocks, i, j, k);
			
			case SUBTYPE_PEDESTAL_DOWN:
				
				return renderPedestalDown(renderBlocks, i, j, k);
			
			case SUBTYPE_TABLE:
				
				return renderTable(renderBlocks, i, j, k);
			
			default:
				
				renderBlocks.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderBlocks.blockAccess, i, j, k));
				
				return renderBlocks.renderStandardBlock(this, i, j, k);
			
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		if (isDecorative(iItemDamage)) {
			renderDecorativeInvBlock(renderBlocks, this, iItemDamage, fBrightness);
		}
		else {
			super.renderBlockAsItem(renderBlocks, iItemDamage, fBrightness);
		}
	}
	
	/**
	 * The block param is necessary here as the wood item stubs use them to specify what block
	 * type they represent.
	 */
	@Environment(EnvType.CLIENT)
	protected void renderDecorativeInvBlock(RenderBlocks renderBlocks, Block block, int iItemDamage, float fBrightness) {
		switch (iItemDamage) {
			case SUBTYPE_PEDESTAL_UP:
				
				renderPedestalUpInvBlock(renderBlocks, block);
				
				break;
			
			case SUBTYPE_PEDESTAL_DOWN:
				
				renderPedestalDownInvBlock(renderBlocks, block);
				
				break;
			
			case SUBTYPE_TABLE:
				
				renderTableInvBlock(renderBlocks, block);
				
				break;
			
			case SUBTYPE_COLUMN:
				
				renderBlocks.setRenderBounds((0.5F - COLUM_HALF_WIDTH), 0.0F, (0.5F - COLUM_HALF_WIDTH), (0.5F + COLUM_HALF_WIDTH), 1.0F,
						(0.5F + COLUM_HALF_WIDTH));
				
				RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
				
				break;
			
			default:
				
				renderBlocks.renderBlockAsItemVanilla(block, iItemDamage, fBrightness);
				
				break;
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (isDecorative(iMetadata)) {
			return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
		}
		
		return super.getSelectedBoundingBoxFromPool(world, i, j, k);
	}
	
	@Environment(EnvType.CLIENT)
	public boolean renderPedestalUp(RenderBlocks renderBlocks, int i, int j, int k) {
		// render base
		
		renderBlocks.setRenderBounds(0F, 0F, 0F, 1.0F, PEDESTAL_BASE_HEIGHT, 1.0F);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		// render middle
		
		renderBlocks
				.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH,
						PEDESTAL_BASE_HEIGHT + PEDESTAL_MIDDLE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		// render top
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 1.0F - PEDESTAL_TOP_HEIGHT, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH,
				1.0F, 0.5F + PEDESTAL_TOP_HALF_WIDTH);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public void renderPedestalUpInvBlock(RenderBlocks renderBlocks, Block block) {
		// render base
		
		renderBlocks.setRenderBounds(0F, 0F, 0F, 1.0F, PEDESTAL_BASE_HEIGHT, 1.0F);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);
		
		// render middle
		
		renderBlocks
				.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH,
						PEDESTAL_BASE_HEIGHT + PEDESTAL_MIDDLE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);
		
		// render top
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 1.0F - PEDESTAL_TOP_HEIGHT, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH,
				1.0F, 0.5F + PEDESTAL_TOP_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);
	}
	
	@Environment(EnvType.CLIENT)
	public boolean renderPedestalDown(RenderBlocks renderBlocks, int i, int j, int k) {
		// render base
		
		renderBlocks.setRenderBounds(0F, 1.0F - PEDESTAL_BASE_HEIGHT, 0F, 1.0F, 1.0F, 1.0F);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		// render middle
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT - PEDESTAL_MIDDLE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
				0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		// render top
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 00F, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH, PEDESTAL_TOP_HEIGHT,
				0.5F + PEDESTAL_TOP_HALF_WIDTH);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public void renderPedestalDownInvBlock(RenderBlocks renderBlocks, Block block) {
		// render base
		
		renderBlocks.setRenderBounds(0F, 1.0F - PEDESTAL_BASE_HEIGHT, 0F, 1.0F, 1.0F, 1.0F);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);
		
		// render middle
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT - PEDESTAL_MIDDLE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
				0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);
		
		// render top
		
		renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 00F, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH, PEDESTAL_TOP_HEIGHT,
				0.5F + PEDESTAL_TOP_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);
	}
	
	@Environment(EnvType.CLIENT)
	public boolean renderTable(RenderBlocks renderBlocks, int i, int j, int k) {
		// render top
		
		renderBlocks.setRenderBounds(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		if (this.doesTableHaveLeg(renderBlocks.blockAccess, i, j, k)) {
			// render leg
			
			renderBlocks.setRenderBounds(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH, TABLE_LEG_HEIGHT,
					0.5F + TABLE_LEG_HALF_WIDTH);
			
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public void renderTableInvBlock(RenderBlocks renderBlocks, Block block) {
		// render top
		
		renderBlocks.setRenderBounds(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_TABLE);
		
		// render leg
		
		renderBlocks.setRenderBounds(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH, TABLE_LEG_HEIGHT,
				0.5F + TABLE_LEG_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_TABLE);
	}
}