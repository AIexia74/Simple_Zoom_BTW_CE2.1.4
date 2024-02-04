// FCMOD

package btw.item.items;

import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class FishingRodItem extends ItemFishingRod
{
	public FishingRodItem(int iItemID )
	{
		super( iItemID );

		setMaxDamage( 32 );

		setBuoyant();
		setFilterableProperties(FILTERABLE_NARROW);

		setUnlocalizedName( "fishingRod" );
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player )
	{
		boolean appliedBait = false;
		
		if (player.fishEntity == null) {
			for (int i = 0; i < 9; i++) {
				ItemStack stackInSlot = player.inventory.getStackInSlot(i);

				if (stackInSlot != null && FishingRodBaitingRecipe.isFishingBait(stackInSlot)) {
					world.playSoundAtEntity(player, "mob.slime.attack", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

					player.inventory.consumeInventoryItem(stackInSlot.itemID);
					ItemStack baitedRodStack = player.getCurrentEquippedItem().copy();
					baitedRodStack.itemID = BTWItems.baitedFishingRod.itemID;
					player.inventory.setInventorySlotContents(player.inventory.currentItem, baitedRodStack);

					appliedBait = true;
					break;
				}
			}

			if (appliedBait) {
				return stack;
			}
		}
		
		player.swingItem();
		return super.onItemRightClick(stack, world, player);
	}
	
	//----------- Client Side Functionality -----------//
    
    public Icon getCastIcon() {
        return func_94597_g();
    }
    
    @Override
    public Icon getAnimationIcon(EntityPlayer player) {
    	if (player.getHeldItem() != null && player.getHeldItem().itemID == this.itemID && player.fishEntity != null) {
    		return getCastIcon();
    	}
    	else return itemIcon;
    }
}
