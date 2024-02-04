package btw.world.feature.trees.grower;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import java.util.Random;

public abstract class AbstractTreeGrower {
	public final String name;
	
	protected final TreeGrowers.TreeWoodType woodType;
	
	protected int minTreeHeight;
	protected int maxTreeHeight;
	
	protected AbstractTreeGrower(String name, int minTreeHeight, int maxTreeHeight, TreeGrowers.TreeWoodType woodType) {
		this.name = name;
		this.woodType = woodType;
		
		this.minTreeHeight = minTreeHeight;
		this.maxTreeHeight = maxTreeHeight;
	}
	
	public abstract boolean growTree(World world, Random rand, int x, int y, int z, boolean isWorldGen);
	
	protected void setBlock(World world, int x, int y, int z, int id, boolean notify) {
        this.setBlockAndMetadata(world, x, y, z, id, 0, notify);
    }
	
    protected void setBlockAndMetadata(World world, int x, int y, int z, int id, int meta, boolean notify) {
        if (notify) {
            world.setBlock(x, y, z, id, meta, 3);
        }
        else {
            world.setBlock(x, y, z, id, meta, 2);
        }
    }
	
	protected boolean isReplaceable(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		
		return world.isAirBlock(x, y, z) || block.isLeafBlock(world, x, y, z) || block.blockMaterial.isReplaceable();
	}
	
	protected boolean isLogReplaceable(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		
		return this.isReplaceable(world, x, y, z) || block.isLog(world, x, y, z);
	}
}