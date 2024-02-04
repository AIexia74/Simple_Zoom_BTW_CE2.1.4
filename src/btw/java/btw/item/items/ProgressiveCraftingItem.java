// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class ProgressiveCraftingItem extends Item
{
	static public final int PROGRESS_TIME_INTERVAL = 4;
	static public final int DEFAULT_MAX_DAMAGE = (120 * 20 / PROGRESS_TIME_INTERVAL);
	
    public ProgressiveCraftingItem(int iItemID )
    {
    	super( iItemID );
    	
        maxStackSize = 1;        
        
        setMaxDamage(getProgressiveCraftingMaxDamage());
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player )
    {
    	player.setItemInUse( stack, getMaxItemUseDuration( stack ) );

        return stack;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.miscUse;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
    	// stupid large so it's never actually hit in practice
    	
        return 72000;
    }    
    
    @Override
    public void updateUsingItem(ItemStack stack, World world, EntityPlayer player)
    {
    	int iUseCount = player.getItemInUseCount();
    	
        if ( getMaxItemUseDuration( stack ) - iUseCount > getItemUseWarmupDuration() )
        {
        	if ( iUseCount % 4 == 0 )
        	{
        		playCraftingFX(stack, world, player);
        	}
            
            if ( !world.isRemote && iUseCount % PROGRESS_TIME_INTERVAL == 0 )
            {
            	int iDamage = stack.getItemDamage();
            	
            	iDamage -= 1;
            	
            	if ( iDamage > 0 )
            	{            	
            		stack.setItemDamage( iDamage );
            	}
            	else
            	{
            		// set item usage to immediately complete
            		
            		player.setItemInUseCount(1);
            	}
            }
        }
    }
    
    @Override
    public boolean ignoreDamageWhenComparingDuringUse()
    {
    	return true;
    }

    //------------- Class Specific Methods ------------//

    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player)
    {
    }
    
    protected int getProgressiveCraftingMaxDamage()
    {
    	return DEFAULT_MAX_DAMAGE;
    }
    
	//------------ Client Side Functionality ----------//
}
