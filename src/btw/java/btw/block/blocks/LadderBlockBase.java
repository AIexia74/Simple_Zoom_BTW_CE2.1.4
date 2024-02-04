// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LadderBlockBase extends Block {
	protected static final float LADDER_THICKNESS = 3 / 16F;
	
	protected static final AxisAlignedBB boxCollision = new AxisAlignedBB(0D, 0D, 1D - LADDER_THICKNESS, 1D, 1D, 1D);
	
	protected LadderBlockBase(int iBlockID) {
		super(iBlockID, Material.circuits);
		
		setHardness(0.4F);
		setAxesEffectiveOn(true);
		
		setBuoyant();
		
		setStepSound(soundLadderFootstep);
	}
	
	@Override
	public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
		return BTWBlocks.ladder.blockID;
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		AxisAlignedBB transformedBox = boxCollision.makeTemporaryCopy();
		
		transformedBox.rotateAroundYToFacing(getFacing(blockAccess, i, j, k));
		
		return transformedBox;
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
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		for (int iTempFacing = 2; iTempFacing <= 5; iTempFacing++) {
			if (canAttachToFacing(world, i, j, k, Block.getOppositeFacing(iTempFacing))) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
		if (canAttachToFacing(world, i, j, k, Block.getOppositeFacing(iFacing))) {
			iMetadata = setFacing(iMetadata, iFacing);
		}
		else {
			// specified facing isn't valid, search for another
			
			for (int iTempFacing = 2; iTempFacing <= 5; iTempFacing++) {
				if (canAttachToFacing(world, i, j, k, iTempFacing)) {
					iMetadata = setFacing(iMetadata, Block.getOppositeFacing(iTempFacing));
					
					break;
				}
			}
		}
		
		return iMetadata;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int iChangedBlockID) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (!canAttachToFacing(world, i, j, k, Block.getOppositeFacing(getFacing(iMetadata)))) {
			dropBlockAsItem(world, i, j, k, iMetadata, 0);
			world.setBlockToAir(i, j, k);
		}
		
		super.onNeighborBlockChange(world, i, j, k, iChangedBlockID);
	}
	
	@Override
	public int getRenderType() {
		return 8;
	}
	
	@Override
	public boolean isBlockClimbable(World world, int i, int j, int k) {
		return true;
	}
	
	@Override
	public int getFacing(int iMetadata) {
		return (iMetadata & 3) + 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing) {
		int iFlatFacing = MathHelper.clamp_int(iFacing, 2, 5) - 2;
		
		iMetadata &= ~3;
		
		return iMetadata | iFlatFacing;
	}
	
	@Override
	public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing) {
		return iFacing == Block.getOppositeFacing(getFacing(world, i, j, k));
	}
	
	@Override
	public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iInitialFacing, int iRotatedFacing) {
		int iOldMetadata = world.getBlockMetadata(i, j, k);
		
		return setFacing(iOldMetadata, Block.getOppositeFacing(iRotatedFacing));
	}
	
	@Override
	public boolean canItemPassIfFilter(ItemStack filteredItem) {
		int iFilterableProperties = filteredItem.getItem().getFilterableProperties(filteredItem);
		
		return (iFilterableProperties & Item.FILTERABLE_SOLID_BLOCK) == 0;
	}
	
	@Override
	public boolean canMobsSpawnOn(World world, int i, int j, int k) {
		return false;
	}
	
	//------------- Class Specific Methods ------------//
	
	protected boolean canAttachToFacing(World world, int i, int j, int k, int iFacing) {
		if (iFacing >= 2) {
			BlockPos targetPos = new BlockPos(i, j, k, iFacing);
			
			return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z, Block.getOppositeFacing(iFacing));
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	protected static final double LADDER_HORIZONTAL_OFFSET = 0.05000000074505806D;
	
	@Environment(EnvType.CLIENT)
	private Icon filterIcon;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon("ladder");
		
		filterIcon = register.registerIcon("fcBlockHopper_ladder");
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getHopperFilterIcon() {
		return filterIcon;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
		return renderLadder(renderer, x, y, z);
	}
	
	@Environment(EnvType.CLIENT)
	public boolean renderLadder(RenderBlocks renderBlocks, int x, int y, int z) {
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		int facing = getFacing(blockAccess, x, y, z);
		
		Tessellator tessellator = Tessellator.instance;
		
		tessellator.setBrightness(getMixedBrightnessForBlock(blockAccess, x, y, z));
		float brightness = 1.0F;
		tessellator.setColorOpaque_F(brightness, brightness, brightness);
		
		Icon icon = blockIcon;
		
		if (renderBlocks.hasOverrideBlockTexture()) {
			icon = renderBlocks.getOverrideTexture();
		}
		
		double minU = icon.getMinU();
		double minV = icon.getMinV();
		double maxU = icon.getMaxU();
		double maxV = icon.getMaxV();
		
		if (facing == 5) {
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 1, z + 1, minU, minV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 0, z + 1, minU, maxV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 0, z + 0, maxU, maxV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 1, z + 0, maxU, minV);
			
			// Back side
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 1, z + 0, maxU, minV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 0, z + 0, maxU, maxV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 0, z + 1, minU, maxV);
			tessellator.addVertexWithUV(x + LADDER_HORIZONTAL_OFFSET, y + 1, z + 1, minU, minV);
		}
		else if (facing == 4) {
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 0, z + 1, maxU, maxV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 1, z + 1, maxU, minV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 1, z + 0, minU, minV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 0, z + 0, minU, maxV);
			
			// Back side
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 0, z + 0, minU, maxV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 1, z + 0, minU, minV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 1, z + 1, maxU, minV);
			tessellator.addVertexWithUV((x + 1) - LADDER_HORIZONTAL_OFFSET, y + 0, z + 1, maxU, maxV);
		}
		else if (facing == 3) {
			tessellator.addVertexWithUV(x + 1, y + 0, z + LADDER_HORIZONTAL_OFFSET, maxU, maxV);
			tessellator.addVertexWithUV(x + 1, y + 1, z + LADDER_HORIZONTAL_OFFSET, maxU, minV);
			tessellator.addVertexWithUV(x + 0, y + 1, z + LADDER_HORIZONTAL_OFFSET, minU, minV);
			tessellator.addVertexWithUV(x + 0, y + 0, z + LADDER_HORIZONTAL_OFFSET, minU, maxV);
			
			// Back side
			tessellator.addVertexWithUV(x + 0, y + 0, z + LADDER_HORIZONTAL_OFFSET, minU, maxV);
			tessellator.addVertexWithUV(x + 0, y + 1, z + LADDER_HORIZONTAL_OFFSET, minU, minV);
			tessellator.addVertexWithUV(x + 1, y + 1, z + LADDER_HORIZONTAL_OFFSET, maxU, minV);
			tessellator.addVertexWithUV(x + 1, y + 0, z + LADDER_HORIZONTAL_OFFSET, maxU, maxV);
		}
		else if (facing == 2) {
			tessellator.addVertexWithUV(x + 1, y + 1, (z + 1) - LADDER_HORIZONTAL_OFFSET, minU, minV);
			tessellator.addVertexWithUV(x + 1, y + 0, (z + 1) - LADDER_HORIZONTAL_OFFSET, minU, maxV);
			tessellator.addVertexWithUV(x + 0, y + 0, (z + 1) - LADDER_HORIZONTAL_OFFSET, maxU, maxV);
			tessellator.addVertexWithUV(x + 0, y + 1, (z + 1) - LADDER_HORIZONTAL_OFFSET, maxU, minV);
			
			// Back Side
			tessellator.addVertexWithUV(x + 0, y + 1, (z + 1) - LADDER_HORIZONTAL_OFFSET, maxU, minV);
			tessellator.addVertexWithUV(x + 0, y + 0, (z + 1) - LADDER_HORIZONTAL_OFFSET, maxU, maxV);
			tessellator.addVertexWithUV(x + 1, y + 0, (z + 1) - LADDER_HORIZONTAL_OFFSET, minU, maxV);
			tessellator.addVertexWithUV(x + 1, y + 1, (z + 1) - LADDER_HORIZONTAL_OFFSET, minU, minV);
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
}
