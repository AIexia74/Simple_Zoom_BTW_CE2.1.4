package btw.crafting.manager;

import btw.crafting.recipe.types.TurntableRecipe;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class TurntableCraftingManager {
	public static TurntableCraftingManager instance = new TurntableCraftingManager();
	
	private ArrayList<TurntableRecipe> recipes = new ArrayList();
	
	private TurntableCraftingManager() {}
	
	public TurntableRecipe addRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int[] metadatas, int rotationsToCraft) {
		TurntableRecipe recipe =
				new TurntableRecipe(output, outputMetadata, itemsEjected, block, metadatas, rotationsToCraft);
		
		recipes.add(recipe);
		return recipe;
	}
	
	public boolean removeRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int[] metadatas) {
		TurntableRecipe recipeToRemove =
				new TurntableRecipe(output, outputMetadata, itemsEjected, block, metadatas, 0);
		
		for (TurntableRecipe recipe : recipes) {
			if (recipe.matchesRecipe(recipeToRemove)) {
				recipes.remove(recipe);
				return true;
			}
		}
		
		return false;
	}
	
	public TurntableRecipe getRecipe(Block block, int metadata) {
		for (TurntableRecipe recipe : recipes) {
			if (recipe.matchesInputs(block, metadata)) {
				return recipe;
			}
		}
		
		return null;
	}
	
	public ItemStack[] getRecipeResult(Block block, int metadata) {
		for (TurntableRecipe recipe : recipes) {
			if (recipe.matchesInputs(block, metadata)) {
				return recipe.getItemsEjected();
			}
		}
		
		return null;
	}
}