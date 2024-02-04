package net.minecraft.src;

import btw.BTWAddon;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.items.*;
import btw.item.items.legacy.LegacyWheatItem;
import btw.util.ReflectionUtils;
import com.prupe.mcpatcher.cit.CITUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

public class Item
{
    private CreativeTabs tabToDisplayOn = null;

    /** The RNG used by the Item subclasses. */
    protected static Random itemRand = new Random();

    /** A 32000 elements Item array. */
    public static Item[] itemsList = new Item[32000];
    
    public static final int FILTERABLE_NO_PROPERTIES = 0;
    public static final int FILTERABLE_SOLID_BLOCK = 1;
    public static final int FILTERABLE_SMALL = 2;
    public static final int FILTERABLE_NARROW = 4;
    public static final int FILTERABLE_FINE = 8;
    public static final int FILTERABLE_THIN = 16;
    
    public static Item shovelIron = ( new ShovelItem( 0, EnumToolMaterial.IRON ) ).setUnlocalizedName( "shovelIron" );
    public static Item pickaxeIron = ( new PickaxeItem( 1, EnumToolMaterial.IRON ) ).setUnlocalizedName( "pickaxeIron" );
    public static Item axeIron = ( new AxeItem( 2, EnumToolMaterial.IRON ) ).setUnlocalizedName( "hatchetIron" );
    public static Item flintAndSteel = ( new FlintAndSteelItem( 3 ) ).setUnlocalizedName( "flintAndSteel" );
    public static Item appleRed = ( new ItemFood( 4, 1, 0F, false ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("apple");
    public static ItemBow bow = new BowItem( 5 );
    public static Item arrow = new ArrowItem( 6 );
    public static Item coal = ( new ItemCoal( 7 ) ).setIncineratedInCrucible().setfurnaceburntime(FurnaceBurnTime.COAL).setFilterableProperties(
            FILTERABLE_SMALL).setUnlocalizedName("coal");
    public static Item diamond = ( new Item( 8 ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item ingotIron = (new Item(9)).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item ingotGold = (new Item(10)).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item swordIron = ( new SwordItem( 11, EnumToolMaterial.IRON ) ).setUnlocalizedName( "swordIron" );
    public static Item swordWood = ( new SwordItem( 12, EnumToolMaterial.WOOD ) ).setUnlocalizedName( "swordWood" );
    public static Item shovelWood = ( new ShovelItem( 13, EnumToolMaterial.WOOD ) ).setDamageVsEntity(2).setUnlocalizedName("shovelWood");
    public static Item pickaxeWood = ( new PickaxeItem( 14, EnumToolMaterial.WOOD, 1 ) ).setUnlocalizedName( "pickaxeWood" );
    public static Item axeWood = ( new AxeItem( 15, EnumToolMaterial.WOOD ) ).setUnlocalizedName( "hatchetWood" );
    public static Item swordStone = ( new SwordItem( 16, EnumToolMaterial.STONE ) ).setUnlocalizedName( "swordStone" );
    public static Item shovelStone = new ShovelItemStone( 17 );
    public static Item pickaxeStone = ( new PickaxeItem( 18, EnumToolMaterial.STONE ) ).setUnlocalizedName( "pickaxeStone" );
    public static Item axeStone = ( new AxeItem( 19, EnumToolMaterial.STONE ) ).setUnlocalizedName( "hatchetStone" );
    public static Item swordDiamond = ( new SwordItem( 20, EnumToolMaterial.EMERALD ) ).setUnlocalizedName( "swordDiamond" );
    public static Item shovelDiamond = ( new ShovelItem( 21, EnumToolMaterial.EMERALD ) ).setUnlocalizedName( "shovelDiamond" );
    public static Item pickaxeDiamond = ( new PickaxeItem( 22, EnumToolMaterial.EMERALD ) ).setUnlocalizedName( "pickaxeDiamond" );
    public static Item axeDiamond = ( new AxeItem( 23, EnumToolMaterial.EMERALD ) ).setUnlocalizedName( "hatchetDiamond" );
    public static Item stick = new ShaftItem( 24 );
    public static Item bowlEmpty = ( new Item( 25 ) ).setBuoyant().setIncineratedInCrucible().setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item bowlSoup = ( new MushroomSoupItem( 26, 3 ) ).setUnlocalizedName( "mushroomStew" );
    public static Item swordGold = ( new SwordItem( 27, EnumToolMaterial.GOLD ) ).setUnlocalizedName( "swordGold" );
    public static Item shovelGold = ( new ShovelItem( 28, EnumToolMaterial.GOLD ) ).setUnlocalizedName( "shovelGold" );
    public static Item pickaxeGold = ( new PickaxeItem( 29, EnumToolMaterial.GOLD ) ).setUnlocalizedName( "pickaxeGold" );
    public static Item axeGold = ( new AxeItem( 30, EnumToolMaterial.GOLD ) ).setUnlocalizedName( "hatchetGold" );
    public static Item silk = ( new Item( 31 ) ).setBuoyant().setBellowsBlowDistance(2).setIncineratedInCrucible().setFilterableProperties(
            FILTERABLE_SMALL | FILTERABLE_THIN).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item feather = ( new Item( 32 ) ).setBuoyant().setfurnaceburntime(FurnaceBurnTime.KINDLING).setIncineratedInCrucible().setBellowsBlowDistance(3).setFilterableProperties(
            FILTERABLE_SMALL | FILTERABLE_THIN).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item gunpowder = ( new Item( 33 ) ).setBellowsBlowDistance(3).setFilterableProperties(FILTERABLE_FINE).setUnlocalizedName("sulphur").setPotionEffect(PotionHelper.gunpowderEffect).setCreativeTab(CreativeTabs.tabMaterials);
    public static Item hoeWood = ( new HoeItem( 34, EnumToolMaterial.WOOD ) ).setUnlocalizedName( "hoeWood" );
    public static Item hoeStone = ( new HoeItem( 35, EnumToolMaterial.STONE ) ).setUnlocalizedName( "hoeStone" );
    public static Item hoeIron = ( new HoeItem( 36, EnumToolMaterial.IRON ) ).setUnlocalizedName( "hoeIron" );
    public static Item hoeDiamond = ( new HoeItem( 37, EnumToolMaterial.EMERALD ) ).setUnlocalizedName( "hoeDiamond" );
    public static Item hoeGold = ( new HoeItem( 38, EnumToolMaterial.GOLD ) ).setUnlocalizedName( "hoeGold" );
    public static Item seeds = ( new SeedItem( 39, Block.crops.blockID ) ).setAsBasicChickenFood().setUnlocalizedName("seeds").setCreativeTab(null);
    public static Item wheat = new LegacyWheatItem( 40 );
    public static Item bread = ( new ItemFood( 41, 3, 0.25F, false ) ).setUnlocalizedName( "bread" );
    public static ItemArmor helmetLeather = (ItemArmor)( new ArmorItemLeather( 42, 0 ) ).setUnlocalizedName( "helmetCloth" );
    public static ItemArmor plateLeather = (ItemArmor)( new ArmorItemLeather( 43, 1 ) ).setUnlocalizedName( "chestplateCloth" );
    public static ItemArmor legsLeather = (ItemArmor)( new ArmorItemLeather( 44, 2 ) ).setUnlocalizedName( "leggingsCloth" );
    public static ItemArmor bootsLeather = (ItemArmor)( new ArmorItemLeather( 45, 3 ) ).setUnlocalizedName( "bootsCloth" );
    public static ItemArmor helmetChain = (ItemArmor)( new ArmorItemChain( 46, 0, 3 ) ).setUnlocalizedName( "helmetChain" );
    public static ItemArmor plateChain = (ItemArmor)( new ArmorItemChain( 47, 1, 4 ) ).setUnlocalizedName( "chestplateChain" );
    public static ItemArmor legsChain = (ItemArmor)( new ArmorItemChain( 48, 2, 4 ) ).setUnlocalizedName( "leggingsChain" );
    public static ItemArmor bootsChain = (ItemArmor)( new ArmorItemChain( 49, 3, 2 ) ).setUnlocalizedName( "bootsChain" );
    public static ItemArmor helmetIron = (ItemArmor)( new ArmorItemIron( 50, 0, 5 ) ).setUnlocalizedName( "helmetIron" );
    public static ItemArmor plateIron = (ItemArmor)( new ArmorItemIron( 51, 1, 8 ) ).setUnlocalizedName( "chestplateIron" );
    public static ItemArmor legsIron = (ItemArmor)( new ArmorItemIron( 52, 2, 7 ) ).setUnlocalizedName( "leggingsIron" );
    public static ItemArmor bootsIron = (ItemArmor)( new ArmorItemIron( 53, 3, 4 ) ).setUnlocalizedName( "bootsIron" );
    public static ItemArmor helmetDiamond = (ItemArmor)( new ArmorItemDiamond( 54, 0, 5 ) ).setUnlocalizedName( "helmetDiamond" );
    public static ItemArmor plateDiamond = (ItemArmor)( new ArmorItemDiamond( 55, 1, 8 ) ).setUnlocalizedName( "chestplateDiamond" );
    public static ItemArmor legsDiamond = (ItemArmor)( new ArmorItemDiamond( 56, 2, 7 ) ).setUnlocalizedName( "leggingsDiamond" );
    public static ItemArmor bootsDiamond = (ItemArmor)( new ArmorItemDiamond( 57, 3, 4 ) ).setUnlocalizedName( "bootsDiamond" );
    public static ItemArmor helmetGold = (ItemArmor)( new ArmorItemGold( 58, 0, 5 ) ).setUnlocalizedName( "helmetGold" );
    public static ItemArmor plateGold = (ItemArmor)( new ArmorItemGold( 59, 1, 8 ) ).setUnlocalizedName( "chestplateGold" );
    public static ItemArmor legsGold = (ItemArmor)( new ArmorItemGold( 60, 2, 7 ) ).setUnlocalizedName( "leggingsGold" );
    public static ItemArmor bootsGold = (ItemArmor)( new ArmorItemGold( 61, 3, 4 ) ).setUnlocalizedName( "bootsGold" );
    public static Item flint = new FlintItem( 62 );
    public static Item porkRaw = ( new FoodItem(63, FoodItem.PORK_CHOP_RAW_HUNGER_HEALED, FoodItem.PORK_CHOP_SATURATION_MODIFIER, true, "porkchopRaw", true ) ).setStandardFoodPoisoningEffect();
    public static Item porkCooked = ( new ItemFood(64, FoodItem.PORK_CHOP_COOKED_HUNGER_HEALED, FoodItem.PORK_CHOP_SATURATION_MODIFIER, true ) ).setUnlocalizedName("porkchopCooked");
    public static Item painting = ( new ItemHangingEntity( 65, EntityPainting.class ) ).setBuoyant().setIncineratedInCrucible().setUnlocalizedName("painting");
    public static Item appleGold = ( new ItemAppleGold( 66, 1, 0F, false ) ).setAlwaysEdible().setPotionEffect( Potion.regeneration.id, 5, 0, 1F ).setNonBuoyant().setNotIncineratedInCrucible().setFilterableProperties(
            FILTERABLE_SMALL).setUnlocalizedName("appleGold");
    public static Item sign = new SignItem( 67 );
    public static Item doorWood = new DoorItemWood( 68 );
    public static Item bucketEmpty = new BucketItemEmpty( 69 );
    public static Item bucketWater = new BucketItemWater( 70 );
    public static Item bucketLava = new BucketItemLava( 71 );
    public static Item minecartEmpty = ( new MinecartItem( 72, 0 ) ).setUnlocalizedName( "minecart" );
    public static Item saddle = ( new ItemSaddle( 73 ) ).setBuoyant().setIncineratedInCrucible().setUnlocalizedName("saddle");
    public static Item doorIron = (new ItemDoor(74, Material.iron)).setUnlocalizedName("doorIron");
    public static Item redstone = new RedstoneItem( 75 );
    public static Item snowball = new SnowballItem( 76 );
    public static Item boat = new BoatItem( 77 );
    public static Item leather = ( new Item( 78 ) ).setBuoyant().setIncineratedInCrucible().setFilterableProperties(FILTERABLE_THIN).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item bucketMilk = new BucketItemMilk( 79 );
    public static Item brick = new BrickItem( 80 );
    public static Item clay = new ClayItem( 81 );
    public static Item reed = ( new ItemReed( 82, Block.reed ) ).setBuoyant().setfurnaceburntime(FurnaceBurnTime.KINDLING).setIncineratedInCrucible().setFilterableProperties(
            FILTERABLE_NARROW).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item paper = ( new Item( 83 ) ).setBuoyant().setBellowsBlowDistance(3).setfurnaceburntime(FurnaceBurnTime.KINDLING).setIncineratedInCrucible().setFilterableProperties(
            FILTERABLE_SMALL | FILTERABLE_THIN).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc);
    public static Item book = new BookItem( 84 );
    public static Item slimeBall = new SlimeballItem( 85 );
    public static Item minecartCrate = ( new MinecartItem( 86, 1 ) ).setUnlocalizedName( "minecartChest" );
    public static Item minecartPowered = ( new MinecartItem( 87, 2 ) ).setUnlocalizedName( "minecartFurnace" );
    public static Item egg = new EggItem( 88 );
    public static Item compass = (new Item(89)).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools);
    public static ItemFishingRod fishingRod = new FishingRodItem( 90 );
    public static Item pocketSundial = (new Item(91)).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools);
    public static Item lightStoneDust = ( new Item( 92 ) ).setBellowsBlowDistance(3).setFilterableProperties(FILTERABLE_FINE).setUnlocalizedName("yellowDust").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item fishRaw = ( new FoodItem(93, FoodItem.FISH_RAW_HUNGER_HEALED, FoodItem.FISH_SATURATION_MODIFIER, false, "fishRaw" ) ).setStandardFoodPoisoningEffect();
    public static Item fishCooked = ( new ItemFood(94, FoodItem.FISH_COOKED_HUNGER_HEALED, FoodItem.FISH_SATURATION_MODIFIER, false ) ).setUnlocalizedName("fishCooked");
    public static Item dyePowder = new DyeItem( 95 );
    public static Item bone = new BoneItem( 96 );
    public static Item sugar = ( new Item( 97 ) ).setBuoyant().setBellowsBlowDistance(3).setIncineratedInCrucible().setFilterableProperties(FILTERABLE_FINE).setUnlocalizedName("sugar").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item cake = ( new ItemReed( 98, Block.cake ) ).setBuoyant().setIncineratedInCrucible().setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood);
    public static Item bed = ( new BedItem(99, Block.bed.blockID) ).setBuoyant().setIncineratedInCrucible().setMaxStackSize(1).setUnlocalizedName("bed");
    public static Item redstoneRepeater = new RedstoneRepeaterItem( 100 );
    public static Item cookie = ( new ItemFood( 101, 1, 1F, false ) ).setAlwaysEdible().setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("cookie");
    public static ItemMap map = new MapItem( 102 );
    public static ItemShears shears = (ItemShears)( new ShearsItem( 103 ) ).setUnlocalizedName( "shears" );
    public static Item melon = new HighResolutionFoodItem( 104, 2, 0F, false, "melon" );
    public static Item pumpkinSeeds = ( new SeedFoodItem( 105, 1, 0F, Block.pumpkinStem.blockID ) ).setAsBasicChickenFood().setBellowsBlowDistance(2).setFilterableProperties(
            FILTERABLE_FINE).setUnlocalizedName("seeds_pumpkin");
    public static Item melonSeeds = ( new SeedItem( 106, Block.melonStem.blockID ) ).setAsBasicChickenFood().setUnlocalizedName("seeds_melon");
    public static Item beefRaw = ( new FoodItem(107, FoodItem.BEEF_RAW_HUNGER_HEALED, FoodItem.BEEF_SATURATION_MODIFIER, true, "beefRaw", true ) ).setStandardFoodPoisoningEffect();
    public static Item beefCooked = ( new ItemFood(108, FoodItem.BEEF_COOKED_HUNGER_HEALED, FoodItem.BEEF_SATURATION_MODIFIER, true ) ).setUnlocalizedName("beefCooked");
    public static Item chickenRaw = ( new FoodItem(109, FoodItem.CHICKEN_RAW_HUNGER_HEALED, FoodItem.CHICKEN_SATURATION_MODIFIER, true, "chickenRaw" ) ).setStandardFoodPoisoningEffect();
    public static Item chickenCooked = (new ItemFood(110, FoodItem.CHICKEN_COOKED_HUNGER_HEALED, FoodItem.CHICKEN_SATURATION_MODIFIER, true ) ).setUnlocalizedName("chickenCooked");
    public static Item rottenFlesh = new RottenFleshItem( 111 );
    public static Item enderPearl = ( new ItemEnderPearl( 112 ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("enderPearl");
    public static Item blazeRod = ( new Item( 113 ) ).setfurnaceburntime(FurnaceBurnTime.BLAZE_ROD).setFilterableProperties(FILTERABLE_NARROW).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item ghastTear = ( new Item( 114 ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("ghastTear").setPotionEffect(PotionHelper.ghastTearEffect).setCreativeTab(CreativeTabs.tabBrewing);
    public static Item goldNugget = ( new Item( 115 ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item netherStalkSeeds = ( new SeedItem( 116, Block.netherStalk.blockID ) ).setBellowsBlowDistance(1).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4");
    public static ItemPotion potion = new PotionItem( 117 );
    public static Item glassBottle = ( new GlassBottleItem( 118 ) ).setBuoyant().setUnlocalizedName("glassBottle");
    public static Item spiderEye = ( (new ItemFood( 119, 2, 0.8F, false ) ).setPotionEffect( Potion.poison.id, 5, 0, 1F ) ).setNeutralBuoyant().setFilterableProperties(
            FILTERABLE_SMALL).setPotionEffect(PotionHelper.goldenCarrotEffect).setUnlocalizedName("spiderEye");
    public static Item fermentedSpiderEye = ( new Item( 120 ) ).setNeutralBuoyant().setIncineratedInCrucible().setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("fermentedSpiderEye").setPotionEffect(PotionHelper.fermentedSpiderEyeEffect).setCreativeTab(CreativeTabs.tabBrewing);
    public static Item blazePowder = ( new Item( 121 ) ).setBellowsBlowDistance(3).setFilterableProperties(FILTERABLE_FINE).setUnlocalizedName("blazePowder").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(CreativeTabs.tabBrewing);
    public static Item magmaCream = ( new Item( 122 ) ).setNeutralBuoyant().setUnlocalizedName("magmaCream").setPotionEffect(PotionHelper.magmaCreamEffect).setCreativeTab(CreativeTabs.tabBrewing);
    public static Item brewingStand = (new ItemReed(123, Block.brewingStand)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing);
    public static Item cauldron = (new ItemReed(124, Block.cauldron)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
    public static Item eyeOfEnder = ( new ItemEnderEye( 125 ) ).setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("eyeOfEnder");
    public static Item speckledMelon = ( new Item( 126 ) ).setUnlocalizedName( "speckledMelon" ).setCreativeTab( CreativeTabs.tabFood );
    public static Item monsterPlacer = (new ItemMonsterPlacer(127)).setUnlocalizedName("monsterPlacer");

    /**
     * Bottle o' Enchanting. Drops between 1 and 3 experience orbs when thrown.
     */
    public static Item expBottle = (new ItemExpBottle(128)).setUnlocalizedName("expBottle");
    public static Item fireballCharge = new FireChargeItem( 129 );
    public static Item writableBook = ( new ItemWritableBook( 130 ) ).setBuoyant().setIncineratedInCrucible().setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc);
    public static Item writtenBook = ( new ItemEditableBook( 131 ) ).setBuoyant().setIncineratedInCrucible().setUnlocalizedName("writtenBook");
    public static Item emerald = (new Item(132)).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials);
    public static Item itemFrame = ( new ItemHangingEntity( 133, EntityItemFrame.class ) ).setBuoyant().setIncineratedInCrucible().setFilterableProperties(
            FILTERABLE_SOLID_BLOCK).setUnlocalizedName("frame");
    public static Item flowerPot = ( new ItemReed( 134, Block.flowerPot ) ).setBuoyant().setFilterableProperties(FILTERABLE_SOLID_BLOCK).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations);
    public static Item carrot = ( new SeedFoodItem( 135, 3, 0F, Block.carrot.blockID ) ).setFilterableProperties(FILTERABLE_SMALL).setAsBasicPigFood().setUnlocalizedName("carrots").setCreativeTab(null);
    public static Item potato = ( new SeedFoodItem( 136, 3, 0F, Block.potato.blockID ) ).setFilterableProperties(FILTERABLE_SMALL).setAsBasicPigFood().setUnlocalizedName("potato");
    public static Item bakedPotato = ( new ItemFood( 137, 2, 0F, false ) ).setFilterableProperties(FILTERABLE_SMALL).setAsBasicPigFood().setUnlocalizedName("potatoBaked");
    public static Item poisonousPotato = ( new ItemFood( 138, 1, 0F, false ) ).setPotionEffect( Potion.poison.id, 5, 0, 0.6F ).setFilterableProperties(
            FILTERABLE_SMALL).setUnlocalizedName("potatoPoisonous");
    public static ItemEmptyMap emptyMap = new EmptyMapItem( 139 );
    public static Item goldenCarrot = ( new ItemFood( 140, 1, 0F, false ) ).setNonBuoyant().setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("carrotGolden");
    public static Item skull = ( new ItemSkull( 141 ) ).setBuoyant().setIncineratedInCrucible().setFilterableProperties(FILTERABLE_SOLID_BLOCK).setUnlocalizedName("skull");
    public static Item carrotOnAStick = new CarrotOnAStickItem( 142 );
    public static Item netherStar = new NetherStarItem( 143 );
    public static Item pumpkinPie = ( new ItemFood( 144, 2, 2.5F, false ) ).setAlwaysEdible().setUnlocalizedName( "pumpkinPie" ).setCreativeTab( CreativeTabs.tabFood );
    public static Item firework = (new ItemFirework(145)).setUnlocalizedName("fireworks");
    public static Item fireworkCharge = (new ItemFireworkCharge(146)).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc);
    public static ItemEnchantedBook enchantedBook = (ItemEnchantedBook)( new EnchantedBookItem( 147 ) ).setMaxStackSize( 1 ).setUnlocalizedName( "enchantedBook" );
    public static Item comparator = (new PlaceAsBlockItem(148, Block.redstoneComparatorIdle.blockID)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone);
    public static Item netherrackBrick = ( new Item(149 ) ).setUnlocalizedName( "netherbrick" );
    public static Item netherQuartz = ( new NetherQuartzItem( 150 ) ).setUnlocalizedName( "netherquartz" ).setCreativeTab( CreativeTabs.tabMaterials );
    public static Item minecartTnt = ( new StubItem( 151 ) ).setUnlocalizedName( "minecartTnt" );
    public static Item minecartHopper = ( new StubItem( 152 ) ).setUnlocalizedName( "minecartHopper" );
    // Added aliases to avoid annoying naming differences between client and server
    @Environment(EnvType.CLIENT)
    public static Item tntMinecart = minecartTnt;
    @Environment(EnvType.CLIENT)
    public static Item hopperMinecart = minecartHopper;
    
    public static Item record13 = (new ItemRecord(2000, "13")).setUnlocalizedName("record");
    public static Item recordCat = (new ItemRecord(2001, "cat")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordBlocks = (new ItemRecord(2002, "blocks")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordChirp = (new ItemRecord(2003, "chirp")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordFar = (new ItemRecord(2004, "far")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordMall = (new ItemRecord(2005, "mall")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordMellohi = (new ItemRecord(2006, "mellohi")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordStal = (new ItemRecord(2007, "stal")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordStrad = (new ItemRecord(2008, "strad")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordWard = (new ItemRecord(2009, "ward")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item record11 = (new ItemRecord(2010, "11")).setUnlocalizedName("record").setCreativeTab( null );
    public static Item recordWait = (new ItemRecord(2011, "wait")).setUnlocalizedName("record").setCreativeTab((CreativeTabs)null);

    /** The ID of this item. */
    public final int itemID;

    /** Maximum size of the stack. */
    protected int maxStackSize = 64;

    /** Maximum damage an item can handle. */
    private int maxDamage = 0;

    /** If true, render the object in full 3D, like weapons and tools. */
    protected boolean full3D = false;

    /**
     * Some items (like dyes) have multiple subtypes on same item, this is field define this behavior
     */
    protected boolean hasSubtypes = false;
    private Item containerItem = null;
    private String potionEffect = null;

    /** The unlocalized name of this item. */
    private String unlocalizedName;

    /** Icon index in the icons table. */
    @Environment(EnvType.CLIENT)
    public Icon itemIcon;
    
    public static final boolean[] itemReplaced = new boolean[32000];
	public static final String[] itemReplacedBy = new String[32000];
    private Class entityClass = EntityItem.class;

    public Item(int par1)
    {
        this.itemID = 256 + par1;

        // FCMOD: Code added
        if ( !suppressConflictWarnings)
    	// END FCMOD
        if (itemsList[256 + par1] != null)
        {
            System.out.println("CONFLICT @ " + par1);
        }

        itemsList[256 + par1] = this;
    }

    public Item setMaxStackSize(int par1)
    {
        this.maxStackSize = par1;
        return this;
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    @Environment(EnvType.CLIENT)
    public int getSpriteNumber()
    {
        return 1;
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int par1)
    {
        return this.itemIcon;
    }

    /**
     * Returns the icon index of the stack given as argument.
     */
    @Environment(EnvType.CLIENT)
    public final Icon getIconIndex(ItemStack par1ItemStack)
    {
    	return CITUtils.getIcon(this.getIconFromDamage(par1ItemStack.getItemDamage()), par1ItemStack, 0);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    /**
     * Returns the maximum size of the stack for a specific item. *Isn't this more a Set than a Get?*
     */
    public int getItemStackLimit()
    {
        return this.maxStackSize;
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int par1)
    {
        return 0;
    }

    public boolean getHasSubtypes()
    {
        return this.hasSubtypes;
    }

    protected Item setHasSubtypes(boolean par1)
    {
        this.hasSubtypes = par1;
        return this;
    }

    /**
     * Returns the maximum damage an item can take.
     */
    public int getMaxDamage()
    {
        return this.maxDamage;
    }

    /**
     * set max damage of an Item
     */
    protected Item setMaxDamage(int par1)
    {
        this.maxDamage = par1;
        return this;
    }

    public boolean isDamageable()
    {
        return this.maxDamage > 0 && !this.hasSubtypes;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
        return false;
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving)
    {
        return false;
    }

    /**
     * Returns the damage against a given entity.
     */
    public int getDamageVsEntity(Entity par1Entity)
    {
        return 1;
    }

    /**
     * Called when a player right clicks an entity with an item.
     */
    public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving)
    {
        return false;
    }

    /**
     * Sets bFull3D to True and return the object.
     */
    public Item setFull3D()
    {
        this.full3D = true;
        return this;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Environment(EnvType.CLIENT)
    public boolean isFull3D()
    {
        return this.full3D;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @Environment(EnvType.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return false;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public Item setUnlocalizedName(String par1Str)
    {
        this.unlocalizedName = par1Str;
        return this;
    }

    /**
     * Gets the localized name of the given item stack.
     */
    public String getLocalizedName(ItemStack par1ItemStack)
    {
        String var2 = this.getUnlocalizedName(par1ItemStack);
        return var2 == null ? "" : StatCollector.translateToLocal(var2);
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return "item." + this.unlocalizedName;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "item." + this.unlocalizedName;
    }

    public Item setContainerItem(Item par1Item)
    {
        this.containerItem = par1Item;
        return this;
    }

    /**
     * If this returns true, after a recipe involving this item is crafted the container item will be added to the
     * player's inventory instead of remaining in the crafting grid.
     */
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    public boolean getShareTag()
    {
        return true;
    }

    public Item getContainerItem()
    {
        return this.containerItem;
    }

    /**
     * True if this Item has a container item (a.k.a. crafting result)
     */
    public boolean hasContainerItem()
    {
        return this.containerItem != null;
    }

    public String getStatName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
    }

    public String func_77653_i(ItemStack par1ItemStack)
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName(par1ItemStack) + ".name");
    }

    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return 16777215;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate( ItemStack stack, World world, EntityPlayer entity, int iInventorySlot, boolean bIsHandHeldItem ) {}

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {}

    /**
     * false for all Items except sub-classes of ItemMapBase
     */
    public boolean isMap()
    {
        return false;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.none;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 0;
    }

    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4) {}

    /**
     * Sets the string representing this item's effect on a potion when used as an ingredient.
     */
    public Item setPotionEffect(String par1Str)
    {
        this.potionEffect = par1Str;
        return this;
    }

    /**
     * Returns a string representing what this item does to a potion.
     */
    public String getPotionEffect()
    {
        return this.potionEffect;
    }

    /**
     * Returns true if this item serves as a potion ingredient (its ingredient information is not null).
     */
    public boolean isPotionIngredient()
    {
        return this.potionEffect != null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Environment(EnvType.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {}

    public String getItemDisplayName(ItemStack par1ItemStack)
    {
        return ("" + StringTranslate.getInstance().translateNamedKey(this.getLocalizedName(par1ItemStack))).trim();
    }

    @Environment(EnvType.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemEnchanted();
    }

    /**
     * Return an item rarity from EnumRarity
     */
    @Environment(EnvType.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return this.getItemStackLimit() == 1 && this.isDamageable();
    }

    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float var4 = 1.0F;
        float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
        float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
        double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)var4;
        double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par2EntityPlayer.yOffset;
        double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)var4;
        Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = 5.0D;
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return par1World.rayTraceBlocks_do_do(var13, var23, par3, !par3);
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 0;
    }

    @Environment(EnvType.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return this.getIconFromDamage(par1);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Environment(EnvType.CLIENT)
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    @Environment(EnvType.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return this.tabToDisplayOn;
    }

    /**
     * returns this;
     */
    public Item setCreativeTab(CreativeTabs par1CreativeTabs)
    {
        this.tabToDisplayOn = par1CreativeTabs;
        return this;
    }

    public boolean func_82788_x()
    {
        return true;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(this.unlocalizedName);
    }

    static
    {
        StatList.initStats();
    }

	// FCMOD: Added New
    public static boolean suppressConflictWarnings = false;
    
    /**
     * Method which replaces canPlaceItemBlockOnSide() in ItemBlock. Allows the client to prevent item usage before it is relayed
     * to the server by returning false.  Only called on client.
     */    
    public boolean canItemBeUsedByPlayer(World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack)
    {
    	return true;    	
    }
    
    public boolean doZombiesConsume()
    {
    	return false;
    }
    
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
        return false;
    }
    
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        return false;
    }
    
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        return 1F;
    }
    
    public boolean isMultiUsePerClick()
    {
    	return true;
    }
    
    public float getExhaustionOnUsedToHarvestBlock(int iBlockID, World world, int i, int j, int k, int iBlockMetadata)
    {
    	return 0.025F; // standard default exhaustion amount
    }
    
    public void initializeStackOnGiveCommand(Random rand, ItemStack stack)
    {
    }
    
    public void updateUsingItem(ItemStack stack, World world, EntityPlayer player)
    {
    }
    
    public int getItemUseWarmupDuration()
    {
    	return 7;
    }
    
    public boolean ignoreDamageWhenComparingDuringUse()
    {
    	return false;
    }
	
    //------------ Addon interfacing related functionality ----------//

	/**
	 * Replaces a reference to an existing item (vanilla or btw)
	 * @param id The block id to be replaced
	 * @param newClass The class of the new item
	 * @param parameters Optional additional parameters to pass to the block, not including the id.
	 */
    @Deprecated
	public static Item replaceItem(int id, Class newClass, BTWAddon addonPerformingReplacement, Object ... parameters) {
		return replaceItem(id, newClass, new String[] {}, addonPerformingReplacement, parameters);
	}

	/**
	 * Replaces a reference to an existing item (vanilla or btw), with a list of valid addons for which to be able to overwrite their replacement
	 * @param id The block id to be replaced
	 * @param newClass The class of the new item
	 * @param validAddonNamesForOverwrite An array of addon names which should be ignored when handling conflicts
	 * @param parameters Optional additional parameters to pass to the block, not including the id.
	 */
    @Deprecated
    public static Item replaceItem(int id, Class newClass, String[] validAddonNamesForOverwrite, BTWAddon addonPerformingReplacement, Object ... parameters) {
    	if (itemReplaced[id]) {
    		String replacedBy = itemReplacedBy[id];
			boolean isValidOverwrite = false;
			
			for (String addonName : validAddonNamesForOverwrite) {
				if (replacedBy.equals(addonName)) {
					isValidOverwrite = true;
				}
			}
			
			if (!isValidOverwrite) {
				throw new RuntimeException("Multiple addons attempting to replace item " + itemsList[id]);
			}
    	}

        suppressConflictWarnings = true;

    	Item newItem = null;

    	Class[] parameterTypes = new Class[parameters.length + 1];
    	Object[] parameterValues = new Object[parameters.length + 1];

    	parameterTypes[0] = Integer.TYPE;
    	parameterValues[0] = id - 256;

    	Item original = itemsList[id];
    	itemsList[id] = null;

    	for (int i = 0; i < parameters.length; i++) {
    		Class<?> type = parameters[i].getClass();

    		Class<?> primitiveType = ReflectionUtils.getPrimitiveFromBoxedClass(type);

    		if (primitiveType != null) {
    			type = primitiveType;
    		}

    		parameterTypes[i + 1] = type;
    		parameterValues[i + 1] = parameters[i];
    	}

    	Constructor constructorToUse = ReflectionUtils.findMatchingConstructor(newClass, parameterTypes);

    	if (constructorToUse == null) {
    		String message = "No appropriate constructor found for " + itemsList[id] + ": ";

    		for (Class<?> paramType : parameterTypes) {
    			message += paramType.getSimpleName() + ", ";
    		}

    		throw new RuntimeException(message);
    	}

    	try {
    		constructorToUse.setAccessible(true);
    		newItem = (Item) constructorToUse.newInstance(parameterValues);
    	} catch (InstantiationException e) {
    		throw new RuntimeException("A problem has occured attempting to instantiate replacement for " + itemsList[id]);
    	} catch (IllegalArgumentException e) {
    		throw new RuntimeException("Incompatible types passed to specified constructor for " + itemsList[id]);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	itemReplaced[id] = true;

    	if (addonPerformingReplacement != null) {
    		itemReplacedBy[id] = addonPerformingReplacement.getName();
    	}
    	else {
    		AddonHandler.logWarning("Deprecated item replacement call for item " + itemsList[id] + ". Please attach addon when handling replacement for mutual replacement handling.");
    		itemReplacedBy[id] = "";
    	}

    	newItem.setFilterableProperties(original.filterablePropertiesBitfield).setBuoyancy(original.getBuoyancy(0)).setCreativeTab(original.getCreativeTab());

    	if (original.isIncineratedInCrucible())
    		newItem.setIncineratedInCrucible();
    	else
    		newItem.setNotIncineratedInCrucible();

    	itemsList[id] = newItem;

        suppressConflictWarnings = false;

    	return newItem;
    }

    //----------- Animal Food related functionality -----------//
    
    public static final int BASE_HERBIVORE_ITEM_FOOD_VALUE = (EntityAnimal.BASE_GRAZE_FOOD_VALUE * 32 );
    public static final int BASE_PIG_ITEM_FOOD_VALUE = (EntityAnimal.BASE_GRAZE_FOOD_VALUE * 32 );
    public static final int BASE_CHICKEN_ITEM_FOOD_VALUE = (EntityAnimal.BASE_GRAZE_FOOD_VALUE * 32 );

    private int herbivoreFoodValue = 0;
    private int birdFoodValue = 0;
    private int pigFoodValue = 0;
    
    public int getHerbivoreFoodValue(int iItemDamage)
    {
    	return herbivoreFoodValue;
    }
    
    public Item setHerbivoreFoodValue(int iFoodValue)
    {
        herbivoreFoodValue = iFoodValue;
    	
    	return this;
    }
    
    public Item setAsBasicHerbivoreFood()
    {
    	return setHerbivoreFoodValue(BASE_HERBIVORE_ITEM_FOOD_VALUE);
    }
    
    public int getChickenFoodValue(int iItemDamage)
    {
    	return birdFoodValue;
    }
    
    public Item setChickenFoodValue(int iFoodValue)
    {
        birdFoodValue = iFoodValue;
    	
    	return this;
    }
    
    public Item setAsBasicChickenFood()
    {
    	return setChickenFoodValue(BASE_CHICKEN_ITEM_FOOD_VALUE);
    }
    
    public int getPigFoodValue(int iItemDamage)
    {
    	return pigFoodValue;
    }
    
    public Item setPigFoodValue(int iFoodValue)
    {
        pigFoodValue = iFoodValue;
    	
    	return this;
    }
    
    public Item setAsBasicPigFood()
    {
    	return setPigFoodValue(BASE_PIG_ITEM_FOOD_VALUE);
    }
    
    public boolean isWolfFood()
    {
    	return false;
    }
    
    public int getWolfHealAmount()
    {
    	return 0;
    }
    
    //------------- Buoyancy related functionality ------------//
    
	private float buoyancy = -1.0F;
	
    public Item setBuoyancy(float fBuoyancy)
    {
        buoyancy = fBuoyancy;
    	
    	return this;
    }
    
    public Item setBuoyant() { return setBuoyancy(1F); }
    public Item setNonBuoyant() { return setBuoyancy(-1F); }
    public Item setNeutralBuoyant() { return setBuoyancy(0F); }
    
    public float getBuoyancy(int iItemDamage)
    {
    	return buoyancy;
    }
    
    public int getWeightWhenWorn()
    {
    	return 0;
    }
    
    //------------- Bellows related functionality ------------//
    
	private int bellowsBlowDistance = 0;
	
	/**
	 * 3 = light powders or light large surface objects like paper or bat wings
	 * 2 = seeds, dyes, fibers, chunkier powders like ground netherrack or sand, 
	 * 	   heavier sheets like bark or wicker
	 * 1 = fabric or wool, small leather like straps, arrows, heavier small mobsdrops like creeper 
	 * 	   oysters, and witch wart, dirt and gravel, heavy seeds like cocoa beans and netherwart
	 * 0 = everything else
	 */
    public Item setBellowsBlowDistance(int iDistance)
    {
        bellowsBlowDistance = iDistance;
    	
    	return this;
    }
    
    public int getBellowsBlowDistance(int iItemDamage)
    {
    	return bellowsBlowDistance;
    }
    
    //------------- Enchanting related functionality ------------//
    
	private int infernalMaxNumEnchants = 0;
	private int infernalMaxEnchantmentCost = 0;
	
    public Item setInfernalMaxNumEnchants(int iMaxNumEnchants)
    {
        infernalMaxNumEnchants = iMaxNumEnchants;
    	
    	return this;
    }
    
    public int getInfernalMaxNumEnchants()
    {
    	return infernalMaxNumEnchants;
    }    
    
    public Item setInfernalMaxEnchantmentCost(int iMaxEnchantmentCost)
    {
        infernalMaxEnchantmentCost = iMaxEnchantmentCost;
    	
    	return this;
    }
    
    public int getInfernalMaxEnchantmentCost()
    {
    	return infernalMaxEnchantmentCost;
    }
    
    public boolean isEnchantmentApplicable(Enchantment enchantment)
    {
    	return enchantment.type == EnumEnchantmentType.all;
    }
    
    //------------- Crafting related functionality ------------//    
    
    protected int defaultFurnaceBurnTime = 0;
    protected boolean isInceratedInCrucible = false;
    
    public boolean isConsumedInCrafting()
    {
    	return true;
    }
    
    public boolean isDamagedInCrafting()
    {
    	return false;
    }    
    
    public void onUsedInCrafting(int iItemDamage, EntityPlayer player, ItemStack outputStack)
    {
    	onUsedInCrafting(player, outputStack);
    }
    
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
    }
    
    public void onDamagedInCrafting(EntityPlayer player)
    {
    }
    
    public void onBrokenInCrafting(EntityPlayer player)
    {
    }
    
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	return defaultFurnaceBurnTime;
    }
    
    public Item setfurnaceburntime(int iBurnTime)
    {
        defaultFurnaceBurnTime = iBurnTime;
    	
    	return this;
    }
    
    public Item setfurnaceburntime(FurnaceBurnTime burnTime)
    {
    	setfurnaceburntime(burnTime.burnTime);
    	
    	return this;
    }
    
    public int getCampfireBurnTime(int iItemDamage)
    {
    	return getFurnaceBurnTime(iItemDamage);
    }
    
    /**
     * Used to override default activation behavior on certain blocks like accessing inventory on furnace and campfires 
     */
    public boolean getCanItemStartFireOnUse(int iItemDamage)
    {
    	return false;
    }    
    
    /**
     * Used to override default activation behavior on certain blocks like accessing inventory on furnace and campfires 
     */
    public boolean getCanItemBeSetOnFireOnUse(int iItemDamage)
    {
    	return false;
    }
    
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
		return !getCanItemBeSetOnFireOnUse(iItemDamage) && !getCanItemStartFireOnUse(iItemDamage) &&
               getCampfireBurnTime(iItemDamage) > 0;
    }
    
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage)
    {
		return !getCanItemBeSetOnFireOnUse(iItemDamage) && !getCanItemStartFireOnUse(iItemDamage) &&
               getFurnaceBurnTime(iItemDamage) > 0;
    }
    
    public boolean isIncineratedInCrucible()
    {
    	return isInceratedInCrucible;
    }
    
    public Item setIncineratedInCrucible()
    {
        isInceratedInCrucible = true;
    	
    	return this;
    }
    
    public Item setNotIncineratedInCrucible()
    {
        isInceratedInCrucible = false;
    	
    	return this;
    }
    
    public boolean doesConsumeContainerItemWhenCrafted(Item containerItem)
    {
    	return false;
    }
    
    //------------- Hopper Filtering Functionality -----------//
    
    protected int filterablePropertiesBitfield = 0;
    
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	return true;
    }
    
    public int getFilterableProperties(ItemStack stack)
    {
    	return filterablePropertiesBitfield;
    }
    
    public Item setFilterableProperties(int iProperties)
    {
        filterablePropertiesBitfield = iProperties;
    	
    	return this;
    }
    
    //------------- Deprecated tool functionality ------------//
    
    public static void setAllPicksToBeEffectiveVsBlock(Block block)
    {
    	block.setPicksEffectiveOn(true);
    }
    
    public static void setAllAxesToBeEffectiveVsBlock(Block block)
    {
    	block.setAxesEffectiveOn(true);
    }
    
    public static void setAllShovelsToBeEffectiveVsBlock(Block block)
    {
    	block.setShovelsEffectiveOn(true);
    }
    
    //----- Block Dispenser Related Functionality -----//
    
	/**
	 * This method should return true if the item is successfully placed (in which case the BD will 
	 * consume the corresponding item in its inventory), false otherwise.  Co-ordinates specify
	 * the BD position.
	 */
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
		BTWBlocks.blockDispenser.spitOutItem(world, i, j, k, stack);
		
        world.playAuxSFX( 1000, i, j, k, 0 ); // normal pitch click							        
        
		return true;
	}
    
    //------- Item Entity Related Functionality -------//
    
    public boolean hasCustomItemEntity() {
    	return this.entityClass != EntityItem.class;
    }
    
    public Class getCustomItemEntity() {
    	return this.entityClass;
    }
    
    public void setCustomItemEntity(Class entityClass) {
    	this.entityClass = entityClass;
    }
    
    public EntityItem createItemAsEntityInWorld(World world, double x, double y, double z, ItemStack stack) {
    	return null;
    }
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
    	return null;
    }
    
	//------- Animated Icon Related Functionality -------//

    /**
     * Used to change the item icon the player is holding. Used in bow pulling and casting fishing rod 
     */
	public Icon getAnimationIcon(EntityPlayer player) {
		return null;
	}
    
    // END FCMOD    
}
