//FCMOD

package btw.block.tileentity;

import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.manager.CrucibleCraftingManager;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import net.minecraft.src.*;

public class CrucibleTileEntity extends CookingVesselTileEntity
{
    public CrucibleTileEntity()
    {
    }

    @Override
    public void readFromNBT( NBTTagCompound nbttagcompound )
    {
        super.readFromNBT(nbttagcompound);

        cookCounter = nbttagcompound.getInteger("m_iCrucibleCookCounter");
    	
        if ( nbttagcompound.hasKey( "m_iStokedCooldownCounter" ) )
        {
            stokedCooldownCounter = nbttagcompound.getInteger("m_iStokedCooldownCounter");
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

    	nbttagcompound.setInteger("m_iCrucibleCookCounter", cookCounter);
    	nbttagcompound.setInteger("m_iStokedCooldownCounter", stokedCooldownCounter);
    }

    //************* IInventory ************//
    
    @Override
    public String getInvName()
    {
        return "Crucible";
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
        containsValidIngredientsForState = false;
		
    	if (fireUnderType == 1 )
    	{
    		// regular fire

    		if (CrucibleCraftingManager.getInstance().getCraftingResult(this) != null )
    		{
                containsValidIngredientsForState = true;
    		}
    	}
    	else if (fireUnderType == 2 )
    	{
    		// stoked fire
    		
    		if ( doesContainExplosives() )
    		{
                containsValidIngredientsForState = true;
    		}
    		else if (CrucibleStokedCraftingManager.getInstance().getCraftingResult(this) != null )
			{
                containsValidIngredientsForState = true;
			}
    		else if (getFirstStackThatContainsItemsDestroyedByStokedFire() >= 0 )
    		{
                containsValidIngredientsForState = true;
    		}
    	}
    }
    
    @Override
    protected BulkCraftingManager getCraftingManager(int iFireType)
    {
    	if ( iFireType == 1 )
    	{
    		return CrucibleCraftingManager.getInstance();
    	}
    	else if ( iFireType == 2 )
    	{
    		return CrucibleStokedCraftingManager.getInstance();
    	}    	
    	
    	return null;
    }
    
    @Override
    protected boolean attemptToCookStoked()
    {
		int iBurnableSlot = getFirstStackThatContainsItemsDestroyedByStokedFire();
		
		if ( iBurnableSlot >= 0 )
		{
			decrStackSize( iBurnableSlot, 1 );
			
			return true;
		}
		
		return super.attemptToCookStoked();
    }
    
    //------------- Class Specific Methods ------------//
    
    private int getFirstStackThatContainsItemsDestroyedByStokedFire()
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            if ( getStackInSlot( i ) != null )
            {
            	if ( getStackInSlot( i ).getItem().isIncineratedInCrucible() )
            	{
            		return i;
            	}
            }
        }
        
    	return -1;
    }
}