package btw.block;

import btw.BTWMod;
import btw.block.blocks.*;
import btw.block.blocks.legacy.LegacyCandleBlock;
import btw.block.blocks.legacy.LegacyCornerBlock;
import btw.block.blocks.legacy.LegacyFarmlandBlockFertilized;
import btw.block.blocks.legacy.LegacySidingBlock;
import btw.block.material.CementMaterial;
import btw.block.material.NetherGrothMaterial;
import btw.block.material.SoulforgedSteelMaterial;
import btw.block.stepsound.SquishStepSound;
import btw.block.tileentity.*;
import btw.block.tileentity.beacon.BeaconTileEntity;
import btw.block.tileentity.dispenser.*;
import btw.util.ColorUtils;
import btw.world.feature.trees.grower.TreeGrowers;
import net.minecraft.src.*;

public class BTWBlocks {
    public static final Material cementMaterial = (new CementMaterial(MapColor.stoneColor));
    public static final Material soulforgedSteelMaterial = (new SoulforgedSteelMaterial(MapColor.ironColor));
    public static final Material netherGrothMaterial = (new NetherGrothMaterial(MapColor.foliageColor)).setMobsCantSpawnOn();
    public static final Material candleMaterial = (new MaterialLogic(MapColor.airColor)).setNoPushMobility().setDoesNotBreakSaw().setRequiresTool();
    public static final Material legacyCandleMaterial = (new MaterialLogic(MapColor.airColor)).setNoPushMobility().setDoesNotBreakSaw();
    public static final Material miscMaterial = new Material(MapColor.dirtColor); // used for aesthetic blocks to avoid tool dependancies
    public static final Material logMaterial = (new Material(MapColor.woodColor)).setBurning().setRequiresTool().setMobsCantSpawnOn().setDoesNotBreakSaw();
    public static final Material plankMaterial = (new Material(MapColor.woodColor)).setBurning().setRequiresTool().setMobsCantSpawnOn().setDoesNotBreakSaw();
    public static final Material ashMaterial = (new MaterialLogic(MapColor.stoneColor)).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility().setDoesNotBreakSaw();
    public static final Material naturalClayMaterial = (new Material(MapColor.clayColor)).setRequiresTool();
    public static final Material wickerMaterial = (new Material(MapColor.woodColor)).setBurning().setRequiresTool().setAxesEfficientOn().setDoesNotBreakSaw().setMobsCantSpawnOn();
    public static final Material basketMaterial = (new Material(MapColor.woodColor)).setDoesNotBreakSaw().setMobsCantSpawnOn();
    public static final Material milkMaterial = (new MaterialLogic(MapColor.snowColor)).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility();
    public static final Material netherRockMaterial = (new Material(MapColor.stoneColor)).setRequiresTool().setNetherMobsCanSpawnOn();
    public static final Material bloodMaterial =  (new MaterialLiquid(MapColor.tntColor)).setNoPushMobility();

    public static StepSound stepSoundSquish = new SquishStepSound("squish", 0.5F, 0.1F);

    public static Block sandAndGravelSlab;

    public static Block arcaneVessel;

    public static Block axlePowerSource;

    public static Block blackStoneSidingAndCorner;
    public static Block blackStoneMouldingAndDecorative;

    public static Block aestheticEarth;

    public static Block legacyCandle;

    public static Block sandstoneSidingAndCorner;
    public static Block sandstoneMouldingAndDecorative;

    public static Block oakWoodSidingAndCorner;

    public static Block stoneSidingAndCorner;

    public static Block brickSidingAndCorner;
    public static Block brickMouldingAndDecorative;

    public static Block netherBrickSidingAndCorner;
    public static Block netherBrickMouldingAndDecorative;

    public static Block whiteStoneStairs;
    public static Block whiteStoneSidingAndCorner;
    public static Block whiteStoneMouldingAndDecroative;

    public static Block stakeString;
    public static Block stake;

    public static Block screwPump;

    public static Block spruceWoodSidingAndCorner;
    public static Block spruceWoodMouldingAndDecorative;

    public static Block birchWoodSidingAndCorner;
    public static Block birchWoodMouldingAndDecorative;

    public static Block jungleWoodSidingAndCorner;
    public static Block jungleWoodMouldingAndDecorative;

    public static Block stoneBrickSidingAndCorner;
    public static Block stoneBrickMouldingAndDecorative;

    public static Block legacyFertilizedFarmland;

    public static Block woolSlabTop;

    public static DirtSlabBlock dirtSlab;

    public static Block netherGroth;

    public static Block infernalEnchanter;

    public static Block soulforgedSteelBlock;

    public static Block glowingDetectorLogic;

    public static Block bloodWoodLeaves;

    public static Block bloodWoodLog;

    public static Block aestheticVegetation;

    public static Block stoneMouldingAndDecorative;

    public static Block aestheticOpaque;
    public static Block aestheticNonOpaque;

    public static Block miningCharge;

    public static Block buddyBlock;

    public static KilnBlock kiln;

    public static Block woolSlab;

    public static Block soulforge;

    public static Block lightBlockOff;
    public static Block lightBlockOn;

    public static HibachiBlock hibachi;

    public static Block hopper;

    public static Block saw;

    public static Block platform;

    public static Block cement;

    public static Block pulley;

    public static Block steelPressurePlate;

    public static Block oakWoodMouldingAndDecorative;
    public static Block legacyStoneAndOakCorner;

    public static BlockDispenserBlock blockDispenser;

    public static Block cauldron;

    public static Block woodenDetectorRail;
    public static Block steelDetectorRail;

    public static Block companionCube;

    public static Block detectorBlock;
    public static Block detectorLogic;

    public static Block lens;

    public static Block hempCrop;

    public static Block handCrank;

    public static Block millstone;

    public static Block anchor;

    public static Block ropeBlock;

    public static Block legacyStoneAndOakSiding;

    public static Block axle;

    public static RedstoneClutchBlock redstoneClutch;

    public static Block turntable;

    public static Block bellows;

    public static Block stokedFire;

    public static Block unfiredPottery;

    public static Block crucible;
    public static Block planter;
    public static Block vase;

    // extended blocks

    public static Block rottenFleshBlock;
    public static Block placedShaft;
    public static Block dormandSoulforge;

    public static Block stoneStairs;
    public static Block rottenFleshSlab;
    public static Block boneSlab;
    public static Block freshPumpkin;

    public static Block bloodWoodSidingAndCorner;
    public static Block bloodWoodMouldingAndDecorative;
    public static Block bloodWoodStairs;

    public static ChewedLogBlock oakChewedLog;
    public static ChewedLogBlock spruceChewedLog;
    public static ChewedLogBlock birchChewedLog;
    public static ChewedLogBlock jungleChewedLog;

    public static Block looseDirt;
    public static Block looseDirtSlab;

    public static CampfireBlock unlitCampfire;
    public static CampfireBlock smallCampfire;
    public static CampfireBlock mediumCampfire;
    public static CampfireBlock largeCampfire;

    public static UnfiredBrickBlock placedUnfiredBrick;
    public static Block placedBrick;
    public static Block looseBrick;
    public static Block looseBrickSlab;

    public static Block looseCobblestone;
    public static Block looseCobblestoneSlab;

