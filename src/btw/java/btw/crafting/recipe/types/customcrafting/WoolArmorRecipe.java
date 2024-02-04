// FCMOD

package btw.crafting.recipe.types.customcrafting;

import btw.item.items.ArmorItemWood;
import btw.item.items.WoolItem;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;

public class WoolArmorRecipe extends ShapedRecipes
{
    public WoolArmorRecipe(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack )
    {
    	super( par1, par2, par3ArrayOfItemStack, par4ItemStack );
    }
    
    @Override
    public ItemStack getCraftingResult( InventoryCrafting inventory )
    {
    	ItemStack resultStack = super.getCraftingResult( inventory );
    	
    	if ( resultStack != null && resultStack.getItem() instanceof ArmorItemWood)
    	{
    		int iAverageColor = WoolItem.averageWoolColorsInGrid(inventory);
    		
    		ArmorItemWood woolArmorItem = (ArmorItemWood)resultStack.getItem();
    		
            woolArmorItem.func_82813_b( resultStack, iAverageColor );
    	}
    	
    	return resultStack;
    }    
}
