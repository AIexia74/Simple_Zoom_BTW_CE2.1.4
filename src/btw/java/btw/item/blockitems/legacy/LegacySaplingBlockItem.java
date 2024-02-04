package btw.item.blockitems.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.legacy.LegacySaplingBlock;
import net.minecraft.src.Block;
import net.minecraft.src.ItemMultiTextureTile;

public class LegacySaplingBlockItem extends ItemMultiTextureTile {
	public LegacySaplingBlockItem(int itemID) {
		super(itemID, Block.sapling, LegacySaplingBlock.saplingTypes);
	}
	
	@Override
	public int getBlockIDToPlace(int itemDamage, int facing, float hitX, float hitY, float hitZ) {
		switch (itemDamage & 3) {
			default:
				return BTWBlocks.oakSapling.blockID;
			case 1:
				return BTWBlocks.spruceSapling.blockID;
			case 2:
				return BTWBlocks.birchSapling.blockID;
			case 3:
				return BTWBlocks.jungleSapling.blockID;
		}
	}
	
	@Override
	public int getMetadata(int itemDamage) {
		return itemDamage % 4;
	}
}
