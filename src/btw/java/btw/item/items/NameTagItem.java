package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class NameTagItem extends Item {
	public NameTagItem(int id) {
		super(id);
		this.setUnlocalizedName("fcItemNameTag");
		//this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean itemInteractionForEntity(ItemStack stack, EntityLiving entity) {
		String name = stack.getDisplayName();
		
		//Does not allow blank names (or unchanged from base name)
		if (name.equals("") || !stack.hasDisplayName())
			return false;
		
		//Applies the name
		entity.func_94058_c(name);
		entity.setPersistent(true);
		
		stack.stackSize--;
		
		return true;
	}
}