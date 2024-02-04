// FCMOD

package btw.crafting.recipe.types;

import btw.inventory.util.InventoryUtils;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

import java.util.List;
import java.util.ArrayList;

public class BulkRecipe
{
    private final List<ItemStack> recipeOutputStacks;
    private final List<ItemStack> recipeInputStacks;
    
    private final boolean metadataExclusive;
    
    public BulkRecipe(ItemStack recipeOutputStack, List<ItemStack> recipeInputStacks )
    {
    	this( recipeOutputStack, recipeInputStacks, false );
    }

    public BulkRecipe(ItemStack recipeOutputStack, List<ItemStack> recipeInputStacks, boolean bMetaDataExclusive )
    {
        ArrayList<ItemStack> outputArrayList = new ArrayList<ItemStack>();
        
        outputArrayList.add( recipeOutputStack.copy() );

		recipeOutputStacks = outputArrayList;
		this.recipeInputStacks = recipeInputStacks;
		metadataExclusive = bMetaDataExclusive;
    }

    public BulkRecipe(List<ItemStack> recipeOutputStacks, List<ItemStack> recipeInputStacks, boolean bMetaDataExclusive )
    {
		this.recipeOutputStacks = recipeOutputStacks;
		this.recipeInputStacks = recipeInputStacks;
		metadataExclusive = bMetaDataExclusive;
    }
 
    public List<ItemStack> getCraftingOutputList()
    {
    	return recipeOutputStacks;
    }
    
    public List<ItemStack> getCraftingIngrediantList()
    {
    	return recipeInputStacks;
    }
    
    public ItemStack getFirstIngredient()
    {
    	if (recipeInputStacks != null && recipeInputStacks.size() > 0 )
    	{
    		return recipeInputStacks.get(0);
    	}
    	
    	return null;
    }
    
    public boolean doesInventoryContainIngredients(IInventory inventory)
    {    	
    	if (recipeInputStacks != null && recipeInputStacks.size() > 0 )
    	{
            for (int listIndex = 0; listIndex < recipeInputStacks.size(); listIndex++ )
            {
	    		ItemStack tempStack = (ItemStack) recipeInputStacks.get(listIndex);
	    		
	    		if ( tempStack != null )
	    		{
	    			if ( InventoryUtils.countItemsInInventory(inventory,
															  tempStack.getItem().itemID, tempStack.getItemDamage(), metadataExclusive)
    					< tempStack.stackSize )
	    			{
	    				return false;
	    			}
	    		}
            }
            
            return true;
    	}
    	
    	return false;
    }
    
    public boolean doesStackSatisfyIngredients(ItemStack stack)
    {
    	if (recipeInputStacks != null && recipeInputStacks.size() == 1 )
    	{
    		ItemStack recipeStack = (ItemStack) recipeInputStacks.get(0);
    		int recipeItemDamage = recipeStack.getItemDamage();
    		
    		if ( stack.itemID == recipeStack.itemID && stack.stackSize >= recipeStack.stackSize &&
        		( recipeItemDamage == InventoryUtils.IGNORE_METADATA ||
				  (!metadataExclusive && stack.getItemDamage() == recipeItemDamage ) ||
				  (metadataExclusive && stack.getItemDamage() != recipeItemDamage ) ) )
			{
    			return true;
			}
    	}
    	
    	return false;
    }
    
    public boolean consumeInventoryIngredients(IInventory inventory)
    {
    	boolean bSuccessful = true;

    	if (recipeInputStacks != null && recipeInputStacks.size() > 0 )
    	{
            for (int listIndex = 0; listIndex < recipeInputStacks.size(); listIndex++ )
            {
	    		ItemStack tempStack = (ItemStack) recipeInputStacks.get(listIndex);
	    		
	    		if ( tempStack != null )
	    		{
	    			if ( !InventoryUtils.consumeItemsInInventory(inventory,
																 tempStack.getItem().itemID, tempStack.getItemDamage(),
																 tempStack.stackSize, metadataExclusive) )
					{
	    				bSuccessful = false;
					}
	    		}
            }
    	}

    	return bSuccessful;
    }
    
    public boolean matches( BulkRecipe recipe )
    {
    	if (metadataExclusive == recipe.metadataExclusive &&
			recipeInputStacks.size() == recipe.recipeInputStacks.size() &&
			recipeOutputStacks.size() == recipe.recipeOutputStacks.size() )
    	{
    		for (int iListIndex = 0; iListIndex < recipeInputStacks.size(); iListIndex++ )
    		{
    			if ( !doStacksMatch(recipeInputStacks.get(iListIndex), recipe.recipeInputStacks.get(iListIndex)) )
    			{
    				return false;
    			}
    		}
    		
    		for (int iListIndex = 0; iListIndex < recipeOutputStacks.size(); iListIndex++ )
    		{
    			if ( !doStacksMatch(recipeOutputStacks.get(iListIndex), recipe.recipeOutputStacks.get(iListIndex)) )
    			{
    				return false;
    			}
    		}
    		
    		return true;    		
    	}
    	
    	return false;
    }
    
    private boolean doStacksMatch(ItemStack stack1, ItemStack stack2)
    {
    	return ( stack1.getItem().itemID == stack2.getItem().itemID &&
    		stack1.stackSize == stack2.stackSize &&
    		stack1.getItemDamage() == stack2.getItemDamage() );
    }    
}
