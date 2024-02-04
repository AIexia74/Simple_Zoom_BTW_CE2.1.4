// FCMOD

package btw.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.FenceModel;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class FenceBlock extends Block {
	protected static final FenceModel model = new FenceModel();
	
	protected final String iconName;
	
	public FenceBlock(int iBlockID, String sIconName, Material material) {
		// NOTE: I used metadata to represent neigboring connections in a single
		// release (4.ABCFAFBFC) for oak and netherbrick fences.
		// Keep this in mind should you decide to use metadata again later
		
		super(iBlockID, material);
		
		iconName = sIconName;
		
		initBlockBounds(0D, 0D, 0D, 1D, 1.5D, 1D);
		
		setCreativeTab(CreativeTabs.tabDecorations);
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
	public boolean getBlocksMovement(IBlockAccess blockAccess, int i, int j, int k) {
		// getBlocksMovement() is misnamed and returns true if the block *doesn't* block movement
		
		return false;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		BlockModel tempModel = assembleTemporaryModel(world, i, j, k);
		
		return tempModel.collisionRayTrace(world, i, j, k, startRay, endRay);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB boundingBox, List list, Entity entity) {
		AxisAlignedBB tempBox = model.boxCollisionCenter.makeTemporaryCopy();
		
		tempBox.offset(i, j, k);
		
		if (tempBox.intersectsWith(boundingBox)) {
			list.add(tempBox);
		}
		
		for (int iTempFacing = 2; iTempFacing <= 5; iTempFacing++) {
			if (canConnectToBlockToFacing(world, i, j, k, iTempFacing)) {
				tempBox = model.boxCollisionStruts.makeTemporaryCopy();
				
				tempBox.rotateAroundYToFacing(iTempFacing);
				
				tempBox.offset(i, j, k);
				
				if (tempBox.intersectsWith(boundingBox)) {
					list.add(tempBox);
				}
			}
		}
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
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
	
	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k) {
		return -3;
	}
	
	@Override
	public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k) {
		return world.doesBlockHaveSolidTopSurface(i, j - 1, k);
	}
	
	@Override
	public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k) {
		return -1F;
	}
	
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		return iFacing == 0 || iFacing == 1;
	}
	
	@Override
	public float mobSpawnOnVerticalOffset(World world, int i, int j, int k) {
		// corresponds to the actual collision volume of the fence, which extends
		// half a block above it
		
		return 0.5F;
	}
	
	@Override
	public boolean isFence(int metadata) {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	public static boolean canConnectToBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		BlockPos blockPos = new BlockPos(x, y, z, facing);
		Block block = Block.blocksList[blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z)];
		
		if (block != null) {
			return block.shouldFenceConnectToThisBlockToFacing(blockAccess, blockPos.x, blockPos.y, blockPos.z, Facing.oppositeSide[facing]);
		}
		else {
			return false;
		}
	}
	
	protected BlockModel assembleTemporaryModel(IBlockAccess blockAccess, int i, int j, int k) {
		BlockModel tempModel = model.makeTemporaryCopy();
		
		for (int iTempFacing = 2; iTempFacing <= 5; iTempFacing++) {
			if (canConnectToBlockToFacing(blockAccess, i, j, k, iTempFacing)) {
				BlockModel tempSupportsModel = model.modelStruts.makeTemporaryCopy();
				
				tempSupportsModel.rotateAroundYToFacing(iTempFacing);
				
				tempSupportsModel.makeTemporaryCopyOfPrimitiveList(tempModel);
			}
		}
		
		return tempModel;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		blockIcon = par1IconRegister.registerIcon(iconName);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k) {
		BlockModel tempModel = assembleTemporaryModel(renderBlocks.blockAccess, i, j, k);
		
		return tempModel.renderAsBlock(renderBlocks, this, i, j, k);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		model.renderAsItemBlock(renderBlocks, this, iItemDamage);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, MovingObjectPosition rayTraceHit) {
		AxisAlignedBB tempBox = model.boxBoundsCenter.makeTemporaryCopy();
		
		return tempBox.offset(rayTraceHit.blockX, rayTraceHit.blockY, rayTraceHit.blockZ);
	}
}