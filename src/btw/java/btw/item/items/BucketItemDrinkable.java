// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public abstract class BucketItemDrinkable extends BucketItem
{
	private int hungerHealed;
	private float saturationModifier;
	
    public BucketItemDrinkable(int iItemID, int iHungerHealed, float fSaturationModifier )
    {
        super( iItemID );

        hungerHealed = iHungerHealed;
        saturationModifier = fSaturationModifier;
    }
    
    @Override
    public int getMaxItemUseDuration( ItemStack stack )
    {
        return 32;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack )
    {
        return EnumAction.drink;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player )
    {
    	if ( player.canDrink() )
    	{
    		player.setItemInUse( stack, getMaxItemUseDuration( stack ) );
    	}
    	else
    	{
    		player.onCantConsume();
    	}
        
        return stack;
    }
    
    @Override
    public ItemStack onEaten( ItemStack itemStack, World world, EntityPlayer player )
    {
    	// override to add nutritional value to drinking milk buckets
    	
        if ( !player.capabilities.isCreativeMode )
        {
            --itemStack.stackSize;
        }

        if ( !world.isRemote )
        {
            player.getFoodStats().addStats(hungerHealed, saturationModifier); // food value adjusted for increased hunger meter resolution
        }

        return itemStack.stackSize <= 0 ? new ItemStack( Item.bucketEmpty ) : itemStack;
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
