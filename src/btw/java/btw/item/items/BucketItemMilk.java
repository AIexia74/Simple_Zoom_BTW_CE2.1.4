// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class BucketItemMilk extends BucketItemDrinkable
{
    public BucketItemMilk(int iItemID )
    {
        super( iItemID, 6, 0.25F );        
        
        setUnlocalizedName( "milk" );
    }
    
    @Override
    public int getBlockID()
    {
        return BTWBlocks.placedMilkBucket.blockID;
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player )
    {
        if ( !world.isRemote )
        {
            player.clearActivePotions();
        }
        
        return super.onEaten( itemStack, world, player );
    }
}
