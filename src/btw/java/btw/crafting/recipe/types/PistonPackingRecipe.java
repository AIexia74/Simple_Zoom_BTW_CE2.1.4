package btw.crafting.recipe.types;

import net.minecraft.src.Block;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.ItemStack;

public class PistonPackingRecipe {
	private final Block output;
	private final int outputMetadata;
	private final ItemStack[] input;
	
	public PistonPackingRecipe(Block output, int outputMetadata, ItemStack[] input) {
		this.output = output;
		this.outputMetadata = outputMetadata;
		this.input = input;
	}
	
	public boolean matchesRecipe(PistonPackingRecipe recipe) {
		if (output.blockID == recipe.output.blockID &&
				outputMetadata == recipe.outputMetadata)
		{
			return input.equals(recipe.input);
		}
		
		return false;
	}
	
	public boolean matchesInputs(ItemStack[] inputToMatch) {
		if (this.input.length == inputToMatch.length) {
			for (int i = 0; i < this.input.length; i++) {
				if (this.input[i].getItem().itemID == inputToMatch[i].getItem().itemID) {
					if (this.input[i].getItemDamage() != inputToMatch[i].getItemDamage() &&
							this.input[i].getItemDamage() != InventoryUtils.IGNORE_METADATA) {
						return false;
					}
				}
			}
			
			return true;
		}
		
		return false;
	}

	public Block getOutput() {
		return output;
	}

	public int getOutputMetadata() {
		return outputMetadata;
	}

	public ItemStack[] getInput() {
		return input;
	}
}
