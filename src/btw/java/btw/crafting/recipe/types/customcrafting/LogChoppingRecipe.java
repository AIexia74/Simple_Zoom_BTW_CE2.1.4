package btw.crafting.recipe.types.customcrafting;

import btw.inventory.util.InventoryUtils;
import btw.item.items.AxeItem;
import net.minecraft.src.*;

public class LogChoppingRecipe implements IRecipe {
	private ItemStack output;
	private ItemStack[] secondaryOutputs;
	
	private ItemStack outputLowQuality;
	private ItemStack[] secondaryOutputsLowQuality;
	
	private ItemStack input;
	
	private boolean hasLowQualityOutput = false;
	
	public LogChoppingRecipe(ItemStack output, ItemStack[] secondaryOutputs, ItemStack input) {
		this.output = output;
		this.secondaryOutputs = secondaryOutputs;
		this.input = input;
	}

	/**
	 * Low quality output used for stone axes
	 */
	public LogChoppingRecipe(ItemStack output, ItemStack[] secondaryOutputs, ItemStack outputLowQuality, ItemStack[] secondaryOutputsLowQuality, ItemStack input) {
		this(output, secondaryOutputs, input);
		
		this.outputLowQuality = outputLowQuality;
		this.secondaryOutputsLowQuality = secondaryOutputsLowQuality;
		
		this.hasLowQualityOutput = true;
	}
	
	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		ItemStack axeStack = null;
		ItemStack inputStack = null;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null) {
				if (isAxe(stack)) {
					if (axeStack == null) {
						axeStack = stack;
					}
					else {
						return false;
					}
				}
				else if (stack.itemID == this.input.itemID && 
						(stack.getItemDamage() == this.input.getItemDamage() || this.input.getItemDamage() == InventoryUtils.IGNORE_METADATA))
				{
					if (inputStack == null) {
						inputStack = stack;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
		}

		return axeStack != null && inputStack != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		ItemStack axeStack = null;
		ItemStack inputStack = null;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null) {
				if (isAxe(stack)) {
					if (axeStack == null) {
						axeStack = stack;
					}
					else {
						return null;
					}
				}
				else if (stack.itemID == this.input.itemID && 
						(stack.getItemDamage() == this.input.getItemDamage() || this.input.getItemDamage() == InventoryUtils.IGNORE_METADATA))
				{
					if (inputStack == null) {
						inputStack = stack;                        
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
			}
		}

		if (inputStack != null && axeStack != null) {
			ItemStack resultStack = null;
			AxeItem axeItem = (AxeItem)axeStack.getItem();

			if (this.hasLowQualityOutput && isLowQualityAxe(axeStack)) { // wood, stone & gold
				resultStack = this.outputLowQuality.copy();
			}
			else {
				resultStack = this.output.copy();
			}

			return resultStack;
		}

		return null;
	}

	@Override
	public boolean matches(IRecipe recipe) {
		if (recipe instanceof LogChoppingRecipe) {
			LogChoppingRecipe logRecipe = (LogChoppingRecipe) recipe;
			
			if (logRecipe.input.isItemEqual(this.input)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean hasSecondaryOutput() {
		return this.secondaryOutputs != null;
	}

	@Override
	public ItemStack[] getSecondaryOutput(IInventory inventory) {
		ItemStack axeStack = null;
		ItemStack inputStack = null;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null) {
				if (isAxe(stack)) {
					if (axeStack == null) {
						axeStack = stack;
					}
					else {
						return null;
					}
				}
				else if (stack.itemID == this.input.itemID) {
					if (inputStack == null) {
						inputStack = stack;                        
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
			}
		}

		if (inputStack != null && axeStack != null) {
			ItemStack[] resultStacks = null;
			AxeItem axeItem = (AxeItem)axeStack.getItem();

			if (this.hasLowQualityOutput && isLowQualityAxe(axeStack)) { // wood, stone & gold
				resultStacks = this.secondaryOutputsLowQuality;
			}
			else {
				resultStacks = this.secondaryOutputs;
			}

			return resultStacks;
		}

		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.output;
	}
	
	public ItemStack[] getSecondaryOutput() {
		return this.secondaryOutputs;
	}
	
	public ItemStack getRecipeOutputLowQuality() {
		return this.outputLowQuality;
	}
	
	public ItemStack[] getSecondaryOutputLowQuality() {
		return this.secondaryOutputsLowQuality;
	}
	
	public boolean getHasLowQualityOutputs() {
		return this.hasLowQualityOutput;
	}
	
	public ItemStack getInput() {
		return this.input;
	}

	//------------- Class Specific Methods ------------//

	private boolean isAxe(ItemStack stack) {
		return stack.getItem() instanceof AxeItem;
	}
	
	private boolean isLowQualityAxe(ItemStack stack) {
		return isAxe(stack) && ((AxeItem) stack.getItem()).toolMaterial.getHarvestLevel() <= 1;
	}
}