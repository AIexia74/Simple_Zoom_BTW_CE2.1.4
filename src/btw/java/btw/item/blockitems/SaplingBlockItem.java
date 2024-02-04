package btw.item.blockitems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class SaplingBlockItem extends ItemBlock {
	private final Block block;
	
	public SaplingBlockItem(int itemID, Block block) {
		super(itemID);
		this.block = block;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int itemDamage) {
		return itemDamage;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIconFromDamage(int itemDamage) {
		return this.block.getIcon(2, itemDamage);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public String getUnlocalizedName(ItemStack itemStack) {
		int itemDamage = itemStack.getItemDamage();
		
		if (itemDamage >= 7) {
			return super.getUnlocalizedName() + "." + "mature";
		}
		else {
			return super.getUnlocalizedName();
		}
	}
}
