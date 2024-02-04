// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.entity.InfiniteArrowEntity;
import btw.entity.RottenArrowEntity;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class BowItem extends ItemBow
{
    public BowItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
    	setfurnaceburntime(3 * FurnaceBurnTime.SHAFT.burnTime);
        setIncineratedInCrucible();

		setInfernalMaxEnchantmentCost(30);
		setInfernalMaxNumEnchants(3);
        
        setUnlocalizedName( "bow" );
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int iTicksInUseRemaining )
    {
        ItemStack arrowStack = getFirstArrowStackInHotbar(player);
        
        boolean bInfiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel( Enchantment.infinity.effectId, itemStack ) > 0;
        
        if ( arrowStack != null || bInfiniteArrows )
        {
            float fPullStrength = getCurrentPullStrength(player, itemStack, iTicksInUseRemaining);
            
            if ( fPullStrength < 0.1F )
            {
                return;
            }
            
            EntityArrow entityArrow;
            
            if ( arrowStack != null )
            {
            	entityArrow = createArrowEntityForItem(world, player, arrowStack.itemID, fPullStrength);
            	
        		player.inventory.consumeInventoryItem( arrowStack.itemID );
            }
            else
            {
            	entityArrow = (EntityArrow) EntityList.createEntityOfType(InfiniteArrowEntity.class, world, player, fPullStrength * getPullStrengthToArrowVelocityMultiplier());
            }
  
            if ( entityArrow != null )
            {
	            if ( fPullStrength == 1.0F )
	            {
	                entityArrow.setIsCritical( true );
	            }
	            
	            applyBowEnchantmentsToArrow(itemStack, entityArrow);
	            
	            if ( !world.isRemote )
	            {
	                world.spawnEntityInWorld( entityArrow );
	            }
            }
            
            itemStack.damageItem( 1, player );
            
            playerBowSound(world, player, fPullStrength);
            
            if ( itemStack.stackSize == 0 )
            {
                player.inventory.mainInventory[player.inventory.currentItem] = null;            	
            }            
        }        
    }

    @Override
    public ItemStack onItemRightClick( ItemStack stack, World world, EntityPlayer player )
    {
    	// Function overriden to not require infinity bows to have a supply of arrows
    	
        if (player.capabilities.isCreativeMode ||
            getFirstArrowStackInHotbar(player) != null ||
    		EnchantmentHelper.getEnchantmentLevel( Enchantment.infinity.effectId, stack ) > 0 )
        {
            player.setItemInUse( stack, getMaxItemUseDuration( stack ) );
        }

        return stack;
    }
    
    @Override
    public int getItemEnchantability()
    {
        return 0;
    }
    
    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment)
    {
    	if ( enchantment.type == EnumEnchantmentType.bow )
    	{
    		return true;
    	}
    	
    	return super.isEnchantmentApplicable(enchantment);
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		if ( outputStack.itemID == Item.stick.itemID )
		{
	    	if (player.timesCraftedThisTick == 0 )
			{
				player.playSound( "random.bow", 0.25F, player.worldObj.rand.nextFloat() * 0.25F + 1.5F );
			}
		}
    }
    
    //------------- Class Specific Methods ------------//

    protected float getCurrentPullStrength(EntityPlayer player, ItemStack itemStack, int iTicksInUseRemaining)
    {
        int iTicksInUse = getMaxItemUseDuration( itemStack ) - iTicksInUseRemaining;
        
        float fPullStrength = (float)iTicksInUse / 20F;
        fPullStrength = ( fPullStrength * fPullStrength + fPullStrength * 2.0F ) / 3F;
        
        if ( fPullStrength > 1.0F )
        {
            fPullStrength = 1.0F;
        }
        
        fPullStrength *= player.getBowPullStrengthModifier();
        
        return fPullStrength;
    }
    
    public ItemStack getFirstArrowStackInHotbar(EntityPlayer player)
    {
    	for ( int iTempSlot = 0; iTempSlot < 9; iTempSlot++ )
    	{
    		ItemStack tempStack = player.inventory.getStackInSlot( iTempSlot );
    		
    		if ( tempStack != null && canItemBeFiredAsArrow(tempStack.itemID) )
    		{
    			return tempStack;
    		}
    	}
    	
    	return null;
    }
    
    public boolean canItemBeFiredAsArrow(int iItemID)
    {    	
    	return iItemID == Item.arrow.itemID || iItemID == BTWItems.rottenArrow.itemID;
    }
    
    public float getPullStrengthToArrowVelocityMultiplier()
    {
    	return 2.0F;
    }
    
	protected EntityArrow createArrowEntityForItem(World world, EntityPlayer player, int iItemID, float fPullStrength)
	{
		EntityArrow entityArrow = null;
		
		if ( iItemID == BTWItems.rottenArrow.itemID )
		{
			entityArrow = (EntityArrow) EntityList.createEntityOfType(RottenArrowEntity.class, world, player, fPullStrength * 0.55F * getPullStrengthToArrowVelocityMultiplier());
		}
		else if ( iItemID == Item.arrow.itemID )
		{
			entityArrow = (EntityArrow) EntityList.createEntityOfType(EntityArrow.class, world, player, fPullStrength * getPullStrengthToArrowVelocityMultiplier());
		}
		
		return entityArrow;
	}
	
    protected void applyBowEnchantmentsToArrow(ItemStack bowStack, EntityArrow entityArrow)
    {
        int iPowerLevel = EnchantmentHelper.getEnchantmentLevel( Enchantment.power.effectId, bowStack );
    	
        if ( iPowerLevel > 0 )
        {
            entityArrow.setDamage( entityArrow.getDamage() + (double)iPowerLevel * 0.5D + 0.5D );
        }

        int iPunchLevel = EnchantmentHelper.getEnchantmentLevel( Enchantment.punch.effectId, bowStack );

        if (iPunchLevel > 0)
        {
            entityArrow.setKnockbackStrength(iPunchLevel);
        }

        if ( EnchantmentHelper.getEnchantmentLevel( Enchantment.flame.effectId, bowStack ) > 0 )
        {
            entityArrow.setFire(100);
        }
    }
    
    protected void playerBowSound(World world, EntityPlayer player, float fPullStrength)
    {
    	world.playSoundAtEntity( player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + fPullStrength * 0.5F );
    }
    
	public Icon getDrawIcon(int itemInUseDuration) {
		if (itemInUseDuration >= 18) {
			return getItemIconForUseDuration(2);
		} else if (itemInUseDuration > 12) {
			return getItemIconForUseDuration(1);
		} else if (itemInUseDuration > 0) {
			return getItemIconForUseDuration(0);
		}
		return itemIcon;
	}
    
	@Override
	public Icon getAnimationIcon(EntityPlayer player) {
		ItemStack itemInUse = player.getItemInUse();

		if (itemInUse != null && itemInUse.itemID == this.itemID) {
			int timeInUse = itemInUse.getMaxItemUseDuration() - player.getItemInUseCount();

			return getDrawIcon(timeInUse);
		}
		else return itemIcon;
	}
}