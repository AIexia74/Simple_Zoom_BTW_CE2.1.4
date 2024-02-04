package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;

import java.util.Random;

public class SwampTreeGrower extends AbstractTreeGrower {
	public SwampTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType) {
		super(name, minTreeHeight, maxTreeHeight, woodType);
	}
	
	@Override
	public boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		int treeHeight = rand.nextInt(this.maxTreeHeight - this.minTreeHeight + 1) + minTreeHeight;
		
		if (isWorldGen) {
			for (; world.getBlockMaterial(x, y - 1, z) == Material.water; --y) {
				;
			}
		}
		
		boolean var7 = true;
		
		if (y >= 1 && y + treeHeight + 1 <= 128) {
			for (int j = y; j <= y + 1 + treeHeight; ++j) {
				byte var9 = 0;
				
				if (j >= y + 1 + treeHeight - 2 && isWorldGen) {
					var9 = 3;
				}
				
				for (int i = x - var9; i <= x + var9 && var7; ++i) {
					for (int k = z - var9; k <= z + var9 && var7; ++k) {
						if (j >= 0 && j < 128) {
							int blockID = world.getBlockId(i, j, k);
							Block block = Block.blocksList[blockID];
							
							if (block != null && !block.isLeafBlock(world, i, j, k)) {
								if (blockID != Block.waterStill.blockID && blockID != Block.waterMoving.blockID) {
									var7 = false;
								}
								else if (j > y) {
									var7 = false;
								}
							}
						}
						else {
							var7 = false;
						}
					}
				}
			}
			
			if (!var7) {
				return false;
			}
			else {
				int blockID = world.getBlockId(x, y - 1, z);
				Block block = Block.blocksList[blockID];
				
				if (block != null && block.canSaplingsGrowOnBlock(world, x, y - 1, z) && y < 128 - treeHeight - 1) {
					if ((blockID == Block.grass.blockID || blockID == Block.dirt.blockID)) {
						this.setBlock(world, x, y - 1, z, Block.dirt.blockID, isWorldGen);
					}
					
					for (int j = y - 3 + treeHeight; j <= y + treeHeight; ++j) {
						int currentOffsetFromTop = j - (y + treeHeight);
						int canopySize = 2 - currentOffsetFromTop / 2;
						
						for (int i = x - canopySize; i <= x + canopySize; ++i) {
							int xDist = i - x;
							
							for (int k = z - canopySize; k <= z + canopySize; ++k) {
								int zDist = k - z;
								
								int blockIDToReplace = world.getBlockId(i, j, k);
								Block blockToReplace = Block.blocksList[blockIDToReplace];
								
								if ((Math.abs(xDist) != canopySize || Math.abs(zDist) != canopySize || rand.nextInt(2) != 0 && currentOffsetFromTop != 0) &&
										(blockToReplace == null || blockToReplace.isLeafBlock(world, i, j, k) || blockToReplace.blockMaterial.isReplaceable()))
								{
									this.setBlockAndMetadata(world, i, j, k, this.woodType.leavesBlockID, this.woodType.leavesMetadata, isWorldGen);
								}
							}
						}
					}
					
					for (int j = 0; j < treeHeight; ++j) {
						int blockIDAbove = world.getBlockId(x, y + j, z);
						Block blockAbove = Block.blocksList[blockIDAbove];
						
						if (blockAbove == null || blockAbove.isLeafBlock(world, x, y + j, z) || blockAbove.blockMaterial == Material.water) {
							if (j == 0) {
								this.setBlockAndMetadata(world, x, y + j, z, this.woodType.stumpBlockID, this.woodType.stumpMetadata, isWorldGen);
							}
							else {
								this.setBlockAndMetadata(world, x, y + j, z, this.woodType.woodBlockID, this.woodType.woodMetadata, isWorldGen);
							}
						}
					}
					
					for (int j = y - 3 + treeHeight; j <= y + treeHeight; ++j) {
						int var10 = j - (y + treeHeight);
						int var11 = 2 - var10 / 2;
						
						for (int i = x - var11; i <= x + var11; ++i) {
							for (int k = z - var11; k <= z + var11; ++k) {
								int blockIDForVines = world.getBlockId(x, y + j, z);
								Block blockForVines = Block.blocksList[blockIDForVines];
								
								if (blockForVines == null || blockForVines.isLeafBlock(world, x, y + j, z)) {
									if (rand.nextInt(4) == 0 && world.getBlockId(i - 1, j, k) == 0) {
										this.generateVines(world, i - 1, j, k, 8, isWorldGen);
									}
									
									if (rand.nextInt(4) == 0 && world.getBlockId(i + 1, j, k) == 0) {
										this.generateVines(world, i + 1, j, k, 2, isWorldGen);
									}
									
									if (rand.nextInt(4) == 0 && world.getBlockId(i, j, k - 1) == 0) {
										this.generateVines(world, i, j, k - 1, 1, isWorldGen);
									}
									
									if (rand.nextInt(4) == 0 && world.getBlockId(i, j, k + 1) == 0) {
										this.generateVines(world, i, j, k + 1, 4, isWorldGen);
									}
								}
							}
						}
					}
					
					return true;
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * Generates vines at the given position until it hits a block.
	 */
	private void generateVines(World par1World, int par2, int par3, int par4, int par5, boolean isWorldGen) {
		this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5, isWorldGen);
		int var6 = 4;
		
		while (true) {
			--par3;
			
			if (par1World.getBlockId(par2, par3, par4) != 0 || var6 <= 0) {
				return;
			}
			
			this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5, isWorldGen);
			--var6;
		}
	}
}
