package net.minecraft.src;

import btw.item.BTWItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessRecipes implements IRecipe
{
    /** Is the ItemStack that you get when craft the recipe. */
    private final ItemStack recipeOutput;

    private ItemStack[] recipeSecondaryOutputs;

    /** Is a List of ItemStack that composes the recipe. */
    private final List recipeItems;

    public ShapelessRecipes(ItemStack recipeOutput, List recipeItems)
    {
    	this(recipeOutput, null, recipeItems);
    }
    
    //@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public ShapelessRecipes(ItemStack recipeOutput, ItemStack[] recipeSecondaryOutputs, List recipeItems)
    {
        this.recipeOutput = recipeOutput;
		this.recipeSecondaryOutputs = recipeSecondaryOutputs;
        this.recipeItems = recipeItems;
    }

    public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        ArrayList var3 = new ArrayList(this.recipeItems);

        for (int var4 = 0; var4 < 4; ++var4)
        {
            for (int var5 = 0; var5 < 4; ++var5)
            {
                ItemStack var6 = par1InventoryCrafting.getStackInRowAndColumn(var5, var4);

                if (var6 != null)
                {
                    // FCMOD: Code added to ignore Moulds
                    if ( var6.itemID == BTWItems.mould.itemID )
                    {
                    	continue;
                    }
                    // END FCMOD
                    
                    boolean var7 = false;
                    Iterator var8 = var3.iterator();

                    while (var8.hasNext())
                    {
                        ItemStack var9 = (ItemStack)var8.next();

                        if (var6.itemID == var9.itemID && (var9.getItemDamage() == 32767 || var6.getItemDamage() == var9.getItemDamage()))
                        {
                            var7 = true;
                            var3.remove(var9);
                            break;
                        }
                    }

                    if (!var7)
                    {
                        return false;
                    }
                }
            }
        }

        return var3.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
        return this.recipeOutput.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return this.recipeItems.size();
    }
    
    // FCMOD: Added
    @Override
    public boolean matches( IRecipe recipe )
    {
    	if ( recipe instanceof ShapelessRecipes )
    	{
    		ShapelessRecipes shapelessRecipe = (ShapelessRecipes)recipe;
    		
			if ( recipeOutput.getItem().itemID == shapelessRecipe.recipeOutput.getItem().itemID &&
				recipeOutput.stackSize == shapelessRecipe.recipeOutput.stackSize && 
				recipeOutput.getItemDamage() == shapelessRecipe.recipeOutput.getItemDamage() )
			{
				if ( recipeItems.size() == shapelessRecipe.recipeItems.size() )
				{
    				for ( int iTempIndex = 0; iTempIndex < recipeItems.size(); iTempIndex++ )
    				{
    					ItemStack item1 = (ItemStack)recipeItems.get( iTempIndex );
    					ItemStack item2 = (ItemStack)shapelessRecipe.recipeItems.get( iTempIndex ); 
    						
    					if ( item1 == null || item2 == null )
    					{
    						if ( item1 != null || item2 != null )
    						{
    							return false;
    						}
    					}
    					else if ( item1.getItem().itemID != item2.getItem().itemID ||
    							item1.stackSize != item2.stackSize ||
    							item1.getItemDamage() != item2.getItemDamage() )
    					{
    						return false;
    					}
    				}
    				
    				return true;
				}
    		}
    	}
    	
    	return false;
    }
    
    @Override
    public boolean hasSecondaryOutput()
    {
    	return recipeSecondaryOutputs != null;
    }
    
    public void setSecondaryOutput(ItemStack[] secondaryOutput)
    {
    	this.recipeSecondaryOutputs = secondaryOutput;
    }
    
    public ItemStack[] getSecondaryOutput(IInventory inventory) {
    	return this.recipeSecondaryOutputs;
    }
    // END FCMOD    
}