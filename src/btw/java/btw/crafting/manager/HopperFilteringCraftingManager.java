package btw.crafting.manager;

import btw.crafting.recipe.types.HopperFilterRecipe;
import net.minecraft.src.Block;
import btw.AddonHandler;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class HopperFilteringCraftingManager {
	public static HopperFilteringCraftingManager instance = new HopperFilteringCraftingManager();
	
	private ArrayList<HopperFilterRecipe> recipes = new ArrayList();
	
	private HopperFilteringCraftingManager() {}
	
	public void addRecipe(ItemStack hopperOutput, ItemStack filteredOutput, ItemStack input, ItemStack filterUsed) {
		if (input.stackSize != 1) {
			AddonHandler.logWarning("Cannot add hopper filtering recipe with input stack size > 1 for input " + input.getItemName());
		}
		
		input.stackSize = 1;
		filterUsed.stackSize = 1;
		
		recipes.add(new HopperFilterRecipe(hopperOutput, filteredOutput, input, filterUsed, false));
	}
	
	public void addSoulRecipe(ItemStack filteredOutput, ItemStack input) {
		if (input.stackSize != 1) {
			AddonHandler.logWarning("Cannot add hopper soul filtering recipe with input stack size > 1 for input " + input.getItemName());
		}
		
		input.stackSize = 1;
		
		recipes.add(new HopperFilterRecipe(null, filteredOutput, input, new ItemStack(Block.slowSand), true));
	}
	
	public boolean removeRecipe(ItemStack hopperOutput, ItemStack filteredOutput, ItemStack input, ItemStack filterUsed) {
		HopperFilterRecipe recipeToRemove =
				new HopperFilterRecipe(hopperOutput, filteredOutput, input, filterUsed, false);
		
		for (HopperFilterRecipe recipe : recipes) {
			if (recipe.matchesRecipe(recipeToRemove)) {
				recipes.remove(recipe);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean removeSoulRecipe(ItemStack filteredOutput, ItemStack input) {
		HopperFilterRecipe recipeToRemove =
				new HopperFilterRecipe(null, filteredOutput, input, new ItemStack(Block.slowSand), true);
		
		for (HopperFilterRecipe recipe : recipes) {
			if (recipe.matchesRecipe(recipeToRemove)) {
				recipes.remove(recipe);
				return true;
			}
		}
		
		return false;
	}
	
	public HopperFilterRecipe getRecipe(ItemStack input, ItemStack filterUsed) {
		for (HopperFilterRecipe recipe : recipes) {
			if (recipe.matchesInputs(input, filterUsed)) {
				return recipe;
			}
		}
		
		return null;
	}
}
