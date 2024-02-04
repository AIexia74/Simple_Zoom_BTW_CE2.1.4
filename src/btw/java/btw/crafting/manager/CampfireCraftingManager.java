// FCMOD

package btw.crafting.manager;

import net.minecraft.src.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CampfireCraftingManager
{
	public static CampfireCraftingManager instance = new CampfireCraftingManager();
	
    private Map recipeMap = new HashMap();

    private CampfireCraftingManager()
    {
    }

    public ItemStack getRecipeResult(int iInputItemID)
    {
        return (ItemStack) recipeMap.get(iInputItemID);
    }

    public void addRecipe(int iInputItemID, ItemStack outputStack)
    {
    	recipeMap.put(iInputItemID, outputStack);
    }
}
