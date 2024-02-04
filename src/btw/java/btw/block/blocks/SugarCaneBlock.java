package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.World;

import java.util.Random;

public class SugarCaneBlock extends SugarCaneBlockBase {
	public SugarCaneBlock(int id) {
		super(id);
		this.setUnlocalizedName("reeds");
	}
	
	@Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int blockBelowID = world.getBlockId(x, y - 1, z);
        Block blockBelow = Block.blocksList[blockBelowID];

    	return blockBelow instanceof SugarCaneBlockBase;
    }

	@Override
	public int idDropped(int par1, Random rand, int par3) {
        return Item.reed.itemID;
    }
}