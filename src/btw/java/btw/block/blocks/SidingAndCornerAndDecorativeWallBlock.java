package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SidingAndCornerAndDecorativeWallBlock extends SidingAndCornerAndDecorativeBlock {
	protected final static float BENCH_WALL_LEG_WIDTH = (8F / 16F);
	protected final static float BENCH_WALL_LEG_HALF_WIDTH = (BENCH_WALL_LEG_WIDTH / 2F);
	
	public SidingAndCornerAndDecorativeWallBlock(int blockID, Material material, String textureName, float hardness, float resistance, StepSound stepSound,
			String name) {
		super(blockID, material, textureName, hardness, resistance, stepSound, name);
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolForFence(IBlockAccess blockAccess, int i, int j, int k) {
		return Block.cobblestoneWall.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
	}
	
	@Override
	public MovingObjectPosition collisionRayTraceFence(World world, int x, int y, int z, Vec3 var5, Vec3 var6) {
		return Block.cobblestoneWall.collisionRayTrace(world, x, y, z, var5, var6);
	}
	
	@Override
	public MovingObjectPosition collisionRayTraceBenchWithLeg(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
		RayTraceUtils rayTrace = new RayTraceUtils(world, x, y, z, startRay, endRay);
		
		// top
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.0F, 0.5F - BENCH_TOP_HEIGHT, 0.0F, 1.0F, 0.5F, 1.0F);
		
		// leg
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5F - BENCH_WALL_LEG_HALF_WIDTH, 0.0F, 0.5F - BENCH_WALL_LEG_HALF_WIDTH,
				0.5F + BENCH_WALL_LEG_HALF_WIDTH, BENCH_LEG_HEIGHT, 0.5F + BENCH_WALL_LEG_HALF_WIDTH);
		
		if (this.doesBenchHaveLeg(world, x - 1, y, z) &&
				Block.blocksList[world.getBlockId(x - 1, y, z)] instanceof SidingAndCornerAndDecorativeWallBlock &&
				world.getBlockMetadata(x - 1, y, z) == SUBTYPE_BENCH)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0F, 0F, 0.3125F, 0.25F, 0.375F, 0.6875F);
		}
		if (this.doesBenchHaveLeg(world, x, y, z - 1) &&
				Block.blocksList[world.getBlockId(x, y, z - 1)] instanceof SidingAndCornerAndDecorativeWallBlock &&
				world.getBlockMetadata(x, y, z - 1) == SUBTYPE_BENCH)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.3125F, 0F, 0F, 0.6875F, 0.375F, 0.25F);
		}
		if (this.doesBenchHaveLeg(world, x + 1, y, z) &&
				Block.blocksList[world.getBlockId(x + 1, y, z)] instanceof SidingAndCornerAndDecorativeWallBlock &&
				world.getBlockMetadata(x + 1, y, z) == SUBTYPE_BENCH)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.75F, 0F, 0.3125F, 1F, 0.375F, 0.6875F);
		}
		if (this.doesBenchHaveLeg(world, x, y, z + 1) &&
				Block.blocksList[world.getBlockId(x, y, z + 1)] instanceof SidingAndCornerAndDecorativeWallBlock &&
				world.getBlockMetadata(x, y, z + 1) == SUBTYPE_BENCH)
		{
			rayTrace.addBoxWithLocalCoordsToIntersectionList(0.3125F, 0F, 0.75F, 0.6875F, 0.375F, 1F);
		}
		
		return rayTrace.getFirstIntersection();
	}
	
	@Override
	public void addCollisionBoxesToListForFence(World world, int x, int y, int z, AxisAlignedBB aabb, List collisionList, Entity entity) {
		Block.cobblestoneWall.addCollisionBoxesToList(world, x, y, z, aabb, collisionList, entity);
	}
	
	@Override
	public boolean isWall(int metadata) {
		return metadata == SUBTYPE_FENCE;
	}
	
	@Override
	public boolean isFence(int metadata) {
		return false;
	}
	
	public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	//------------ Client Side Functionality ----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBench(RenderBlocks renderBlocks, int x, int y, int z) {
		renderBlocks.setRenderBounds(0.0D, 0.375D, 0.0D, 1.0D, 0.5D, 1.0D);
		renderBlocks.renderStandardBlock(this, x, y, z);
		
		if (this.doesBenchHaveLeg(renderBlocks.blockAccess, x, y, z)) {
			renderBlocks.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 0.375D, 0.75D);
			renderBlocks.renderStandardBlock(this, x, y, z);
			
			if (this.doesBenchHaveLeg(renderBlocks.blockAccess, x - 1, y, z) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x - 1, y, z)] instanceof SidingAndCornerAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x - 1, y, z) == SUBTYPE_BENCH)
			{
				renderBlocks.setRenderBounds(0.25D, 0.0D, 0.6875D, 0D, 0.375D, 0.3125D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesBenchHaveLeg(renderBlocks.blockAccess, x, y, z - 1) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x, y, z - 1)] instanceof SidingAndCornerAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x, y, z - 1) == SUBTYPE_BENCH)
			{
				renderBlocks.setRenderBounds(0.6875D, 0.0D, 0.25D, 0.3125D, 0.375D, 0D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesBenchHaveLeg(renderBlocks.blockAccess, x + 1, y, z) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x + 1, y, z)] instanceof SidingAndCornerAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x + 1, y, z) == SUBTYPE_BENCH)
			{
				renderBlocks.setRenderBounds(1D, 0.0D, 0.6875D, 0.75D, 0.375D, 0.3125D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
			if (this.doesBenchHaveLeg(renderBlocks.blockAccess, x, y, z + 1) &&
					Block.blocksList[renderBlocks.blockAccess.getBlockId(x, y, z + 1)] instanceof SidingAndCornerAndDecorativeWallBlock &&
					renderBlocks.blockAccess.getBlockMetadata(x, y, z + 1) == SUBTYPE_BENCH)
			{
				renderBlocks.setRenderBounds(0.6875D, 0.0D, 1D, 0.3125D, 0.375D, 0.75D);
				renderBlocks.renderStandardBlock(this, x, y, z);
			}
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderFence(RenderBlocks render, int x, int y, int z) {
		return WallBlock.renderWall(render, this, x, y, z);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderFenceInvBlock(RenderBlocks renderBlocks, Block block, int itemDamage) {
		Tessellator var3 = Tessellator.instance;
		
		for (int var4 = 0; var4 < 2; ++var4) {
			float var5 = 0.125F;
			
			if (var4 == 0) {
				renderBlocks.setRenderBounds(.25, 0.0D, .25, .75, 1.0D, .75);
			}
			
			if (var4 == 1) {
				renderBlocks.setRenderBounds(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
			}
			
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			var3.startDrawingQuads();
			var3.setNormal(0.0F, -1.0F, 0.0F);
			renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 1.0F, 0.0F);
			renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 0.0F, -1.0F);
			renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 0.0F, 1.0F);
			renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(-1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
			var3.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
		
		renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	}
}