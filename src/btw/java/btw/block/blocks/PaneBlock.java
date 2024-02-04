// FCMOD

package btw.block.blocks;

import btw.world.util.BlockPos;
import com.prupe.mcpatcher.ctm.GlassPaneRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class PaneBlock extends BlockPane {
	public PaneBlock(int iBlockID, String sTextureName, String sSideTextureName, Material material, boolean bCanDropItself) {
		super(iBlockID, sTextureName, sSideTextureName, material, bCanDropItself);
		
		initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		boolean north = canConnectToBlockToFacing(blockAccess, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(blockAccess, x, y, z, 3);
		boolean east = canConnectToBlockToFacing(blockAccess, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(blockAccess, x, y, z, 5);
		
		double minX = .4375;
		double minZ = .4375;
		double maxX = .5625;
		double maxZ = .5625;
		
		if (!(east || west || north || south)) {
			minX = 0;
			minZ = 0;
			maxX = 1;
			maxZ = 1;
		}
		
		if (east)
			minX = 0;
		if (west)
			maxX = 1;
		if (north)
			minZ = 0;
		if (south)
			maxZ = 1;
		
		return AxisAlignedBB.getAABBPool().getAABB(minX, 0, minZ, maxX, 1, maxZ);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectingBox, List list, Entity entity) {
		boolean north = canConnectToBlockToFacing(world, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(world, x, y, z, 3);
		boolean east = canConnectToBlockToFacing(world, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(world, x, y, z, 5);
		
		double height = 1;
		
		if (east) {
			AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.0D, 0.4325D, 0.5D, height, 0.5625D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
		}
		
		if (west) {
			AxisAlignedBB.getAABBPool().getAABB(0.5D, 0.0D, 0.4325D, 1.0D, height, 0.5625D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
		}
		
		if (north) {
			AxisAlignedBB.getAABBPool().getAABB(0.4325D, 0.0D, 0.0D, 0.5625D, height, 0.5D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
		}
		
		if (south) {
			AxisAlignedBB.getAABBPool().getAABB(0.4325D, 0.0D, 0.5D, 0.5625D, height, 1.0D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
		}
		
		if (!south && !north && !east && !west) {
			AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.0D, 0.3125D, 0.5D, height, 0.6875D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
			AxisAlignedBB.getAABBPool().getAABB(0.5D, 0.0D, 0.3125D, 1.0D, height, 0.6875D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
			AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.0D, 0.0D, 0.6875D, height, 0.5D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
			AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.0D, 0.5D, 0.6875D, height, 1.0D).offset(x, y, z).addToListIfIntersects(intersectingBox, list);
		}
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		// parent method is deprecated
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		// parent method is deprecated
	}
	
	@Override
	public boolean shouldWallConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return true;
	}
	
	@Override
	public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
    
    public static boolean canConnectToBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        BlockPos blockPos = new BlockPos(x, y, z, facing);
        Block block = Block.blocksList[blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z)];
        
        if (block != null) {
            return block.shouldPaneConnectToThisBlockToFacing(blockAccess, x, y, z, Facing.oppositeSide[facing]);
        }
        else {
            return false;
        }
    }
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		int var5 = render.blockAccess.getHeight();
		Tessellator var6 = Tessellator.instance;
		var6.setBrightness(this.getMixedBrightnessForBlock(render.blockAccess, x, y, z));
		float var7 = 1.0F;
		int var8 = this.colorMultiplier(render.blockAccess, x, y, z);
		float var9 = (float)(var8 >> 16 & 255) / 255.0F;
		float var10 = (float)(var8 >> 8 & 255) / 255.0F;
		float var11 = (float)(var8 & 255) / 255.0F;
		
		var6.setColorOpaque_F(var7 * var9, var7 * var10, var7 * var11);
		Icon var64;
		Icon var65;
		int var66;
		
		if (render.hasOverrideBlockTexture())
		{
			var64 = render.getOverrideTexture();
			var65 = render.getOverrideTexture();
		}
		else
		{
			var66 = render.blockAccess.getBlockMetadata(x, y, z);
			var64 = render.blockAccess == null ? render.getBlockIconFromSideAndMetadata(this, 0, var66) : render.getBlockIcon(this, render.blockAccess, x, y, z, 0);
			var65 = this.getSideTextureIndex();
		}
		
		var66 = var64.getOriginX();
		int var15 = var64.getOriginY();
		double var16 = (double)var64.getMinU();
		double var18 = (double)var64.getInterpolatedU(8.0D);
		double var20 = (double)var64.getMaxU();
		double var22 = (double)var64.getMinV();
		double var24 = (double)var64.getMaxV();
		int var26 = var65.getOriginX();
		int var27 = var65.getOriginY();
		double var28 = (double)var65.getInterpolatedU(7.0D);
		double var30 = (double)var65.getInterpolatedU(9.0D);
		double var32 = (double)var65.getMinV();
		double var34 = (double)var65.getInterpolatedV(8.0D);
		double var36 = (double)var65.getMaxV();
		double var38 = (double)x;
		double var40 = (double)x + 0.5D;
		double var42 = (double)(x + 1);
		double var44 = (double)z;
		double var46 = (double)z + 0.5D;
		double var48 = (double)(z + 1);
		double var50 = (double)x + 0.5D - 0.0625D;
		double var52 = (double)x + 0.5D + 0.0625D;
		double var54 = (double)z + 0.5D - 0.0625D;
		double var56 = (double)z + 0.5D + 0.0625D;
		
		boolean north = canConnectToBlockToFacing(render.blockAccess, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(render.blockAccess, x, y, z, 3);
		boolean east = canConnectToBlockToFacing(render.blockAccess, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(render.blockAccess, x, y, z, 5);
		
		boolean var62 = this.shouldSideBeRendered(render.blockAccess, x, y + 1, z, 1);
		boolean var63 = this.shouldSideBeRendered(render.blockAccess, x, y - 1, z, 0);
		
		GlassPaneRenderer.renderThin(render, this, var64, x, y, z, north, south, east, west);
		
		if ((!east || !west) && (east || west || north || south))
		{
			if (east && !west)
			{
				if (!GlassPaneRenderer.skipPaneRendering)
				{
					var6.addVertexWithUV(var38, (double)(y + 1), var46, var16, var22);
					var6.addVertexWithUV(var38, (double)(y + 0), var46, var16, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var18, var22);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var16, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var16, var24);
					var6.addVertexWithUV(var38, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var38, (double)(y + 1), var46, var18, var22);
				}
				
				if (!south && !north)
				{
					var6.addVertexWithUV(var40, (double)(y + 1), var56, var28, var32);
					var6.addVertexWithUV(var40, (double)(y + 0), var56, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 0), var54, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1), var54, var30, var32);
					var6.addVertexWithUV(var40, (double)(y + 1), var54, var28, var32);
					var6.addVertexWithUV(var40, (double)(y + 0), var54, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 0), var56, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1), var56, var30, var32);
				}
				
				if (var62 || y < var5 - 1 && render.blockAccess.isAirBlock(x - 1, y + 1, z))
				{
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var34);
				}
				
				if ((var62 || y < var5 - 1 && render.blockAccess.isAirBlock(x - 1, y + 1, z)) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var34);
				}
			}
			else if (!east && west)
			{
				if (!GlassPaneRenderer.skipPaneRendering)
				{
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var18, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var42, (double)(y + 0), var46, var20, var24);
					var6.addVertexWithUV(var42, (double)(y + 1), var46, var20, var22);
					var6.addVertexWithUV(var42, (double)(y + 1), var46, var18, var22);
					var6.addVertexWithUV(var42, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var20, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var20, var22);
				}
				
				if (!south && !north)
				{
					var6.addVertexWithUV(var40, (double)(y + 1), var54, var28, var32);
					var6.addVertexWithUV(var40, (double)(y + 0), var54, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 0), var56, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1), var56, var30, var32);
					var6.addVertexWithUV(var40, (double)(y + 1), var56, var28, var32);
					var6.addVertexWithUV(var40, (double)(y + 0), var56, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 0), var54, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1), var54, var30, var32);
				}
				
				if ((var62 || y < var5 - 1 && render.blockAccess.isAirBlock(x + 1, y + 1, z)) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var32);
				}
				
				if ((var63 || y > 1 && render.blockAccess.isAirBlock(x + 1, y - 1, z)) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var32);
				}
			}
		}
		else
		{
			if (!GlassPaneRenderer.skipPaneRendering)
			{
				var6.addVertexWithUV(var38, (double)(y + 1), var46, var16, var22);
				var6.addVertexWithUV(var38, (double)(y + 0), var46, var16, var24);
				var6.addVertexWithUV(var42, (double)(y + 0), var46, var20, var24);
				var6.addVertexWithUV(var42, (double)(y + 1), var46, var20, var22);
				var6.addVertexWithUV(var42, (double)(y + 1), var46, var16, var22);
				var6.addVertexWithUV(var42, (double)(y + 0), var46, var16, var24);
				var6.addVertexWithUV(var38, (double)(y + 0), var46, var20, var24);
				var6.addVertexWithUV(var38, (double)(y + 1), var46, var20, var22);
			}
			
			if (var62)
			{
				if (!GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var36);
				}
			}
			else
			{
				if (y < var5 - 1 && render.blockAccess.isAirBlock(x - 1, y + 1, z) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)(y + 1) + 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var34);
				}
				
				if (y < var5 - 1 && render.blockAccess.isAirBlock(x + 1, y + 1, z) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)(y + 1) + 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var42, (double)(y + 1) + 0.01D, var54, var28, var32);
				}
			}
			
			if (var63)
			{
				if (!GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var36);
				}
			}
			else
			{
				if (y > 1 && render.blockAccess.isAirBlock(x - 1, y - 1, z) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var56, var30, var36);
					var6.addVertexWithUV(var38, (double)y - 0.01D, var54, var28, var36);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var34);
				}
				
				if (y > 1 && render.blockAccess.isAirBlock(x + 1, y - 1, z) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var32);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var56, var30, var32);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var56, var30, var34);
					var6.addVertexWithUV(var40, (double)y - 0.01D, var54, var28, var34);
					var6.addVertexWithUV(var42, (double)y - 0.01D, var54, var28, var32);
				}
			}
		}
		
		if ((!north || !south) && (east || west || north || south))
		{
			if (north && !south)
			{
				if (!GlassPaneRenderer.skipPaneRendering)
				{
					var6.addVertexWithUV(var40, (double)(y + 1), var44, var16, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var44, var16, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var18, var22);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var16, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var16, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var44, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var44, var18, var22);
				}
				
				if (!west && !east)
				{
					var6.addVertexWithUV(var50, (double)(y + 1), var46, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 0), var46, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 0), var46, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1), var46, var30, var32);
					var6.addVertexWithUV(var52, (double)(y + 1), var46, var28, var32);
					var6.addVertexWithUV(var52, (double)(y + 0), var46, var28, var36);
					var6.addVertexWithUV(var50, (double)(y + 0), var46, var30, var36);
					var6.addVertexWithUV(var50, (double)(y + 1), var46, var30, var32);
				}
				
				if ((var62 || y < var5 - 1 && render.blockAccess.isAirBlock(x, y + 1, z - 1)) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var30, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var28, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var28, var32);
				}
				
				if ((var63 || y > 1 && render.blockAccess.isAirBlock(x, y - 1, z - 1)) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var30, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var28, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var28, var32);
				}
			}
			else if (!north && south)
			{
				if (!GlassPaneRenderer.skipPaneRendering)
				{
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var18, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var48, var20, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var48, var20, var22);
					var6.addVertexWithUV(var40, (double)(y + 1), var48, var18, var22);
					var6.addVertexWithUV(var40, (double)(y + 0), var48, var18, var24);
					var6.addVertexWithUV(var40, (double)(y + 0), var46, var20, var24);
					var6.addVertexWithUV(var40, (double)(y + 1), var46, var20, var22);
				}
				
				if (!west && !east)
				{
					var6.addVertexWithUV(var52, (double)(y + 1), var46, var28, var32);
					var6.addVertexWithUV(var52, (double)(y + 0), var46, var28, var36);
					var6.addVertexWithUV(var50, (double)(y + 0), var46, var30, var36);
					var6.addVertexWithUV(var50, (double)(y + 1), var46, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1), var46, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 0), var46, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 0), var46, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1), var46, var30, var32);
				}
				
				if ((var62 || y < var5 - 1 && render.blockAccess.isAirBlock(x, y + 1, z + 1)) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var34);
				}
				
				if ((var63 || y > 1 && render.blockAccess.isAirBlock(x, y - 1, z + 1)) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var28, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var34);
				}
			}
		}
		else
		{
			if (!GlassPaneRenderer.skipPaneRendering)
			{
				var6.addVertexWithUV(var40, (double)(y + 1), var48, var16, var22);
				var6.addVertexWithUV(var40, (double)(y + 0), var48, var16, var24);
				var6.addVertexWithUV(var40, (double)(y + 0), var44, var20, var24);
				var6.addVertexWithUV(var40, (double)(y + 1), var44, var20, var22);
				var6.addVertexWithUV(var40, (double)(y + 1), var44, var16, var22);
				var6.addVertexWithUV(var40, (double)(y + 0), var44, var16, var24);
				var6.addVertexWithUV(var40, (double)(y + 0), var48, var20, var24);
				var6.addVertexWithUV(var40, (double)(y + 1), var48, var20, var22);
			}
			
			if (var62)
			{
				if (!GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var28, var36);
				}
			}
			else
			{
				if (y < var5 - 1 && render.blockAccess.isAirBlock(x, y + 1, z - 1) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var30, var32);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var44, var30, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var44, var28, var34);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var28, var32);
				}
				
				if (y < var5 - 1 && render.blockAccess.isAirBlock(x, y + 1, z + 1) && !GlassPaneRenderer.skipTopEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var48, var28, var34);
					var6.addVertexWithUV(var50, (double)(y + 1) + 0.005D, var46, var28, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var46, var30, var36);
					var6.addVertexWithUV(var52, (double)(y + 1) + 0.005D, var48, var30, var34);
				}
			}
			
			if (var63)
			{
				if (!GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var28, var36);
				}
			}
			else
			{
				if (y > 1 && render.blockAccess.isAirBlock(x, y - 1, z - 1) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var28, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var30, var32);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var44, var30, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var44, var28, var34);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var28, var32);
				}
				
				if (y > 1 && render.blockAccess.isAirBlock(x, y - 1, z + 1) && !GlassPaneRenderer.skipBottomEdgeRendering)
				{
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var28, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var30, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var48, var28, var34);
					var6.addVertexWithUV(var50, (double)y - 0.005D, var46, var28, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var46, var30, var36);
					var6.addVertexWithUV(var52, (double)y - 0.005D, var48, var30, var34);
				}
			}
		}
		
		return true;
		
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		int iNeighborBlockID = blockAccess.getBlockId(iNeighborI, iNeighborJ, iNeighborK);
		
		return iNeighborBlockID != blockID && currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
}