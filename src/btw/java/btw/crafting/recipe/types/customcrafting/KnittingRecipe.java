// FCMOD

package btw.crafting.recipe.types.customcrafting;

import btw.item.BTWItems;
import btw.item.items.KnittingItem;
import btw.item.items.WoolItem;
import net.minecraft.src.*;

public class KnittingRecipe implements IRecipe
{
	// temporary variables used while processing recipes.  
	private ItemStack stackNeedles;
	private ItemStack stackWool;
	private ItemStack stackWool2;
	
    public boolean matches(InventoryCrafting inventory, World world )
    {
    	return checkForIngredients(inventory);
    }

    public ItemStack getCraftingResult( InventoryCrafting inventory )
    {
    	if ( checkForIngredients(inventory) )
    	{    		
            ItemStack resultStack = new ItemStack( BTWItems.knitting.itemID, 1,
                                                   KnittingItem.DEFAULT_MAX_DAMAGE - 1  );
            
            // messed up mixing in order to match color of final wool knit output that uses only 16 colors
    		int iWoolColor = WoolItem.woolColors[WoolItem.getClosestColorIndex(WoolItem.averageWoolColorsInGrid(inventory))];

            KnittingItem.setColor(resultStack, iWoolColor);
    	
        	return resultStack;
        }

    	return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 3;
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
    
    private boolean checkForIngredients(InventoryCrafting inventory)
    {
        stackNeedles = null;
        stackWool = null;
        stackWool2 = null;
    	
        for ( int iTempSlot = 0; iTempSlot < inventory.getSizeInventory(); iTempSlot++ )
        {
            ItemStack tempStack = inventory.getStackInSlot( iTempSlot );

            if (tempStack != null)
            {
                if ( tempStack.itemID == BTWItems.knittingNeedles.itemID )
                {
                	if (stackNeedles == null )
                	{
                        stackNeedles = tempStack;
                	}
                	else
                	{
                		return false;
                	}
                }
                else if ( tempStack.itemID == BTWItems.wool.itemID )
                {
                    if (stackWool == null )
                    {
                        stackWool = tempStack;
                    }
                    else if (stackWool2 == null )
                    {
                        stackWool2 = tempStack;
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

        return stackNeedles != null && stackWool != null && stackWool2 != null;
    }    
}
