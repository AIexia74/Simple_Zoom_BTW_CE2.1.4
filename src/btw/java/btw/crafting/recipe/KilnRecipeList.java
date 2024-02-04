package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.UnfiredPotteryBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class KilnRecipeList {
	public static final byte COOK_TIME_MULTIPLIER_CLAY = 4;
	public static final byte COOK_TIME_MULTIPLIER_ORE = 8;
	
	public static void addRecipes() {
		//Ore
		RecipeManager.addKilnRecipe(new ItemStack(Item.goldNugget),
				BTWBlocks.goldOreChunk, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(Item.ingotGold),
				BTWBlocks.goldOreChunkStorage, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(Item.goldNugget),
				Block.oreGold, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWItems.ironNugget),
				BTWBlocks.ironOreChunk, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(Item.ingotIron),
				BTWBlocks.ironOreChunkStorage, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWItems.ironNugget),
				Block.oreIron, COOK_TIME_MULTIPLIER_ORE);

		//Charcoal
		RecipeManager.addKilnRecipe(new ItemStack(Item.coal, 1, 1),
				BTWBlocks.bloodWoodLog, COOK_TIME_MULTIPLIER_ORE);
		
		RecipeManager.addKilnRecipe(new ItemStack(Item.coal, 1, 1),
				Block.wood, COOK_TIME_MULTIPLIER_ORE);
		
		//Pottery
		RecipeManager.addKilnRecipe(new ItemStack(Item.brick),
				BTWBlocks.placedUnfiredBrick, COOK_TIME_MULTIPLIER_CLAY);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWBlocks.crucible),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_CRUCIBLE, COOK_TIME_MULTIPLIER_CLAY);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWBlocks.planter),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_PLANTER, COOK_TIME_MULTIPLIER_CLAY);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWBlocks.vase),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_VASE, COOK_TIME_MULTIPLIER_CLAY);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWItems.urn),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_URN, COOK_TIME_MULTIPLIER_CLAY);
		
		//Disabled until moulds are re-implemented
		//RecipeManager.addKilnRecipe(new ItemStack(FCBetterThanWolves.fcItemMould),
		//		FCBetterThanWolves.fcUnfiredPottery, FCBlockUnfiredPottery.m_iSubtypeMould,
		//		cookTimeMultiplierClay);
		
		RecipeManager.addKilnRecipe(new ItemStack(BTWItems.netherBrick),
				BTWBlocks.unfiredPottery,
				new int[] {UnfiredPotteryBlock.SUBTYPE_NETHER_BRICK, UnfiredPotteryBlock.SUBTYPE_NETHER_BRICK_X_ALIGNED}, COOK_TIME_MULTIPLIER_CLAY);
		
		//Baking
		RecipeManager.addKilnRecipe(new ItemStack(Item.cake),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_UNCOOKED_CAKE);

		RecipeManager.addKilnRecipe(new ItemStack(Item.pumpkinPie),
				BTWBlocks.unfiredPottery, UnfiredPotteryBlock.SUBTYPE_UNCOOKED_PUMPKIN_PIE);

		RecipeManager.addKilnRecipe(new ItemStack(Item.bread),
				BTWBlocks.unfiredPottery, new int[] {UnfiredPotteryBlock.SUBTYPE_UNCOOKED_BREAD, UnfiredPotteryBlock.SUBTYPE_UNCOOKED_BREAD_I_ALIGNED});

		RecipeManager.addKilnRecipe(new ItemStack(Item.cookie, 8),
				BTWBlocks.unfiredPottery, new int[] {UnfiredPotteryBlock.SUBTYPE_UNCOOKED_COOKIES, UnfiredPotteryBlock.SUBTYPE_UNCOOKED_COOKIES_I_ALIGNED});
		
		//Other
		RecipeManager.addKilnRecipe(new ItemStack[] {
					new ItemStack(BTWItems.enderSlag),
					new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE)
				},
				Block.whiteStone, COOK_TIME_MULTIPLIER_ORE);
	}
}
