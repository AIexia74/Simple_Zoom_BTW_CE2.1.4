package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import java.util.Random;

public class BushGrower extends AbstractTreeGrower {
	public BushGrower(String name, TreeGrowers.TreeWoodType woodType) {
		super(name, 1, 1, woodType);
	}
	
	@Override
	public boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen) {
		int var15;
		
		for (; ((var15 = world.getBlockId(x, y, z)) == 0 || var15 == Block.leaves.blockID) && y > 0; --y) {
			;
		}
		
		y++;
		
		if (this.canTreeGrowHere(world, x, y, z, 1, isWorldGen)) {
			this.setBlockAndMetadata(world, x, y, z, this.woodType.woodBlockID, this.woodType.woodMetadata, isWorldGen);
			
			for (int var8 = y; var8 <= y + 2; ++var8) {
				int var9 = var8 - y;
				int var10 = 2 - var9;
				
				for (int var11 = x - var10; var11 <= x + var10; ++var11) {
					int var12 = var11 - x;
					
					for (int var13 = z - var10; var13 <= z + var10; ++var13) {
						int var14 = var13 - z;
						
						if (Math.abs(var12) != var10 || Math.abs(var14) != var10 || rand.nextInt(2) != 0) {
							if (this.isReplaceable(world, var11, var8, var13)) {
								this.setBlockAndMetadata(world, var11, var8, var13, this.woodType.leavesBlockID, this.woodType.leavesMetadata, isWorldGen);
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	protected boolean canTreeGrowHere(World world, int x, int y, int z, int treeHeight, boolean isWorldGen) {
		int blockIDBelow = world.getBlockId(x, y - 1, z);
		Block blockBelow = Block.blocksList[blockIDBelow];
		return blockBelow != null && blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z) && this.isLogReplaceable(world, x, y, z);
	}
}
