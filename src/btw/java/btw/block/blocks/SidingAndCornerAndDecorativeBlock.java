// FCMOD

package btw.block.blocks;

import java.util.List;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.block.util.RayTraceUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11; // client only

public class SidingAndCornerAndDecorativeBlock extends SidingAndCornerBlock {
	public static final int SUBTYPE_BENCH = 12;
	public static final int SUBTYPE_FENCE = 14;
	
	protected final static float BENCH_TOP_HEIGHT = (2F / 16F);
	protected final static float BENCH_LEG_HEIGHT = (0.5F - BENCH_TOP_HEIGHT);
	protected final static float BENCH_LEG_WIDTH = (4F / 16F);
	protected final static float BENCH_LEG_HALF_WIDTH = (BENCH_LEG_WIDTH / 2F);
	
	public final static int OAK_BENCH_TOP_TEXTURE_ID = 93;
	public final static int OAK_BENCH_LEG_TEXTURE_ID = 94;
	
	public SidingAndCornerAndDecorativeBlock(int iBlockID, Material material, String sTextureName, float fHardness, float fResistance, StepSound stepSound,
			String name) {
		super(iBlockID, material, sTextureName, fHardness, fResistance, stepSound, name);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		int iSubtype = world.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_FENCE) {
			addCollisionBoxesToListForFence(world, i, j, k, axisalignedbb, list, entity);
		}
		else {
			super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, list, entity);
		}
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH) {
			return getBlockBoundsFromPoolForBench(blockAccess, i, j, k);
		}
		else if (iSubtype == SUBTYPE_FENCE) {
			return getBlockBoundsFromPoolForFence(blockAccess, i, j, k);
		}
		
		return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		int iBlockID = world.getBlockId(i, j, k);
		
		if (isBlockBench(world, i, j, k) && doesBenchHaveLeg(world, i, j, k)) {
			return collisionRayTraceBenchWithLeg(world, i, j, k, startRay, endRay);
		}
		else if ((iBlockID == blockID && world.getBlockMetadata(i, j, k) == SUBTYPE_FENCE) || iBlockID == Block.fenceGate.blockID) {
			return collisionRayTraceFence(world, i, j, k, startRay, endRay);
		}
		
		return super.collisionRayTrace(world, i, j, k, startRay, endRay);
	}
	
	@Override
	public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
		int iSubtype = world.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH || iSubtype == SUBTYPE_FENCE) {
			return iMetadata;
		}
		
		return super.onBlockPlaced(world, i, j, k, iFacing, fClickX, fClickY, fClickZ, iMetadata);
	}
	
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH) {
			return iFacing == 0;
		}
		else if (iSubtype == SUBTYPE_FENCE) {
			return iFacing == 0 || iFacing == 1;
		}
		
		return super.hasCenterHardPointToFacing(blockAccess, i, j, k, iFacing, bIgnoreTransparency);
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH || iSubtype == SUBTYPE_FENCE) {
			return false;
		}
		
		return super.hasLargeCenterHardPointToFacing(blockAccess, i, j, k, iFacing, bIgnoreTransparency);
	}
	
	@Override
	public int damageDropped(int iMetadata) {
		if (isDecorativeFromMetadata(iMetadata)) {
			return iMetadata;
		}
		
		return super.damageDropped(iMetadata);
	}
	
	@Override
	public boolean getBlocksMovement(IBlockAccess blockAccess, int i, int j, int k) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_FENCE) {
			return false;
		}
		
		return super.getBlocksMovement(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (iMetadata == SUBTYPE_BENCH) {
			return true;
		}
		else if (isDecorativeFromMetadata(iMetadata)) {
			return world.doesBlockHaveSolidTopSurface(i, j, k);
		}
		
		return super.canGroundCoverRestOnBlock(world, i, j, k);
	}
	
	@Override
	public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (iMetadata == SUBTYPE_BENCH) {
			return -0.5F;
		}
		else if (isDecorativeFromMetadata(iMetadata)) {
			return 0F;
		}
		
		return super.groundCoverRestingOnVisualOffset(blockAccess, i, j, k);
	}
	
	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (iMetadata == SUBTYPE_FENCE) {
			return -3;
		}
		else {
			return 0;
		}
	}
	
	@Override
	public int getFacing(int iMetadata) {
		if (iMetadata == SUBTYPE_BENCH || iMetadata == SUBTYPE_FENCE) {
			return 0;
		}
		
		return super.getFacing(iMetadata);
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing) {
		if (iMetadata == SUBTYPE_BENCH || iMetadata == SUBTYPE_FENCE) {
			return iMetadata;
		}
		
		return super.setFacing(iMetadata, iFacing);
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH || iSubtype == SUBTYPE_FENCE) {
			return true;
		}
		
		return super.canRotateOnTurntable(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_FENCE) {
			return true;
		}
		else if (iSubtype == SUBTYPE_BENCH) {
			return false;
		}
		
		return super.canTransmitRotationVerticallyOnTurntable(blockAccess, i, j, k);
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse) {
		if (iMetadata == SUBTYPE_BENCH || iMetadata == SUBTYPE_FENCE) {
			return iMetadata;
		}
		
		return super.rotateMetadataAroundJAxis(iMetadata, bReverse);
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse) {
		int iSubtype = world.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH || iSubtype == SUBTYPE_FENCE) {
			return false;
		}
		
		return super.toggleFacing(world, i, j, k, bReverse);
	}
	
	@Override
	public float mobSpawnOnVerticalOffset(World world, int i, int j, int k) {
		int iSubtype = world.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_FENCE) {
			// corresponds to the actual collision volume of the fence, which extends
			// half a block above it
			return 0.5F;
		}
		else if (iSubtype == SUBTYPE_BENCH) {
			return -0.5F;
		}
		
		return super.mobSpawnOnVerticalOffset(world, i, j, k);
	}
	
	@Override
	public boolean isFence(int metadata) {
		return metadata == SUBTYPE_FENCE;
	}
	
	@Override
	public boolean isBenchOrTable(int metadata) {
		return metadata == SUBTYPE_BENCH;
	}
	
	@Override
	public boolean shouldWallFormPostBelowThisBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return blockAccess.getBlockMetadata(x, y, z) == SUBTYPE_BENCH && doesBenchHaveLeg(blockAccess, x, y, z);
	}
	
	//------------- Class Specific Methods ------------//
	
	public boolean isDecorative(IBlockAccess blockAccess, int i, int j, int k) {
		return isDecorativeFromMetadata(blockAccess.getBlockMetadata(i, j, k));
	}
	
	static public boolean isDecorativeFromMetadata(int iMetadata) {
		return iMetadata == SUBTYPE_BENCH || iMetadata == SUBTYPE_FENCE;
	}
	
	public AxisAlignedBB getBlockBoundsFromPoolForBench(IBlockAccess blockAccess, int i, int j, int k) {
		if (!doesBenchHaveLeg(blockAccess, i, j, k)) {
			return AxisAlignedBB.getAABBPool().getAABB(0D, 0.5D - BENCH_TOP_HEIGHT, 0D, 1D, 0.5D, 1D);
		}
		else {
			return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 1D, 0.5D, 1D);
		}
	}
	
	public AxisAlignedBB getBlockBoundsFromPoolForFence(IBlockAccess blockAccess, int i, int j, int k) {
		AxisAlignedBB fenceBox = AxisAlignedBB.getAABBPool().getAABB(0.375D, 0D, 0.375D, 0.625D, 1D, 0.625D);
		
		if (FenceBlock.canConnectToBlockToFacing(blockAccess, i, j, k, 2)) {
			fenceBox.minZ = 0D;
		}
		
		if (FenceBlock.canConnectToBlockToFacing(blockAccess, i, j, k, 3)) {
			fenceBox.maxZ = 1D;
		}
		
		if (FenceBlock.canConnectToBlockToFacing(blockAccess, i, j, k, 4)) {
			fenceBox.minX = 0D;
		}
		
		if (FenceBlock.canConnectToBlockToFacing(blockAccess, i, j, k, 5)) {
			fenceBox.maxX = 1D;
		}
		
		return fenceBox;
	}
	
	public void addCollisionBoxesToListForFence(World world, int i, int j, int k, AxisAlignedBB intersectingBox, List list, Entity entity) {
		boolean bConnectsNegativeK = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 2);
		boolean bConnectsPositiveK = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 3);
		boolean bConnectsNegativeI = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 4);
		boolean bConnectsPositiveI = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 5);
		
		float fXMin = 0.375F;
		float fXMax = 0.625F;
		float fZMin = 0.375F;
		float fZMax = 0.625F;
		
		if (bConnectsNegativeK) {
			fZMin = 0.0F;
		}
		
		if (bConnectsPositiveK) {
			fZMax = 1.0F;
		}
		
		if (bConnectsNegativeK || bConnectsPositiveK) {
			AxisAlignedBB.getAABBPool().getAABB(fXMin, 0.0F, fZMin, fXMax, 1.5F, fZMax).
					offset(i, j, k).addToListIfIntersects(intersectingBox, list);
		}
		
		if (bConnectsNegativeI) {
			fXMin = 0.0F;
		}
		
		if (bConnectsPositiveI) {
			fXMax = 1.0F;
		}
		
		if (bConnectsNegativeI || bConnectsPositiveI || (!bConnectsNegativeK && !bConnectsPositiveK)) {
			AxisAlignedBB.getAABBPool().getAABB(fXMin, 0.0F, 0.375F, fXMax, 1.5F, 0.625F).
					offset(i, j, k).addToListIfIntersects(intersectingBox, list);
		}
	}
	
	public boolean doesBenchHaveLeg(IBlockAccess blockAccess, int i, int j, int k) {
		int iBlockBelowID = blockAccess.getBlockId(i, j - 1, k);
		
		if (blockID == BTWBlocks.netherBrickSidingAndCorner.blockID) {
			if (iBlockBelowID == Block.netherFence.blockID) {
				return true;
			}
		}
		else if (blockID == iBlockBelowID) {
			int iBlockBelowMetadata = blockAccess.getBlockMetadata(i, j - 1, k);
			
			if (iBlockBelowMetadata == SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE) {
				return true;
			}
		}
		
		boolean positiveIBench = isBlockBench(blockAccess, i + 1, j, k);
		boolean negativeIBench = isBlockBench(blockAccess, i - 1, j, k);
		boolean positiveKBench = isBlockBench(blockAccess, i, j, k + 1);
		boolean negativeKBench = isBlockBench(blockAccess, i, j, k - 1);
		
		if ((!positiveIBench && (!positiveKBench || !negativeKBench)) || (!negativeIBench && (!positiveKBench || !negativeKBench))) {
			return true;
		}
		
		return false;
	}
	
	public boolean isBlockBench(IBlockAccess blockAccess, int i, int j, int k) {
		return blockAccess.getBlockId(i, j, k) == blockID && blockAccess.getBlockMetadata(i, j, k) == SUBTYPE_BENCH;
	}
	
	public MovingObjectPosition collisionRayTraceBenchWithLeg(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		RayTraceUtils rayTrace = new RayTraceUtils(world, i, j, k, startRay, endRay);
		
		// top
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.0F, 0.5F - BENCH_TOP_HEIGHT, 0.0F, 1.0F, 0.5F, 1.0F);
		
		// leg
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5F - BENCH_LEG_HALF_WIDTH, 0.0F, 0.5F - BENCH_LEG_HALF_WIDTH, 0.5F + BENCH_LEG_HALF_WIDTH,
				BENCH_LEG_HEIGHT, 0.5F + BENCH_LEG_HALF_WIDTH);
		
		return rayTrace.getFirstIntersection();
	}
	
	public MovingObjectPosition collisionRayTraceFence(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		RayTraceUtils rayTrace = new RayTraceUtils(world, i, j, k, startRay, endRay);
		
		// post
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
		
		// supports
		
		boolean bConnectsAlongI = false;
		
		boolean bConnectsNegativeK = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 2);
		boolean bConnectsPositiveK = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 3);
		boolean bConnectsNegativeI = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 4);
		boolean bConnectsPositiveI = FenceBlock.canConnectToBlockToFacing(world, i, j, k, 5);
		
		if (bConnectsNegativeI || bConnectsPositiveI) {
			bConnectsAlongI = true;
		}
		
		boolean bConnectsAlongK = false;
		
		if (bConnectsNegativeK || bConnectsPositiveK) {
			bConnectsAlongK = true;
		}
		
		if (!bConnectsAlongI && !bConnectsAlongK) {
			bConnectsAlongI = true;
		}
		
		float var6 = 0.4375F;
		float var7 = 0.5625F;
		float var14 = 0.75F;
		float var15 = 0.9375F;
		
		float var16 = bConnectsNegativeI ? 0.0F : var6;
		float var17 = bConnectsPositiveI ? 1.0F : var7;
		float var18 = bConnectsNegativeK ? 0.0F : var6;
		float var19 = bConnectsPositiveK ? 1.0F : var7;
		
		if (bConnectsAlongI) {
			rayTrace.addBoxWithLocalCoordsToIntersectionList((double) var16, (double) var14, (double) var6, (double) var17, (double) var15, (double) var7);
		}
		
		if (bConnectsAlongK) {
			rayTrace.addBoxWithLocalCoordsToIntersectionList((double) var6, (double) var14, (double) var18, (double) var7, (double) var15, (double) var19);
		}
		
		var14 = 0.375F;
		var15 = 0.5625F;
		
		if (bConnectsAlongI) {
			rayTrace.addBoxWithLocalCoordsToIntersectionList((double) var16, (double) var14, (double) var6, (double) var17, (double) var15, (double) var7);
		}
		
		if (bConnectsAlongK) {
			rayTrace.addBoxWithLocalCoordsToIntersectionList((double) var6, (double) var14, (double) var18, (double) var7, (double) var15, (double) var19);
		}
		
		return rayTrace.getFirstIntersection();
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void getSubBlocks(int iBlockID, CreativeTabs creativeTabs, List list) {
		super.getSubBlocks(iBlockID, creativeTabs, list);
		
		list.add(new ItemStack(iBlockID, 1, SUBTYPE_BENCH));
		
		if (iBlockID != BTWBlocks.netherBrickSidingAndCorner.blockID) {
			list.add(new ItemStack(iBlockID, 1, SUBTYPE_FENCE));
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		BlockPos thisPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK, Block.getOppositeFacing(iSide));
		
		int iSubtype = blockAccess.getBlockMetadata(thisPos.x, thisPos.y, thisPos.z);
		
		if (iSubtype == SUBTYPE_FENCE || iSubtype == SUBTYPE_BENCH) {
			return true;
		}
		
		return super.shouldSideBeRendered(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k) {
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		
		int iSubtype = blockAccess.getBlockMetadata(i, j, k);
		
		if (iSubtype == SUBTYPE_BENCH) {
			return renderBench(renderBlocks, i, j, k);
		}
		else if (iSubtype == SUBTYPE_FENCE) {
			return renderFence(renderBlocks, i, j, k);
		}
		
		return super.renderBlock(renderBlocks, i, j, k);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		int iSubtype = iItemDamage;
		
		Block block = this;
		
		if (iSubtype == SUBTYPE_BENCH) {
			renderBenchInvBlock(renderBlocks, block, iSubtype);
		}
		else if (iSubtype == SUBTYPE_FENCE) {
			renderFenceInvBlock(renderBlocks, block, iSubtype);
		}
		else {
			super.renderBlockAsItem(renderBlocks, iItemDamage, fBrightness);
		}
	}
	
	//------------- Bench Renderers ------------//
	
	@Environment(EnvType.CLIENT)
	public boolean renderBench(RenderBlocks renderBlocks, int i, int j, int k) {
		// render top
		renderBlocks.setRenderBounds(0.0F, 0.5F - BENCH_TOP_HEIGHT, 0.0F, 1.0F, 0.5F, 1.0F);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		if (this.doesBenchHaveLeg(renderBlocks.blockAccess, i, j, k)) {
			// render leg
			renderBlocks.setRenderBounds(0.5F - BENCH_LEG_HALF_WIDTH, 0.0F, 0.5F - BENCH_LEG_HALF_WIDTH, 0.5F + BENCH_LEG_HALF_WIDTH, BENCH_LEG_HEIGHT,
					0.5F + BENCH_LEG_HALF_WIDTH);
			
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public void renderBenchInvBlock(RenderBlocks renderBlocks, Block block, int iItemDamage) {
		// render top
		
		renderBlocks.setRenderBounds(0.0F, 0.5F - BENCH_TOP_HEIGHT, 0.0F, 1.0F, 0.5F, 1.0F);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_BENCH);
		
		// render leg
		
		renderBlocks.setRenderBounds(0.5F - BENCH_LEG_HALF_WIDTH, 0.0F, 0.5F - BENCH_LEG_HALF_WIDTH, 0.5F + BENCH_LEG_HALF_WIDTH, BENCH_LEG_HEIGHT,
				0.5F + BENCH_LEG_HALF_WIDTH);
		
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_BENCH);
	}
	
	//------------- Fence Renderers ------------//
	
	@Environment(EnvType.CLIENT)
	public boolean renderFence(RenderBlocks renderBlocks, int i, int j, int k) {
		// post
		
		renderBlocks.setRenderBounds(0.375D, 0.0D, (double) 0.375D, 0.625D, 1.0D, 0.625D);
		
		renderBlocks.renderStandardBlock(this, i, j, k);
		
		// supports
		boolean bConnectsAlongI = false;
		
		boolean bConnectsNegativeK = FenceBlock.canConnectToBlockToFacing(renderBlocks.blockAccess, i, j, k, 2);
		boolean bConnectsPositiveK = FenceBlock.canConnectToBlockToFacing(renderBlocks.blockAccess, i, j, k, 3);
		boolean bConnectsNegativeI = FenceBlock.canConnectToBlockToFacing(renderBlocks.blockAccess, i, j, k, 4);
		boolean bConnectsPositiveI = FenceBlock.canConnectToBlockToFacing(renderBlocks.blockAccess, i, j, k, 5);
		
		if (bConnectsNegativeI || bConnectsPositiveI) {
			bConnectsAlongI = true;
		}
		
		boolean bConnectsAlongK = false;
		
		if (bConnectsNegativeK || bConnectsPositiveK) {
			bConnectsAlongK = true;
		}
		
		if (!bConnectsAlongI && !bConnectsAlongK) {
			bConnectsAlongI = true;
		}
		
		float var6 = 0.4375F;
		float var7 = 0.5625F;
		float var14 = 0.75F;
		float var15 = 0.9375F;
		
		float var16 = bConnectsNegativeI ? 0.0F : var6;
		float var17 = bConnectsPositiveI ? 1.0F : var7;
		float var18 = bConnectsNegativeK ? 0.0F : var6;
		float var19 = bConnectsPositiveK ? 1.0F : var7;
		
		if (bConnectsAlongI) {
			renderBlocks.setRenderBounds((double) var16, (double) var14, (double) var6, (double) var17, (double) var15, (double) var7);
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		if (bConnectsAlongK) {
			renderBlocks.setRenderBounds((double) var6, (double) var14, (double) var18, (double) var7, (double) var15, (double) var19);
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		var14 = 0.375F;
		var15 = 0.5625F;
		
		if (bConnectsAlongI) {
			renderBlocks.setRenderBounds((double) var16, (double) var14, (double) var6, (double) var17, (double) var15, (double) var7);
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		if (bConnectsAlongK) {
			renderBlocks.setRenderBounds((double) var6, (double) var14, (double) var18, (double) var7, (double) var15, (double) var19);
			renderBlocks.renderStandardBlock(this, i, j, k);
		}
		
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public void renderFenceInvBlock(RenderBlocks renderBlocks, Block block, int itemDamage) {
		Tessellator tessellator = Tessellator.instance;
		
		for (int i = 0; i < 4; ++i) {
			float var5 = 0.125F;
			
			if (i == 0) {
				renderBlocks.setRenderBounds((0.5F - var5), 0.0D, 0.0D, (0.5F + var5), 1.0D, (var5 * 2.0F));
			}
			
			if (i == 1) {
				renderBlocks.setRenderBounds((0.5F - var5), 0.0D, (1.0F - var5 * 2.0F), (0.5F + var5), 1.0D, 1.0D);
			}
			
			var5 = 0.0625F;
			
			if (i == 2) {
				renderBlocks.setRenderBounds((0.5F - var5), (1.0F - var5 * 4.0F), (-var5 * 2.0F), (0.5F + var5), (1.0F - var5), (1.0F + var5 * 2.0F));
			}
			
			if (i == 3) {
				renderBlocks.setRenderBounds((0.5F - var5), (0.5F - var5 * 2.0F), (-var5 * 2.0F), (0.5F + var5), (0.5F + var5), (1.0F + var5 * 2.0F));
			}
			
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
			tessellator.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
		
		renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	}
}