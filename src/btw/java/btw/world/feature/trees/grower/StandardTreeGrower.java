package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.Direction;
import net.minecraft.src.World;

import java.util.Random;

public class StandardTreeGrower extends AbstractTreeGrower {
	protected boolean growVines;
	protected boolean growCocoa;
	
	public StandardTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType) {
		this(name, minTreeHeight, maxTreeHeight, woodType, false, false);
	}
	
	public StandardTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType, boolean growVines, boolean growCocoa) {
		super(name, minTreeHeight, maxTreeHeight, woodType);
		
		this.growVines = growVines;
		this.growCocoa = growCocoa;
	}
	
	@Override
	public boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		int treeHeight = rand.nextInt(this.maxTreeHeight - this.minTreeHeight + 1) + this.minTreeHeight;
		
		if (y >= 1 && y + treeHeight + 1 <= 256) {
			boolean canGrow = canTreeGrowHere(world, x, y, z, treeHeight, isWorldGen);
			
			if (!canGrow) {
				return false;
			}
			else {
				int blockIDBelow = world.getBlockId(x, y - 1, z);
				Block blockBelow = Block.blocksList[blockIDBelow];
				
				if (blockBelow != null && blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z) && y < 256 - treeHeight - 1) {
					if ((blockIDBelow == Block.grass.blockID || blockIDBelow == Block.dirt.blockID)) {
						this.setBlock(world, x, y - 1, z, Block.dirt.blockID, isWorldGen);
					}
					
					byte canopyOffsetFromTop = 3;
					
					for (int j = y - canopyOffsetFromTop + treeHeight; j <= y + treeHeight; ++j) {
						int currentOffsetFromTop = j - (y + treeHeight);
						int canopySize = 1 - currentOffsetFromTop / 2;
						
						for (int i = x - canopySize; i <= x + canopySize; ++i) {
							int xDist = i - x;
							
							for (int k = z - canopySize; k <= z + canopySize; ++k) {
								int zDist = k - z;
								
								if (Math.abs(xDist) != canopySize || Math.abs(zDist) != canopySize || rand.nextInt(2) != 0 && currentOffsetFromTop != 0) {
									int blockID = world.getBlockId(i, j, k);
									Block block = Block.blocksList[blockID];
									
									if (block == null || block.isLeafBlock(world, i, j, k) || block.blockMaterial.isReplaceable()) {
										this.setBlockAndMetadata(world, i, j, k, this.woodType.leavesBlockID, this.woodType.leavesMetadata, isWorldGen);
									}
								}
							}
						}
					}
					
					for (int j = 0; j < treeHeight; ++j) {
						int blockID = world.getBlockId(x, y + j, z);
						Block block = Block.blocksList[blockID];
						
						if (block == null || block.isLeafBlock(world, x, y + j, z)) {
							if (j == 0) {
								this.setBlockAndMetadata(world, x, y + j, z, this.woodType.stumpBlockID, this.woodType.stumpMetadata, isWorldGen);
							}
							else {
								this.setBlockAndMetadata(world, x, y + j, z, this.woodType.woodBlockID, this.woodType.woodMetadata, isWorldGen);
							}
							
							if (this.growVines && j > 0) {
								this.growVinesForTrunk(world, rand, x, y + j, z, isWorldGen);
							}
						}
					}
					
					if (this.growVines) {
						this.growVinesForCanopy(world, rand, x, y, z, treeHeight, isWorldGen);
					}
					
					if (this.growCocoa) {
						this.growCocoa(world, rand, x, y, z, treeHeight, isWorldGen);
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
	
	protected boolean canTreeGrowHere(World world, int x, int y, int z, int treeHeight, boolean isWorldGen) {
		for (int j = y; j <= y + 1 + treeHeight; ++j) {
			byte collisionCheckRadius = 0;
			
			// Only check for leaf collision at world gen
			if (j >= y + 1 + treeHeight - 2 && isWorldGen) {
				collisionCheckRadius = 2;
			}
			
			for (int i = x - collisionCheckRadius; i <= x + collisionCheckRadius; ++i) {
				for (int k = z - collisionCheckRadius; k <= z + collisionCheckRadius; ++k) {
					if (j >= 0 && j < 256) {
						int blockID = world.getBlockId(i, j, k);
						Block block = Block.blocksList[blockID];
						
						if (block != null && !block.isLog(world, i, j, k) && !block.isLeafBlock(world, i, j, k)) {
							return false;
						}
					}
					else {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private void growVinesForTrunk(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y, z)) {
			this.setBlockAndMetadata(world, x - 1, y, z, Block.vine.blockID, 8, isWorldGen);
		}
		
		if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y, z)) {
			this.setBlockAndMetadata(world, x + 1, y, z, Block.vine.blockID, 2, isWorldGen);
		}
		
		if (rand.nextInt(3) > 0 && world.isAirBlock(x, y, z - 1)) {
			this.setBlockAndMetadata(world, x, y, z - 1, Block.vine.blockID, 1, isWorldGen);
		}
		
		if (rand.nextInt(3) > 0 && world.isAirBlock(x, y, z + 1)) {
			this.setBlockAndMetadata(world, x, y, z + 1, Block.vine.blockID, 4, isWorldGen);
		}
	}
	
	private void growVinesForCanopy(World world, Random rand, int x, int y, int z, int treeHeight, boolean isWorldGen) {
		for (int j = y - 3 + treeHeight; j <= y + treeHeight; ++j) {
			int currentOffsetFromTop = j - (y + treeHeight);
			int canopySize = 2 - currentOffsetFromTop / 2;
			
			for (int i = x - canopySize; i <= x + canopySize; ++i) {
				for (int k = z - canopySize; k <= z + canopySize; ++k) {
					int blockID = world.getBlockId(i, j, k);
					Block block = Block.blocksList[blockID];
					
					if (block != null && block.isLeafBlock(world, i, j, k)) {
						if (rand.nextInt(4) == 0 && world.getBlockId(i - 1, j, k) == 0) {
							this.growVinesOnBlock(world, i - 1, j, k, 8, isWorldGen);
						}
						
						if (rand.nextInt(4) == 0 && world.getBlockId(i + 1, j, k) == 0) {
							this.growVinesOnBlock(world, i + 1, j, k, 2, isWorldGen);
						}
						
						if (rand.nextInt(4) == 0 && world.getBlockId(i, j, k - 1) == 0) {
							this.growVinesOnBlock(world, i, j, k - 1, 1, isWorldGen);
						}
						
						if (rand.nextInt(4) == 0 && world.getBlockId(i, j, k + 1) == 0) {
							this.growVinesOnBlock(world, i, j, k + 1, 4, isWorldGen);
						}
					}
				}
			}
		}
	}
	
	private void growVinesOnBlock(World world, int x, int y, int z, int vineMetadata, boolean isWorldGen) {
		this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineMetadata, isWorldGen);
		int var6 = 4;
		
		while (true) {
			--y;
			
			if (world.getBlockId(x, y, z) != 0 || var6 <= 0) {
				return;
			}
			
			this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineMetadata, isWorldGen);
			--var6;
		}
	}
	
	private void growCocoa(World world, Random rand, int x, int y, int z, int treeHeight, boolean isWorldGen) {
		if (rand.nextInt(5) == 0 && treeHeight > 5) {
			for (int j = 0; j < 2; ++j) {
				for (int r = 0; r < 4; ++r) {
					if (rand.nextInt(4 - j) == 0) {
						int growthStage = rand.nextInt(3);
						this.setBlockAndMetadata(world, x + Direction.offsetX[Direction.rotateOpposite[r]], y + treeHeight - 5 + j,
								z + Direction.offsetZ[Direction.rotateOpposite[r]], Block.cocoaPlant.blockID, growthStage << 2 | r,
								isWorldGen);
					}
				}
			}
		}
	}
}