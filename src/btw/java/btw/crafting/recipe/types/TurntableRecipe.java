package btw.crafting.recipe.types;

import net.minecraft.src.Block;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.Arrays;

public class TurntableRecipe {
	private final Block output;
	private final int outputMetadata;
	
	private final ItemStack[] itemsEjected;
	
	private final Block block;
	private final int[] metadatas;
	
	private final int rotationsToCraft;
	
	private TurntableEffect effect;
	private TurntableEffect completionEffect;
	
	public TurntableRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int[] metadatas, int rotationsToCraft) {
		this.output = output;
		this.outputMetadata = outputMetadata;
		
		this.itemsEjected = itemsEjected;
		
		this.block = block;
		this.metadatas = metadatas;
		
		this.rotationsToCraft = rotationsToCraft;
	}
	
	public boolean ignoreMetadata() {
		return metadatas.length == 1 && metadatas[0] == InventoryUtils.IGNORE_METADATA;
	}
	
	public boolean matchesRecipe(TurntableRecipe recipe) {
		if (this.block == recipe.block && 
			(Arrays.equals(this.metadatas, recipe.metadatas) || 
				this.ignoreMetadata() && this.ignoreMetadata())) 
		{
			return this.output.blockID == recipe.output.blockID;
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

	public int[] getInputMetadata() {
		return metadatas;
	}

	public Block getOutputBlock() {
		return output;
	}

	public int getOutputMetadata() {
		return outputMetadata;
	}

	public ItemStack[] getItemsEjected() {
		return itemsEjected;
	}
	
	public int getRotationsToCraft() {
		return this.rotationsToCraft;
	}
	
	public TurntableRecipe setEffect(TurntableEffect effect) {
		this.effect = effect;
		return this;
	}
	
	/**
	 * Sets effect on recipe completed if different than the in progress effect
	 * If a normal effect is set and completion is not set, it will play the
	 * normal effect on completion
	 * @param effect
	 * @return
	 */
	public TurntableRecipe setCompletionEffect(TurntableEffect effect) {
		this.completionEffect = effect;
		return this;
	}
	
	public void playEffect(World world, int x, int y, int z) {
		if (this.effect != null) {
			this.effect.playEffect(world, x, y, z);
		}
	}
	
	public void playCompletionEffect(World world, int x, int y, int z) {
		if (this.completionEffect != null) {
			this.completionEffect.playEffect(world, x, y, z);
		}
		else if (this.effect != null) {
			this.effect.playEffect(world, x, y, z);
		}
	}

	public static abstract interface TurntableEffect {
		public abstract void playEffect(World world, int x, int y, int z);
	}
}