// FCMOD

package btw.crafting.recipe.types.customcrafting;

import btw.item.items.WoolItem;
import net.minecraft.src.BlockCloth;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;

public class WoolBlockRecipe extends ShapedRecipes
{
    public WoolBlockRecipe(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack )
    {
    	super( par1, par2, par3ArrayOfItemStack, par4ItemStack );
    }
    
    @Override
    public ItemStack getCraftingResult( InventoryCrafting inventory )
    {
    	ItemStack resultStack = super.getCraftingResult( inventory );
    	
    	if ( resultStack != null  )
    	{
    		int iAverageColor = WoolItem.averageWoolColorsInGrid(inventory);
    		
    		int iClosestColorIndex = WoolItem.getClosestColorIndex(iAverageColor);
    		
    		resultStack.setItemDamage( BlockCloth.getBlockFromDye( iClosestColorIndex ) );
    	}
    	
    	return resultStack;
    }
    
}
