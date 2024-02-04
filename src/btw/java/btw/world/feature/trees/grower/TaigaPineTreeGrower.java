package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import java.util.Random;

public class TaigaPineTreeGrower extends AbstractTreeGrower {
	public TaigaPineTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType) {
		super(name, minTreeHeight, maxTreeHeight, woodType);
	}
	
	@Override
	public boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		int treeHeight = rand.nextInt(this.maxTreeHeight - this.minTreeHeight + 1) + this.minTreeHeight;
		int var7 = treeHeight - rand.nextInt(2) - 3;
		int var8 = treeHeight - var7;
		int var9 = 1 + rand.nextInt(var8 + 1);
		boolean var10 = true;
		
		if (y >= 1 && y + treeHeight + 1 <= 128) {
			int var11;
			int var13;
			int var14;
			int var15;
			int var18;
			
			for (var11 = y; var11 <= y + 1 + treeHeight && var10; ++var11) {
				if (var11 - y < var7) {
					var18 = 0;
				}
				else {
					var18 = var9;
				}
				
				for (var13 = x - var18; var13 <= x + var18 && var10; ++var13) {
					for (var14 = z - var18; var14 <= z + var18 && var10; ++var14) {
						if (var11 >= 0 && var11 < 128) {
							var15 = world.getBlockId(var13, var11, var14);
							Block block = Block.blocksList[var15];
							
							if (block != null && !block.isLeafBlock(world, var13, var11, var14) && !block.blockMaterial.isReplaceable()) {
								var10 = false;
							}
						}
						else {
							var10 = false;
						}
					}
				}
			}
			
			if (!var10) {
				return false;
			}
			else {
				var11 = world.getBlockId(x, y - 1, z);
				Block blockBelow = Block.blocksList[var11];
				
				if (blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z) && y < 128 - treeHeight - 1) {
					if (var11 == Block.grass.blockID || var11 == Block.dirt.blockID) {
						this.setBlock(world, x, y - 1, z, Block.dirt.blockID, isWorldGen);
					}
					
					var18 = 0;
					
					for (var13 = y + treeHeight; var13 >= y + var7; --var13) {
						for (var14 = x - var18; var14 <= x + var18; ++var14) {
							var15 = var14 - x;
							
							for (int var16 = z - var18; var16 <= z + var18; ++var16) {
								int var17 = var16 - z;
								
								if ((Math.abs(var15) != var18 || Math.abs(var17) != var18 || var18 <= 0)) {
									int blockID = world.getBlockId(var14, var13, var16);
									Block block = Block.blocksList[blockID];
									
									if (block == null || block.isLeafBlock(world, var14, var13, var16) || block.blockMaterial.isReplaceable()) {
										this.setBlockAndMetadata(world, var14, var13, var16, this.woodType.leavesBlockID, this.woodType.leavesMetadata, isWorldGen);
									}
								}
							}
						}
						
						if (var18 >= 1 && var13 == y + var7 + 1) {
							--var18;
						}
						else if (var18 < var9) {
							++var18;
						}
					}
					
					for (var13 = 0; var13 < treeHeight - 1; ++var13) {
						var14 = world.getBlockId(x, y + var13, z);
						Block block = Block.blocksList[var14];
						
						if (block == null || block.isLeafBlock(world, x, y + var13, z)) {
							if (var13 == 0) {
								this.setBlockAndMetadata(world, x, y + var13, z, this.woodType.stumpBlockID, this.woodType.stumpMetadata, isWorldGen);
							}
							else {
								this.setBlockAndMetadata(world, x, y + var13, z, this.woodType.woodBlockID, this.woodType.woodMetadata, isWorldGen);
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
}