    public static Block idleOven;
    public static Block burningOven;

    public static Block finiteUnlitTorch;
    public static Block finiteBurningTorch;

    public static Block upperStrataRoughStone;
    public static Block midStrataRoughStone;
    public static Block deepStrataRoughStone;

    public static Block workStump;
    public static WickerBasketBlock wickerBasket;
    public static LogSpikeBlock oakLogSpike;
    public static LogSpikeBlock spruceLogSpike;
    public static LogSpikeBlock birchLogSpike;
    public static LogSpikeBlock jungleLogSpike;
    public static Block infiniteUnlitTorch;

    public static Block workbench;
    public static Block chest;
    public static Block woodenDoor;
    public static Block web;

    public static Block unfiredClay;
    public static MyceliumSlabBlock myceliumSlab;
    public static PlacedToolBlock placedTool;

    public static Block looseBrickStairs;
    public static Block looseCobblestoneStairs;

    public static SmolderingLogBlock smolderingLog;
    public static WoodCindersBlock woodCinders;
    public static CharredStumpBlock charredStump;
    public static Block ashCoverBlock;

    public static LooseSnowBlock looseSnow;
    public static LooseSnowSlabBlock looseSnowSlab;
    public static SolidSnowBlock solidSnow;
    public static SolidSnowSlabBlock solidSnowSlab;

    public static LadderBlock ladder;
    public static LadderBlockFlaming flamingLadder;

    public static PistonShovelBlock pistonShovel;
    public static HamperBlock hamper;

    public static CreeperOysterBlock creeperOysterBlock;
    public static CreeperOysterSlabBlock creeperOysterSlab;

    public static InfiniteBurningTorchBlock infiniteBurningTorch;

    public static BucketBlock placedBucket;
    public static BucketBlockWater placedWaterBucket;
    public static BucketBlockCement placedCementBucket;
    public static BucketBlockMilk placedMilkBucket;
    public static BucketBlockChocolateMilk placedMilkChocolateBucket;

    public static MilkBlock milkFluid;
    public static ChocolateMilkBlock chocolateMilkFluid;

    public static GearBoxBlock gearBox;

    public static MetalSpikeBlock ironSpike;
    public static LightningRodBlock lightningRod;

    public static OreChunkBlockIron ironOreChunk;
    public static OreChunkBlockGold goldOreChunk;

    public static Block looseStoneBrick;
    public static Block looseStoneBrickSlab;
    public static Block looseStoneBrickStairs;

    public static Block looseNetherBrick;
    public static Block looseNetherBrickSlab;
    public static Block looseNEtherBrickStairs;

    public static NetherrackBlockFalling fallingNetherrack;

    public static LavaPillowBlock lavaPillow;

    public static MushroomCapBlock brownMushroomCap;
    public static MushroomCapBlock redMushroomCap;

    public static OreChunkStorageBlockIron ironOreChunkStorage;
    public static OreChunkStorageBlockGold goldOreChunkStorage;

    public static WickerBlock wickerBlock;
    public static WickerSlabBlock wickerSlab;
    public static WickerPaneBlock wickerPane;

    public static GrateBlock gratePane;
    public static SlatsBlock slatsPane;

    public static FarmlandBlock farmland;
    public static FarmlandBlockFertilized fertilizedFarmland;
    public static WheatCropBlock wheatCrop;
    public static WheatCropTopBlock wheatCropTop;
    public static WeedsBlock weeds;
    public static PlanterBlockSoil planterWithSoil;

    public static Block sugarCane;
    public static Block sugarCaneRoots;

    public static Block carrotCrop;
    public static Block floweringCarrotCrop;

    public static Block structureVoid;

    public static Block plainCandle;
    public static Block[] coloredCandle;

    public static SilverfishBlock infestedStone;
    public static SilverfishBlock infestedMidStrataStone;
    public static SilverfishBlock infestedDeepStrataStone;
    public static SilverfishBlock infestedCobblestone;
    public static SilverfishBlock infestedStoneBrick;
    public static SilverfishBlock infestedMossyStoneBrick;
    public static SilverfishBlock infestedCrackedStoneBrick;
    public static SilverfishBlock infestedChiseledStoneBrick;

    public static GrassSlabBlock grassSlab;

    public static Block bedroll;

    public static BlockHalfSlab cobblestoneSlab;
    public static BlockHalfSlab cobblestoneDoubleSlab;
    public static BlockHalfSlab stoneBrickSlab;
    public static BlockHalfSlab stoneBrickDoubleSlab;
    public static BlockHalfSlab stoneSlab;
    public static BlockHalfSlab stoneDoubleSlab;

    public static Block midStrataCobblestoneStairs;
    public static Block deepStrataCobblestoneStairs;
    public static Block looseMidStrataCobblestoneStairs;
    public static Block looseDeepStrataCobblestoneStairs;

    public static Block midStrataStoneBrickStairs;
    public static Block deepStrataStoneBrickStairs;
    public static Block looseMidStrataStoneBrickStairs;
    public static Block looseDeepStrataStoneBrickStairs;

    public static Block midStrataStoneStairs;
    public static Block deepStrataStoneStairs;

    public static Block midStrataStoneBrickSidingAndCorner;
    public static Block midStrataStoneBrickMouldingAndDecorative;
    public static Block deepStrataStoneBrickSidingAndCorner;
    public static Block deepStrataStoneBrickMouldingAndDecorative;

    public static Block midStrataStoneSidingAndCorner;
    public static Block midStrataStoneMouldingAndDecorative;
    public static Block deepStrataStoneSidingAndCorner;
    public static Block deepStrataStoneMouldingAndDecorative;

    public static SilverfishBlock infestedMidStrataCobblestone;
    public static SilverfishBlock infestedDeepstrataCobblestone;

    public static SilverfishBlock infestedMidStrataStoneBrick;
    public static SilverfishBlock infestedMidStrataMossyStoneBrick;
    public static SilverfishBlock infestedMidStrataCrackedStoneBrick;
    public static SilverfishBlock infestedMidStrataChiseledStoneBrick;

    public static SilverfishBlock infestedDeepStrataStoneBrick;
    public static SilverfishBlock infestedDeepStrataMossyStoneBrick;
    public static SilverfishBlock infestedDeepStrataCrackedStoneBrick;
    public static SilverfishBlock infestedDeepStrataChiseledStoneBrick;
    
    public static Block spiderEyeBlock;
    public static Block spiderEyeSlab;
    
    public static Block oakSapling;
    public static Block spruceSapling;
    public static Block birchSapling;
    public static Block jungleSapling;
    
    public static LoomBlock loom;
    
    public static Block looseSparseGrass;
    public static Block looseSparseGrassSlab;
    
    public static boolean[] potentialFluidSources = new boolean[4096];
    
    public static void instantiateModBlocks()
    {
        sandAndGravelSlab = new SandAndGravelSlabBlock(BTWMod.instance.parseID("fcBlockSlabFallingID"));

        arcaneVessel = new ArcaneVesselBlock(BTWMod.instance.parseID("fcBlockArcaneVesselID"));

        axlePowerSource = new AxlePowerSourceBlock(BTWMod.instance.parseID("fcBlockAxlePowerSourceID"));

        blackStoneSidingAndCorner = (new SidingAndCornerAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockSidingAndCornerBlackStoneID"),
                Material.rock, "fcBlockDecorativeBlackStone", 1.5F, 10F,Block.soundStoneFootstep, "fcBlockSidingBlackStone")).setPicksEffectiveOn();

