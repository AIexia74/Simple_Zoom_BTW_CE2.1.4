package btw.block.blocks;

import btw.block.blocks.legacy.LegacySugarCaneBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.World;

import java.util.Random;

public class SugarCaneBlockRoots extends SugarCaneBlockBase {
	public SugarCaneBlockRoots(int id) {
		super(id);
		this.setUnlocalizedName("fcBlockReedRoots");
	}

	@Override
	public int idDropped(int par1, Random rand, int par3) {
        return BTWItems.sugarCaneRoots.itemID;
    }
	
	@Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int blockBelowID = world.getBlockId(x, y - 1, z);
        Block blockBelow = Block.blocksList[blockBelowID];

    	return blockBelow != null && 
    		!(blockBelow instanceof SugarCaneBlockBase) &&
    		!(blockBelow instanceof LegacySugarCaneBlock) &&
    		super.canPlaceBlockAt(world, x, y, z);
    }
}