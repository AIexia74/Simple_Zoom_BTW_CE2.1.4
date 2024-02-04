package net.minecraft.src;

public interface IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCrafting var1, World var2);

    /**
     * Returns an Item that is the result of this recipe
     */
    ItemStack getCraftingResult(InventoryCrafting var1);

    /**
     * Returns the size of the recipe area
     */
    int getRecipeSize();

    ItemStack getRecipeOutput();
    
    // FCMOD: Added
    public abstract boolean matches( IRecipe recipe );
    
    // NOTE: That secondary output is manually flagged rather than maintaining an internal 
    // list of outputs because of SlotCrafting having no idea what recipe is being produced when 
    // the output is collected.  It would be a mess of base class edits to change this.
    
    public abstract boolean hasSecondaryOutput();
    
    public abstract ItemStack[] getSecondaryOutput(IInventory inventory);
    // END FCMOD
}