        blackStoneMouldingAndDecorative = (new MouldingAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockMouldingAndDecorativeBlackStoneID"), Material.rock,
                "fcBlockDecorativeBlackStone", "fcBlockColumnBlackStone_side",
                blackStoneSidingAndCorner.blockID, 1.5F, 10F, Block.soundStoneFootstep, "fcBlockMouldingBlackStone")).setPicksEffectiveOn();

        aestheticEarth = new AestheticOpaqueEarthBlock(BTWMod.instance.parseID("fcBlockAestheticOpaqueEarthID"));

        legacyCandle = new LegacyCandleBlock(BTWMod.instance.parseID("fcBlockCandleID"));

        sandstoneSidingAndCorner = (new SandstoneSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSandstoneSidingAndCornerID"))).setPicksEffectiveOn();
        sandstoneMouldingAndDecorative = (new SandstoneMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSandstoneMouldingAndDecorativeID"),
                sandstoneSidingAndCorner.blockID)).setPicksEffectiveOn();

        oakWoodSidingAndCorner = new WoodSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodOakSidingAndCornerID"),
                "FCBlockDecorativeWoodOak", "fcBlockWoodOakSiding");

        stoneSidingAndCorner = (new SidingAndCornerAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockSmoothStoneSidingAndCornerID"),
                Material.rock, "fcBlockDecorativeStone", 1.5F, 10F, Block.soundStoneFootstep, "fcStoneSiding")).setPicksEffectiveOn();

        brickSidingAndCorner = (new SidingAndCornerAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockBrickSidingAndCornerID"),
                Material.rock, "fcBlockDecorativeBrick", 2.0F, 10F, Block.soundStoneFootstep, "fcBrickSiding")).setPicksEffectiveOn();

        brickMouldingAndDecorative = (new MouldingAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockBrickMouldingAndDecorativeID"),
                Material.rock, "fcBlockDecorativeBrick", "fcBlockColumnBrick_side",
                brickSidingAndCorner.blockID, 2.0F, 10F, Block.soundStoneFootstep, "fcBrickMoulding")).setPicksEffectiveOn();

        netherBrickSidingAndCorner = (new NetherBrickSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockNetherBrickSidingAndCornerID"))).setPicksEffectiveOn();

        netherBrickMouldingAndDecorative = (new NetherBrickMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockNetherBrickMouldingAndDecorativeID"),
                netherBrickSidingAndCorner.blockID)).setPicksEffectiveOn();

        whiteStoneStairs = new WhiteStoneStairsBlock(BTWMod.instance.parseID("fcBlockWhiteStoneStairsID"));

        whiteStoneSidingAndCorner = (new SidingAndCornerAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockWhiteStoneSidingAndCornerID"),
                Material.rock, "fcBlockDecorativeWhiteStone", 1.5F, 10F, Block.soundStoneFootstep, "fcWhiteStoneSiding")).setPicksEffectiveOn();

        whiteStoneMouldingAndDecroative = (new MouldingAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockWhiteStoneMouldingAndDecorativeID"),
                Material.rock, "fcBlockDecorativeWhiteStone", "fcBlockColumnWhiteStone_side",
                whiteStoneSidingAndCorner.blockID, 1.5F, 10F, Block.soundStoneFootstep, "fcWhiteStoneMoulding")).setPicksEffectiveOn();

        stakeString = new StakeStringBlock(BTWMod.instance.parseID("fcBlockStakeStringID"));
        stake = new StakeBlock(BTWMod.instance.parseID("fcBlockStakeID"));

        screwPump = new ScrewPumpBlock(BTWMod.instance.parseID("fcBlockScrewPumpID"));

        spruceWoodSidingAndCorner = new WoodSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodSpruceSidingAndCornerID"), "fcBlockDecorativeWoodSpruce", "fcWoodSpruceSiding");

        spruceWoodMouldingAndDecorative = new WoodMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodSpruceMouldingID"),
                "fcBlockDecorativeWoodSpruce", "fcBlockColumnWoodSpruce_side",
                spruceWoodSidingAndCorner.blockID, "fcWoodSpruceMoulding");

        birchWoodSidingAndCorner = new WoodSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodBirchSidingAndCornerID"),
                "fcBlockDecorativeWoodBirch", "fcWoodBirchSiding");

        birchWoodMouldingAndDecorative = new WoodMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodBirchMouldingID"),
                "fcBlockDecorativeWoodBirch", "fcBlockColumnWoodBirch_side",
                birchWoodSidingAndCorner.blockID, "fcWoodBirchMoulding");

        jungleWoodSidingAndCorner = new WoodSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodJungleSidingAndCornerID"),
                "fcBlockDecorativeWoodJungle", "fcWoodJungleSiding");

        jungleWoodMouldingAndDecorative = new WoodMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodJungleMouldingID"),
                "fcBlockDecorativeWoodJungle", "fcBlockColumnWoodJungle_side",
                jungleWoodSidingAndCorner.blockID, "fcWoodJungleMoulding");

        stoneBrickSidingAndCorner = (new SidingAndCornerAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockStoneBrickSidingAndCornerID"),
                Material.rock, "fcBlockDecorativeStoneBrick", 1.5F, 10F,
                Block.soundStoneFootstep, "fcStoneBrickSiding")).setPicksEffectiveOn();

        stoneBrickMouldingAndDecorative = (new MouldingAndDecorativeWallBlock(BTWMod.instance.parseID("fcBlockStoneBrickMouldingID"),
                Material.rock, "fcBlockDecorativeStoneBrick", "fcBlockColumnStoneBrick_side",
                stoneBrickSidingAndCorner.blockID, 1.5F, 10F, Block.soundStoneFootstep, "fcStoneBrickMoulding")).setPicksEffectiveOn();

        legacyFertilizedFarmland = new LegacyFarmlandBlockFertilized(BTWMod.instance.parseID("fcBlockFarmlandFertilizedID"));

        woolSlabTop = new WoolSlabBlock(BTWMod.instance.parseID("fcBlockWoolSlabTopID"), true);

        dirtSlab = new DirtSlabBlock(BTWMod.instance.parseID("fcBlockDirtSlabID"));

        netherGroth = new NetherGrothBlock(BTWMod.instance.parseID("fcBlockNetherGrowthID"));

        infernalEnchanter = new InfernalEnchanterBlock(BTWMod.instance.parseID("fcInfernalEnchanterID"));

        soulforgedSteelBlock = new SoulforgedSteelBlock(BTWMod.instance.parseID("fcSoulforgedSteelBlockID"));

        glowingDetectorLogic = new GlowingDetectorLogicBlock(BTWMod.instance.parseID("fcBlockDetectorGlowingLogicID"));

        bloodWoodLeaves = new BloodWoodLeavesBlock(BTWMod.instance.parseID("fcLeavesID"));

        bloodWoodLog = new BloodWoodLogBlock(BTWMod.instance.parseID("fcBloodWoodID"));

        aestheticVegetation = new AestheticVegetationBlock(BTWMod.instance.parseID("fcAestheticVegetationID"));

        stoneMouldingAndDecorative = (new MouldingAndDecorativeWallBlock(BTWMod.instance.parseID("fcStoneMouldingID"),
                Material.rock, "fcBlockDecorativeStone", "fcBlockColumnStone_side", "fcBlockColumnStone_top", "fcBlockPedestalStone_side", "fcBlockPedestalStone_top",
                stoneSidingAndCorner.blockID, 1.5F, 10F, Block.soundStoneFootstep, "fcStoneMoulding")).setPicksEffectiveOn();

        aestheticOpaque = new AestheticOpaqueBlock(BTWMod.instance.parseID("fcAestheticOpaqueID"));
        aestheticNonOpaque = new AestheticNonOpaqueBlock(BTWMod.instance.parseID("fcAestheticNonOpaqueID"));

        miningCharge = new MiningChargeBlock(BTWMod.instance.parseID("fcMiningChargeID"));

        buddyBlock = new BuddyBlock(BTWMod.instance.parseID("fcBuddyBlockID"));

        kiln = new KilnBlock(BTWMod.instance.parseID("fcKilnID"));

        woolSlab = new WoolSlabBlock(BTWMod.instance.parseID("fcWoolSlabID"), false);

        soulforge = new SoulforgeBlock(BTWMod.instance.parseID("fcAnvilID"));

        lightBlockOff = new LightBlock(BTWMod.instance.parseID("fcLightBulbOffID"), false);
        lightBlockOn = new LightBlock(BTWMod.instance.parseID("fcLightBulbOnID"), true);

        hibachi = new HibachiBlock(BTWMod.instance.parseID("fcBBQID"));

        hopper = new HopperBlock(BTWMod.instance.parseID("fcHopperID"));

        saw = new SawBlock(BTWMod.instance.parseID("fcSawID"));

        platform = new PlatformBlock(BTWMod.instance.parseID("fcPlatformID"));

        cement = new CementBlock(BTWMod.instance.parseID("fcCementID"));

        pulley = new PulleyBlock(BTWMod.instance.parseID("fcPulleyID"));

        steelPressurePlate = (new PressurePlateBlock(BTWMod.instance.parseID("fcPressurePlateObsidianID"),
                "fcBlockPressurePlateSoulforgedSteel", soulforgedSteelMaterial, EnumMobType.players)).setHardness(1.0F).setResistance(2000F).
                setStepSound(Block.soundMetalFootstep).setUnlocalizedName("pressurePlate");

        oakWoodMouldingAndDecorative = new WoodMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcMouldingID"),
                "FCBlockDecorativeWoodOak", "fcBlockColumnWoodOak_side", oakWoodSidingAndCorner.blockID, "fcBlockWoodOakMoulding");

        legacyStoneAndOakCorner = new LegacyCornerBlock(BTWMod.instance.parseID("fcCornerID"));

        blockDispenser = new BlockDispenserBlock(BTWMod.instance.parseID("fcBlockDispenserID"));

        cauldron = new CauldronBlock(BTWMod.instance.parseID("fcCauldronID"));

        woodenDetectorRail = (new DetectorRailBlock(BTWMod.instance.parseID("fcDetectorRailWoodID"))).
                setHardness(0.7F).setStepSound(Block.soundMetalFootstep).setUnlocalizedName("fcBlockDetectorRailWood");

        steelDetectorRail = (new DetectorRailBlock(BTWMod.instance.parseID("fcDetectorRailObsidianID"))).
                setHardness(0.7F).setStepSound(Block.soundMetalFootstep).setUnlocalizedName("fcBlockDetectorRailSoulforgedSteel");

        companionCube = new CompanionCubeBlock(BTWMod.instance.parseID("fcCompanionCubeID"));

        detectorBlock = new DetectorBlock(BTWMod.instance.parseID("fcBlockDetectorID"));
        detectorLogic = (new DetectorLogicBlock(BTWMod.instance.parseID("fcBlockDetectorLogicID"))).setUnlocalizedName("fcBlockDetectorLogic");

        lens = new LensBlock(BTWMod.instance.parseID("fcBlockLensID"));

        hempCrop = new HempCropBlock(BTWMod.instance.parseID("fcHempCropID"));

        handCrank = new HandCrankBlock(BTWMod.instance.parseID("fcHandCrankID"));

        millstone = new MillstoneBlock(BTWMod.instance.parseID("fcMillStoneID"));

        anchor = new AnchorBlock(BTWMod.instance.parseID("fcAnchorID"));

        ropeBlock = new RopeBlock(BTWMod.instance.parseID("fcRopeBlockID"));

        legacyStoneAndOakSiding = new LegacySidingBlock(BTWMod.instance.parseID("fcOmniSlabID"));

        axle = new AxleBlock(BTWMod.instance.parseID("fcAxleBlockID"));

        redstoneClutch = new RedstoneClutchBlock(BTWMod.instance.parseID("fcGearBoxID"));

        turntable = new TurntableBlock(BTWMod.instance.parseID("fcTurntableID"));

        bellows = new BellowsBlock(BTWMod.instance.parseID("fcBellowsID"));

        stokedFire = new StokedFireBlock(BTWMod.instance.parseID("fcStokedFireID"));

        unfiredPottery = new UnfiredPotteryBlock(BTWMod.instance.parseID("fcUnfiredPotteryID"));

        crucible = new CrucibleBlock(BTWMod.instance.parseID("fcCrucibleID"));
        planter = new PlanterBlock(BTWMod.instance.parseID("fcPlanterID"));
        vase = new VaseBlock(BTWMod.instance.parseID("fcVaseID"));

        rottenFleshBlock = new RottenFleshBlock(BTWMod.instance.parseID("fcBlockRottenFleshID"));

        placedShaft = new PlacedShaftBlock(BTWMod.instance.parseID("fcBlockShaftID"));

        dormandSoulforge = new DormantSoulforgeBlock(BTWMod.instance.parseID("fcBlockSoulforgeDormantID"));

        stoneStairs = (new StairsBlock(BTWMod.instance.parseID("fcBlockSmoothstoneStairsID"), Block.stone, 0)).setPicksEffectiveOn().setUnlocalizedName("fcBlockSmoothstoneStairs");

        rottenFleshSlab = new RottenFleshSlabBlock(BTWMod.instance.parseID("fcBlockRottenFleshSlabID"));

        boneSlab = new BoneSlabBlock(BTWMod.instance.parseID("fcBlockBoneSlabID"));

        freshPumpkin = (new PumpkinBlock(BTWMod.instance.parseID("fcBlockPumpkinFreshID"), false));
        ((StemBlock)Block.pumpkinStem).setFruitBlock(freshPumpkin); // do this after pumpkin is instantiated

        bloodWoodSidingAndCorner = new WoodSidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodBloodSidingAndCornerID"),
                "fcBlockDecorativeWoodBlood", "fcWoodBloodSiding");

        bloodWoodMouldingAndDecorative = new WoodMouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockWoodBloodMouldingAndDecorativeID"),
                "fcBlockDecorativeWoodBlood", "fcBlockColumnWoodBlood_side",
                bloodWoodSidingAndCorner.blockID, "fcWoodBloodMoulding");

        bloodWoodStairs = (new WoodStairsBlock(BTWMod.instance.parseID("fcBlockWoodBloodStairsID"),
                Block.planks, 4)).setUnlocalizedName("fcBlockWoodBloodStairs");
    
        oakLogSpike = (LogSpikeBlock) new LogSpikeBlock(BTWMod.instance.parseID("fcBlockLogSpikeID"),
                "fcBlockLogStrippedOak_top", "fcBlockLogStrippedOak_side")
                .setUnlocalizedName("fcBlockLogSpike");
        spruceLogSpike = (LogSpikeBlock) new LogSpikeBlock(BTWMod.instance.parseID("fcBlockLogSpikeSpruceID"),
                "fcBlockLogStrippedSpruce_top", "fcBlockLogStrippedSpruce_side")
                .setUnlocalizedName("fcBlockLogSpikeSpruce");
        birchLogSpike = (LogSpikeBlock) new LogSpikeBlock(BTWMod.instance.parseID("fcBlockLogSpikeBirchID"),
                "fcBlockLogStrippedBirch_top", "fcBlockLogStrippedBirch_side")
                .setUnlocalizedName("fcBlockLogSpikeBirch");
        jungleLogSpike = (LogSpikeBlock) new LogSpikeBlock(BTWMod.instance.parseID("fcBlockLogSpikeJungleID"),
                "fcBlockLogStrippedJungle_top", "fcBlockLogStrippedJungle_side")
                .setUnlocalizedName("fcBlockLogSpikeJungle");

        oakChewedLog = (ChewedLogBlock) new ChewedLogBlock(BTWMod.instance.parseID("fcBlockLogDamagedID"),
                "fcBlockLogStrippedOak_top", "fcBlockLogStrippedOak_side", "fcBlockTrunkTop",
                oakLogSpike)
                .setUnlocalizedName("fcBlockLogDamaged");
        spruceChewedLog = (ChewedLogBlock) new ChewedLogBlock(BTWMod.instance.parseID("fcBlockLogDamagedSpruceID"),
                "fcBlockLogStrippedSpruce_top", "fcBlockLogStrippedSpruce_side", "fcBlockTrunkTopSpruce",
                spruceLogSpike)
                .setUnlocalizedName("fcBlockLogDamagedSpruce");
        birchChewedLog = (ChewedLogBlock) new ChewedLogBlock(BTWMod.instance.parseID("fcBlockLogDamagedBirchID"),
                "fcBlockLogStrippedBirch_top", "fcBlockLogStrippedBirch_side", "fcBlockTrunkTopBirch",
                birchLogSpike)
                .setUnlocalizedName("fcBlockLogDamagedBirch");
        jungleChewedLog = (ChewedLogBlock) new ChewedLogBlock(BTWMod.instance.parseID("fcBlockLogDamagedJungleID"),
                "fcBlockLogStrippedJungle_top", "fcBlockLogStrippedJungle_side", "fcBlockTrunkTopJungle",
                jungleLogSpike)
                .setUnlocalizedName("fcBlockLogDamagedJungle");
        
        LogBlock.chewedLogArray = new Block[] {oakChewedLog, spruceChewedLog, birchChewedLog, jungleChewedLog};

        looseDirt = new LooseDirtBlock(BTWMod.instance.parseID("fcBlockDirtLooseID"));
        looseDirtSlab = new LooseDirtSlabBlock(BTWMod.instance.parseID("fcBlockDirtLooseSlabID"));

        unlitCampfire = (CampfireBlock)(new CampfireBlock(BTWMod.instance.parseID("fcBlockCampfireUnlitID"), 0)).setCreativeTab(CreativeTabs.tabDecorations);
        smallCampfire = (CampfireBlock)(new CampfireBlock(BTWMod.instance.parseID("fcBlockCampfireSmallID"), 1).setLightValue(0.25F));
        mediumCampfire = (CampfireBlock)(new CampfireBlock(BTWMod.instance.parseID("fcBlockCampfireMediumID"), 2).setLightValue(0.5F));
        largeCampfire = (CampfireBlock)(new CampfireBlock(BTWMod.instance.parseID("fcBlockCampfireLargeID"), 3).setLightValue(0.875F));

        placedUnfiredBrick = new UnfiredBrickBlock(BTWMod.instance.parseID("fcBlockUnfiredBrickID"));
        placedBrick = new FiredBrickBlock(BTWMod.instance.parseID("fcBlockCookedBrickID"));

        looseBrick = new LooseBrickBlock(BTWMod.instance.parseID("fcBlockBrickLooseID"));
        looseBrickSlab = new LooseBrickSlabBlock(BTWMod.instance.parseID("fcBlockBrickLooseSlabID"));

        looseCobblestone = new LooseCobblestoneBlock(BTWMod.instance.parseID("fcBlockCobblestoneLooseID"));
        looseCobblestoneSlab = new LooseCobblestoneSlabBlock(BTWMod.instance.parseID("fcBlockCobblestoneLooseSlabID"));

        idleOven = new OvenBlockIdle(BTWMod.instance.parseID("fcBlockFurnaceBrickIdleID"));
        burningOven = new OvenBlockBurning(BTWMod.instance.parseID("fcBlockFurnaceBrickBurningID"));

        finiteUnlitTorch = new FiniteUnlitTorchBlock(BTWMod.instance.parseID("fcBlockTorchFiniteIdleID"));
        finiteBurningTorch = new FiniteBurningTorchBlock(BTWMod.instance.parseID("fcBlockTorchFiniteBurningID"));

        upperStrataRoughStone = (new RoughStoneBlock(BTWMod.instance.parseID("fcBlockStoneRoughID"), 0));
        midStrataRoughStone = (new RoughStoneBlock(BTWMod.instance.parseID("fcBlockStoneRoughMidStrataID"), 1));
        deepStrataRoughStone = (new RoughStoneBlock(BTWMod.instance.parseID("fcBlockStoneRoughDeepStrataID"), 2));

        workStump = new WorkStumpBlock(BTWMod.instance.parseID("fcBlockWorkStumpID"));

        wickerBasket = new WickerBasketBlock(BTWMod.instance.parseID("fcBlockBasketWickerID"));

        infiniteUnlitTorch = new InfiniteUnlitTorchBlock(BTWMod.instance.parseID("fcBlockTorchIdleID"));

        // flammable versions of vanilla blocks.  Keep flammability attributes out of class so as not to affect original blocks.
        workbench = (new WorkbenchBlock(BTWMod.instance.parseID("fcBlockWorkbenchID"))).setFireProperties(5, 20);
        chest = (new ChestBlock(BTWMod.instance.parseID("fcBlockChestID"))).setFireProperties(5, 20);
        woodenDoor = (new DoorBlockWood(BTWMod.instance.parseID("fcBlockDoorWoodID"))).setFireProperties(5, 20);
        web = (new WebBlock(BTWMod.instance.parseID("fcBlockWebID"))).setFireProperties(60, 100).setCreativeTab(CreativeTabs.tabDecorations);

        unfiredClay = new UnfiredClayBlock(BTWMod.instance.parseID("fcBlockUnfiredClayID"));
        myceliumSlab = new MyceliumSlabBlock(BTWMod.instance.parseID("fcBlockMyceliumSlabID"));
        placedTool = new PlacedToolBlock(BTWMod.instance.parseID("fcBlockToolPlacedID"));

        looseBrickStairs = new LooseBrickStairsBlock(BTWMod.instance.parseID("fcBlockBrickLooseStairsID"));
        looseCobblestoneStairs = new LooseCobblestoneStairsBlock(BTWMod.instance.parseID("fcBlockCobblestoneLooseStairsID"), 0);

        smolderingLog = new SmolderingLogBlock(BTWMod.instance.parseID("fcBlockLogSmoulderingID"));
        woodCinders = new WoodCindersBlock(BTWMod.instance.parseID("fcBlockWoodCindersID"));
        charredStump = new CharredStumpBlock(BTWMod.instance.parseID("fcBlockStumpCharredID"));
        ashCoverBlock = new AshGroundCoverBlock(BTWMod.instance.parseID("fcBlockAshGroundCoverID"));

        looseSnow = new LooseSnowBlock(BTWMod.instance.parseID("fcBlockSnowLooseID"));
        looseSnowSlab = new LooseSnowSlabBlock(BTWMod.instance.parseID("fcBlockSnowLooseSlabID"));
        solidSnow = new SolidSnowBlock(BTWMod.instance.parseID("fcBlockSnowSolidID"));
        solidSnowSlab = new SolidSnowSlabBlock(BTWMod.instance.parseID("fcBlockSnowSolidSlabID"));

        ladder = new LadderBlock(BTWMod.instance.parseID("fcBlockLadderID"));
        flamingLadder = new LadderBlockFlaming(BTWMod.instance.parseID("fcBlockLadderOnFireID"));

        pistonShovel = new PistonShovelBlock(BTWMod.instance.parseID("fcBlockShovelID"));
        hamper = new HamperBlock(BTWMod.instance.parseID("fcBlockHamperID"));

        creeperOysterBlock = new CreeperOysterBlock(BTWMod.instance.parseID("fcBlockCreeperOystersID"));
        creeperOysterSlab = new CreeperOysterSlabBlock(BTWMod.instance.parseID("fcBlockCreeperOystersSlabID"));

        infiniteBurningTorch = (InfiniteBurningTorchBlock)(new InfiniteBurningTorchBlock(BTWMod.instance.parseID("fcBlockTorchNetherBurningID"), true))
                .setCreativeTab(CreativeTabs.tabDecorations);

        placedBucket = new BucketBlock(BTWMod.instance.parseID("fcBlockBucketEmptyID"));
        placedWaterBucket = new BucketBlockWater(BTWMod.instance.parseID("fcBlockBucketWaterID"));
        placedCementBucket = new BucketBlockCement(BTWMod.instance.parseID("fcBlockBucketCementID"));
        placedMilkBucket = new BucketBlockMilk(BTWMod.instance.parseID("fcBlockBucketMilkID"));
        placedMilkChocolateBucket = new BucketBlockChocolateMilk(BTWMod.instance.parseID("fcBlockBucketMilkChocolateID"));

        milkFluid = new MilkBlock(BTWMod.instance.parseID("fcBlockMilkID"));
        chocolateMilkFluid = new ChocolateMilkBlock(BTWMod.instance.parseID("fcBlockMilkChocolateID"));

        gearBox = new GearBoxBlock(BTWMod.instance.parseID("fcBlockGearBoxID"));

        ironSpike = new MetalSpikeBlock(BTWMod.instance.parseID("fcBlockSpikeIronID"));
        lightningRod = new LightningRodBlock(BTWMod.instance.parseID("fcBlockLightningRodID"));

        ironOreChunk = new OreChunkBlockIron(BTWMod.instance.parseID("fcBlockChunkOreIronID"));
        goldOreChunk = new OreChunkBlockGold(BTWMod.instance.parseID("fcBlockChunkOreGoldID"));

        looseStoneBrick = new LooseStoneBrickBlock(BTWMod.instance.parseID("fcBlockStoneBrickLooseID"));
        looseStoneBrickSlab = new LooseStoneBrickSlabBlock(BTWMod.instance.parseID("fcBlockStoneBrickLooseSlabID"));
        looseStoneBrickStairs = new LooseStoneBrickStairsBlock(BTWMod.instance.parseID("fcBlockStoneBrickLooseStairsID"), 0);

        looseNetherBrick = new LooseNetherBrickBlock(BTWMod.instance.parseID("fcBlockNetherBrickLooseID"));
        looseNetherBrickSlab = new LooseNEtherBrickSlabBlock(BTWMod.instance.parseID("fcBlockNetherBrickLooseSlabID"));
        looseNEtherBrickStairs = new LooseNetherBrickStairsBlock(BTWMod.instance.parseID("fcBlockNetherBrickLooseStairsID"));

        fallingNetherrack = new NetherrackBlockFalling(BTWMod.instance.parseID("fcBlockNetherrackFallingID"));

        lavaPillow = new LavaPillowBlock(BTWMod.instance.parseID("fcBlockLavaPillowID"));

        brownMushroomCap = new MushroomCapBlock(BTWMod.instance.parseID("fcBlockMushroomCapBrownID"), 0);
        redMushroomCap = new MushroomCapBlock(BTWMod.instance.parseID("fcBlockMushroomCapRedID"), 1);

        ironOreChunkStorage = new OreChunkStorageBlockIron(BTWMod.instance.parseID("fcBlockChunkOreStorageIronID"));
        goldOreChunkStorage = new OreChunkStorageBlockGold(BTWMod.instance.parseID("fcBlockChunkOreStorageGoldID"));

        wickerBlock = new WickerBlock(BTWMod.instance.parseID("fcBlockWickerID"));
        wickerSlab = new WickerSlabBlock(BTWMod.instance.parseID("fcBlockWickerSlabID"));
        wickerPane = new WickerPaneBlock(BTWMod.instance.parseID("fcBlockWickerPaneID"));

        gratePane = new GrateBlock(BTWMod.instance.parseID("fcBlockGrateID"));
        slatsPane = new SlatsBlock(BTWMod.instance.parseID("fcBlockSlatsID"));

        farmland = new FarmlandBlock(BTWMod.instance.parseID("fcBlockFarmlandNewID"));
        fertilizedFarmland = new FarmlandBlockFertilized(BTWMod.instance.parseID("fcBlockFarmlandFertilizedNewID"));

        wheatCrop = new WheatCropBlock(BTWMod.instance.parseID("fcBlockWheatCropID"));
        wheatCropTop = new WheatCropTopBlock(BTWMod.instance.parseID("fcBlockWheatCropTopID"));
        weeds = new WeedsBlock(BTWMod.instance.parseID("fcBlockWeedsID"));
        planterWithSoil = new PlanterBlockSoil(BTWMod.instance.parseID("fcBlockPlanterSoilID"));

        sugarCane = new SugarCaneBlock(BTWMod.instance.parseID("fcBlockReedsID"));
        sugarCaneRoots = new SugarCaneBlockRoots(BTWMod.instance.parseID("fcBlockReedRootsID"));

        carrotCrop = new CarrotBlock(BTWMod.instance.parseID("fcBlockCarrotID"));
        floweringCarrotCrop = new CarrotBlockFlowers(BTWMod.instance.parseID("fcBlockCarrotFlowersID"));

        plainCandle = new CandleBlock(BTWMod.instance.parseID("fcBlockCandlePlainID"), 16, "fcBlockCandle_plain");

        coloredCandle = new Block[16];

        for (int i = 0; i < 16; i++) {
            String idProperty = "fcBlockCandle" + ColorUtils.colorOrderCapital[i] + "ID";

            coloredCandle[i] = new CandleBlock(BTWMod.instance.parseID(idProperty), i, "fcBlockCandle_" + ColorUtils.colorOrder[i]);
        }

        infestedStone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneID"),
                Block.stone, 0)
                .setUnlocalizedName("fcBlockSilverfishStone");
        infestedMidStrataStone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneSecondStrataID"),
                Block.stone, 1)
                .setUnlocalizedName("fcBlockSilverfishStone2");
        infestedDeepStrataStone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneThirdStrataID"),
                Block.stone, 2)
                .setUnlocalizedName("fcBlockSilverfishStone3");
        infestedCobblestone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishCobblestoneID"),
                Block.cobblestone, 0)
                .setUnlocalizedName("fcBlockSilverfishCobblestone");
        infestedStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksID"),
                Block.stoneBrick, 0)
                .setUnlocalizedName("fcBlockSilverfishStoneBrick");
        infestedMossyStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksMossyID"),
                Block.stoneBrick, 1)
                .setUnlocalizedName("fcBlockSilverfishStoneBrickMossy");
        infestedCrackedStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksCrackedID"),
                Block.stoneBrick, 2)
                .setUnlocalizedName("fcBlockSilverfishStoneCracked");
        infestedChiseledStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksChiseledID"),
                Block.stoneBrick, 3)
                .setUnlocalizedName("fcBlockSilverfishStoneChiseled");

        grassSlab = new GrassSlabBlock(BTWMod.instance.parseID("fcBlockGrassSlabID"));

        bedroll = new BedrollBlock(BTWMod.instance.parseID("fcBlockBedrollID")).setHardness(0.1F).setBuoyant().setUnlocalizedName("fcBlockBedroll").disableStats();

        cobblestoneSlab = new CobblestoneSlabBlock(BTWMod.instance.parseID("fcBlockCobblestoneSlabSingleID"), false);
        cobblestoneDoubleSlab = new CobblestoneSlabBlock(BTWMod.instance.parseID("fcBlockCobblestoneSlabDoubleID"), true);

        stoneBrickSlab = new StoneBrickSlabBlock(BTWMod.instance.parseID("fcBlockStoneBrickSlabSingleID"), false);
        stoneBrickDoubleSlab = new StoneBrickSlabBlock(BTWMod.instance.parseID("fcBlockStoneBrickSlabDoubleID"), true);

        stoneSlab = new SmoothStoneSlabBlock(BTWMod.instance.parseID("fcBlockSmoothStoneSlabSingleID"), false);
        stoneDoubleSlab = new SmoothStoneSlabBlock(BTWMod.instance.parseID("fcBlockSmoothStoneSlabDoubleID"), true);

        midStrataCobblestoneStairs = new CobblestoneStairsBlock(BTWMod.instance.parseID("fcBlockCobblestoneStairsMidStrataID"), 1);
        deepStrataCobblestoneStairs = new CobblestoneStairsBlock(BTWMod.instance.parseID("fcBlockCobblestoneStairsDeepStrataID"), 2);
        looseMidStrataCobblestoneStairs = new LooseCobblestoneStairsBlock(BTWMod.instance.parseID("fcBlockCobblestoneLooseStairsMidStrataID"), 1);
        looseDeepStrataCobblestoneStairs = new LooseCobblestoneStairsBlock(BTWMod.instance.parseID("fcBlockCobblestoneLooseStairsDeepStrataID"), 2);

        midStrataStoneBrickStairs = new StoneBrickStairsBlock(BTWMod.instance.parseID("fcBlockStoneBrickStairsMidStrataID"), 1);
        deepStrataStoneBrickStairs = new StoneBrickStairsBlock(BTWMod.instance.parseID("fcBlockStoneBrickStairsDeepStrataID"), 2);
        looseMidStrataStoneBrickStairs = new LooseStoneBrickStairsBlock(BTWMod.instance.parseID("fcBlockStoneBrickLooseStairsMidStrataID"), 1);
        looseDeepStrataStoneBrickStairs = new LooseStoneBrickStairsBlock(BTWMod.instance.parseID("fcBlockStoneBrickLooseStairsDeepStrataID"), 2);

        midStrataStoneStairs = (new StairsBlock(BTWMod.instance.parseID("fcBlockSmoothstoneStairsMidStrataID"), Block.stone, 1))
                .setPicksEffectiveOn().setUnlocalizedName("fcBlockSmoothstoneStairs");
        deepStrataStoneStairs = (new StairsBlock(BTWMod.instance.parseID("fcBlockSmoothstoneStairsDeepStrataID"), Block.stone, 2))
                .setPicksEffectiveOn().setUnlocalizedName("fcBlockSmoothstoneStairs");

        midStrataStoneBrickSidingAndCorner = (new SidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockStoneBrickSidingAndCornerMidStrataID"),
                Material.rock, "stonebricksmooth_1", 1.5F, 10F,
                Block.soundStoneFootstep, "fcStoneBrickSiding")).setPicksEffectiveOn();

        midStrataStoneBrickMouldingAndDecorative = (new MouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockStoneBrickMouldingMidStrataID"),
                Material.rock, "stonebricksmooth_1", "stonebricksmooth_1", midStrataStoneBrickSidingAndCorner.blockID,
                1.5F, 10F, Block.soundStoneFootstep, "fcStoneBrickMoulding")).setPicksEffectiveOn();

        deepStrataStoneBrickSidingAndCorner = (new SidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockStoneBrickSidingAndCornerDeepStrataID"),
                Material.rock, "stonebricksmooth_2", 1.5F, 10F, Block.soundStoneFootstep, "fcStoneBrickSiding")).setPicksEffectiveOn();

        deepStrataStoneBrickMouldingAndDecorative = (new MouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockStoneBrickMouldingDeepStrataID"),
                Material.rock, "stonebricksmooth_2", "stonebricksmooth_2", deepStrataStoneBrickSidingAndCorner.blockID,
                1.5F, 10F, Block.soundStoneFootstep, "fcStoneBrickMoulding")).setPicksEffectiveOn();

        midStrataStoneSidingAndCorner = (new SidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSmoothStoneSidingAndCornerMidStrataID"),
                Material.rock, "fcBlockStone_1", 1.5F, 10F, Block.soundStoneFootstep, "fcStoneSiding")).setPicksEffectiveOn();

        midStrataStoneMouldingAndDecorative = (new MouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSmoothStoneMouldingMidStrataID"),
                Material.rock, "fcBlockStone_1", "fcBlockColumnStone_side_1", midStrataStoneSidingAndCorner.blockID, 1.5F, 10F,
                Block.soundStoneFootstep, "fcStoneMoulding")).setPicksEffectiveOn();

        deepStrataStoneSidingAndCorner = (new SidingAndCornerAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSmoothStoneSidingAndCornerDeepStrataID"),
                Material.rock, "fcBlockStone_2", 1.5F, 10F, Block.soundStoneFootstep, "fcStoneSiding")).setPicksEffectiveOn();

        deepStrataStoneMouldingAndDecorative = (new MouldingAndDecorativeBlock(BTWMod.instance.parseID("fcBlockSmoothStoneMouldingDeepStrataID"),
                Material.rock, "fcBlockStone_2", "fcBlockColumnStone_side_2", deepStrataStoneSidingAndCorner.blockID, 1.5F, 10F,
                Block.soundStoneFootstep, "fcStoneMoulding")).setPicksEffectiveOn();

        infestedMidStrataCobblestone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishCobblestoneMidStrataID"),
                Block.cobblestone, 1)
                .setUnlocalizedName("fcBlockSilverfishCobblestone");
        infestedDeepstrataCobblestone = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishCobblestoneDeepStrataID"),
                Block.cobblestone, 2)
                .setUnlocalizedName("fcBlockSilverfishCobblestone");

        infestedMidStrataStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksMidStrataID"),
                Block.stoneBrick, 4)
                .setUnlocalizedName("fcBlockSilverfishStoneBrick");
        infestedMidStrataMossyStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksMossyMidStrataID"),
                Block.stoneBrick, 5)
                .setUnlocalizedName("fcBlockSilverfishStoneBrickMossy");
        infestedMidStrataCrackedStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksCrackedMidStrataID"),
                Block.stoneBrick, 6)
                .setUnlocalizedName("fcBlockSilverfishStoneCracked");
        infestedMidStrataChiseledStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksChiseledMidStrataID"),
                Block.stoneBrick, 7)
                .setUnlocalizedName("fcBlockSilverfishStoneChiseled");

        infestedDeepStrataStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksDeepStrataID"),
                Block.stoneBrick, 8)
                .setUnlocalizedName("fcBlockSilverfishStoneBrick");
        infestedDeepStrataMossyStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksMossyDeepStrataID"),
                Block.stoneBrick, 9)
                .setUnlocalizedName("fcBlockSilverfishStoneBrickMossy");
        infestedDeepStrataCrackedStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksCrackedDeepStrataID"),
                Block.stoneBrick, 10)
                .setUnlocalizedName("fcBlockSilverfishStoneCracked");
        infestedDeepStrataChiseledStoneBrick = (SilverfishBlock) new SilverfishBlock(BTWMod.instance.parseID("fcBlockSilverfishStoneBricksChiseledDeepStrataID"),
                Block.stoneBrick, 11)
                .setUnlocalizedName("fcBlockSilverfishStoneChiseled");
    
        spiderEyeBlock = new SpiderEyeBlock(BTWMod.instance.parseID("fcBlockSpiderEyeID"));
        spiderEyeSlab = new SpiderEyeSlab(BTWMod.instance.parseID("fcBlockSpiderEyeSlabID"));
        
        oakSapling = new SaplingBlock(BTWMod.instance.parseID("fcBlockSaplingOakID"), "fcBlockSaplingOak", "fcBlockSaplingOak_0")
                .addTreeGrower(TreeGrowers.OAK_TREE, 9)
                .addTreeGrower(TreeGrowers.BIG_OAK_TREE, 1);
    
        spruceSapling = new SaplingBlock(BTWMod.instance.parseID("fcBlockSaplingSpruceID"), "fcBlockSaplingSpruce", "fcBlockSaplingSpruce_0")
                .addTreeGrower(TreeGrowers.SPRUCE_TREE, 10)
                .addTreeGrower(TreeGrowers.PINE_TREE, 10);
    
        birchSapling = new SaplingBlock(BTWMod.instance.parseID("fcBlockSaplingBirchID"), "fcBlockSaplingBirch", "fcBlockSaplingBirch_0")
                .addTreeGrower(TreeGrowers.BIRCH_TREE, 10);
    
        jungleSapling = new SaplingBlock(BTWMod.instance.parseID("fcBlockSaplingJungleID"), "fcBlockSaplingJungle", "fcBlockSaplingJungle_0")
                .addTreeGrower(TreeGrowers.JUNGLE_TREE, 10)
                .add2x2TreeGrower(TreeGrowers.BIG_JUNGLE_TREE, 10);
        
        //loom = new LoomBlock(BTWMod.instance.parseID("fcBlockLoomID"));
        
        looseSparseGrass = new LooseSparseGrassBlock(BTWMod.instance.parseID("fcBlockGrassSparseLooseID"));
    }

    public static void createModTileEntityMappings() {
        TileEntity.addMapping(CementTileEntity.class, "Cement");
        TileEntity.addMapping(CauldronTileEntity.class, "fcCauldron");
        TileEntity.addMapping(MillstoneTileEntity.class, "MillStone");
        TileEntity.replaceVanillaMapping(TileEntityHopper.class, HopperTileEntity.class, "Hopper"); // needs to be a replace due to vanilla Hopper
        TileEntity.addMapping(BlockDispenserTileEntity.class, "BlockDispenser");
        TileEntity.addMapping(PulleyTileEntity.class, "Pulley");
        TileEntity.addMapping(TurntableTileEntity.class, "Turntable");
        TileEntity.addMapping(VaseTileEntity.class, "Vase");
        TileEntity.addMapping(CrucibleTileEntity.class, "Crucible");

        TileEntity.addMapping(InfernalEnchanterTileEntity.class, "fcInfernalEnchanter");
        TileEntity.addMapping(AnvilTileEntity.class, "fcAnvil");

        TileEntity.addMapping(ArcaneVesselTileEntity.class, "fcArcaneVessel");

        TileEntity.addMapping(CampfireTileEntity.class, "fcCampfire");
        TileEntity.addMapping(UnfiredBrickTileEntity.class, "fcUnfiredBrick");
        TileEntity.addMapping(OvenTileEntity.class, "fcFurnaceBrick");
        TileEntity.addMapping(FiniteTorchTileEntity.class, "fcTorchFinite");
        TileEntity.addMapping(WickerBasketTileEntity.class, "fcBasket");
        TileEntity.addMapping(PlacedToolTileEntity.class, "fcToolPlaced");
        TileEntity.addMapping(HamperTileEntity.class, "fcHamper");
    
        TileEntity.addMapping(LoomTileEntity.class, "fcLoom");

        // vanilla tile entity substitutions
        TileEntity.replaceVanillaMapping(TileEntityChest.class, ChestTileEntity.class, "Chest");
        TileEntity.replaceVanillaMapping(TileEntityMobSpawner.class, MobSpawnerTileEntity.class, "MobSpawner");
        TileEntity.replaceVanillaMapping(TileEntityBeacon.class, BeaconTileEntity.class, "Beacon");
        TileEntity.replaceVanillaMapping(TileEntityEnderChest.class, EnderChestTileEntity.class, "EnderChest");
    }

    public static void initBlocksPotentialFluidSources() {
        for (int blockID = 1; blockID < 4096; blockID++) {
            Block block = Block.blocksList[blockID];
            potentialFluidSources[blockID] = block instanceof FluidSource;
        }
    }
}
