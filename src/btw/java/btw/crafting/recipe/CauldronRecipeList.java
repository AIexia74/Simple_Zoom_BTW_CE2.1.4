package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.AestheticVegetationBlock;
import btw.crafting.manager.CauldronCraftingManager;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.BlockCloth;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class CauldronRecipeList {
	public static void addRecipes() {
		RecipeManager.addCauldronRecipe(
				new ItemStack(Item.gunpowder, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.brimstone),
						new ItemStack(BTWItems.nitre),
						new ItemStack(BTWItems.coalDust)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.filament, 1),
				new ItemStack[] {
						new ItemStack(Item.lightStoneDust),
						new ItemStack(Item.redstone),
						new ItemStack(Item.silk)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.filament, 1),
				new ItemStack[] {
						new ItemStack(Item.lightStoneDust),
						new ItemStack(Item.redstone),
						new ItemStack(BTWItems.hempFibers)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.element, 1),
				new ItemStack[] {
						new ItemStack(Item.blazePowder),
						new ItemStack(Item.redstone),
						new ItemStack(Item.silk)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.element, 1),
				new ItemStack[] {
						new ItemStack(Item.blazePowder),
						new ItemStack(Item.redstone),
						new ItemStack(BTWItems.hempFibers)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.fuse, 2),
				new ItemStack[] {
						new ItemStack(Item.gunpowder),
						new ItemStack(Item.silk)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.fuse, 2),
				new ItemStack[] {
						new ItemStack(Item.gunpowder),
						new ItemStack(BTWItems.hempFibers)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.blastingOil, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.hellfireDust),
						new ItemStack(BTWItems.tallow)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.netherSludge, 8),
				new ItemStack[] {
						new ItemStack(BTWItems.potash, 1, InventoryUtils.IGNORE_METADATA),
						new ItemStack(BTWItems.hellfireDust, 8, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.nethercoal, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.hellfireDust, 1),
						new ItemStack(BTWItems.coalDust, 1)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.concentratedHellfire, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.hellfireDust, 8)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.tannedLeather, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.scouredLeather, 1),
						new ItemStack(BTWItems.bark, 5, 0) // oak
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.tannedLeather, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.scouredLeather, 1),
						new ItemStack(BTWItems.bark, 3, 1) // spruce
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.tannedLeather, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.scouredLeather, 1),
						new ItemStack(BTWItems.bark, 8, 2) // birch
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.tannedLeather, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.scouredLeather, 1),
						new ItemStack(BTWItems.bark, 2, 3) // jungle
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.tannedLeather, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.scouredLeather, 1),
						new ItemStack(BTWItems.bark, 8, 4) // blood wood
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cutTannedLeather, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.cutScouredLeather, 2),
						new ItemStack(BTWItems.bark, 5, 0) // oak
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cutTannedLeather, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.cutScouredLeather, 2),
						new ItemStack(BTWItems.bark, 3, 1) // spruce
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cutTannedLeather, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.cutScouredLeather, 2),
						new ItemStack(BTWItems.bark, 8, 2) // birch
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cutTannedLeather, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.cutScouredLeather, 2),
						new ItemStack(BTWItems.bark, 2, 3) // jungle
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cutTannedLeather, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.cutScouredLeather, 2),
						new ItemStack(BTWItems.bark, 8, 4) // blood wood
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.donut, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.flour, 1),
						new ItemStack(Item.sugar, 1)
				});
		
		// only up to 14 to avoid white on white conversion
		for (int i = 0; i < 15; i++) {
			RecipeManager.addCauldronRecipe(
					new ItemStack(Block.cloth, 8, BlockCloth.getBlockFromDye(i)),
					new ItemStack[] {
							new ItemStack(Item.dyePowder, 1, i),
							new ItemStack(Block.cloth, 8, 0),
					});
			
			RecipeManager.addCauldronRecipe(
					new ItemStack(BTWBlocks.woolSlab, 16, BlockCloth.getBlockFromDye(i)),
					new ItemStack[] {
							new ItemStack(Item.dyePowder, 1, i),
							new ItemStack(BTWBlocks.woolSlab, 16, 0),
					});
			
			RecipeManager.addCauldronRecipe(
					new ItemStack(BTWItems.wool, 32, i),
					new ItemStack[] {
							new ItemStack(Item.dyePowder, 1, i),
							new ItemStack(BTWItems.wool, 32, 15),
					});
		}
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(Block.cloth, 8, 12),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(Item.itemsList[Block.cloth.blockID], 8, 0)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWBlocks.woolSlab, 16, 12),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(Item.itemsList[BTWBlocks.woolSlab.blockID], 16, 0)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.wool, 32, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.dung, 1),
						new ItemStack(BTWItems.wool, 32, 15),
				});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWBlocks.aestheticVegetation, 1, AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING),
				new ItemStack[] {
						new ItemStack(BTWBlocks.oakSapling),
						new ItemStack(BTWBlocks.spruceSapling),
						new ItemStack(BTWBlocks.birchSapling),
						new ItemStack(BTWBlocks.jungleSapling),
						new ItemStack(BTWItems.soulUrn, 8),
						new ItemStack(Item.netherStalkSeeds)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack[] {
						new ItemStack(BTWBlocks.looseDirt),
						new ItemStack(BTWItems.netherGrothSpores),
				},
				new ItemStack[] {
						new ItemStack(Block.mycelium),
						new ItemStack(BTWItems.brownMushroom),
						new ItemStack(BTWItems.redMushroom),
						new ItemStack(BTWItems.soulUrn, 8),
						new ItemStack(BTWItems.dung),
						new ItemStack(Item.netherStalkSeeds)
				});
		
		// bleaching
		CauldronCraftingManager.getInstance().addRecipe(
				new ItemStack(Block.cloth, 8, 0),
				new ItemStack[] {
						new ItemStack(BTWItems.potash, 1, InventoryUtils.IGNORE_METADATA),
						new ItemStack(Block.cloth, 8, 0 ),
				}, true);
		
		CauldronCraftingManager.getInstance().addRecipe(
				new ItemStack(BTWBlocks.woolSlab, 16, 0),
				new ItemStack[] {
						new ItemStack(BTWItems.potash, 1, InventoryUtils.IGNORE_METADATA),
						new ItemStack(BTWBlocks.woolSlab, 16, 0),
				}, true);
		
		CauldronCraftingManager.getInstance().addRecipe(
				new ItemStack(BTWItems.wool, 32, 15),
				new ItemStack[] {
						new ItemStack(BTWItems.potash, 1, InventoryUtils.IGNORE_METADATA),
						new ItemStack(BTWItems.wool, 32, 15),
				}, true);
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(Item.dyePowder, 1, 2),
				new ItemStack[] {
						new ItemStack(Block.cactus)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cementBucket, 1),
				new ItemStack[] {
						new ItemStack(Block.sand),
						new ItemStack(Block.gravel),
						new ItemStack(Item.bucketEmpty),
						new ItemStack(BTWItems.soulUrn)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.hardBoiledEgg),
				new ItemStack[] {
						new ItemStack(BTWItems.rawEgg)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(Block.pistonBase, 4),
				new ItemStack[] {
						new ItemStack(BTWItems.soap),
						new ItemStack(Block.pistonStickyBase, 4)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.boiledPotato),
				new ItemStack[] {
						new ItemStack(Item.potato)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.boiledPotato),
				new ItemStack[] {
						new ItemStack(Item.bakedPotato)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.cookedCarrot),
				new ItemStack[] {
						new ItemStack(BTWItems.carrot)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWBlocks.aestheticOpaque, 4, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_CLEAN),
				new ItemStack[] {
						new ItemStack(BTWItems.soap),
						new ItemStack(BTWBlocks.aestheticOpaque, 4, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack[] {
						new ItemStack(BTWItems.chowder, 2),
						new ItemStack(Item.bucketEmpty)
				},
				
				new ItemStack[] {
						new ItemStack(Item.bucketMilk),
						new ItemStack(Item.fishCooked),
						new ItemStack(Item.bowlEmpty, 2)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(Item.beefCooked),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(Item.porkCooked),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(Item.chickenCooked),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(BTWItems.cookedMutton),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(BTWItems.cookedWolfChop),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.heartyStew, 5),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(BTWItems.flour),
						new ItemStack(BTWItems.cookedMysteryMeat),
						new ItemStack(Item.bowlEmpty, 5)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack(BTWItems.chickenSoup, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.boiledPotato),
						new ItemStack(BTWItems.cookedCarrot),
						new ItemStack(Item.chickenCooked),
						new ItemStack(Item.bowlEmpty, 3)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack[] {
						new ItemStack(Item.bowlSoup, 2),
						new ItemStack(Item.bucketEmpty)
				},
				new ItemStack[] {
						new ItemStack(BTWItems.brownMushroom, 3),
						new ItemStack(Item.bucketMilk),
						new ItemStack(Item.bowlEmpty, 2)
				});
		
		RecipeManager.addCauldronRecipe(
				new ItemStack[] {
						new ItemStack(BTWItems.chocolate, 2),
						new ItemStack(Item.bucketEmpty)
				},
				new ItemStack[] {
						new ItemStack(Item.dyePowder, 1, 3),
						new ItemStack(Item.sugar),
						new ItemStack(Item.bucketMilk)
				});
		
		//------ Stoked Cauldron Recipes ------//
		
		// glue recipes
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(Item.leather, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.tannedLeather, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.scouredLeather, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cutScouredLeather, 2)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cutLeather, 2)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cutTannedLeather, 2)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.belt, 2)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.leatherStrap, 8)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(Item.helmetLeather, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 4),
				new ItemStack[] {
						new ItemStack(Item.plateLeather, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 3),
				new ItemStack[] {
						new ItemStack(Item.legsLeather, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(Item.bootsLeather, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(Item.saddle, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.gimpHelmet, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.gimpChest, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.gimpLeggings, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.gimpBoots, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.breedingHarness, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(Item.book, 2, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(Item.writableBook, 2, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(Item.writtenBook, 2, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 1),
				new ItemStack[] {
						new ItemStack(Item.enchantedBook, 2, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.tannedLeatherHelmet, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 4),
				new ItemStack[] {
						new ItemStack(BTWItems.tannedLeatherChest, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 3),
				new ItemStack[] {
						new ItemStack(BTWItems.tannedLeatherLeggings, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.glue, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.tannedLeatherBoots, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		// tallow recipes
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(Item.porkCooked, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(Item.porkRaw, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cookedWolfChop, 8)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.rawWolfChop, 8)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(Item.beefCooked, 4)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(Item.beefRaw, 4)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cookedMutton, 4)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.rawMutton, 4)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.rawMysteryMeat, 2)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.tallow, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.cookedMysteryMeat, 2)
				});
		
		// Potash Recipes
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(Block.wood, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWBlocks.bloodWoodLog, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(Block.planks, 6, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.woodSidingStubID, 12, InventoryUtils.IGNORE_METADATA),
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.woodMouldingStubID, 24, InventoryUtils.IGNORE_METADATA),
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.woodCornerStubID, 48, InventoryUtils.IGNORE_METADATA),
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.sawDust, 16)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.bark, 64, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.soulDust, 16)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.potash, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.straw, 16)
				});
		
		// misc stoked recipes
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.soap, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.potash, 1),
						new ItemStack(BTWItems.tallow, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack[] {
						new ItemStack(Item.silk, 2),
						new ItemStack(Item.stick, 2)
				},
				new ItemStack[] {
						new ItemStack(Item.bow, 1, InventoryUtils.IGNORE_METADATA)
				}
		);
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack[] {
						new ItemStack(Item.stick, 2),
						new ItemStack(Item.silk, 1),
						new ItemStack(Item.bone, 1)
				},
				new ItemStack[] {
						new ItemStack(BTWItems.compositeBow, 1, InventoryUtils.IGNORE_METADATA)
				}
		);
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack[] {
						new ItemStack(Item.flint, 1),
						new ItemStack(Item.stick, 1),
						new ItemStack(Item.feather, 1)
				},
				new ItemStack[] {
						new ItemStack(Item.arrow, 1, InventoryUtils.IGNORE_METADATA)
				}
		);
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack[] {
						new ItemStack(Item.flint, 1)
				},
				new ItemStack[] {
						new ItemStack(BTWItems.rottenArrow, 1, InventoryUtils.IGNORE_METADATA)
				}
		);
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack(BTWItems.kibble, 2),
				new ItemStack[] {
						new ItemStack(Item.rottenFlesh, 4),
						new ItemStack(Item.dyePowder, 4, 15),
						new ItemStack(Item.sugar, 1)
				});
		
		RecipeManager.addStokedCauldronRecipe(
				new ItemStack[] {
						new ItemStack(BTWItems.brimstone, 1),
						new ItemStack(BTWItems.soulFlux, 1)
				},
				new ItemStack[] {
						new ItemStack(BTWItems.enderSlag, 1)
				}
		);
	}
}
