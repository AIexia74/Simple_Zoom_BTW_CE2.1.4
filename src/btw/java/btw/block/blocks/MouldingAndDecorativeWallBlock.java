package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import btw.client.render.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class MouldingAndDecorativeWallBlock extends MouldingAndDecorativeBlock {
	protected static final double TABLE_WALL_LEG_WIDTH = (8D / 16D);
	protected static final double TABLE_WALL_LEG_HALF_WIDTH = (TABLE_WALL_LEG_WIDTH / 2D);
	
	public MouldingAndDecorativeWallBlock(int blockID, Material material, String textureName, String columnSideTextureName, int matchingCornerBlockID,
			float hardness, float resistance, StepSound stepSound, String name) {
		super(blockID, material, textureName, columnSideTextureName, matchingCornerBlockID, hardness, resistance, stepSound, name);
	}
	
	public MouldingAndDecorativeWallBlock(int blockID, Material material, String textureName, String columnSideTextureName,
			String columnTopAndBottomTextureName, String pedestalSideTextureName, String pedestalTopAndBottomTextureName, int matchingCornerBlockID,
			float hardness, float resistance, StepSound stepSound, String name) {
		super(blockID, material, textureName, columnSideTextureName, columnTopAndBottomTextureName, pedestalSideTextureName,
				pedestalTopAndBottomTextureName, matchingCornerBlockID, hardness, resistance, stepSound, name);
	}
	
	@Override
	public MovingObjectPosition collisionRayTraceTableWithLeg(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
		RayTraceUtils rayTrace = new RayTraceUtils(world, x, y, z, startRay, endRay);
		
		// top
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.0F, 0.5F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 0.5F, 1.0F);
		
		// leg
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5F - TABLE_WALL_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_WALL_LEG_HALF_WIDTH,
				0.5F + TABLE_WALL_LEG_HALF_WIDTH, TABLE_TOP_HEIGHT, 0.5F + TABLE_WALL_LEG_HALF_WIDTH);
		
		if (this.doesTableHaveLeg(world, x - 1, y, z) &&
				Block.blocksList[world.getBlockId(x - 1, y, z)] instanceof MouldingAndDecorativeWallBlock &&
				world.getBlockMetadata(x - 1, y, z) == SUBTYPE_TABLE)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0F, 0F, 0.3125F, 0.25F, 0.875F, 0.6875F);
		}
		if (this.doesTableHaveLeg(world, x, y, z - 1) &&
				Block.blocksList[world.getBlockId(x, y, z - 1)] instanceof MouldingAndDecorativeWallBlock &&
				world.getBlockMetadata(x, y, z - 1) == SUBTYPE_TABLE)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.3125F, 0F, 0F, 0.6875F, 0.875F, 0.25F);
		}
		if (this.doesTableHaveLeg(world, x + 1, y, z) &&
				Block.blocksList[world.getBlockId(x + 1, y, z)] instanceof MouldingAndDecorativeWallBlock &&
				world.getBlockMetadata(x + 1, y, z) == SUBTYPE_TABLE)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.75F, 0F, 0.3125F, 1F, 0.875F, 0.6875F);
		}
		if (this.doesTableHaveLeg(world, x, y, z + 1) &&
				Block.blocksList[world.getBlockId(x, y, z + 1)] instanceof MouldingAndDecorativeWallBlock &&
				world.getBlockMetadata(x, y, z + 1) == SUBTYPE_TABLE)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.3125F, 0F, 0.75F, 0.6875F, 0.875F, 1F);
		}
		
		return rayTrace.getFirstIntersection();
	}
	
	//------------ Client Side Functionality ----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderTable(RenderBlocks renderBlocks, int x, int y, int z) {
		renderBlocks.setRenderBounds(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderBlocks.renderStandardBlock(this, x, y, z);
		
		if (this.doesTableHaveLeg(renderBlocks.blockAccess, x, y, z)) {
			renderBlocks.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 0.875D, 0.75D);
			renderBlocks.renderStandardBlock(this, x, y, z);
			
			if (this.doesTableHaveLeg(renderBlocks.blockAccess, x - 1, y, z) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x - 1, y, z)] instanceof MouldingAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x - 1, y, z) == SUBTYPE_TABLE)
			{
				renderBlocks.setRenderBounds(0.25D, 0.0D, 0.6875D, 0D, 0.875D, 0.3125D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesTableHaveLeg(renderBlocks.blockAccess, x, y, z - 1) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x, y, z - 1)] instanceof MouldingAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x, y, z - 1) == SUBTYPE_TABLE)
			{
				renderBlocks.setRenderBounds(0.6875D, 0.0D, 0.25D, 0.3125D, 0.875D, 0D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesTableHaveLeg(renderBlocks.blockAccess, x + 1, y, z) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x + 1, y, z)] instanceof MouldingAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x + 1, y, z) == SUBTYPE_TABLE)
			{
				renderBlocks.setRenderBounds(1D, 0.0D, 0.6875D, 0.75D, 0.875D, 0.3125D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesTableHaveLeg(renderBlocks.blockAccess, x, y, z + 1) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x, y, z + 1)] instanceof MouldingAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x, y, z + 1) == SUBTYPE_TABLE)
			{
				renderBlocks.setRenderBounds(0.6875D, 0.0D, 1D, 0.3125D, 0.875D, 0.75D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderTableInvBlock(RenderBlocks renderBlocks, Block block) {
		renderBlocks.setRenderBounds(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 15);
		renderBlocks.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 0.875D, 0.75D);
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, 15);
	}
}
