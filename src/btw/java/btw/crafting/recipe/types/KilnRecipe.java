package btw.crafting.recipe.types;

import net.minecraft.src.Block;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.ItemStack;

import java.util.Arrays;

public class KilnRecipe {
	private final ItemStack[] output;
	private final Block block;
	private final int[] metadatas;
	private final byte cookTimeMultiplier;
	
	public KilnRecipe(ItemStack[] output, Block block, int[] metadatas, byte cookTimeMultiplier) {
		this.output = output;
		this.block = block;
		this.metadatas = metadatas;
		this.cookTimeMultiplier = cookTimeMultiplier;
	}
	
	public boolean ignoreMetadata() {
		return metadatas.length == 1 && metadatas[0] == InventoryUtils.IGNORE_METADATA;
	}
	
	public boolean matchesRecipe(KilnRecipe recipe) {
		if (this.block == recipe.block && 
			(Arrays.equals(this.metadatas, recipe.metadatas) || 
				this.ignoreMetadata() && recipe.ignoreMetadata())) 
		{
			return output.equals(recipe.output);
		}
		
		return false;
	}
	
	public boolean matchesInputs(Block block, int metadata) {
		boolean containsGivenMetadata = false;
		
		for (int i : this.metadatas) {
			if (i == metadata) {
				containsGivenMetadata = true;
				break;
			}
		}
		
		return this.block.blockID == block.blockID && (containsGivenMetadata || this.ignoreMetadata());
	}
	
	public boolean matchesInputs(Block block, int[] metadatas) {
		return this.block.blockID == block.blockID &&
				(Arrays.equals(this.metadatas, metadatas) || this.ignoreMetadata());
	}

	public Block getInputblock() {
		return block;
	}

	public ItemStack[] getOutput() {
		return output;
	}

	public int[] getInputMetadata() {
		return metadatas;
	}

	public byte getCookTimeMultiplier() {
		return cookTimeMultiplier;
	}
}