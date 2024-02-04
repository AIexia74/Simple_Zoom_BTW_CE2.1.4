package btw.item.items;

import net.minecraft.src.*;

public class BandageItem extends PotionItem {
	public BandageItem(int itemID) {
		super(itemID);
		this.setUnlocalizedName("fcItemBandage");
	}
    
    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            itemStack.stackSize--;
        }

        if (!world.isRemote) {
        	player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 1));
        }

        return itemStack;
    }
	
	@Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 120;
    }
}