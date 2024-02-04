// FCMOD

package btw.item.items;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Potion;

public class CreeperOysterItem extends FoodItem
{
    public CreeperOysterItem(int iItemID )
    {
    	super(iItemID, FoodItem.CREEPER_OYSTERS_HUNGER_HEALED,
			  FoodItem.CREEPER_OYSTERS_SATURATION_MODIFIER, false,
			  FoodItem.CREEPER_OYSTERS_ITEM_NAME);
    	
    	setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_SMALL);
    	
    	setPotionEffect( Potion.poison.id, 5, 0, 1F ) ;
	}
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		if (player.timesCraftedThisTick == 0 )
		{
			player.playSound( "mob.slime.attack", 0.5F, ( player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.1F + 0.7F );
		}
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
