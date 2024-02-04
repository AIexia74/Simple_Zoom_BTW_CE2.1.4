// FCMOD

package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class WallBlock extends BlockWall {
	public WallBlock(int iBlockID, Block baseBlock) {
		super(iBlockID, baseBlock);
		
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		return iFacing == 0 || iFacing == 1;
	}
	
	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k) {
		return -3;
	}
	
	@Override
	public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
		int iType = iMetadata & 1;
		
		if (iType == 0) // cobble
		{
			return BTWItems.stone.itemID;
		}
		
		return super.idDropped(iMetadata, rand, iFortuneModifier);
	}
	
	@Override
	public int damageDropped(int metadata) {
		int type = metadata & 1;
		
		if (type == 0) // cobble
		{
			return metadata >> 2;
		}
		
		return super.damageDropped(metadata);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
		int iType = iMetadata & 1;
		
		if (iType == 0) // cobble
		{
			if (!world.isRemote) {
				int iNumDropped = 4;
				
				for (int k1 = 0; k1 < iNumDropped; k1++) {
					int iItemID = idDropped(iMetadata, world.rand, iFortuneModifier);
					
					if (iItemID > 0) {
						dropBlockAsItem_do(world, i, j, k, new ItemStack(iItemID, 1, damageDropped(iMetadata)));
					}
				}
			}
		}
		else {
			super.dropBlockAsItemWithChance(world, i, j, k, iMetadata, fChance, iFortuneModifier);
		}
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
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		// override to deprecate parent
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		boolean post = wallHasPost(blockAccess, x, y, z, true, true);
		
		boolean east = canConnectToBlockToFacing(blockAccess, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(blockAccess, x, y, z, 5);
		boolean north = canConnectToBlockToFacing(blockAccess, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(blockAccess, x, y, z, 3);
		
		boolean eastFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 4);
		boolean westFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 5);
		boolean northFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 2);
		boolean southFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 3);
		
		double minX = .3125;
		double minZ = .3125;
		double maxX = .6875;
		double maxZ = .6875;
		
		double height = 0.8125;
		if (eastFullWall || westFullWall || northFullWall || southFullWall || post)
			height = 1.0;
		
		if (post) {
			minX = .25;
			minZ = .25;
			maxX = .75;
			maxZ = .75;
		}
		
		if (east)
			minX = 0;
		if (west)
			maxX = 1;
		if (north)
			minZ = 0;
		if (south)
			maxZ = 1;
		
		return AxisAlignedBB.getAABBPool().getAABB(minX, 0.0D, minZ, maxX, height, maxZ);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List collisionList, Entity entity) {
		boolean post = wallHasPost(world, x, y, z, true, true);
		
		boolean east = canConnectToBlockToFacing(world, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(world, x, y, z, 5);
		boolean north = canConnectToBlockToFacing(world, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(world, x, y, z, 3);
		
		if (post) {
			AxisAlignedBB.getAABBPool().getAABB(0.25D, 0.0D, 0.25D, 0.75D, 1.5D, 0.75D).offset(x, y, z).addToListIfIntersects(aabb, collisionList);
		}
		
		double height = 1.5;
		
		if (east) {
			AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.0D, 0.3125D, 0.5D, height, 0.6875D).offset(x, y, z).addToListIfIntersects(aabb, collisionList);
		}
		
		if (west) {
			AxisAlignedBB.getAABBPool().getAABB(0.5D, 0.0D, 0.3125D, 1.0D, height, 0.6875D).offset(x, y, z).addToListIfIntersects(aabb, collisionList);
		}
		
		if (north) {
			AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.0D, 0.0D, 0.6875D, height, 0.5D).offset(x, y, z).addToListIfIntersects(aabb, collisionList);
		}
		
		if (south) {
			AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.0D, 0.5D, 0.6875D, height, 1.0D).offset(x, y, z).addToListIfIntersects(aabb, collisionList);
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		AxisAlignedBB box = getBlockBoundsFromPoolBasedOnState(world, i, j, k);
		
		box.maxY = 1.5D;
		
		return box.offset(i, j, k);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 var5, Vec3 var6) {
		boolean post = wallHasPost(world, x, y, z, true, true);
		
		boolean east = canConnectToBlockToFacing(world, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(world, x, y, z, 5);
		boolean north = canConnectToBlockToFacing(world, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(world, x, y, z, 3);
		
		boolean eastFullWall = shouldHaveFullHeightWallToFacing(world, x, y, z, 4);
		boolean westFullWall = shouldHaveFullHeightWallToFacing(world, x, y, z, 5);
		boolean northFullWall = shouldHaveFullHeightWallToFacing(world, x, y, z, 2);
		boolean southFullWall = shouldHaveFullHeightWallToFacing(world, x, y, z, 3);
		
		RayTraceUtils raytracer = new RayTraceUtils(world, x, y, z, var5, var6);
		
		if (post) {
			raytracer.addBoxWithLocalCoordsToIntersectionList(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
		}
		
		if (east) {
			double height = 0.8125;
			if (eastFullWall)
				height = 1.0;
			
			raytracer.addBoxWithLocalCoordsToIntersectionList(0.0D, 0.0D, 0.3125D, 0.5D, height, 0.6875D);
		}
		
		if (west) {
			double height = 0.8125;
			if (westFullWall)
				height = 1.0;
			
			raytracer.addBoxWithLocalCoordsToIntersectionList(0.5D, 0.0D, 0.3125D, 1.0D, height, 0.6875D);
		}
		
		if (north) {
			double height = 0.8125;
			if (northFullWall)
				height = 1.0;
			
			raytracer.addBoxWithLocalCoordsToIntersectionList(0.3125D, 0.0D, 0.0D, 0.6875D, height, 0.5D);
		}
		
		if (south) {
			double height = 0.8125;
			if (southFullWall)
				height = 1.0;
			
			raytracer.addBoxWithLocalCoordsToIntersectionList(0.3125D, 0.0D, 0.5D, 0.6875D, height, 1.0D);
		}
		
		return raytracer.getFirstIntersection();
	}
	
	@Override
	public boolean isWall(int metadata) {
		return true;
	}
	
	public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	/**
	 * normal mossy
	 */
	public int getStoneType(int metadata) {
		return metadata & 1;
	}
	
	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata.
	 * BEWARE: different blocks store strata differently
	 */
	public int getStrata(IBlockAccess blockAccess, int i, int j, int k) {
		return getStrata(blockAccess.getBlockMetadata(i, j, k));
	}
	
	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata.
	 * BEWARE: different blocks store strata differently
	 */
	public int getStrata(int iMetadata) {
		return (iMetadata & 12) >>> 2;
	}
	
	public static boolean canConnectToBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		BlockPos blockPos = new BlockPos(x, y, z, facing);
		Block block = Block.blocksList[blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z)];
		
		if (block != null) {
			return block.shouldWallConnectToThisBlockToFacing(blockAccess, blockPos.x, blockPos.y, blockPos.z, Facing.oppositeSide[facing]);
		}
		else {
			return false;
		}
	}
	
	public static boolean wallHasPost(IBlockAccess blockAccess, int x, int y, int z, boolean checkAbove, boolean checkBelow) {
		int idAbove = blockAccess.getBlockId(x, y + 1, z);
		Block blockAbove = Block.blocksList[idAbove];
		int metaAbove = blockAccess.getBlockMetadata(x, y + 1, z);
		int idBelow = blockAccess.getBlockId(x, y - 1, z);
		Block blockBelow = Block.blocksList[idBelow];
		int metaBelow = blockAccess.getBlockMetadata(x, y - 1, z);
		
		//Get whether the wall should connect to each facing
		boolean north = canConnectToBlockToFacing(blockAccess, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(blockAccess, x, y, z, 3);
		boolean east = canConnectToBlockToFacing(blockAccess, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(blockAccess, x, y, z, 5);
		boolean NS = north && south && !east && !west;
		boolean EW = !north && !south && east && west;
		
		//If the wall does not have exactly 2 connections it must have a post
		if (!NS && !EW)
			return true;
		
		if ((blockAbove != null && blockAbove.isWall(metaAbove)) || (blockBelow != null && blockBelow.isWall(metaBelow))) {
			//Recursively checks wall above
			if (blockAbove != null && blockAbove.isWall(metaAbove) && checkAbove) {
				return wallHasPost(blockAccess, x, y + 1, z, true, false);
			}
			
			//Checks wall below
			if (blockBelow != null && blockBelow.isWall(metaBelow) && checkBelow) {
				//return wallHasPost(blockAccess, x, y - 1, z, false, true);
			}
		}
		
		boolean airAbove = blockAccess.isAirBlock(x, y + 1, z) || WorldUtils.isGroundCoverOnBlock(blockAccess, x, y, z);
		boolean solidSurface = blockAbove != null && blockAbove.hasLargeCenterHardPointToFacing(blockAccess, x, y + 1, z, 0);
		boolean paneAbove = blockAbove instanceof BlockPane;
		
		boolean paneToSide = false;
		
		for (int i = 0; i < 4; i++) {
			BlockPos blockPos = new BlockPos(x, y, z, i + 2);
			int idOffset = blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z);
			if (Block.blocksList[idOffset] instanceof BlockPane) {
				paneToSide = true;
			}
		}
		
		//No post if air above
		if (airAbove && !paneToSide) {
			return false;
		}
		
		//No post if solid surface and BOTH connections are full height walls
		if (solidSurface || paneToSide) {
			if (NS) {
				boolean northFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 2);
				boolean southFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 3);
				
				return !(northFullWall && southFullWall);
			}
			else {
				boolean eastFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 4);
				boolean westFullWall = shouldHaveFullHeightWallToFacing(blockAccess, x, y, z, 5);
				
				return !(eastFullWall && westFullWall);
			}
		}
		
		return blockAbove != null && blockAbove.shouldWallFormPostBelowThisBlock(blockAccess, x, y + 1, z);
	}
	
	public static boolean shouldHaveFullHeightWallToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		boolean connect = canConnectToBlockToFacing(blockAccess, x, y, z, facing);
		
		//Sanity check for non-connecting facing
		if (!connect) {
			return false;
		}
		
		int idAbove = blockAccess.getBlockId(x, y + 1, z);
		int metaAbove = blockAccess.getBlockMetadata(x, y + 1, z);
		Block blockAbove = Block.blocksList[idAbove];
		boolean solidSurfaceAbove = blockAbove != null && blockAbove.hasLargeCenterHardPointToFacing(blockAccess, x, y + 1, z, 0);
		boolean paneAbove = blockAbove instanceof BlockPane;
		boolean canPaneAboveConnectToFacing = false;
		
		if (paneAbove) {
			BlockPos blockPosPane = new BlockPos(x, y + 1, z, facing);
			canPaneAboveConnectToFacing = PaneBlock.canConnectToBlockToFacing(blockAccess, blockPosPane.x, blockPosPane.y, blockPosPane.z, Facing.oppositeSide[facing]);
		}
		
		//Gets coordinates for block in facing direction
		BlockPos blockPos = new BlockPos(x, y, z, facing);
		
		Block sideBlock = Block.blocksList[blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z)];
		boolean solidSide = sideBlock.hasLargeCenterHardPointToFacing(blockAccess, blockPos.x, blockPos.y, blockPos.z, Facing.oppositeSide[facing]);
		
		int idOffset = blockAccess.getBlockId(blockPos.x, blockPos.y, blockPos.z);
		boolean paneToSide = Block.blocksList[idOffset] instanceof BlockPane;
		
		int idAboveOffset = blockAccess.getBlockId(blockPos.x, blockPos.y + 1, blockPos.z);
		int metaAboveOffset = blockAccess.getBlockMetadata(blockPos.x, blockPos.y + 1, blockPos.z);
		Block blockAboveOffset = Block.blocksList[idAboveOffset];
		
		boolean solidSurfaceDiagonal = blockAboveOffset != null && blockAboveOffset.hasLargeCenterHardPointToFacing(blockAccess, blockPos.x, blockPos.y + 1, blockPos.z, 0);
		boolean paneAboveDiagonal = blockAboveOffset instanceof BlockPane;
		
		if (blockAbove != null && blockAbove.isWall(metaAbove) && !solidSurfaceDiagonal && !paneToSide) {
			return canConnectToBlockToFacing(blockAccess, x, y + 1, z, facing);
		}
		
		boolean wallOrBenchAbove = (blockAbove != null && (blockAbove.isWall(metaAbove) || blockAbove.isBenchOrTable(metaAbove)));
		boolean paneAboveInSameDirection = (paneAbove && canPaneAboveConnectToFacing);
		
		boolean aboveConditionMet = wallOrBenchAbove ||
				solidSurfaceAbove ||
				paneAboveInSameDirection;
		
		boolean wallOrBenchDiagonal = (blockAboveOffset != null && (blockAboveOffset.isWall(metaAboveOffset) || blockAboveOffset.isBenchOrTable(metaAboveOffset)));
		// Ignore diagonals if solid block is to the side, unless block above is a pane
		// in which case it must connect in the same direction as this wall
		boolean canIgnoreDiagonals = (solidSide && (!paneAbove || canPaneAboveConnectToFacing));
		
		boolean diagonalConditionMet = wallOrBenchDiagonal ||
				solidSurfaceDiagonal ||
				canIgnoreDiagonals ||
				paneAboveDiagonal;
		
		// Always make full height connection when connecting to a pane
		// Otherwise both the block above and diagonally above must be correct
		return paneToSide || (aboveConditionMet && diagonalConditionMet);
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		return renderWall(render, this, x, y, z);
	}
	
	@Environment(EnvType.CLIENT)
	public static boolean renderWall(RenderBlocks render, Block block, int x, int y, int z) {
		boolean post = wallHasPost(render.blockAccess, x, y, z, true, true);
		
		boolean east = canConnectToBlockToFacing(render.blockAccess, x, y, z, 4);
		boolean west = canConnectToBlockToFacing(render.blockAccess, x, y, z, 5);
		boolean north = canConnectToBlockToFacing(render.blockAccess, x, y, z, 2);
		boolean south = canConnectToBlockToFacing(render.blockAccess, x, y, z, 3);
		
		boolean eastFullWall = shouldHaveFullHeightWallToFacing(render.blockAccess, x, y, z, 4);
		boolean westFullWall = shouldHaveFullHeightWallToFacing(render.blockAccess, x, y, z, 5);
		boolean northFullWall = shouldHaveFullHeightWallToFacing(render.blockAccess, x, y, z, 2);
		boolean southFullWall = shouldHaveFullHeightWallToFacing(render.blockAccess, x, y, z, 3);
		
		if (post) {
			render.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
			render.renderStandardBlock(block, x, y, z);
		}
		
		if (east) {
			double height = 0.8125;
			if (eastFullWall) {
				height = 1.0;
			}
			
			render.setRenderBounds(0.0D, 0.0D, 0.3125D, 0.5D, height, 0.6875D);
			render.renderStandardBlock(block, x, y, z);
		}
		
		if (west) {
			double height = 0.8125;
			if (westFullWall) {
				height = 1.0;
			}
			
			render.setRenderBounds(0.5D, 0.0D, 0.3125D, 1.0D, height, 0.6875D);
			render.renderStandardBlock(block, x, y, z);
		}
		
		if (north) {
			double height = 0.8125;
			if (northFullWall) {
				height = 1.0;
			}
			
			render.setRenderBounds(0.3125D, 0.0D, 0.0D, 0.6875D, height, 0.5D);
			render.renderStandardBlock(block, x, y, z);
		}
		
		if (south) {
			double height = 0.8125;
			if (southFullWall) {
				height = 1.0;
			}
			
			render.setRenderBounds(0.3125D, 0.0D, 0.5D, 0.6875D, height, 1.0D);
			render.renderStandardBlock(block, x, y, z);
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 3; i++) {
			par3List.add(new ItemStack(par1, 1, 0 + i << 2));
			par3List.add(new ItemStack(par1, 1, 1 + (i << 2)));
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int side, int metadata) {
		int strata = getStrata(metadata); // mossy and cobble both save in first 2 bits
		int type = getStoneType(metadata);
		return type == 1 ? Block.cobblestoneMossy.getIcon(side, strata) : Block.cobblestone.getIcon(side, strata);
	}
}