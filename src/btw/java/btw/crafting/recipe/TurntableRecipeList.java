package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.UnfiredPotteryBlock;
import btw.client.fx.BTWEffectManager;
import btw.crafting.recipe.types.TurntableRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class TurntableRecipeList {
	private static final int NUM_ROTATIONS_POTTERY = 8;
	
	public static TurntableRecipe.TurntableEffect
			potteryEffect = (world, x, y, z) -> world.playAuxSFX(BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID, x, y, z, BTWBlocks.unfiredClay.blockID);
	
	public static void addRecipes() {
		RecipeManager.addTurntableRecipe(BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_CRUCIBLE,
				new ItemStack[] {
						new ItemStack(Item.clay, 1)
				},
				BTWBlocks.unfiredClay, NUM_ROTATIONS_POTTERY)
				.setEffect(potteryEffect);
		
		RecipeManager.addTurntableRecipe(BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_PLANTER,
				new ItemStack[] {
						new ItemStack(Item.clay, 2)
				},
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_CRUCIBLE, NUM_ROTATIONS_POTTERY)
				.setEffect(potteryEffect);
		
		RecipeManager.addTurntableRecipe(BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_VASE,
				new ItemStack[] {
						new ItemStack(Item.clay, 2)
				},
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_PLANTER, NUM_ROTATIONS_POTTERY)
				.setEffect(potteryEffect);
		
		RecipeManager.addTurntableRecipe(BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_URN,
				new ItemStack[] {
						new ItemStack(Item.clay, 2)
				},
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_VASE, NUM_ROTATIONS_POTTERY)
				.setEffect(potteryEffect);
		
		RecipeManager.addTurntableRecipe(null, 0,
				new ItemStack[] {
						new ItemStack(Item.clay, 2)
				},
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_URN, NUM_ROTATIONS_POTTERY)
				.setEffect(potteryEffect);
	}
}
