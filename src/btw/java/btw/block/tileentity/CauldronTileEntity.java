// FCMOD

package btw.block.tileentity;

import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CauldronStokedCraftingManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class CauldronTileEntity extends CookingVesselTileEntity
{
    public CauldronTileEntity()
    {
    }

    @Override
    public void readFromNBT( NBTTagCompound nbttagcompound )
    {
        super.readFromNBT(nbttagcompound);
        
        if ( nbttagcompound.hasKey( "m_iCauldronCookCounter" ) )
        {
			cookCounter = nbttagcompound.getInteger("m_iCauldronCookCounter");
        }
        
        if ( nbttagcompound.hasKey( "m_iRenderCooldownCounter" ) )
        {
			stokedCooldownCounter = nbttagcompound.getInteger("m_iRenderCooldownCounter");
        }
        
        if ( nbttagcompound.hasKey( "m_bContainsValidIngrediantsForState" ) )
        {
			containsValidIngredientsForState = nbttagcompound.getBoolean("m_bContainsValidIngrediantsForState");
        }
    }

    @Override
    public void writeToNBT( NBTTagCompound nbttagcompound )
    {
        super.writeToNBT(nbttagcompound);
        
    	nbttagcompound.setInteger("m_iCauldronCookCounter", cookCounter);
    	nbttagcompound.setInteger("m_iRenderCooldownCounter", stokedCooldownCounter);
    }

    //************* IInventory ************//
    
    @Override
    public String getInvName()
    {
        return "Cauldron";
    }

    @Override
    public boolean isStackValidForSlot( int iSlot, ItemStack stack )
    {
        return true;
    }
    
    @Override
    public boolean isInvNameLocalized()
    {
    	return true;
    }
    
    //************* FCTileEntityCookingVessel ************//
    
    @Override
    public void validateContentsForState()
    {
    	// FCTODO: Move most of this into parent class

		containsValidIngredientsForState = false;
		
    	if (fireUnderType == 1 )
    	{
    		// regular fire

    		if (CauldronCraftingManager.getInstance().getCraftingResult(this) != null )
    		{
				containsValidIngredientsForState = true;
    		}
    		else if (getUncookedItemInventoryIndex() >= 0 )
    		{
				containsValidIngredientsForState = true;
    		}
    		else if (InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.dung.itemID) >= 0 )
    		{
    			if ( containsNonFoulFood() )
    			{
					containsValidIngredientsForState = true;
    			}
    		}
    	}
    	else if (fireUnderType == 2 )
    	{
    		// stoked fire
    		
    		if ( doesContainExplosives() )
    		{
				containsValidIngredientsForState = true;
    		}
			else if (CauldronStokedCraftingManager.getInstance().getCraftingResult(this) != null )
			{
				containsValidIngredientsForState = true;
			}
    	}
    }
    
    protected BulkCraftingManager getCraftingManager(int iFireType)
    {
    	if ( iFireType == 1 )
    	{
    		return CauldronCraftingManager.getInstance();
    	}
    	else if ( iFireType == 2 )
    	{
    		return CauldronStokedCraftingManager.getInstance();
    	}    	
    	
    	return null;
    }
    
    protected boolean attemptToCookNormal()
    {
		int iDungIndex = InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.dung.itemID);
    	
    	if ( iDungIndex >= 0 )
    	{
    		if ( taintAllNonFoulFoodInInventory() )
    		{
    	        return true;
    		}
    	}
    	
    	if ( super.attemptToCookNormal() )
    	{
    		return true;
    	}
    	
		return attemptToCookFood();
    }
    
    //************* Class Specific Methods ************//
    
    private boolean attemptToCookFood()
    {
    	int iUncookedFoodIndex = getUncookedItemInventoryIndex();
    	
		if ( iUncookedFoodIndex >= 0 )
		{
        	ItemStack tempStack = FurnaceRecipes.smelting().getSmeltingResult(
					contents[iUncookedFoodIndex].getItem().itemID);
        	
        	// we have to copy the furnace recipe stack so we don't end up with a pointer to the
        	// actual smelting result ItemStack in our inventory
        	ItemStack cookedStack = tempStack.copy();
	        
	        decrStackSize( iUncookedFoodIndex, 1 );
	        
	        if ( !InventoryUtils.addItemStackToInventory(this, cookedStack) )
	        {    	        	
	        	ItemUtils.ejectStackWithRandomOffset(worldObj, xCoord, yCoord + 1, zCoord, cookedStack);
	        }
	        
	        return true;
		}
		
		return false;
    }
    
    public int getUncookedItemInventoryIndex()
    {
    	for (int tempIndex = 0; tempIndex < INVENTORY_SIZE; tempIndex++ )
    	{
    		if (contents[tempIndex] != null )
    		{
	    		Item tempItem = contents[tempIndex].getItem();
	    		
	    		if ( tempItem != null )
	    		{
		    		if ( ( tempItem instanceof ItemFood ) &&
						FurnaceRecipes.smelting().getSmeltingResult( tempItem.itemID ) != null )
		    		{
		    			// this is raw food that has a cooked state
		    			
		    			return tempIndex;
		    		}
	    		}
    		}
    	}
    	
    	return -1;
    }
    
    private boolean containsNonFoulFood()
    {
    	for (int tempIndex = 0; tempIndex < INVENTORY_SIZE; tempIndex++ )
    	{
    		if (contents[tempIndex] != null )
    		{
	    		Item tempItem = contents[tempIndex].getItem();
	    		
	    		if ( tempItem != null )
	    		{
	    			int iTempItemID = tempItem.itemID;
	    			
		    		if( tempItem.itemID != BTWItems.foulFood.itemID &&
		    			iTempItemID != BTWItems.brownMushroom.itemID &&
		    			iTempItemID != BTWItems.redMushroom.itemID &&
		    			( tempItem instanceof ItemFood ) )
	    			{
		    			return true;
		    		}
	    		}
    		}
    	}
    	
    	return false;
    }
    
    private boolean taintAllNonFoulFoodInInventory()
    {
    	boolean bFoodDestroyed = false;
    	
    	for (int tempIndex = 0; tempIndex < INVENTORY_SIZE; tempIndex++ )
    	{
    		if (contents[tempIndex] != null )
    		{
	    		Item tempItem = contents[tempIndex].getItem();
	    		
	    		if ( tempItem != null )
	    		{
	    			int iTempItemID = tempItem.itemID;
	    			
		    		if ( tempItem.itemID != BTWItems.foulFood.itemID && iTempItemID != BTWItems.brownMushroom.itemID &&
		    			iTempItemID != BTWItems.redMushroom.itemID && ( tempItem instanceof ItemFood ) )
		    		{
		    			int stackSize = contents[tempIndex].stackSize;

						contents[tempIndex] = null;
		                
		                ItemStack spoiledStack = new ItemStack( BTWItems.foulFood, stackSize );
		                
		                setInventorySlotContents( tempIndex, spoiledStack );
		                
		                bFoodDestroyed = true;
		    		}
	    		}
    		}
    	}
    	
    	if ( bFoodDestroyed )
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
}