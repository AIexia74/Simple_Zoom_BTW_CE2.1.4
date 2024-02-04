package btw.crafting.manager;

import btw.crafting.recipe.types.SawRecipe;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class SawCraftingManager {
	public static SawCraftingManager instance = new SawCraftingManager();
	
	private ArrayList<SawRecipe> recipes = new ArrayList();
	
	private SawCraftingManager() {}
	
	public void addRecipe(ItemStack[] output, Block block, int[] metadatas) {
		SawRecipe recipe = new SawRecipe(output, block, metadatas);
		
		recipes.add(recipe);
	}
	
	public boolean removeRecipe(ItemStack[] output, Block block, int[] metadatas) {
		SawRecipe recipeToRemove = new SawRecipe(output, block, metadatas);
		
		for (SawRecipe recipe : recipes) {
			if (recipe.matchesRecipe(recipeToRemove)) {
				recipes.remove(recipe);
				return true;
			}
		}
		
		return false;
	}
	
	public SawRecipe getRecipe(Block block, int metadata) {
		for (SawRecipe recipe : recipes) {
			if (recipe.matchesInputs(block, metadata)) {
				return recipe;
			}
		}
		
		return null;
	}
	
	public ItemStack[] getRecipeResult(Block block, int metadata) {
		for (SawRecipe recipe : recipes) {
			if (recipe.matchesInputs(block, metadata)) {
				return recipe.getOutput();
			}
		}
		
		return null;
	}
}