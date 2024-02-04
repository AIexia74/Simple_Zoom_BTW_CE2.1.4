package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class PistonPackingRecipeList {
	public static void addRecipes() {
		//Soil
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticEarth, AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH,
				new ItemStack(BTWBlocks.looseDirt, 2));
		
		RecipeManager.addPistonPackingRecipe(Block.dirt,
				new ItemStack(BTWItems.dirtPile, 8));
		
		RecipeManager.addPistonPackingRecipe(Block.gravel,
				new ItemStack(BTWItems.gravelPile, 8));
		
		RecipeManager.addPistonPackingRecipe(Block.sand,
				new ItemStack(BTWItems.sandPile, 8));
		
		RecipeManager.addPistonPackingRecipe(Block.slowSand,
				new ItemStack(BTWItems.soulSandPile, 8));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.unfiredClay,
				new ItemStack(Item.clay, 9));
		RecipeManager.addPistonPackingRecipe(BTWBlocks.unfiredClay,
				new ItemStack(BTWItems.unfiredBrick, 9));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.solidSnow,
				new ItemStack(Item.snowball, 8));
		
		//Stone and Ore
		RecipeManager.addPistonPackingRecipe(BTWBlocks.goldOreChunkStorage,
				new ItemStack(BTWItems.goldOreChunk, 9));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.ironOreChunkStorage,
				new ItemStack(BTWItems.ironOreChunk, 9));
		
		RecipeManager.addPistonPackingRecipe(Block.sandStone,
				new ItemStack(Block.sand, 2));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_FLINT,
				new ItemStack(Item.flint, 9));
		
		for (int strata = 0; strata < 3; strata++) {
			RecipeManager.addPistonPackingRecipe(BTWBlocks.looseCobblestone, strata << 2,
					new ItemStack(BTWItems.stone, 8, strata));
		}
		
		
		//Bricks
		RecipeManager.addPistonPackingRecipe(BTWBlocks.looseBrick,
				new ItemStack(Item.brick, 8));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.looseNetherBrick,
				new ItemStack(BTWItems.netherBrick, 8));
		
		for (int strata = 0; strata < 3; strata++) {
			RecipeManager.addPistonPackingRecipe(BTWBlocks.looseStoneBrick, strata << 2,
					new ItemStack(BTWItems.stoneBrick, 4, strata));
		}
		
		//Mob Drops
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_BONE,
				new ItemStack(Item.bone, 9));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.creeperOysterBlock,
				new ItemStack(BTWItems.creeperOysters, 16));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.spiderEyeBlock,
				new ItemStack(Item.spiderEye, 16));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.rottenFleshBlock,
				new ItemStack(Item.rottenFlesh, 9));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK,
				new ItemStack(Item.enderPearl, 9));
		
		//Other
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticEarth, AestheticOpaqueEarthBlock.SUBTYPE_DUNG,
				new ItemStack(BTWItems.dung, 9));
		
		RecipeManager.addPistonPackingRecipe(BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_SOAP,
				new ItemStack(BTWItems.soap, 9));
	}
}
