package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class MyceliumSlabBlockItem extends SlabBlockItem {
	public MyceliumSlabBlockItem(int id) {
		super(id);
	}

	@Override
	public boolean canCombineWithBlock(World world, int x, int y, int z, int itemDamage) {
		int blockID = world.getBlockId(x, y, z);

		if (blockID == BTWBlocks.dirtSlab.blockID) {
			int targetSubtype = ((DirtSlabBlock) BTWBlocks.dirtSlab).getSubtype(world, x, y, z);

			if (targetSubtype == DirtSlabBlock.SUBTYPE_DIRT) {
				return true;
			}
		}
		else if (blockID == BTWBlocks.myceliumSlab.blockID) {
			return true;
		}

		return false;
	}

	@Override
	public boolean convertToFullBlock(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);

		if (blockID == BTWBlocks.dirtSlab.blockID) {
			DirtSlabBlock slabBlock = (DirtSlabBlock)(BTWBlocks.dirtSlab);

			boolean isTargetUpsideDown = slabBlock.getIsUpsideDown(world, x, y, z);
			int targetSubType = slabBlock.getSubtype(world, x, y, z);

			if (isTargetUpsideDown) {
				if (targetSubType == DirtSlabBlock.SUBTYPE_DIRT) {
					return world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
				}
			}

			return world.setBlockWithNotify(x, y, z, Block.mycelium.blockID);
		}
		else if (blockID == BTWBlocks.myceliumSlab.blockID) {
			return world.setBlockWithNotify(x, y, z, Block.mycelium.blockID);
		}

		return false;
	}
}