// FCMOD

package btw.item.blockitems;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class DormantSoulforgeBlockItem extends ItemBlock
{
    public DormantSoulforgeBlockItem(int iItemID )
    {
    	super( iItemID );
    	
        setUnlocalizedName( "fcBlockSoulforgeDormant" );
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		if (player.timesCraftedThisTick == 0 )
		{
			player.playSound( "random.anvil_land", 0.3F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F );
		}
    }
}
