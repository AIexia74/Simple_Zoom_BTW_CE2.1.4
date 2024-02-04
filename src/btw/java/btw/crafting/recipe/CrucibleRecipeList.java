package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticNonOpaqueBlock;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class CrucibleRecipeList {
	public static void addRecipes() {
		// stoked recipes
		
		// gold melting
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 3 ),
				new ItemStack( Item.ingotGold, 2 ),
				new ItemStack[] {
						new ItemStack(Item.pickaxeGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 2 ),
				new ItemStack( Item.goldNugget, 12 ),
				new ItemStack[] {
						new ItemStack(Item.axeGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 2 ),
				new ItemStack( Item.goldNugget, 12 ),
				new ItemStack[] {
						new ItemStack(Item.swordGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 2 ),
				new ItemStack( Item.goldNugget, 6 ),
				new ItemStack[] {
						new ItemStack(Item.hoeGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 1 ),
				new ItemStack( Item.goldNugget, 6 ),
				new ItemStack[] {
						new ItemStack(Item.shovelGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 5 ),
				new ItemStack( Item.goldNugget, 30 ),
				new ItemStack[] {
						new ItemStack(Item.helmetGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 8 ),
				new ItemStack( Item.goldNugget, 48 ),
				new ItemStack[] {
						new ItemStack(Item.plateGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 7 ),
				new ItemStack( Item.goldNugget, 42 ),
				new ItemStack[] {
						new ItemStack(Item.legsGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 4 ),
				new ItemStack( Item.goldNugget, 24 ),
				new ItemStack[] {
						new ItemStack(Item.bootsGold, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 4 ),
				new ItemStack( Item.goldNugget, 3 ),
				new ItemStack[] {
						new ItemStack( Item.pocketSundial )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotGold, 1 ),
				new ItemStack[] {
						new ItemStack( Item.goldNugget, 1 ),
						new ItemStack( BTWItems.ironNugget, 2 ),
				},
				new ItemStack[] {
						new ItemStack( Block.railPowered, 2 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Item.ingotGold, 9 ),
				new ItemStack[] {
						new ItemStack( Block.blockGold )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 8 ),
				new ItemStack( Item.goldNugget, 5 ),
				new ItemStack[] {
						new ItemStack( BTWItems.ocularOfEnder)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 16 ),
				new ItemStack( Item.goldNugget, 11 ),
				new ItemStack[] {
						new ItemStack(BTWItems.enderSpectacles, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 8 ),
				new ItemStack( Item.goldNugget, 6 ),
				new ItemStack[] {
						new ItemStack( BTWItems.goldenDung)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 3 ),
				new ItemStack( Item.goldNugget, 2 ),
				new ItemStack[] {
						new ItemStack( BTWItems.redstoneLatch)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 3 ),
				new ItemStack( Item.goldNugget, 2 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.redstoneClutch)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 3 ),
				new ItemStack( Item.goldNugget, 2 ),
				new ItemStack[] {
						new ItemStack( Block.music )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 90 ),
				new ItemStack( Item.goldNugget, 60 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.dormandSoulforge)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.goldNugget, 11 ),
				new ItemStack( Item.goldNugget, 8 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.lightningRod)
				});
		
		// iron melting recipes
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 4 ),
				new ItemStack( BTWItems.ironNugget, 2 ),
				new ItemStack[] {
						new ItemStack( BTWItems.mail)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 5 ),
				new ItemStack[] {
						new ItemStack( Item.bucketEmpty )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 5 ),
				new ItemStack[] {
						new ItemStack( Item.bucketLava )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 5 ),
				new ItemStack[] {
						new ItemStack( Item.bucketWater )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 5 ),
				new ItemStack[] {
						new ItemStack( Item.bucketMilk )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 5 ),
				new ItemStack[] {
						new ItemStack( BTWItems.cementBucket)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 3 ),
				new ItemStack( Item.ingotIron, 2 ),
				new ItemStack[] {
						new ItemStack(Item.pickaxeIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 12 ),
				new ItemStack[] {
						new ItemStack(Item.axeIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 12 ),
				new ItemStack[] {
						new ItemStack(Item.swordIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack(Item.hoeIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack(Item.shovelIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 5 ),
				new ItemStack( BTWItems.ironNugget, 30 ),
				new ItemStack[] {
						new ItemStack(Item.helmetIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 8 ),
				new ItemStack( BTWItems.ironNugget, 48 ),
				new ItemStack[] {
						new ItemStack(Item.plateIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 42 ),
				new ItemStack[] {
						new ItemStack(Item.legsIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 4 ),
				new ItemStack( BTWItems.ironNugget, 24 ),
				new ItemStack[] {
						new ItemStack(Item.bootsIron, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 20 ),
				new ItemStack( BTWItems.ironNugget, 13 ),
				new ItemStack[] {
						new ItemStack(Item.helmetChain, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 32 ),
				new ItemStack( BTWItems.ironNugget, 21 ),
				new ItemStack[] {
						new ItemStack(Item.plateChain, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 28 ),
				new ItemStack( BTWItems.ironNugget, 19 ),
				new ItemStack[] {
						new ItemStack(Item.legsChain, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 16 ),
				new ItemStack( BTWItems.ironNugget, 11 ),
				new ItemStack[] {
						new ItemStack(Item.bootsChain, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 4 ),
				new ItemStack( BTWItems.ironNugget, 3 ),
				new ItemStack[] {
						new ItemStack( Item.compass )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack[] {
						//new ItemStack( Item.ingotIron, 6 ),
						//new ItemStack( Item.goldNugget, 6 ),
						new ItemStack( Item.ingotIron, 4 ),
						new ItemStack( Item.goldNugget, 4 ),
				},
				new ItemStack[] {
						new ItemStack( Item.doorIron )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 4 ),
				new ItemStack( BTWItems.ironNugget, 3 ),
				new ItemStack[] {
						new ItemStack( Item.map )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 12 ),
				new ItemStack[] {
						new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 1 ),
				new ItemStack[] {
						new ItemStack( Block.railDetector )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 1 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.woodenDetectorRail)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack[] {
						//new ItemStack( Item.ingotIron, 1 ),
						new ItemStack( BTWItems.ironNugget, 1 ),
						new ItemStack( BTWItems.steelNugget, 3 ),
				},
				new ItemStack[] {
						new ItemStack( BTWBlocks.steelDetectorRail, 1 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.pulley)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 3 ),
				new ItemStack( Item.ingotIron, 2 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.saw)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Item.ingotIron, 9 ),
				new ItemStack[] {
						new ItemStack( Block.blockIron )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 5 ),
				new ItemStack( BTWItems.ironNugget, 30 ),
				new ItemStack[] {
						new ItemStack( Item.minecartEmpty )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 5 ),
				new ItemStack( BTWItems.ironNugget, 30 ),
				new ItemStack[] {
						new ItemStack( Item.minecartCrate )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 5 ),
				new ItemStack( BTWItems.ironNugget, 30 ),
				new ItemStack[] {
						new ItemStack( Item.minecartPowered )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 3 ),
				new ItemStack( BTWItems.ironNugget, 1 ),
				new ItemStack[] {
						new ItemStack( Block.rail, 2 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 3 ),
				new ItemStack( BTWItems.ironNugget, 18 ),
				new ItemStack[] {
						new ItemStack( Block.fenceIron, 8 ) // iron bars
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 42 ),
				new ItemStack[] {
						new ItemStack( Item.cauldron )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 42 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.cauldron)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.ironNugget, 20 ),
				new ItemStack[] {
						new ItemStack( BTWItems.screw)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 6 ),
				new ItemStack( BTWItems.ironNugget, 20 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.screwPump)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack( BTWItems.gimpHelmet)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 12 ),
				new ItemStack[] {
						new ItemStack( BTWItems.gimpChest)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack( BTWItems.gimpLeggings)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 2 ),
				new ItemStack( BTWItems.ironNugget, 12 ),
				new ItemStack[] {
						new ItemStack( BTWItems.gimpBoots)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 7 ),
				new ItemStack( BTWItems.ironNugget, 42 ),
				new ItemStack[] {
						new ItemStack( Block.anvil )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.ironNugget, 1 ),
				new ItemStack[] {
						new ItemStack( Block.tripWireSource, 1 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( FCBetterThanWolves.fcItemNuggetIron, 4 ),
				new ItemStack( BTWItems.ironNugget, 2 ),
				new ItemStack[] {
						new ItemStack(BTWItems.ironChisel, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 1 ),
				new ItemStack( BTWItems.ironNugget, 6 ),
				new ItemStack[] {
						new ItemStack( BTWItems.metalFragment)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 54 ),
				new ItemStack( BTWItems.ironNugget, 36 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.pistonShovel)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				//new ItemStack( Item.ingotIron, 11 ),
				new ItemStack( BTWItems.ironNugget, 8 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.ironSpike)
				});
		
		// diamond melting recipes
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack(BTWItems.diamondIngot, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.diamondArmorPlate)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack(BTWItems.diamondIngot, 1),
				new ItemStack[] {
						new ItemStack(BTWItems.diamondChisel, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack(BTWItems.diamondIngot, 2),
				new ItemStack[] {
						new ItemStack(BTWItems.diamondShears, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 3 ),
				new ItemStack[] {
						new ItemStack(Item.pickaxeDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 2 ),
				new ItemStack[] {
						new ItemStack(Item.axeDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 2 ),
				new ItemStack[] {
						new ItemStack(Item.swordDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 1 ),
				new ItemStack[] {
						new ItemStack(Item.hoeDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 1 ),
				new ItemStack[] {
						new ItemStack(Item.shovelDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 6 ),
				new ItemStack[] {
						new ItemStack(Item.helmetDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 8 ),
				new ItemStack[] {
						new ItemStack(Item.plateDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 7 ),
				new ItemStack[] {
						new ItemStack(Item.legsDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.diamondIngot, 4 ),
				new ItemStack[] {
						new ItemStack(Item.bootsDiamond, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		// steel items
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 5 ),
				new ItemStack[] {
						new ItemStack(BTWItems.battleaxe, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 2 ),
				new ItemStack[] {
						new ItemStack(BTWItems.steelAxe, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 3 ),
				new ItemStack[] {
						new ItemStack(BTWItems.steelPickaxe, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 4 ),
				new ItemStack[] {
						new ItemStack(BTWItems.mattock, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 3 ),
				new ItemStack[] {
						new ItemStack(BTWItems.steelSword, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 2 ),
				new ItemStack[] {
						new ItemStack(BTWItems.steelHoe, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 1 ),
				new ItemStack[] {
						new ItemStack(BTWItems.steelShovel, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 1 ),
				new ItemStack[] {
						new ItemStack( BTWItems.steelArmorPlate)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 10 ),
				new ItemStack[] {
						new ItemStack(BTWItems.plateHelmet, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 14 ),
				new ItemStack[] {
						new ItemStack(BTWItems.plateBreastplate, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 12 ),
				new ItemStack[] {
						new ItemStack(BTWItems.plateLeggings, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 8 ),
				new ItemStack[] {
						new ItemStack(BTWItems.plateBoots, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.steelNugget, 3 ),
				new ItemStack[] {
						new ItemStack( BTWItems.broadheadArrowHead, 1 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.steelNugget, 3 ),
				new ItemStack[] {
						new ItemStack( BTWItems.broadheadArrow, 4 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 16 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_STEEL)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 16 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.soulforgedSteelBlock)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 1 ),
				new ItemStack[] {
						new ItemStack(BTWItems.tuningFork, 1, InventoryUtils.IGNORE_METADATA)
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 2 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.steelPressurePlate, 1 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.steelNugget, 12 ),
				new ItemStack[] {
						new ItemStack( BTWBlocks.aestheticNonOpaque, 1, AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD)
				});
		
		// non-melting recipes
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Item.ingotGold, 1  ),
				new ItemStack[] {
						new ItemStack( Item.goldNugget, 9 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Item.ingotIron, 1  ),
				new ItemStack[] {
						new ItemStack( BTWItems.ironNugget, 9 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 1  ),
				new ItemStack[] {
						new ItemStack( BTWItems.steelNugget, 9 )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWItems.soulforgedSteelIngot, 1 ),
				new ItemStack[] {
						new ItemStack( Item.ingotIron, 1 ),
						new ItemStack( BTWItems.coalDust, 1 ),
						new ItemStack( BTWItems.soulUrn, 1 ),
						new ItemStack( BTWItems.soulFlux, 1 )
				});
		
		// glass creation
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Block.glass, 4 ),
				new ItemStack[] {
						new ItemStack( Block.sand, 8 ),
						new ItemStack( Item.netherQuartz )
				});
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( Block.glass, 3 ),
				new ItemStack[] {
						new ItemStack( Block.thinGlass, 8 )
				});
		
		// smoothstone creation
		
		RecipeManager.addStokedCrucibleRecipe(
				new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_STONE),
				new ItemStack[] {
						new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE)
				});
	}
}
