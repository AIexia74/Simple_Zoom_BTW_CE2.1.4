package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import btw.util.ColorUtils;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class MillstoneRecipeList {
	public static void addRecipes() {
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 2, ColorUtils.RED.colorID), new ItemStack(Block.plantRed));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 2, ColorUtils.YELLOW.colorID), new ItemStack(Block.plantYellow));
		
		// cocoa powder
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 1, ColorUtils.BROWN.colorID),
				new ItemStack(BTWItems.cocoaBeans));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.flour),
				new ItemStack(Item.wheat));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.flour),
				new ItemStack(BTWItems.wheat));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.sugar), new ItemStack(Item.reed));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.hempFibers, 4), new ItemStack(BTWItems.hemp));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.scouredLeather), new ItemStack(Item.leather));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.cutScouredLeather), new ItemStack(BTWItems.cutLeather));
		
		// the following output stacks are split so they eject separately from the Mill Stone
		RecipeManager.addMillStoneRecipe(
				new ItemStack[] {
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.silk),
						new ItemStack(Item.dyePowder, 1, ColorUtils.RED.colorID),	
						new ItemStack(Item.dyePowder, 1, ColorUtils.RED.colorID),
						new ItemStack(Item.dyePowder, 1, ColorUtils.RED.colorID),
						new ItemStack(BTWItems.rawWolfChop, 1, 0)
				},
				new ItemStack[] {
						new ItemStack(BTWBlocks.companionCube)
				});
		
		// companion slab
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.rawWolfChop, 1, 0), new ItemStack(BTWBlocks.companionCube, 1, 1));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.coalDust, 2), new ItemStack(Item.coal, 1, InventoryUtils.IGNORE_METADATA));
		
		// bone meal
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 3, ColorUtils.WHITE.colorID), new ItemStack(Item.bone));
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 6, ColorUtils.WHITE.colorID), new ItemStack(Item.skull.itemID, 1, 0));
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.dyePowder, 6, ColorUtils.WHITE.colorID), new ItemStack(Item.skull.itemID, 1, 1));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.rottenFlesh, 2), new ItemStack(Item.skull.itemID, 1, 2));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.nitre, 2), new ItemStack(Item.skull.itemID, 1, 4));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.groundNetherrack), new ItemStack(Block.netherrack));
		
		RecipeManager.addMillStoneRecipe(new ItemStack(Item.blazePowder, 2), new ItemStack(Item.blazeRod));
	}
}
