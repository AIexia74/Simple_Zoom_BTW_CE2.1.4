package btw.crafting.manager;

import btw.crafting.recipe.types.PistonPackingRecipe;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PistonPackingCraftingManager {
	public static PistonPackingCraftingManager instance = new PistonPackingCraftingManager();

	private ArrayList<PistonPackingRecipe> recipes = new ArrayList();

	private PistonPackingCraftingManager() {}

	public void addRecipe(Block output, int outputMetadata, ItemStack[] input) {
		recipes.add(new PistonPackingRecipe(output, outputMetadata, input));
	}

	public boolean removeRecipe(Block output, int outputMetadata, ItemStack[] input) {
		PistonPackingRecipe recipeToRemove = new PistonPackingRecipe(output, outputMetadata, input);

		for (PistonPackingRecipe recipe : recipes) {
			if (recipe.matchesRecipe(recipeToRemove)) {
				return true;
			}
		}

		return false;
	}

	public PistonPackingRecipe getRecipe(ItemStack[] input) {
		for (PistonPackingRecipe recipe : recipes) {
			if (recipe.matchesInputs(input)) {
				return recipe;
			}
		}

		return null;
	}

	public PistonPackingRecipe getValidRecipeFromItemList(List<EntityItem> itemList) {
		if (itemList == null || itemList.isEmpty()) {
			return null;
		}

		// First key is the item id
		// Second map contains metadata as key and count as value
		Map<Integer, Map<Integer, Integer>> itemCountMap = new HashMap();

		//Collects all item entities by type into a single count
		//Makes multiple stacks of the same item count together
		for (EntityItem entity : itemList) {
			if (!entity.isDead) {
				int itemID = entity.getEntityItem().itemID;
				int metadata = entity.getEntityItem().getItemDamage();
				int stackSize = entity.getEntityItem().stackSize;

				if (itemCountMap.containsKey(itemID)) {
					Map<Integer, Integer> metadataMap = itemCountMap.get(itemID);
					
					int currentSize = 0;
					
					if (metadataMap.containsKey(metadata)) {
						currentSize = metadataMap.get(metadata); 
					}
					
					metadataMap.put(metadata, currentSize + stackSize);
				}
				else {
					itemCountMap.put(itemID, new HashMap());
					Map<Integer, Integer> metadataMap = itemCountMap.get(itemID);
					metadataMap.put(metadata, stackSize);
				}
			}
		}

		for (PistonPackingRecipe recipe : recipes) {
			boolean recipeMatch = true;

			for (ItemStack recipeStack : recipe.getInput()) {
				//If any item in the recipe is missing, or any item doesn't have enough ingredients, immediately move on to the next recipe
				if (!itemCountMap.containsKey(recipeStack.itemID)) {
					recipeMatch = false;
					break;
				}
				else {
					if (recipeStack.getItemDamage() == InventoryUtils.IGNORE_METADATA) {
						int totalCount = 0;
						
						for (Integer i : itemCountMap.get(recipeStack.itemID).values()) {
							totalCount += i;
						}
						
						if (totalCount < recipeStack.stackSize) {
							recipeMatch = false;
							break;
						}
					}
					else if (itemCountMap.get(recipeStack.itemID).containsKey(recipeStack.getItemDamage())){
						int count = itemCountMap.get(recipeStack.itemID).get(recipeStack.getItemDamage());
						
						if (count < recipeStack.stackSize) {
							recipeMatch = false;
							break;
						}
					}
					else {
						recipeMatch = false;
						break;
					}
				}
			}

			if (recipeMatch) {
				return recipe;
			}
		}

		return null;
	}
}