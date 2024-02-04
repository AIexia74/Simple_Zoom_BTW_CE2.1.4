// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.block.blocks.DirtSlabBlock;
import btw.block.blocks.GrassSlabBlock;
import btw.block.blocks.MyceliumSlabBlock;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class DirtSlabBlockItem extends SlabBlockItem {
	public DirtSlabBlockItem(int id) {
		super(id);        
	}

	@Override
	public int getMetadata(int itemDamage) {
		return itemDamage << 1;    	
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		switch(itemstack.getItemDamage()) {
		case DirtSlabBlock.SUBTYPE_PACKED_EARTH:
			return super.getUnlocalizedName() + "." + "packed";
		case DirtSlabBlock.SUBTYPE_GRASS:
			return super.getUnlocalizedName() + "." + "grass";
		default:
			return super.getUnlocalizedName();
		}
	}

	//------------- FCItemBlockSlab ------------//

	@Override
	public boolean canCombineWithBlock(World world, int x, int y, int z, int itemDamage) {
		int blockID = world.getBlockId(x, y, z);

		if (blockID == BTWBlocks.dirtSlab.blockID) {
			int targetSubtype = ((DirtSlabBlock) BTWBlocks.dirtSlab).getSubtype(world, x, y, z);

			if (targetSubtype == DirtSlabBlock.SUBTYPE_PACKED_EARTH || itemDamage == DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
				if (targetSubtype == itemDamage) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		else if (blockID == BTWBlocks.myceliumSlab.blockID || blockID == BTWBlocks.grassSlab.blockID) {
			if (itemDamage != DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
				return true;
			}
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

			int newBlockID = Block.dirt.blockID;
			int newMetadata = 0;

			if (targetSubType == DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
				newBlockID = BTWBlocks.aestheticEarth.blockID;
				newMetadata = AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH;
			}
			else if (isTargetUpsideDown) {
				if (targetSubType == DirtSlabBlock.SUBTYPE_GRASS) {
					newBlockID = Block.grass.blockID;
				}
			}

			return world.setBlockAndMetadataWithNotify(x, y, z, newBlockID, newMetadata);
		}
		else if (blockID == BTWBlocks.myceliumSlab.blockID) {
			if (((MyceliumSlabBlock) BTWBlocks.myceliumSlab).getIsUpsideDown(world, x, y, z)) {
				return world.setBlockAndMetadataWithNotify(x, y, z, Block.mycelium.blockID, 0);
			}
			else {
				return world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, 0);
			}
		}
		else if (blockID == BTWBlocks.grassSlab.blockID) {
			if (((GrassSlabBlock) BTWBlocks.grassSlab).getIsUpsideDown(world, x, y, z)) {
				return world.setBlockAndMetadataWithNotify(x, y, z, Block.grass.blockID, 0);
			}
			else {
				return world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, 0);
			}
		}

		return false;
	}

	//------------- Class Specific Methods ------------//
}
