package btw.item.items;

import btw.entity.CorpseEyeEntity;
import net.minecraft.src.*;

public class CorpseEyeItem extends Item {
	public CorpseEyeItem(int par1) {
		super(par1);
		
        setMaxDamage(9);
		
        this.setUnlocalizedName("fcItemCorpseEye");
		
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {

		if (!world.isRemote) {
			int targetX = MathHelper.floor_double(player.posX);
			int targetY = MathHelper.floor_double(player.boundingBox.minY + 1.62D);
			int targetZ = MathHelper.floor_double(player.posZ);
			
			//only search old body location if actually died before, and in same dimension
			if (player.deathCount > 0 && player.lastDeathDimension == player.dimension) {
				targetX = player.lastDeathLocationX;
				targetY = player.lastDeathLocationY;
				targetZ = player.lastDeathLocationZ;
				}

			CorpseEyeEntity var6 = (CorpseEyeEntity) EntityList.createEntityOfType(CorpseEyeEntity.class, world, player.posX,
					player.posY + 1.62D - (double) player.yOffset, player.posZ);

            var6.setItemDamage(itemStack.getItemDamage());
			
			var6.moveTowards((double) targetX, targetY, (double) targetZ);
			world.spawnEntityInWorld(var6);
			
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.playAuxSFXAtEntity((EntityPlayer) null, 1002, (int) player.posX, (int) player.posY, (int) player.posZ, 0);

			if (!player.capabilities.isCreativeMode) {
				--itemStack.stackSize;
			}
		}

		return itemStack;
	}
}
