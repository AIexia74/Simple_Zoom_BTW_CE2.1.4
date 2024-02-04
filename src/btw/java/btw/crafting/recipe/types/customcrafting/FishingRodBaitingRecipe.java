// FCMOD

package btw.crafting.recipe.types.customcrafting;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class FishingRodBaitingRecipe implements IRecipe
{
    public boolean matches(InventoryCrafting craftingInventory, World world )
    {
        ItemStack rodStack = null;
        ItemStack baitStack = null;

        for ( int iTempSlot = 0; iTempSlot < craftingInventory.getSizeInventory(); ++iTempSlot )
        {
            ItemStack tempStack = craftingInventory.getStackInSlot( iTempSlot );

            if (tempStack != null)
            {
                if ( tempStack.itemID == Item.fishingRod.itemID )
                {
                	if ( rodStack == null )
                	{
                		rodStack = tempStack;
                	}
                	else
                	{
                		return false;
                	}
                }
                else if ( isFishingBait(tempStack) )
                {
                    if ( baitStack == null )
                    {
                        baitStack = tempStack;
                    }
                    else
                    {
                    	return false;
                    }
                }
                else
                {
                	return false;
                }
            }
        }

        return rodStack != null && baitStack != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult( InventoryCrafting craftingInventory )
    {
        ItemStack resultStack = null;
        
        ItemStack rodStack = null;
        ItemStack baitStack = null;
        
        for ( int iTempSlot = 0; iTempSlot < craftingInventory.getSizeInventory(); ++iTempSlot )
        {
            ItemStack tempStack = craftingInventory.getStackInSlot( iTempSlot );

            if ( tempStack != null )
            {
                if ( tempStack.itemID == Item.fishingRod.itemID )
                {
                	if ( rodStack == null )
                	{
                		rodStack = tempStack;
                		
                        resultStack = tempStack.copy();
                        resultStack.stackSize = 1;
                        resultStack.itemID = BTWItems.baitedFishingRod.itemID;
                	}
                	else
                	{
                		return null;
                	}
                }
                else if ( isFishingBait(tempStack) )
                {
                    if ( baitStack == null )
                    {
                        baitStack = tempStack;
                    }
                    else
                    {
                    	return null;
                    }
                }
                else
                {
                	return null;
                }
            }
        }

        if ( baitStack != null && rodStack != null )
        {
        	return resultStack;
        }
        
    	return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    @Override
    public boolean matches( IRecipe recipe )
    {
    	return false;
    }
    
    @Override
    public boolean hasSecondaryOutput()
    {
    	return false;
    }
    
    @Override
    public ItemStack[] getSecondaryOutput(IInventory inventory) {
    	return null;
    }
    
	//------------- Class Specific Methods ------------//
    
    public static boolean isFishingBait(ItemStack stack)
    {
    	int iItemID = stack.itemID;
    		
    	if ( iItemID == BTWItems.creeperOysters.itemID ||
    		iItemID == BTWItems.batWing.itemID ||
    		iItemID == BTWItems.witchWart.itemID ||
    		iItemID == Item.spiderEye.itemID || 
    		iItemID == Item.rottenFlesh.itemID )
    	{
    		return true;
    	}
    	
    	return false;
    }    
}
