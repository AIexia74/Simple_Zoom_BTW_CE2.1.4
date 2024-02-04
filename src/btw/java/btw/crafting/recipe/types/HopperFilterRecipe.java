package btw.crafting.recipe.types;

import net.minecraft.src.ItemStack;

public class HopperFilterRecipe {
	private final ItemStack hopperOutput;
	private final ItemStack filteredOutput;
	private final ItemStack input;
	private final ItemStack filterUsed;
	private final boolean containsSouls;
	
	public HopperFilterRecipe(ItemStack hopperOutput, ItemStack filteredOutput, ItemStack input, ItemStack filterUsed, boolean containsSouls) {
		this.hopperOutput = hopperOutput;
		this.filteredOutput = filteredOutput;
		this.input = input;
		this.filterUsed = filterUsed;
		this.containsSouls = containsSouls;
	}
	
	public boolean matchesRecipe(HopperFilterRecipe recipe) {
		return this.hopperOutput.equals(recipe.hopperOutput) &&
				this.filteredOutput.equals(recipe.filteredOutput) &&
				this.input.isItemEqual(recipe.input) &&
				this.filterUsed.isItemEqual(recipe.filterUsed );
	}
	
	public boolean matchesInputs(ItemStack inputToCheck, ItemStack filterToCheck) {
		return this.input.isItemEqual(inputToCheck) &&
				this.filterUsed.isItemEqual(filterToCheck);
	}

	public ItemStack getHopperOutput() {
		return hopperOutput;
	}

	public ItemStack getFilteredOutput() {
		return filteredOutput;
	}

	public ItemStack getInput() {
		return input;
	}

	public ItemStack getFilterUsed() {
		return filterUsed;
	}
	
	public boolean getContainsSouls() {
		return containsSouls;
	}
}
