package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.SidingAndCornerBlock;
import btw.block.blocks.WoodMouldingAndDecorativeBlock;
import btw.block.blocks.WoodSidingAndCornerAndDecorativeBlock;
import btw.crafting.manager.*;
import btw.crafting.recipe.types.TurntableRecipe;
import btw.crafting.recipe.types.customcrafting.LogChoppingRecipe;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.ArrayList;

public class RecipeManager {
	public static void addAllModRecipes() {
		CraftingRecipeList.addRecipes();
		SoulforgeRecipeList.addRecipes();
		
		CauldronRecipeList.addRecipes();
		CrucibleRecipeList.addRecipes();
		
		HopperRecipeList.addRecipes();
		
		KilnRecipeList.addRecipes();
		
		MillstoneRecipeList.addRecipes();
		
		PistonPackingRecipeList.addRecipes();
		
		SawRecipeList.addRecipes();
		
		SmeltingRecipeList.addRecipes();
		
		TurntableRecipeList.addRecipes();
	}
	
	public static ShapedRecipes addRecipe(ItemStack itemStack, Object[] pattern) {
		return CraftingManager.getInstance().addRecipe(itemStack, pattern);
	}
	
	public static ShapelessRecipes addShapelessRecipe(ItemStack itemStack, Object[] inputs) {
		return CraftingManager.getInstance().addShapelessRecipes(itemStack, inputs);
	}
	
	public static ShapelessRecipes addShapelessRecipeWithSecondaryOutputIndicator(ItemStack itemStack, ItemStack secondaryOutput, Object[] inputs) {
		return addShapelessRecipeWithSecondaryOutputIndicator(itemStack, new ItemStack[] {secondaryOutput}, inputs);
	}
	
	public static ShapelessRecipes addShapelessRecipeWithSecondaryOutputIndicator(ItemStack itemStack, ItemStack[] secondaryOutputs, Object[] inputs) {
		ShapelessRecipes recipe = CraftingManager.getInstance().addShapelessRecipes(itemStack, inputs);
		
		if (recipe != null) {
			recipe.setSecondaryOutput(secondaryOutputs);
		}
		
		return recipe;
	}
	
	public static ShapedRecipes addShapedRecipeWithCustomClass(Class<? extends ShapedRecipes> recipeClass, ItemStack itemStack, Object[] pattern) {
		return CraftingManager.getInstance().addShapedRecipeWithCustomClass(recipeClass, itemStack, pattern);
	}
	
	public static void removeVanillaRecipe(ItemStack itemStack, Object[] pattern) {
		CraftingManager.getInstance().removeRecipe(itemStack, pattern);
	}
	
	public static void removeVanillaShapelessRecipe(ItemStack itemStack, Object[] inputs) {
		CraftingManager.getInstance().removeShapelessRecipe(itemStack, inputs);
	}
	
	public static void addSoulforgeRecipe(ItemStack itemStack, Object[] pattern) {
		SoulforgeCraftingManager.getInstance().addRecipe(itemStack, pattern);
	}
	
	public static void addShapelessSoulforgeRecipe(ItemStack itemStack, Object[] inputs) {
		SoulforgeCraftingManager.getInstance().addShapelessRecipe(itemStack, inputs);
	}
	
	public static void removeSoulforgeRecipe(ItemStack itemStack, Object[] pattern) {
		SoulforgeCraftingManager.getInstance().removeRecipe(itemStack, pattern);
	}
	
	public static void removeShapelessSoulforgeRecipe(ItemStack itemStack, Object[] inputs) {
		SoulforgeCraftingManager.getInstance().removeShapelessRecipe(itemStack, inputs);
	}
	
	public static void addCauldronRecipe(ItemStack outputStack, ItemStack[] inputStacks) {
		CauldronCraftingManager.getInstance().addRecipe(outputStack, inputStacks);
	}
	
	public static void addCauldronRecipe(ItemStack[] outputStacks, ItemStack[] inputStacks) {
		CauldronCraftingManager.getInstance().addRecipe(outputStacks, inputStacks);
	}
	
	public static void addStokedCauldronRecipe(ItemStack outputStack, ItemStack[] inputStacks) {
		CauldronStokedCraftingManager.getInstance().addRecipe(outputStack, inputStacks);
	}
	
