package btw.crafting.recipe;

import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class SmeltingRecipeList {
	public static void addRecipes() {
		// Must remove before adding replacement recipes as only one recipe can be associated with an itemID
		removeVanillaSmeltingRecipes();
		addCampfireRecipes();
		addSmeltingRecipes();
	}
	
	private static void removeVanillaSmeltingRecipes() {
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreIron.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreGold.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreDiamond.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.sand.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.cobblestone.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Item.clay.itemID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.wood.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreEmerald.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.netherrack.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreCoal.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreRedstone.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreLapis.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreNetherQuartz.blockID);
		FurnaceRecipes.smelting().getSmeltingList().remove(Block.glass.blockID);
	}
	
	private static void addSmeltingRecipes() {
		// food recipes (default cook times)
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawWolfChop.itemID, new ItemStack(BTWItems.cookedWolfChop), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.breadDough.itemID, new ItemStack(Item.bread), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawEgg.itemID, new ItemStack(BTWItems.friedEgg), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawMutton.itemID, new ItemStack(BTWItems.cookedMutton), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.carrot.itemID, new ItemStack(BTWItems.cookedCarrot), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawKebab.itemID, new ItemStack(BTWItems.cookedKebab), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawMysteryMeat.itemID, new ItemStack(BTWItems.cookedMysteryMeat), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawScrambledEggs.itemID, new ItemStack(BTWItems.cookedScrambledEggs), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawMushroomOmelet.itemID, new ItemStack(BTWItems.cookedMushroomOmelet), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.unbakedCake.itemID, new ItemStack(Item.cake), 0);
		FurnaceRecipes.smelting().addSmelting(BTWItems.unbakedCookies.itemID, new ItemStack(Item.cookie, 8), 0);
		FurnaceRecipes.smelting().addSmelting(BTWItems.unbakedPumpkinPie.itemID, new ItemStack(Item.pumpkinPie), 0);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.rawLiver.itemID, new ItemStack(BTWItems.cookedLiver), 0);
		
		// non-food (custom cook times)
		FurnaceRecipes.smelting().addSmelting(BTWItems.unfiredNetherBrick.itemID, new ItemStack(BTWItems.netherBrick), 0, 2);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.unfiredBrick.itemID, new ItemStack(Item.brick), 0, 2);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.ironOreChunk.itemID, new ItemStack(BTWItems.ironNugget), 0, 3);
		
		FurnaceRecipes.smelting().addSmelting(BTWItems.goldOreChunk.itemID, new ItemStack(Item.goldNugget), 0, 3);
	}
	
	private static void addCampfireRecipes() {
		RecipeManager.addCampfireRecipe(BTWItems.rawWolfChop.itemID, new ItemStack(BTWItems.cookedWolfChop));
		RecipeManager.addCampfireRecipe(BTWItems.rawMutton.itemID, new ItemStack(BTWItems.cookedMutton));
		RecipeManager.addCampfireRecipe(BTWItems.rawMysteryMeat.itemID, new ItemStack(BTWItems.cookedMysteryMeat));
		RecipeManager.addCampfireRecipe(BTWItems.rawLiver.itemID, new ItemStack(BTWItems.cookedLiver));
		RecipeManager.addCampfireRecipe(Item.porkRaw.itemID, new ItemStack(Item.porkCooked));
		RecipeManager.addCampfireRecipe(Item.beefRaw.itemID, new ItemStack(Item.beefCooked));
		RecipeManager.addCampfireRecipe(Item.chickenRaw.itemID, new ItemStack(Item.chickenCooked));
		RecipeManager.addCampfireRecipe(Item.fishRaw.itemID, new ItemStack(Item.fishCooked));
	}
}
