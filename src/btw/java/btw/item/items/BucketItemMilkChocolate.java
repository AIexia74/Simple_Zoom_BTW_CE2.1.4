// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class BucketItemMilkChocolate extends BucketItemDrinkable
{
    public BucketItemMilkChocolate(int iItemID )
    {
    	super( iItemID, 9, 0.25F );
    	
        setUnlocalizedName( "fcItemBucketChocolateMilk" );
    }
    
    @Override
    public int getBlockID()
    {
        return BTWBlocks.placedMilkChocolateBucket.blockID;
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
    
    @Override
    public boolean doesConsumeContainerItemWhenCrafted(Item containerItem)
    {
    	if ( containerItem.itemID == Item.bucketEmpty.itemID )
    	{
    		return true;
    	}
    	
    	return false;
    }
}
