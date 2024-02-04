// FCMOD

package btw.crafting.manager;

import btw.crafting.recipe.types.BulkRecipe;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

import java.util.List;
import java.util.ArrayList;

public abstract class BulkCraftingManager
{
    private List<BulkRecipe> recipes;
    
    protected BulkCraftingManager()
    {
        recipes = new ArrayList<BulkRecipe>();
    }   

    public void addRecipe(ItemStack outputStack, ItemStack inputStacks[])
    {
    	addRecipe(outputStack, inputStacks, false);
    }
    
    public void addRecipe(ItemStack outputStack, ItemStack inputStack)
    {
    	addRecipe(outputStack, inputStack, false);
    }
    
    public void addRecipe(ItemStack outputStacks[], ItemStack inputStacks[])
    {
    	addRecipe(outputStacks, inputStacks, false);
    }
    
    public void addRecipe(ItemStack outputStack, ItemStack inputStacks[], boolean bMetadataExclusive)
    {
        ItemStack outputStacks[] = new ItemStack[1];
        
        outputStacks[0] = outputStack.copy();
        
        addRecipe(outputStacks, inputStacks, bMetadataExclusive);
    }
    
    public void addRecipe(ItemStack outputStack, ItemStack inputStack, boolean bMetadataExclusive)
    {
        ItemStack outputStacks[] = new ItemStack[1];
        
        outputStacks[0] = outputStack.copy();
        
        ItemStack inputStacks[] = new ItemStack[1];
        
        inputStacks[0] = inputStack.copy();
        
        addRecipe(outputStacks, inputStacks, bMetadataExclusive);
    }
    
    public void addRecipe(ItemStack outputStacks[], ItemStack inputStacks[], boolean bMetadataExclusive)
    {
    	BulkRecipe recipe = createRecipe(outputStacks, inputStacks, bMetadataExclusive);
    	
        recipes.add(recipe);
    }
    
    public boolean removeRecipe(ItemStack outputStack, ItemStack inputStacks[])
    {
    	return removeRecipe(outputStack, inputStacks, false);
    }
    
    public boolean removeRecipe(ItemStack outputStack, ItemStack inputStack)
    {
    	return removeRecipe(outputStack, inputStack, false);
    }
    
    public boolean removeRecipe(ItemStack outputStacks[], ItemStack inputStacks[])
    {
    	return removeRecipe(outputStacks, inputStacks, false);
    }
    
    public boolean removeRecipe(ItemStack outputStack, ItemStack inputStacks[], boolean bMetadataExclusive)
    {
        ItemStack outputStacks[] = new ItemStack[1];
        
        outputStacks[0] = outputStack.copy();
        
        return removeRecipe(outputStacks, inputStacks, bMetadataExclusive);
    }
    
    public boolean removeRecipe(ItemStack outputStack, ItemStack inputStack, boolean bMetadataExclusive)
    {
        ItemStack outputStacks[] = new ItemStack[1];
        
        outputStacks[0] = outputStack.copy();
        
        ItemStack inputStacks[] = new ItemStack[1];
        
        inputStacks[0] = inputStack.copy();
        
        return removeRecipe(outputStacks, inputStacks, bMetadataExclusive);
    }
    
    /*
     * Returns true if the recipe was successfully removed
     */
    public boolean removeRecipe(ItemStack outputStacks[], ItemStack inputStacks[], boolean bMetadataExclusive)
    {
    	BulkRecipe recipe = createRecipe(outputStacks, inputStacks, bMetadataExclusive);
    	
    	int iMatchingIndex = getMatchingRecipeIndex(recipe);
    	
    	if ( iMatchingIndex >= 0 )
    	{
    		recipes.remove(iMatchingIndex);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    public List<ItemStack> getCraftingResult(IInventory inventory)
    {
        for(int i = 0; i < recipes.size(); i++)
        {
        	BulkRecipe tempRecipe = recipes.get(i);
        	
            if( tempRecipe.doesInventoryContainIngredients(inventory) )
            {
                return tempRecipe.getCraftingOutputList();
            }
        }
        
    	return null;
    }
    
    public List<ItemStack> getCraftingResult(ItemStack inputStack)
    {
        for(int i = 0; i < recipes.size(); i++)
        {
        	BulkRecipe tempRecipe = recipes.get(i);
        	
            if ( tempRecipe.doesStackSatisfyIngredients(inputStack) )
            {
                return tempRecipe.getCraftingOutputList();
            }
        }
        
    	return null;
    }
    
    public List<ItemStack> getValidCraftingIngrediants(IInventory inventory)
    {
        for(int i = 0; i < recipes.size(); i++)
        {
        	BulkRecipe tempRecipe = recipes.get(i);
        	
            if( tempRecipe.doesInventoryContainIngredients(inventory) )
            {
                return tempRecipe.getCraftingIngrediantList();
            }
        }
        
    	return null;
    }
    
    /**
     * Checks if any recipe is satisfied by the single input stack, and returns the required
     * ingredient stack if it does (null otherwise)
     */
    public ItemStack getValidSingleIngredient(ItemStack inputStack)
    {    	
        for (int i = 0; i < recipes.size(); i++ )
        {
        	BulkRecipe tempRecipe = recipes.get(i);
        	
            if ( tempRecipe.doesStackSatisfyIngredients(inputStack) )
            {
                return tempRecipe.getFirstIngredient();
            }
        }
        
    	return null;
    }
    
    public boolean hasRecipeForSingleIngredient(ItemStack inputStack)
    {
    	return getValidSingleIngredient(inputStack) != null;
    }
    
    public List<ItemStack> consumeIngredientsAndReturnResult(IInventory inventory)
    {
        for(int i = 0; i < recipes.size(); i++)
        {
        	BulkRecipe tempRecipe = recipes.get(i);
        	
            if( tempRecipe.doesInventoryContainIngredients(inventory) )
            {
            	tempRecipe.consumeInventoryIngredients(inventory);
            	
                return tempRecipe.getCraftingOutputList();
            }
        }
        
    	return null;
    }
    
    private BulkRecipe createRecipe(ItemStack outputStacks[], ItemStack inputStacks[], boolean bMetadataExclusive)
    {
        ArrayList<ItemStack> inputArrayList = new ArrayList<ItemStack>();
        
        int iInputStacksArrayLength = inputStacks.length;
        
        for ( int iTempIndex = 0; iTempIndex < iInputStacksArrayLength; iTempIndex++ )
        {
        	inputArrayList.add( inputStacks[iTempIndex].copy() );            
        }

        ArrayList<ItemStack> outputArrayList = new ArrayList<ItemStack>();
        
        int iOutputStacksArrayLength = outputStacks.length;
        
        for ( int iTempIndex = 0; iTempIndex < iOutputStacksArrayLength; iTempIndex++ )
        {
        	outputArrayList.add( outputStacks[iTempIndex].copy() );            
        }
        
        return new BulkRecipe( outputArrayList, inputArrayList, bMetadataExclusive );
    }

    private int getMatchingRecipeIndex(BulkRecipe recipe)
    {
    	int iMatchingRecipeIndex = -1;
    	
        for (int iIndex = 0; iIndex < recipes.size(); iIndex++ )
        {
            BulkRecipe tempRecipe = recipes.get(iIndex);

            if ( tempRecipe.matches( recipe ) )
            {
                return iIndex;
            }            
        }

    	return -1;
    }
}