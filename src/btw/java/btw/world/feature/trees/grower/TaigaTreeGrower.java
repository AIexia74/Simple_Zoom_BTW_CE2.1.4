package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import java.util.Random;

public class TaigaTreeGrower extends AbstractTreeGrower {
	public TaigaTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType) {
		super(name, minTreeHeight, maxTreeHeight, woodType);
	}
	
	@Override
	public boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		int treeHeight = rand.nextInt(maxTreeHeight - minTreeHeight + 1) + minTreeHeight;
		int var7 = 1 + rand.nextInt(2);
		int var8 = treeHeight - var7;
		int var9 = 2 + rand.nextInt(2);
		boolean var10 = true;
		
		if (y >= 1 && y + treeHeight + 1 <= 256) {
			int var11;
			int var13;
			int var15;
			int var21;
			
			for (var11 = y; var11 <= y + 1 + treeHeight && var10; ++var11) {
				boolean var12 = true;
				
				if (var11 - y < var7) {
					var21 = 0;
				}
				else {
					var21 = var9;
				}
				
				for (var13 = x - var21; var13 <= x + var21 && var10; ++var13) {
					for (int var14 = z - var21; var14 <= z + var21 && var10; ++var14) {
						if (var11 >= 0 && var11 < 256) {
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
				
				if (blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z) && y < 256 - treeHeight - 1) {
					if (var11 == Block.grass.blockID || var11 == Block.dirt.blockID) {
						this.setBlock(world, x, y - 1, z, Block.dirt.blockID, isWorldGen);
					}
					
					var21 = rand.nextInt(2);
					var13 = 1;
					byte var22 = 0;
					int var17;
					int var16;
					
					for (var15 = 0; var15 <= var8; ++var15) {
						var16 = y + treeHeight - var15;
						
						for (var17 = x - var21; var17 <= x + var21; ++var17) {
							int var18 = var17 - x;
							
							for (int var19 = z - var21; var19 <= z + var21; ++var19) {
								int var20 = var19 - z;
								
								if ((Math.abs(var18) != var21 || Math.abs(var20) != var21 || var21 <= 0)) {
									int blockID = world.getBlockId(var17, var16, var19);
									Block block = Block.blocksList[blockID];
									
									if (block == null || block.isLeafBlock(world, var17, var16, var19) || block.blockMaterial.isReplaceable()) {
										this.setBlockAndMetadata(world, var17, var16, var19, this.woodType.leavesBlockID, this.woodType.leavesMetadata, isWorldGen);
									}
								}
							}
						}
						
						if (var21 >= var13) {
							var21 = var22;
							var22 = 1;
							++var13;
							
							if (var13 > var9) {
								var13 = var9;
							}
						}
						else {
							++var21;
						}
					}
					
					var15 = rand.nextInt(3);
					
					for (var16 = 0; var16 < treeHeight - var15; ++var16) {
						if (this.isReplaceable(world, x, y + var16, z)) {
							if (var13 == 0) {
								this.setBlockAndMetadata(world, x, y + var16, z, this.woodType.stumpBlockID, this.woodType.stumpMetadata, isWorldGen);
							}
							else {
								this.setBlockAndMetadata(world, x, y + var16, z, this.woodType.woodBlockID, this.woodType.woodMetadata, isWorldGen);
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
