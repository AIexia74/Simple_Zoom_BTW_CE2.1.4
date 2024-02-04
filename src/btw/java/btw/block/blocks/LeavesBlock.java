// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LeavesBlock extends BlockLeaves {
	protected static final int ADJACENT_TREE_BLOCK_ARRAY_WIDTH = 32;
	protected static final int ARRAY_WIDTH_HALF = ADJACENT_TREE_BLOCK_ARRAY_WIDTH / 2;
	protected static final int ADJACENT_TREE_BLOCK_SEARCH_DIST = 4;
	protected static final int ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST = ADJACENT_TREE_BLOCK_SEARCH_DIST + 1;
	
	protected int[][][] adjacentTreeBlocks3D = new int[ADJACENT_TREE_BLOCK_ARRAY_WIDTH][ADJACENT_TREE_BLOCK_ARRAY_WIDTH][ADJACENT_TREE_BLOCK_ARRAY_WIDTH];
	
	public LeavesBlock(int iBlockID) {
		super(iBlockID);
		
		setHardness(0.2F);
		setAxesEffectiveOn(true);
		
		setBuoyant();
		
		setLightOpacity(1);
		
		setFireProperties(Flammability.LEAVES);
		
		setStepSound(soundGrassFootstep);
		
		setUnlocalizedName("leaves");
	}
	
	@Override
	public float getMovementModifier(World world, int i, int j, int k) {
		return 0.5F;
	}
	
	@Override
	public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k) {
		return true;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
		// override of parent function to get rid of apple drop on oak
		
		if (!world.isRemote) {
			int iChanceOfSaplingDrop = 20;
			
			if (world.rand.nextInt(iChanceOfSaplingDrop) == 0) {
				int iIdDropped = idDropped(iMetadata, world.rand, iFortuneModifier);
				
				dropBlockAsItem_do(world, i, j, k, new ItemStack(iIdDropped, 1, damageDropped(iMetadata)));
			}
		}
	}
	
	@Override
	public int idDropped(int metadata, Random rand, int fortuneModifier) {
		int type = metadata & 3;
		
		switch (type) {
			default:
				return BTWBlocks.oakSapling.blockID;
			case 1:
				return BTWBlocks.spruceSapling.blockID;
			case 2:
				return BTWBlocks.birchSapling.blockID;
			case 3:
				return BTWBlocks.jungleSapling.blockID;
		}
	}
	
	@Override
	public int damageDropped(int metadata) {
		return 0;
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random rand) {
		if (!world.isRemote) {
			int iMetadata = world.getBlockMetadata(i, j, k);
			
			// 8 bit on metadata indicates that the block has been flagged by a neighbor change (in breakBlock() in BlockLeaves or BlockLog)
			// 4 bit indicates that it was placed by a player and should never decay
			if ((iMetadata & 8) != 0 && (iMetadata & 4) == 0) {
				if (world.checkChunksExist(i - ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST, j - ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST,
						k - ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST, i + ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST, j + ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST,
						k + ADJACENT_TREE_BLOCK_CHUNK_CHECK_DIST)) {
					updateAdjacentTreeBlockArray(world, i, j, k);
					
					int var12 = adjacentTreeBlocks3D[ARRAY_WIDTH_HALF][ARRAY_WIDTH_HALF][ARRAY_WIDTH_HALF];
					
					if (var12 >= 0) {
						int iNewMetadata = iMetadata & 7;
						
						world.SetBlockMetadataWithNotify(i, j, k, iNewMetadata, 4);
					}
					else {
						this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
						world.setBlockToAir(i, j, k);
					}
				}
			}
		}
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
		return bIgnoreTransparency;
	}
	
	@Override
	public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread) {
		super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
		
		generateAshOnBurn(world, i, j, k);
	}
	
	@Override
	public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
		return createStackedBlock(world.getBlockMetadata(i, j, k));
	}
	
	@Override
	public boolean canMobsSpawnOn(World world, int i, int j, int k) {
		return world.provider.dimensionId != -1;
	}
	
	@Override
	public boolean isLeafBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	protected void generateAshOnBurn(World world, int i, int j, int k) {
		for (int iTempJ = j; iTempJ > 0; iTempJ--) {
			if (AshGroundCoverBlock.canAshReplaceBlock(world, i, iTempJ, k)) {
				int iBlockBelowID = world.getBlockId(i, iTempJ - 1, k);
				Block blockBelow = Block.blocksList[iBlockBelowID];
				
				if (blockBelow != null && blockBelow.canGroundCoverRestOnBlock(world, i, iTempJ - 1, k)) {
					world.setBlockWithNotify(i, iTempJ, k, BTWBlocks.ashCoverBlock.blockID);
					
					break;
				}
			}
			else if (world.getBlockId(i, iTempJ, k) != Block.fire.blockID) {
				break;
			}
		}
	}
	
	protected void updateAdjacentTreeBlockArray(World world, int x, int y, int z) {
		for (int i = -ADJACENT_TREE_BLOCK_SEARCH_DIST; i <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++i) {
			for (int j = -ADJACENT_TREE_BLOCK_SEARCH_DIST; j <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++j) {
				for (int k = -ADJACENT_TREE_BLOCK_SEARCH_DIST; k <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++k) {
					int blockID = world.getBlockId(x + i, y + j, z + k);
					Block block = Block.blocksList[blockID];
					
					if (block != null && block.canSupportLeaves(world, x, y, z)) {
						adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] = 0;
					}
					else if (block != null && block.isLeafBlock(world, x, y, z)) {
						adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] = -2;
					}
					else {
						adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] = -1;
					}
				}
			}
		}
		
		for (int distance = 1; distance <= 4; ++distance) {
			for (int i = -ADJACENT_TREE_BLOCK_SEARCH_DIST; i <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++i) {
				for (int j = -ADJACENT_TREE_BLOCK_SEARCH_DIST; j <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++j) {
					for (int k = -ADJACENT_TREE_BLOCK_SEARCH_DIST; k <= ADJACENT_TREE_BLOCK_SEARCH_DIST; ++k) {
						if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] == distance - 1) {
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF - 1][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF - 1][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] = distance;
							}
							
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF + 1][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF + 1][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF] = distance;
							}
							
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF - 1][k + ARRAY_WIDTH_HALF] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF - 1][k + ARRAY_WIDTH_HALF] = distance;
							}
							
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF + 1][k + ARRAY_WIDTH_HALF] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF + 1][k + ARRAY_WIDTH_HALF] = distance;
							}
							
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF - 1] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF - 1] = distance;
							}
							
							if (adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF + 1] == -2) {
								adjacentTreeBlocks3D[i + ARRAY_WIDTH_HALF][j + ARRAY_WIDTH_HALF][k + ARRAY_WIDTH_HALF + 1] = distance;
							}
						}
					}
				}
			}
		}
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(World world, int i, int j, int k, Random rand) {
		if (world.isRainingAtPos(i, j + 1, k) && !world.doesBlockHaveSolidTopSurface(i, j - 1, k) && rand.nextInt(15) == 1) {
			world.spawnParticle("dripWater", i + rand.nextDouble(), j - 0.05D, k + rand.nextDouble(), 0D, 0D, 0D);
		}
	}
}