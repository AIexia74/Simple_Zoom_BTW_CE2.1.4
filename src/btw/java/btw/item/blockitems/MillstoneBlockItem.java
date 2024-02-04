// FCMOD

package btw.item.blockitems;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class MillstoneBlockItem extends ItemBlock
{
    public MillstoneBlockItem(int iItemID )
    {
    	super( iItemID );
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player )
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			player.playSound( "random.anvil_break", 0.5F, world.rand.nextFloat() * 0.25F + 1.25F );
		}
		
    	super.onCreated( stack, world, player );
    }    
}
