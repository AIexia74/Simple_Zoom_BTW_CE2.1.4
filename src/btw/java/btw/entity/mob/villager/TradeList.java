package btw.entity.mob.villager;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.AestheticVegetationBlock;
import btw.item.BTWItems;
import btw.util.ColorUtils;
import net.minecraft.src.Block;
import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;

public class TradeList {
	//Aliased for readability
	private static final int FARMER = VillagerEntity.PROFESSION_ID_FARMER;
	private static final int LIBRARIAN = VillagerEntity.PROFESSION_ID_LIBRARIAN;
	private static final int PRIEST = VillagerEntity.PROFESSION_ID_PRIEST;
	private static final int BLACKSMITH = VillagerEntity.PROFESSION_ID_BLACKSMITH;
	private static final int BUTCHER = VillagerEntity.PROFESSION_ID_BUTCHER;
	
	public static void addVillagerTrades() {
		// TODO: Rebalance some of the outlier values in trades
		addFarmerTrades();
		addLibrarianTrades();
		addPriestTrades();
		addBlacksmithTrades();
		addButcherTrades();
	}
	
	private static void addFarmerTrades() {
		//Level 1
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWBlocks.looseDirt.blockID, 48, 64, 1F, 1).setDefault(FARMER);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.wood.blockID, 0, 32, 48, 0.15F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.wood.blockID, 1, 32, 48, 0.15F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.wood.blockID, 2, 32, 48, 0.15F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.wood.blockID, 3, 32, 48, 0.15F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.wool.itemID, ColorUtils.BROWN.colorID, 16, 24, 1F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Item.dyePowder.itemID, ColorUtils.WHITE.colorID, 32, 48, 1F, 1);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(FARMER, Item.hoeIron.itemID, 1, 1, 1);
		
		//Level 2
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.flour.itemID, 24, 32, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Item.sugar.itemID, 10, 20, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.cocoaBeans.itemID, 10, 16, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.brownMushroom.itemID, 10, 16, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.hempSeeds.itemID, 24, 32, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Item.egg.itemID, 12, 12, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.thinGlass.blockID, 16, 32, 1F, 2);
		VillagerEntity.addTradeToBuySingleItem(FARMER, Item.bucketMilk.itemID, 1, 2, 0.25F, 2);
		
		VillagerEntity.addTradeToSellMultipleItems(FARMER, BTWItems.wheat.itemID, 8, 16, 1F, 2);
		VillagerEntity.addTradeToSellMultipleItems(FARMER, Item.appleRed.itemID, 2, 4, 0.5F, 2);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(FARMER, BTWBlocks.millstone.blockID, 2, 2, 2);
		
		//Mandatory Trades
		VillagerEntity.addTradeToSellSingleItem(FARMER, BTWItems.sugarCaneRoots.itemID, 2, 3, 1F, 2).setMandatory();
		
		//Level 3
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, Block.melon.blockID, 8, 10, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWBlocks.freshPumpkin.blockID, 10, 16, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.stumpRemover.itemID, 8, 12, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWItems.chocolate.itemID, 1, 2, 1F, 3);
		VillagerEntity.addTradeToBuySingleItem(FARMER, Item.shears.itemID, 1, 1, 0.5F, 3);
		VillagerEntity.addTradeToBuySingleItem(FARMER, Item.flintAndSteel.itemID, 1, 1, 0.5F, 3);
		VillagerEntity.addComplexTrade(FARMER,
				BTWItems.stake.itemID, 0, 1, 1,
				Item.silk.itemID, 0, 16, 32,
				Item.emerald.itemID, 0, 1, 1,
				0.5F, 3);
		VillagerEntity.addTradeToBuySingleItem(FARMER, BTWItems.soap.itemID, 1, 2, 1F, 3)
				.registerEffectForTrade(VillagerEntity.PROFESSION_ID_FARMER, villager -> {
					villager.worldObj.playSoundAtEntity(villager, "mob.slime.attack", 1.0F, (villager.rand.nextFloat() - villager.rand.nextFloat()) * 0.2F + 1.0F);
					villager.setDirtyPeasant(0);
				})
				.setConditional(villager -> villager.getDirtyPeasant() > 0);
		
		VillagerEntity.addTradeToSellMultipleItems(FARMER, Item.bread.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellMultipleItems(FARMER, BTWItems.cookedMushroomOmelet.itemID, 8, 12, 0.5F, 3);
		VillagerEntity.addTradeToSellMultipleItems(FARMER, BTWItems.cookedScrambledEggs.itemID, 8, 12, 0.5F, 3);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(FARMER, BTWItems.waterWheel.itemID, 3, 3, 3);
		
		//Level 4
		VillagerEntity.addTradeToBuySingleItem(FARMER, BTWItems.cementBucket.itemID, 2, 4, 1F, 4);
		VillagerEntity.addTradeToBuyMultipleItems(FARMER, BTWBlocks.lightBlockOff.blockID, 2, 4, 1F, 4);
		
		VillagerEntity.addTradeToSellMultipleItems(FARMER, Item.cookie.itemID, 8, 16, 1F, 4);
		VillagerEntity.addTradeToSellMultipleItems(FARMER, Item.pumpkinPie.itemID, 1, 2, 1F, 4);
		VillagerEntity.addTradeToSellSingleItem(FARMER, Item.cake.itemID, 2, 4, 1F, 4);
		
		VillagerEntity.addLevelUpTradeToBuy(FARMER, BTWBlocks.planterWithSoil.blockID, 0, 8, 12, 4, 4, 4);
		
		//Level 5
		VillagerEntity.addTradeToSellSingleItem(FARMER, Block.mycelium.blockID, 10, 20, 1F, 5);
		VillagerEntity.addArcaneScrollTrade(FARMER, Enchantment.looting.effectId, 16, 32, 1F, 5);
	}
	
	private static void addLibrarianTrades() {
		//Level 1
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.paper.itemID, 24, 32, 1F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.dyePowder.itemID, ColorUtils.BLACK.colorID, 24, 32, 1F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.feather.itemID, 16, 24, 1F, 1);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(LIBRARIAN, Item.enchantedBook.itemID, 2, 2, 1);
		
		//Level 2
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.book.itemID, 1, 3, 1F, 2);
		VillagerEntity.addTradeToBuySingleItem(LIBRARIAN, Item.writableBook.itemID, 1, 1, 1F, 2);
		VillagerEntity.addTradeToBuySingleItem(LIBRARIAN, Block.bookShelf.blockID, 1, 1, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.netherStalkSeeds.itemID, 16, 24, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.lightStoneDust.itemID, 24, 32, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWItems.nitre.itemID, 32, 48, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWItems.batWing.itemID, 8, 12, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.spiderEye.itemID, 4, 8, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.redstone.itemID, 32, 48, 1F, 2);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(LIBRARIAN, Item.brewingStand.itemID, 2, 2, 2);
		
		//Level 3
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWItems.witchWart.itemID, 6, 10, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWItems.mysteriousGland.itemID, 14, 16, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.fermentedSpiderEye.itemID, 4, 8, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.ghastTear.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.magmaCream.itemID, 8, 12, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, Item.blazePowder.itemID, 4, 6, 1F, 3);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(LIBRARIAN, BTWBlocks.blockDispenser.blockID, 4, 4, 3);
		
		//Level 4
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWBlocks.detectorBlock.blockID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWBlocks.buddyBlock.blockID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWBlocks.blockDispenser.blockID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuySingleItem(LIBRARIAN, BTWBlocks.lens.blockID, 2, 3, 1F, 4);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(LIBRARIAN, BTWItems.enderSpectacles.itemID, 3, 3, 4);
		
		//Level 5
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWItems.brimstone.itemID, 16, 32, 1F, 5);
		VillagerEntity.addTradeToBuyMultipleItems(LIBRARIAN, BTWBlocks.aestheticVegetation.blockID,
				AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING, 8, 16, 1F, 5);
		VillagerEntity.addTradeToBuySingleItem(LIBRARIAN, BTWItems.netherGrothSpores.itemID, 2, 3, 1F, 5);
		
		VillagerEntity.addArcaneScrollTrade(LIBRARIAN, Enchantment.power.effectId, 32, 48, 1F, 5);
		
		//Mandatory trades
		VillagerEntity.addItemConversionTrade(LIBRARIAN, Item.enderPearl.itemID, 6, 8, Item.eyeOfEnder.itemID, 1F, 5).setMandatory();
	}
	
	private static void addPriestTrades() {
		//Level 1
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, BTWItems.hemp.itemID, 18, 22, 1F, 1).setDefault(PRIEST);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, BTWItems.redMushroom.itemID, 10, 16, 1F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, Block.cactus.blockID, 32, 64, 1F, 1);
		VillagerEntity.addTradeToBuySingleItem(PRIEST, Item.painting.itemID, 2, 3, 0.5F, 1);
		VillagerEntity.addTradeToBuySingleItem(PRIEST, Item.flintAndSteel.itemID, 1, 1, 1F, 1);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(PRIEST, Block.enchantmentTable.blockID, 2, 2, 1);
		
		//Level 2
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.swordIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.axeIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.pickaxeIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.helmetIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.plateIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.legsIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.bootsIron.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.swordDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.axeDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.pickaxeDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.helmetDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.plateDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.legsDiamond.itemID, 2, 4, 0.25F, 2);
		VillagerEntity.addEnchantmentTrade(PRIEST, Item.bootsDiamond.itemID, 2, 4, 0.25F, 2);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(PRIEST, BTWBlocks.arcaneVessel.blockID, 2, 2, 2);
		
		//Level 3
		((VillagerEntity.WeightedMerchantRecipeEntry) VillagerEntity.addTradeToBuyMultipleItems(PRIEST,
				BTWItems.candle.itemID, 4, 8,
				1F, 3))
				.setRandomMetas(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, 0);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, Item.skull.itemID, 0, 2, 4, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, Item.skull.itemID, 2, 2, 4, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, Item.skull.itemID, 4, 2, 4, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_BONE, 2, 3, 1F, 3);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(PRIEST, Item.skull.itemID, 1, 3, 3, 3);
		
		//Level 4
		VillagerEntity.addTradeToBuyMultipleItems(PRIEST, BTWItems.soulUrn.itemID, 2, 3, 2F, 4);
		VillagerEntity.addTradeToBuySingleItem(PRIEST, BTWItems.canvas.itemID, 2, 3, 1F, 5);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(PRIEST, BTWBlocks.infernalEnchanter.blockID, 4, 4, 4);
		
		//Mandatory trades
		VillagerEntity.addSkullconversionTrade(PRIEST, 1, 6, 8, 5, 1F, 4).setMandatory();
		VillagerEntity.addComplexTrade(PRIEST,
				BTWBlocks.dormandSoulforge.blockID, 0, 1, 1,
				Item.netherStar.itemID, 0, 1, 1,
				BTWBlocks.soulforge.blockID, 0, 1, 1,
				1F, 4)
				.setMandatory()
				.registerEffectForTrade(PRIEST, villager -> {
					  villager.worldObj.playSoundAtEntity(villager, "random.anvil_land", 0.3F, villager.rand.nextFloat() * 0.1F + 0.9F);
					  villager.worldObj.playSoundAtEntity(villager, "ambient.cave.cave4", 0.5F, villager.rand.nextFloat() * 0.05F + 0.5F);
				});
		
		//Level 5
		VillagerEntity.addArcaneScrollTrade(PRIEST, Enchantment.fortune.effectId, 48, 64, 1F, 5);
	}
	
	private static void addBlacksmithTrades() {
		//Level 1
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, Item.coal.itemID, 16, 24, 1F, 1).setDefault(BLACKSMITH);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, Block.wood.blockID, 2, 32, 48, 1F, 1);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.ironNugget.itemID, 18, 27, 1F, 1);
		VillagerEntity.addTradeToBuySingleItem(BLACKSMITH, BTWBlocks.idleOven.blockID, 1, 1, 1F, 1);
		
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.swordIron.itemID, 4, 6, 1F, 1);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.axeIron.itemID, 4, 6, 1F, 1);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.pickaxeIron.itemID, 6, 9, 1F, 1);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.shovelIron.itemID, 2, 3, 1F, 1);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.hoeIron.itemID, 2, 3, 1F, 1);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BLACKSMITH, BTWBlocks.hibachi.blockID, 1, 1, 1);
		
		//Level 2
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.nethercoal.itemID, 12, 20, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWBlocks.hibachi.blockID, 2, 3, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.creeperOysters.itemID, 14, 16, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, Item.goldNugget.itemID, 18, 27, 1F, 2);
		VillagerEntity.addTradeToBuySingleItem(BLACKSMITH, Item.diamond.itemID, 2, 3, 1F, 2);
		
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.bootsIron.itemID, 4, 6, 1F, 2);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.helmetIron.itemID, 10, 15, 1F, 2);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.plateIron.itemID, 16, 24, 1F, 2);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.legsIron.itemID, 14, 21, 1F, 2);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BLACKSMITH, BTWBlocks.bellows.blockID, 2, 2, 2);
		
		//Level 3
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.swordDiamond.itemID, 8, 12, 1F, 3);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.axeDiamond.itemID, 8, 12, 1F, 3);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.pickaxeDiamond.itemID, 12, 18, 1F, 3);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.shovelDiamond.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.hoeDiamond.itemID, 4, 6, 1F, 3);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BLACKSMITH, BTWBlocks.crucible.blockID, 3, 3, 3);
		
		//Level 4
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.soulUrn.itemID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.haft.itemID, 6, 8, 1F, 4);
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWBlocks.miningCharge.blockID, 4, 6, 1F, 4);
		
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.bootsDiamond.itemID, 8, 12, 1F, 4);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.helmetDiamond.itemID, 20, 30, 1F, 4);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.plateDiamond.itemID, 32, 48, 1F, 4);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.legsDiamond.itemID, 28, 42, 1F, 4);
		
		VillagerEntity.addLevelUpTradeToBuy(BLACKSMITH, BTWItems.soulforgedSteelIngot.itemID, 0, 8, 8, 4, 4, 4);
		
		//Level 5
		VillagerEntity.addTradeToBuyMultipleItems(BLACKSMITH, BTWItems.soulFlux.itemID, 16, 24, 1F, 5);
		
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.bootsChain.itemID, 4, 6, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.helmetChain.itemID, 10, 15, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.plateChain.itemID, 16, 24, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, Item.legsChain.itemID, 14, 21, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, BTWItems.steelSword.itemID, 16, 24, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, BTWItems.steelAxe.itemID, 16, 24, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, BTWItems.steelPickaxe.itemID, 24, 36, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, BTWItems.steelShovel.itemID, 8, 16, 1F, 5);
		VillagerEntity.addTradeToSellSingleItem(BLACKSMITH, BTWItems.steelHoe.itemID, 16, 24, 1F, 5);
		VillagerEntity.addArcaneScrollTrade(BLACKSMITH, Enchantment.unbreaking.effectId, 32, 48, 1F, 5);
	}
	
	private static void addButcherTrades() {
		//Level 1
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, Item.arrow.itemID, 24, 32, 1F, 1);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, Item.shears.itemID, 1, 1, 0.5F, 1);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, Item.fishingRod.itemID, 1, 1, 0.5F, 1);
		
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, Item.beefRaw.itemID, 8, 10, 1F, 1).setDefault(BUTCHER);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, Item.porkRaw.itemID, 8, 10, 1F, 1);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, Item.chickenRaw.itemID, 10, 12, 1F, 1);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, Item.fishRaw.itemID, 10, 12, 1F, 1);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.rawMutton.itemID, 10, 12, 1F, 1);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, Item.leather.itemID, 7, 9, 1F, 1);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BUTCHER, BTWBlocks.cauldron.blockID, 1, 1, 1);
		
		//Level 2
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.dung.itemID, 10, 16, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.rawWolfChop.itemID, 6, 8, 1F, 2);
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.bark.itemID, 1, 48, 64, 1F, 2);
		
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.steakAndPotatoes.itemID, 4, 8, 1F, 2);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.hamAndEggs.itemID, 4, 8, 1F, 2);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.tastySandwich.itemID, 4, 8, 1F, 2);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.chowder.itemID, 10, 12, 1F, 2);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.cookedKebab.itemID, 4, 8, 1F, 2);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BUTCHER, BTWBlocks.saw.blockID, 2, 2, 2);
		
		//Level 3
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.carrot.itemID, 10, 16, 1F, 3);
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, Item.potato.itemID, 10, 16, 1F, 3);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, BTWItems.rawLiver.itemID, 1, 2, 1F, 3);
		
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.tannedLeather.itemID, 4, 8, 1F, 3);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.porkDinner.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.steakDinner.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.wolfDinner.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.chickenSoup.itemID, 4, 6, 1F, 3);
		VillagerEntity.addTradeToSellSingleItem(BUTCHER, Item.saddle.itemID, 2, 3, 1F, 3);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BUTCHER, BTWItems.breedingHarness.itemID, 3, 3, 3);
		
		//Level 4
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.rawMysteryMeat.itemID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, BTWItems.screw.itemID, 2, 3, 1F, 4);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, BTWItems.compositeBow.itemID, 2, 3, 1F, 4);
		
		VillagerEntity.addTradeToSellMultipleItems(BUTCHER, BTWItems.heartyStew.itemID, 3, 4, 1F, 4);
		VillagerEntity.addTradeToSellSingleItem(BUTCHER, BTWItems.tannedLeatherBoots.itemID, 2, 3, 0.5F, 4);
		VillagerEntity.addTradeToSellSingleItem(BUTCHER, BTWItems.tannedLeatherChest.itemID, 6, 8, 0.5F, 4);
		VillagerEntity.addTradeToSellSingleItem(BUTCHER, BTWItems.tannedLeatherHelmet.itemID, 3, 4, 0.5F, 4);
		VillagerEntity.addTradeToSellSingleItem(BUTCHER, BTWItems.tannedLeatherLeggings.itemID, 4, 6, 0.5F, 4);
		
		VillagerEntity.addLevelUpTradeToBuySingleItem(BUTCHER, BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY, 4, 4, 4);
		
		//Mandatory trades
		VillagerEntity.addSkullconversionTrade(BUTCHER, 0, 6, 8, 1, 1F, 4).setMandatory();
		
		//Level 5
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.dynamite.itemID, 4, 6, 1F, 5);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, BTWItems.battleaxe.itemID, 4, 5, 1F, 5);
		VillagerEntity.addTradeToBuySingleItem(BUTCHER, BTWBlocks.companionCube.blockID, 1, 2, 1F, 5)
				.registerEffectForTrade(BUTCHER, villager ->
						villager.worldObj.playSoundAtEntity(villager, "mob.wolf.hurt",
								5.0F,
								(villager.rand.nextFloat() - villager.rand.nextFloat()) * 0.2F + 1.0F));
		VillagerEntity.addTradeToBuyMultipleItems(BUTCHER, BTWItems.broadheadArrow.itemID, 6, 12, 1F, 5);
		VillagerEntity.addComplexTrade(BUTCHER,
				BTWBlocks.lightningRod.blockID, 0, 1, 1,
				BTWItems.soap.itemID, 0, 1, 1,
				Item.emerald.itemID, 0, 3, 5,
				0.5F, 5)
				.registerEffectForTrade(BUTCHER, villager ->
						villager.worldObj.playSoundAtEntity(villager, "random.classic_hurt", 1F, villager.getSoundPitch() * 2.0F));
		
		VillagerEntity.addArcaneScrollTrade(BUTCHER, Enchantment.sharpness.effectId, 32, 48, 1F, 5);
	}
}
