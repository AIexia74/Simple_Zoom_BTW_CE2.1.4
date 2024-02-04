package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class HopperRecipeList {
	public static void addRecipes() {
		RecipeManager.addHopperFilteringRecipe(new ItemStack(Block.sand), new ItemStack(Item.flint),
				new ItemStack(Block.gravel),
				new ItemStack(BTWBlocks.wickerPane));
		
		RecipeManager.addHopperFilteringRecipe(new ItemStack(BTWItems.wheatSeeds, 2), new ItemStack(BTWItems.straw),
				new ItemStack(BTWItems.wheat),
				new ItemStack(BTWBlocks.wickerPane));
		
		RecipeManager.addHopperFilteringRecipe(new ItemStack(Item.melonSeeds, 2), null,
				new ItemStack(BTWItems.mashedMelon),
				new ItemStack(BTWBlocks.wickerPane));
		
		//Soul filtering
		RecipeManager.addHopperSoulRecipe(new ItemStack(BTWItems.hellfireDust),
				new ItemStack(BTWItems.groundNetherrack));
		
		RecipeManager.addHopperSoulRecipe(new ItemStack(BTWItems.sawDust),
				new ItemStack(BTWItems.soulDust));
		
		RecipeManager.addHopperSoulRecipe(new ItemStack(BTWItems.brimstone),
				new ItemStack(Item.lightStoneDust));
	}
}