	public static void addStokedCauldronRecipe(ItemStack[] outputStacks, ItemStack[] inputStacks) {
		CauldronStokedCraftingManager.getInstance().addRecipe(outputStacks, inputStacks);
	}
	
	public static void addCrucibleRecipe(ItemStack outputStack, ItemStack[] inputStacks) {
		CrucibleCraftingManager.getInstance().addRecipe(outputStack, inputStacks);
	}
	
	public static void addCrucibleRecipe(ItemStack[] outputStacks, ItemStack[] inputStacks) {
		CrucibleCraftingManager.getInstance().addRecipe(outputStacks, inputStacks);
	}
	
	public static void addStokedCrucibleRecipe(ItemStack outputStack, ItemStack[] inputStacks) {
		CrucibleStokedCraftingManager.getInstance().addRecipe(outputStack, inputStacks);
	}
	
	public static void addStokedCrucibleRecipe(ItemStack[] outputStacks, ItemStack[] inputStacks) {
		CrucibleStokedCraftingManager.getInstance().addRecipe(outputStacks, inputStacks);
	}
	
	public static void addMillStoneRecipe(ItemStack outputStack, ItemStack inputStack) {
		MillStoneCraftingManager.getInstance().addRecipe(outputStack, inputStack);
	}
	
	public static void addMillStoneRecipe(ItemStack[] outputStacks, ItemStack[] inputStacks) {
		MillStoneCraftingManager.getInstance().addRecipe(outputStacks, inputStacks);
	}
	
	public static void addCampfireRecipe(int iInputItemID, ItemStack outputStack) {
		CampfireCraftingManager.instance.addRecipe(iInputItemID, outputStack);
	}
	
