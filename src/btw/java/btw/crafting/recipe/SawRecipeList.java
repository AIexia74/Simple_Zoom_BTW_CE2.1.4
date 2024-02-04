package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class SawRecipeList {
	public static void addRecipes() {
		//Logs
		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Block.planks, 4, 0),
					new ItemStack(BTWItems.sawDust, 2),
					new ItemStack(BTWItems.bark, 1, 0)
				},
				Block.wood, new int[] {0, 4, 8});
		
		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Block.planks, 4, 1),
					new ItemStack(BTWItems.sawDust, 2),
					new ItemStack(BTWItems.bark, 1, 1)
				},
				Block.wood, new int[] {1, 5, 9});
		
		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Block.planks, 4, 2),
					new ItemStack(BTWItems.sawDust, 2),
					new ItemStack(BTWItems.bark, 1, 2)
				},
				Block.wood, new int[] {2, 6, 10});
		
		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Block.planks, 4, 3),
					new ItemStack(BTWItems.sawDust, 2),
					new ItemStack(BTWItems.bark, 1, 3)
				},
				Block.wood, new int[] {3, 7, 11});

		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Block.planks, 4, 4),
					new ItemStack(BTWItems.soulDust, 1),
					new ItemStack(BTWItems.sawDust, 1),
					new ItemStack(BTWItems.bark, 1, 4)
				},
				BTWBlocks.bloodWoodLog);
		
		//Planks
		RecipeManager.addSubBlockRecipesToSaw(Block.planks, 0,
				BTWBlocks.oakWoodSidingAndCorner,
				BTWBlocks.oakWoodMouldingAndDecorative,
				BTWItems.woodSidingStubID,
				BTWItems.woodMouldingStubID,
				BTWItems.woodCornerStubID,
				0);
		
		RecipeManager.addSubBlockRecipesToSaw(Block.planks, 1,
				BTWBlocks.spruceWoodSidingAndCorner,
				BTWBlocks.spruceWoodMouldingAndDecorative,
				BTWItems.woodSidingStubID,
				BTWItems.woodMouldingStubID,
				BTWItems.woodCornerStubID,
				1);
		
		RecipeManager.addSubBlockRecipesToSaw(Block.planks, 2,
				BTWBlocks.birchWoodSidingAndCorner,
				BTWBlocks.birchWoodMouldingAndDecorative,
				BTWItems.woodSidingStubID,
				BTWItems.woodMouldingStubID,
				BTWItems.woodCornerStubID,
				2);

		RecipeManager.addSubBlockRecipesToSaw(Block.planks, 3,
				BTWBlocks.jungleWoodSidingAndCorner,
				BTWBlocks.jungleWoodMouldingAndDecorative,
				BTWItems.woodSidingStubID,
				BTWItems.woodMouldingStubID,
				BTWItems.woodCornerStubID,
				3);
		
		RecipeManager.addSubBlockRecipesToSaw(Block.planks, 4,
				BTWBlocks.bloodWoodSidingAndCorner,
				BTWBlocks.bloodWoodMouldingAndDecorative,
				BTWItems.woodSidingStubID,
				BTWItems.woodMouldingStubID,
				BTWItems.woodCornerStubID,
				4);
		
		//Wood slabs
		RecipeManager.addSawRecipe(new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 2, 0),
				Block.woodSingleSlab, new int[] {0, 8});

		RecipeManager.addSawRecipe(new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 2, 1),
				Block.woodSingleSlab, new int[] {1, 9});
		
		RecipeManager.addSawRecipe(new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 2, 2),
				Block.woodSingleSlab, new int[] {2, 10});
		
		RecipeManager.addSawRecipe(new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 2, 3),
				Block.woodSingleSlab, new int[] {3, 11});
		
		RecipeManager.addSawRecipe(new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 2, 4),
				Block.woodSingleSlab, new int[] {4, 12});
		
		RecipeManager.addSawRecipe(new ItemStack(Block.woodSingleSlab, 2, 0),
				Block.woodDoubleSlab, 0);

		RecipeManager.addSawRecipe(new ItemStack(Block.woodSingleSlab, 2, 1),
				Block.woodDoubleSlab, 1);

		RecipeManager.addSawRecipe(new ItemStack(Block.woodSingleSlab, 2, 2),
				Block.woodDoubleSlab, 2);

		RecipeManager.addSawRecipe(new ItemStack(Block.woodSingleSlab, 2, 3),
				Block.woodDoubleSlab, 3);

		RecipeManager.addSawRecipe(new ItemStack(Block.woodSingleSlab, 2, 4),
				Block.woodDoubleSlab, 4);
		
		//Wood stairs
		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Item.itemsList[BTWItems.woodSidingStubID], 1, 0),
					new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 1, 0)
				},
				Block.stairsWoodOak);

		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Item.itemsList[BTWItems.woodSidingStubID], 1, 1),
					new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 1, 1)
				},
				Block.stairsWoodSpruce);

		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Item.itemsList[BTWItems.woodSidingStubID], 1, 2),
					new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 1, 2)
				},
				Block.stairsWoodBirch);

		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Item.itemsList[BTWItems.woodSidingStubID], 1, 3),
					new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 1, 3)
				},
				Block.stairsWoodJungle);

		RecipeManager.addSawRecipe(new ItemStack[] {
					new ItemStack(Item.itemsList[BTWItems.woodSidingStubID], 1, 4),
					new ItemStack(Item.itemsList[BTWItems.woodMouldingStubID], 1, 4)
				},
				BTWBlocks.bloodWoodStairs);
		
		//Other Slabs
		RecipeManager.addSawRecipe(new ItemStack(BTWBlocks.wickerSlab, 2),
				BTWBlocks.wickerBlock);
		RecipeManager.addSawRecipe(new ItemStack(BTWBlocks.wickerSlab, 2),
				BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_WICKER);

		RecipeManager.addSawRecipe(new ItemStack(BTWBlocks.wickerPane, 2),
				BTWBlocks.wickerSlab);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.bone, 5),
				BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_BONE);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.bone, 2),
				BTWBlocks.boneSlab);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.rottenFlesh, 5),
				BTWBlocks.rottenFleshBlock);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.rottenFlesh, 2),
				BTWBlocks.rottenFleshSlab);
		
		RecipeManager.addSawRecipe(new ItemStack(BTWItems.creeperOysters, 8),
				BTWBlocks.creeperOysterBlock);

		RecipeManager.addSawRecipe(new ItemStack(BTWItems.creeperOysters, 4),
				BTWBlocks.creeperOysterSlab);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.spiderEye, 8),
				BTWBlocks.spiderEyeBlock);
		
		RecipeManager.addSawRecipe(new ItemStack(Item.spiderEye, 4),
				BTWBlocks.spiderEyeSlab);
		
		//Other
		RecipeManager.addSawRecipe(new ItemStack(Item.melon, 5),
				Block.melon);

		RecipeManager.addSawRecipe(new ItemStack(Block.vine),
				Block.vine);
	}
}
