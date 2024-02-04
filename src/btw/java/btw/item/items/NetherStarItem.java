//FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemSimpleFoiled;
import net.minecraft.src.ItemStack;

public class NetherStarItem extends ItemSimpleFoiled
{
    public NetherStarItem(int iItemID )
    {
        super( iItemID );

		setFilterableProperties(FILTERABLE_SMALL);
		
        setUnlocalizedName( "netherStar" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		if (player.timesCraftedThisTick == 0 )
		{
			player.playSound( "ambient.cave.cave4", 0.5F, player.worldObj.rand.nextFloat() * 0.05F + 0.5F );
		}
    }
}