	//Kiln recipes
	//Cook time multiplier is a byte just so that the method signatures are all different
	public static void addKilnRecipe(ItemStack outputStack, Block block) {
		addKilnRecipe(outputStack, block, InventoryUtils.IGNORE_METADATA, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack outputStack, Block block, int metadata) {
		addKilnRecipe(outputStack, block, metadata, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack outputStack, Block block, int[] metadatas) {
		addKilnRecipe(outputStack, block, metadatas, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack outputStack, Block block, byte cookTimeMultiplier) {
		addKilnRecipe(outputStack, block, InventoryUtils.IGNORE_METADATA, cookTimeMultiplier);
	}
	
	public static void addKilnRecipe(ItemStack outputStack, Block block, int metadata, byte cookTimeMultiplier) {
		addKilnRecipe(outputStack, block, new int[]{metadata}, cookTimeMultiplier);
	}
	
	public static void addKilnRecipe(ItemStack outputStack, Block block, int[] metadatas, byte cookTimeMultiplier) {
		addKilnRecipe(new ItemStack[]{outputStack}, block, metadatas, cookTimeMultiplier);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block) {
		addKilnRecipe(outputStacks, block, InventoryUtils.IGNORE_METADATA, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block, int metadata) {
		addKilnRecipe(outputStacks, block, new int[]{metadata}, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block, int[] metadatas) {
		addKilnRecipe(outputStacks, block, metadatas, (byte) 1);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block, byte cookTimeMultiplier) {
		addKilnRecipe(outputStacks, block, InventoryUtils.IGNORE_METADATA, cookTimeMultiplier);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block, int metadata, byte cookTimeMultiplier) {
		addKilnRecipe(outputStacks, block, new int[]{metadata}, cookTimeMultiplier);
	}
	
	public static void addKilnRecipe(ItemStack[] outputStacks, Block block, int[] metadatas, byte cookTimeMultiplier) {
		KilnCraftingManager.instance.addRecipe(outputStacks, block, metadatas, cookTimeMultiplier);
	}
	
	//Saw recipes
	public static void addSawRecipe(ItemStack outputStack, Block block) {
		addSawRecipe(outputStack, block, InventoryUtils.IGNORE_METADATA);
	}
	
	public static void addSawRecipe(ItemStack outputStack, Block block, int metadata) {
		addSawRecipe(new ItemStack[]{outputStack}, block, new int[]{metadata});
	}
	
	public static void addSawRecipe(ItemStack outputStack, Block block, int[] metadatas) {
		addSawRecipe(new ItemStack[]{outputStack}, block, metadatas);
	}
	
	public static void addSawRecipe(ItemStack[] outputStacks, Block block) {
		addSawRecipe(outputStacks, block, InventoryUtils.IGNORE_METADATA);
	}
	
	public static void addSawRecipe(ItemStack[] outputStacks, Block block, int metadata) {
		addSawRecipe(outputStacks, block, new int[]{metadata});
	}
	
	public static void addSawRecipe(ItemStack[] outputStacks, Block block, int[] metadata) {
		SawCraftingManager.instance.addRecipe(outputStacks, block, metadata);
	}
	
	//Piston Packing Recipes
	public static void addPistonPackingRecipe(Block output, ItemStack inputStack) {
		addPistonPackingRecipe(output, 0, inputStack);
	}
	
	public static void addPistonPackingRecipe(Block output, int outputMetadata, ItemStack inputStack) {
		addPistonPackingRecipe(output, outputMetadata, new ItemStack[]{inputStack});
	}
	
	public static void addPistonPackingRecipe(Block output, ItemStack[] inputStacks) {
		addPistonPackingRecipe(output, 0, inputStacks);
	}
	
	public static void addPistonPackingRecipe(Block output, int outputMetadata, ItemStack[] inputStacks) {
		PistonPackingCraftingManager.instance.addRecipe(output, outputMetadata, inputStacks);
	}
	
	//Hopper filtering
	
	/**
	 * Note that hopper recipe inputs are limited to stack sizes of 1 (which is enforced upon adding the recipe)
	 *
	 * @param hopperOutput The item that goes inside the hopper
	 * @param input        The item to be filtered
	 * @param filterUsed   The filter to use
	 */
	public static void addHopperFilteringRecipe(ItemStack hopperOutput, ItemStack input, ItemStack filterUsed) {
		HopperFilteringCraftingManager.instance.addRecipe(hopperOutput, null, input, filterUsed);
	}
	
	/**
	 * Note that hopper recipe inputs are limited to stack sizes of 1 (which is enforced upon adding the recipe)
	 *
	 * @param hopperOutput   The item that goes inside the hopper
	 * @param filteredOutput The item that stays outside the hopper
	 * @param input          The item to be filtered
	 * @param filterUsed     The filter to use
	 */
	public static void addHopperFilteringRecipe(ItemStack hopperOutput, ItemStack filteredOutput, ItemStack input, ItemStack filterUsed) {
		HopperFilteringCraftingManager.instance.addRecipe(hopperOutput, filteredOutput, input, filterUsed);
	}
	
	/**
	 * Note that hopper recipe inputs are limited to stack sizes of 1 (which is enforced upon adding the recipe)
	 *
	 * @param filteredOutput The item that stays outside the hopper
	 * @param input          The item to be filtered
	 */
	public static void addHopperSoulRecipe(ItemStack filteredOutput, ItemStack input) {
		HopperFilteringCraftingManager.instance.addSoulRecipe(filteredOutput, input);
	}
	
	public static void addLogChoppingRecipe(ItemStack output, ItemStack[] secondaryOutputs, ItemStack input) {
		CraftingManager.getInstance().getRecipeList().add(new LogChoppingRecipe(output, secondaryOutputs, input));
	}
	
	public static void addLogChoppingRecipe(ItemStack output, ItemStack[] secondaryOutputs, ItemStack outputLowQuality, ItemStack[] secondaryOutputsLowQuality,
											ItemStack input) {
		CraftingManager.getInstance().getRecipeList().add(new LogChoppingRecipe(output, secondaryOutputs, outputLowQuality, secondaryOutputsLowQuality, input));
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, Block block, int rotationsToCraft) {
		return addTurntableRecipe(output, 0, null, block, new int[]{InventoryUtils.IGNORE_METADATA}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, Block block, int rotationsToCraft) {
		return addTurntableRecipe(output, outputMetadata, null, block, new int[]{InventoryUtils.IGNORE_METADATA}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, Block block, int metadata, int rotationsToCraft) {
		return addTurntableRecipe(output, outputMetadata, null, block, new int[]{metadata}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, Block block, int[] metadatas, int rotationsToCraft) {
		return addTurntableRecipe(output, outputMetadata, null, block, metadatas, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, ItemStack[] itemsEjected, Block block, int rotationsToCraft) {
		return addTurntableRecipe(output, 0, itemsEjected, block, new int[]{InventoryUtils.IGNORE_METADATA}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int rotationsToCraft) {
		return addTurntableRecipe(output, outputMetadata, itemsEjected, block, new int[]{InventoryUtils.IGNORE_METADATA}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int metadata, int rotationsToCraft) {
		return addTurntableRecipe(output, outputMetadata, itemsEjected, block, new int[]{metadata}, rotationsToCraft);
	}
	
	public static TurntableRecipe addTurntableRecipe(Block output, int outputMetadata, ItemStack[] itemsEjected, Block block, int[] metadatas, int rotationsToCraft) {
		return TurntableCraftingManager.instance.addRecipe(output, outputMetadata, itemsEjected, block, metadatas, rotationsToCraft);
	}
	
	private static final int[] sidingMetas;
	private static final int[] mouldingMetas;
	private static final int[] cornerMetas;
	
	static {
		//Builds the arrays containing every meta value for each primary subblock
		ArrayList<Integer> sidingMetaList = new ArrayList<>();
		ArrayList<Integer> mouldingMetaList = new ArrayList<>();
		ArrayList<Integer> cornerMetaList = new ArrayList<>();
		
		for (int i = 0; i < 16; i++) {
			if (i != WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH && i != WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE &&
				!((SidingAndCornerBlock) BTWBlocks.oakWoodSidingAndCorner).getIsCorner(i)) {
				sidingMetaList.add(i);
			}
		}
		
		for (int i = 0; i < 16; i++) {
			if (!((WoodMouldingAndDecorativeBlock) BTWBlocks.oakWoodMouldingAndDecorative).isDecorative(i)) {
				mouldingMetaList.add(i);
			}
		}
		
		for (int i = 0; i < 16; i++) {
			if (i != WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH && i != WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE &&
				((SidingAndCornerBlock) BTWBlocks.oakWoodSidingAndCorner).getIsCorner(i)) {
				cornerMetaList.add(i);
			}
		}
		
		sidingMetas = new int[sidingMetaList.size()];
		mouldingMetas = new int[mouldingMetaList.size()];
		cornerMetas = new int[cornerMetaList.size()];
		
		for (int i = 0; i < sidingMetaList.size(); i++) {
			sidingMetas[i] = sidingMetaList.get(i);
		}
		
		for (int i = 0; i < mouldingMetaList.size(); i++) {
			mouldingMetas[i] = mouldingMetaList.get(i);
		}
		
		for (int i = 0; i < cornerMetaList.size(); i++) {
			cornerMetas[i] = cornerMetaList.get(i);
		}
	}
	
	public static void addSubBlockRecipesToSaw(Block baseBlock, int baseMetadata, Block sidingAndCorner, Block moulding) {
		//Base
		RecipeManager.addSawRecipe(new ItemStack(sidingAndCorner, 2, 0), baseBlock, baseMetadata);
		
		//Siding
		RecipeManager.addSawRecipe(new ItemStack(moulding, 2, 0), sidingAndCorner, sidingMetas);
		
		//Moulding
		RecipeManager.addSawRecipe(new ItemStack(sidingAndCorner, 2, 1), moulding, mouldingMetas);
		
		//Corners
		RecipeManager.addSawRecipe(new ItemStack(BTWItems.gear, 2, 0), sidingAndCorner, cornerMetas);
		
		//Fence
		RecipeManager.addSawRecipe(new ItemStack(sidingAndCorner, 1, 1), sidingAndCorner, WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE);
	}
	
	public static void addSubBlockRecipesToSaw(Block baseBlock, int baseMetadata, Block sidingAndCorner, Block moulding, int sidingItemID, int mouldingItemID,
											   int cornerItemID, int itemMetadata) {
		//Base
		RecipeManager.addSawRecipe(new ItemStack(sidingItemID, 2, itemMetadata), baseBlock, baseMetadata);
		
		//Siding
		RecipeManager.addSawRecipe(new ItemStack(mouldingItemID, 2, itemMetadata), sidingAndCorner, sidingMetas);
		
		//Moulding
		RecipeManager.addSawRecipe(new ItemStack(cornerItemID, 2, itemMetadata), moulding, mouldingMetas);
		
		//Corners
		RecipeManager.addSawRecipe(new ItemStack(BTWItems.gear, 2), sidingAndCorner, cornerMetas);
		
		//Fence
		RecipeManager.addSawRecipe(new ItemStack(cornerItemID, 1, itemMetadata), sidingAndCorner, WoodSidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE);
	}
}
