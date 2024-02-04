// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FenceGateBlock extends BlockFenceGate {
	public FenceGateBlock(int iBlockID) {
		super(iBlockID);
		
		setBlockMaterial(BTWBlocks.plankMaterial);
		
		setHardness(1.5F);
		setResistance(5F);
		setAxesEffectiveOn();
		
		setBuoyant();
		setFurnaceBurnTime(FurnaceBurnTime.WOOD_BASED_BLOCK);
		
		setStepSound(soundWoodFootstep);
		
		setUnlocalizedName("fenceGate");
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k) {
		return -3;
	}
	
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8,
			float par9) {
		// override to provide change notifications on open and close
		
		int var10 = par1World.getBlockMetadata(par2, par3, par4);
		
		if (isFenceGateOpen(var10)) {
			par1World.SetBlockMetadataWithNotify(par2, par3, par4, var10 & -5, 3);
		}
		else {
			int var11 = (MathHelper.floor_double((double) (par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
			int var12 = getDirection(var10);
			
			if (var12 == (var11 + 2) % 4) {
				var10 = var11;
			}
			
			par1World.SetBlockMetadataWithNotify(par2, par3, par4, var10 | 4, 3);
		}
		
		par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		// override to prevent trap doors from responding to redstone signal
	}
	
	@Override
	public boolean canPathThroughBlock(IBlockAccess blockAccess, int i, int j, int k, Entity entity, PathFinder pathFinder) {
		if (!pathFinder.CanPathThroughClosedWoodDoor()) {
			// note: getBlocksMovement() is misnamed and returns if the block *doesn't* block movement
			
			if (!pathFinder.canPathThroughOpenWoodDoor() || !getBlocksMovement(blockAccess, i, j, k)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isBreakableBarricade(IBlockAccess blockAccess, int i, int j, int k) {
		return true;
	}
	
	@Override
	public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int i, int j, int k) {
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		return isFenceGateOpen(iMetadata);
	}
	
	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return 2; // iron or better
	}
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		// override to deprecate parent
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		int iDirection = getDirection(blockAccess.getBlockMetadata(i, j, k));
		
		if (iDirection != 2 && iDirection != 0) {
			return AxisAlignedBB.getAABBPool().getAABB(0.375F, 0F, 0F, 0.625F, 1F, 1F);
		}
		else {
			return AxisAlignedBB.getAABBPool().getAABB(0F, 0F, 0.375F, 1F, 1F, 0.625F);
		}
	}
	
	@Override
	public boolean shouldWallConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		int dir = BlockDirectional.getDirection(metadata);
		
		if (facing == 2 || facing == 3) {
			return dir == 1 || dir == 3;
		}
		else if (facing == 4 || facing == 5) {
			return dir == 0 || dir == 2;
		}
		
		return false;
	}
	
	@Override
	public boolean shouldFenceConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		int dir = BlockDirectional.getDirection(metadata);
		
		if (facing == 2 || facing == 3) {
			return dir == 1 || dir == 3;
		}
		else if (facing == 4 || facing == 5) {
			return dir == 0 || dir == 2;
		}
		
		return false;
	}
	
	//------------- Class Specific Methods ------------//
	
	protected boolean isNextToWall(IBlockAccess blockAccess, int x, int y, int z) {
		int meta = blockAccess.getBlockMetadata(x, y, z);
		int dir = BlockDirectional.getDirection(meta);
		int otherID;
		int otherMeta;
		Block otherBlock;
		boolean isNextToWall = false;
		
		if (dir == 0 || dir == 2) {
			otherID = blockAccess.getBlockId(x - 1, y, z);
			otherMeta = blockAccess.getBlockMetadata(x - 1, y, z);
			otherBlock = Block.blocksList[otherID];
			
			if (otherBlock != null && otherBlock.isWall(otherMeta))
				isNextToWall = true;
			
			otherID = blockAccess.getBlockId(x + 1, y, z);
			otherMeta = blockAccess.getBlockMetadata(x + 1, y, z);
			otherBlock = Block.blocksList[otherID];
			
			if (otherBlock != null && otherBlock.isWall(otherMeta))
				isNextToWall = true;
		}
		else if (dir == 1 || dir == 3) {
			otherID = blockAccess.getBlockId(x, y, z - 1);
			otherMeta = blockAccess.getBlockMetadata(x, y, z - 1);
			otherBlock = Block.blocksList[otherID];
			
			if (otherBlock != null && otherBlock.isWall(otherMeta))
				isNextToWall = true;
			
			otherID = blockAccess.getBlockId(x, y, z + 1);
			otherMeta = blockAccess.getBlockMetadata(x, y, z + 1);
			otherBlock = Block.blocksList[otherID];
			
			if (otherBlock != null && otherBlock.isWall(otherMeta))
				isNextToWall = true;
		}
		
		return isNextToWall;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		int var6 = render.blockAccess.getBlockMetadata(x, y, z);
		boolean var7 = this.isFenceGateOpen(var6);
		int var8 = getDirection(var6);
		float var9 = 0.375F;
		float var10 = 0.5625F;
		float var11 = 0.75F;
		float var12 = 0.9375F;
		float var13 = 0.3125F;
		float var14 = 1.0F;
		
		if (isNextToWall(render.blockAccess, x, y, z))
		{
			var9 -= 0.1875F;
			var10 -= 0.1875F;
			var11 -= 0.1875F;
			var12 -= 0.1875F;
			var13 -= 0.1875F;
			var14 -= 0.1875F;
		}
		
		render.setRenderAllFaces(true);
		float var15;
		float var16;
		float var17;
		float var18;
		
		if (var8 != 3 && var8 != 1)
		{
			var15 = 0.0F;
			var17 = 0.125F;
			var16 = 0.4375F;
			var18 = 0.5625F;
			render.setRenderBounds((double)var15, (double)var13, (double)var16, (double)var17, (double)var14, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var15 = 0.875F;
			var17 = 1.0F;
			render.setRenderBounds((double)var15, (double)var13, (double)var16, (double)var17, (double)var14, (double)var18);
			render.renderStandardBlock(this, x, y, z);
		}
		else
		{
			render.setUVRotateTop(1);
			var15 = 0.4375F;
			var17 = 0.5625F;
			var16 = 0.0F;
			var18 = 0.125F;
			render.setRenderBounds((double)var15, (double)var13, (double)var16, (double)var17, (double)var14, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var16 = 0.875F;
			var18 = 1.0F;
			render.setRenderBounds((double)var15, (double)var13, (double)var16, (double)var17, (double)var14, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			render.setUVRotateTop(0);
		}
		
		if (var7)
		{
			if (var8 == 2 || var8 == 0)
			{
				render.setUVRotateTop(1);
			}
			
			if (var8 == 3)
			{
				render.setRenderBounds(0.8125D, (double)var9, 0.0D, 0.9375D, (double)var12, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.8125D, (double)var9, 0.875D, 0.9375D, (double)var12, 1.0D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.5625D, (double)var9, 0.0D, 0.8125D, (double)var10, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.5625D, (double)var9, 0.875D, 0.8125D, (double)var10, 1.0D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.5625D, (double)var11, 0.0D, 0.8125D, (double)var12, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.5625D, (double)var11, 0.875D, 0.8125D, (double)var12, 1.0D);
				render.renderStandardBlock(this, x, y, z);
			}
			else if (var8 == 1)
			{
				render.setRenderBounds(0.0625D, (double)var9, 0.0D, 0.1875D, (double)var12, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.0625D, (double)var9, 0.875D, 0.1875D, (double)var12, 1.0D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.1875D, (double)var9, 0.0D, 0.4375D, (double)var10, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.1875D, (double)var9, 0.875D, 0.4375D, (double)var10, 1.0D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.1875D, (double)var11, 0.0D, 0.4375D, (double)var12, 0.125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.1875D, (double)var11, 0.875D, 0.4375D, (double)var12, 1.0D);
				render.renderStandardBlock(this, x, y, z);
			}
			else if (var8 == 0)
			{
				render.setRenderBounds(0.0D, (double)var9, 0.8125D, 0.125D, (double)var12, 0.9375D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var9, 0.8125D, 1.0D, (double)var12, 0.9375D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.0D, (double)var9, 0.5625D, 0.125D, (double)var10, 0.8125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var9, 0.5625D, 1.0D, (double)var10, 0.8125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.0D, (double)var11, 0.5625D, 0.125D, (double)var12, 0.8125D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var11, 0.5625D, 1.0D, (double)var12, 0.8125D);
				render.renderStandardBlock(this, x, y, z);
			}
			else if (var8 == 2)
			{
				render.setRenderBounds(0.0D, (double)var9, 0.0625D, 0.125D, (double)var12, 0.1875D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var9, 0.0625D, 1.0D, (double)var12, 0.1875D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.0D, (double)var9, 0.1875D, 0.125D, (double)var10, 0.4375D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var9, 0.1875D, 1.0D, (double)var10, 0.4375D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.0D, (double)var11, 0.1875D, 0.125D, (double)var12, 0.4375D);
				render.renderStandardBlock(this, x, y, z);
				render.setRenderBounds(0.875D, (double)var11, 0.1875D, 1.0D, (double)var12, 0.4375D);
				render.renderStandardBlock(this, x, y, z);
			}
		}
		else if (var8 != 3 && var8 != 1)
		{
			var15 = 0.375F;
			var17 = 0.5F;
			var16 = 0.4375F;
			var18 = 0.5625F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var15 = 0.5F;
			var17 = 0.625F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var15 = 0.625F;
			var17 = 0.875F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var10, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			render.setRenderBounds((double)var15, (double)var11, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var15 = 0.125F;
			var17 = 0.375F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var10, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			render.setRenderBounds((double)var15, (double)var11, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
		}
		else
		{
			render.setUVRotateTop(1);
			var15 = 0.4375F;
			var17 = 0.5625F;
			var16 = 0.375F;
			var18 = 0.5F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var16 = 0.5F;
			var18 = 0.625F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var16 = 0.625F;
			var18 = 0.875F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var10, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			render.setRenderBounds((double)var15, (double)var11, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			var16 = 0.125F;
			var18 = 0.375F;
			render.setRenderBounds((double)var15, (double)var9, (double)var16, (double)var17, (double)var10, (double)var18);
			render.renderStandardBlock(this, x, y, z);
			render.setRenderBounds((double)var15, (double)var11, (double)var16, (double)var17, (double)var12, (double)var18);
			render.renderStandardBlock(this, x, y, z);
		}
		
		render.setRenderAllFaces(false);;
		render.setUVRotateTop(0);
		render.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		return true;
	}
}