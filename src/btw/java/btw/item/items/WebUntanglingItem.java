package btw.item.items;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class WebUntanglingItem extends ProgressiveCraftingItem {
	public WebUntanglingItem(int itemID) {
		super(itemID);
		
		setBuoyant();
		setIncineratedInCrucible();
		
		setUnlocalizedName("fcItemWebUntangling");
	}
	
	@Override
	protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player) {
		player.playSound("mob.slime.attack", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
		//player.playSound("step.grass", 0.25F + 0.25F * (float) world.rand.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.25F + 1.75F);
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		player.playSound("mob.slime.small", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
		return new ItemStack(Item.silk, 1, 0);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (player.timesCraftedThisTick == 0 && world.isRemote) {
			player.playSound("mob.slime.attack", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
		}
		
		super.onCreated(stack, world, player);
	}
}
