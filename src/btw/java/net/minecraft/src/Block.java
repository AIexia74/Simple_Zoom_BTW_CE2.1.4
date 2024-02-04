package net.minecraft.src;

import btw.*;
import btw.block.BTWBlocks;
import btw.block.blocks.*;
import btw.block.blocks.legacy.*;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.crafting.manager.KilnCraftingManager;
import btw.crafting.manager.SawCraftingManager;
import btw.crafting.manager.TurntableCraftingManager;
import btw.crafting.recipe.types.KilnRecipe;
import btw.crafting.recipe.types.SawRecipe;
import btw.crafting.recipe.types.TurntableRecipe;
import btw.crafting.util.FurnaceBurnTime;
import btw.entity.FallingBlockEntity;
import btw.item.blockitems.legacy.LegacySaplingBlockItem;
import btw.item.util.ItemUtils;
import btw.util.ReflectionUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import com.prupe.mcpatcher.cc.ColorizeBlock;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// FCMOD: Added

// END FCMOD

public class Block
{
	/**
	 * used as foreach item, if item.tab = current tab, display it on the screen
	 */
	private CreativeTabs displayOnCreativeTab;
	public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound soundSandFootstep = new StepSound("sand", 1.0F, 1.0F);
	public static final StepSound soundSnowFootstep = new StepSound("snow", 1.0F, 1.0F);
	public static final StepSound soundLadderFootstep = new StepSoundSand("ladder", 1.0F, 1.0F);
	public static final StepSound soundAnvilFootstep = new StepSoundAnvil("anvil", 0.3F, 1.0F);

	/** List of ly/ff (BlockType) containing the already registered blocks. */
	public static final Block[] blocksList = new Block[4096];

	/**
	 * An array of 4096 booleans corresponding to the result of the isOpaqueCube() method for each block ID
	 */
	public static final boolean[] opaqueCubeLookup = new boolean[4096];

	/** How much light is subtracted for going through this block */
	public static final int[] lightOpacity = new int[4096];

	public static final boolean[] hasKilnRecipe = new boolean[4096];

	/** Array of booleans that tells if a block can grass
	 * FCNOTE: Misleading name.  This is actually wether grass can grow under the block
	 */
	public static final boolean[] canBlockGrass = new boolean[4096];

	/** Amount of light emitted */
	public static final int[] lightValue = new int[4096];

	/**
	 * Flag if block ID should use the brightest neighbor light value as its own
	 * FCNOTE: Misleading name in that this does not apply to the neighbor below the block,
	 * only to sides and top
	 */ 
	public static boolean[] useNeighborBrightness = new boolean[4096];

	public static Block stone = new StoneBlock( 1 );
	public static BlockGrass grass = new GrassBlock( 2 );
	public static Block dirt = new DirtBlock( 3 );
	public static Block cobblestone = (new CobblestoneBlock(4)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebrick").setCreativeTab(CreativeTabs.tabBlock);
	public static Block planks = new PlanksBlock( 5 );
	public static Block sapling = (new LegacySaplingBlock(6)).setHardness(0.0F).setBuoyant().setStepSound(soundGrassFootstep).setUnlocalizedName("sapling");
	public static Block bedrock = new BedrockBlock( 7 );
	public static BlockFluid waterMoving = (BlockFluid)(new WaterBlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").setCreativeTab(CreativeTabs.tabBlock).disableStats();
	public static Block waterStill = (new WaterBlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats();
	public static BlockFluid lavaMoving = (BlockFluid)(new LavaBlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setUnlocalizedName("lava").setCreativeTab(CreativeTabs.tabBlock).disableStats();
	public static Block lavaStill = (new LavaBlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats();
	public static Block sand = new SandBlock( 12 );
	public static Block gravel = new GravelBlock( 13 );
	public static Block oreGold = (new GoldOreBlock(14)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreGold");
	public static Block oreIron = (new IronOreBlock(15)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreIron");
	public static Block oreCoal = (new CoalOreBlock(16)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreCoal");
	public static Block wood = new LogBlock( 17 );
	public static BlockLeaves leaves = new LeavesBlock( 18 );
	public static Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("sponge");
	public static Block glass = (new GlassBlock(20, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("glass");
	public static Block oreLapis = (new LapisOreBlock(21)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreLapis");
	public static Block blockLapis = (new Block(22, Material.rock)).setPicksEffectiveOn().setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("blockLapis").setCreativeTab(CreativeTabs.tabBlock);
	public static Block dispenser = new DispenserBlock( 23 );
	public static Block sandStone = new SandstoneBlock( 24 );
	public static Block music = new NoteBlock( 25 );
	public static Block bed = (new BedBlock(26)).setHardness(1F).setBuoyant().setUnlocalizedName("bed").disableStats();
	public static Block railPowered = (new BlockRailPowered(27)).setPicksEffectiveOn().setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("goldenRail");
	public static Block railDetector = (new DetectorRailBlock(28)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("detectorRail");
	public static BlockPistonBase pistonStickyBase = (BlockPistonBase)(new PistonBlockBase(29, true)).setUnlocalizedName("pistonStickyBase");
	public static Block web = new WebBlock( 30 );
	public static BlockTallGrass tallGrass = new TallGrassBlock( 31 );
	public static BlockDeadBush deadBush = new DeadBushBlock( 32 );
	public static BlockPistonBase pistonBase = (BlockPistonBase)(new PistonBlockBase(33, false)).setUnlocalizedName("pistonBase");
	public static BlockPistonExtension pistonExtension = new PistonExtensionBlock(34);
	public static Block cloth = new WoolBlock(); // sets own blockID of 35
	public static BlockPistonMoving pistonMoving = new PistonBlockMoving( 36 );
	public static BlockFlower plantYellow = (BlockFlower)(new FlowerBlock( 37 )).setUnlocalizedName("flower");
	public static BlockFlower plantRed = (BlockFlower)(new FlowerBlock( 38 )).setUnlocalizedName("rose");
	public static BlockFlower mushroomBrown = (BlockFlower)(new MushroomBlockBrown(39, "mushroom_brown")).setHardness(0.0F).setBuoyant().setStepSound(soundGrassFootstep).setUnlocalizedName("mushroom");
	public static BlockFlower mushroomRed = (BlockFlower)(new MushroomBlock(40, "mushroom_red")).setHardness(0.0F).setBuoyant().setStepSound(soundGrassFootstep).setUnlocalizedName("mushroom");
	public static Block blockGold = (new OreStorageBlock(41)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockGold");
	public static Block blockIron = (new OreStorageBlock(42)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockIron");
	public static BlockHalfSlab stoneDoubleSlab = (BlockHalfSlab)(new VanillaSlabBlock(43, true)).setPicksEffectiveOn().setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");
	public static BlockHalfSlab stoneSingleSlab = (BlockHalfSlab)(new VanillaSlabBlock(44, false)).setPicksEffectiveOn().setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");
	public static Block brick = (new BrickBlock(45)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabBlock);
	public static Block tnt = new PowderKegBlock( 46 );
	public static Block bookShelf = new BookshelfBlock( 47 );
	public static Block cobblestoneMossy = new MossyCobblestoneBlock( 48 );
	public static Block obsidian = new ObsidianBlock( 49 );
	public static Block torchWood = new InfiniteBurningTorchBlock(50, false);
	public static BlockFire fire = (BlockFire)(new FireBlock(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fire").disableStats();
	public static Block mobSpawner = new MobSpawnerBlock( 52 );
	public static Block stairsWoodOak = (new WoodStairsBlock( 53, planks, 0 ) ).setUnlocalizedName( "stairsWood" );
	public static BlockChest chest = (BlockChest)( new ChestBlock( 54 ) ).setCreativeTab( null );
	public static BlockRedstoneWire redstoneWire = (BlockRedstoneWire)(new RedstoneWireBlock(55)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("redstoneDust").disableStats();
	public static Block oreDiamond = (new DiamondOreBlock(56)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreDiamond");
	public static Block blockDiamond = (new OreStorageBlock(57)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockDiamond");
	public static Block workbench = new WorkbenchBlock( 58 );
	public static Block crops = (new LegacyWheatBlock(59)).setBuoyant().setUnlocalizedName("crops");
	public static Block tilledField = new LegacyFarmlandBlockUnfertilized( 60 );
	public static Block furnaceIdle = new FurnaceBlock( 61, false );
	public static Block furnaceBurning = new FurnaceBlock( 62, true );
	public static Block signPost = new SignBlock( 63, true );
	public static Block doorWood = new DoorBlockWood( 64 );
	public static Block ladder = new LegacyLadderBlock( 65 );
	public static Block rail = new RailBlock( 66 );
	public static Block stairsCobblestone = new CobblestoneStairsBlock( 67 , 0);
	public static Block signWall = new SignBlockWall( 68 );
	public static Block lever = (new LeverBlock(69)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("lever");
	public static Block pressurePlateStone = new PressurePlateBlockStone( 70 );
	public static Block doorIron = (new DoorBlock(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("doorIron").disableStats();
	public static Block pressurePlatePlanks = new PressurePlateBlockWood( 72 );
	public static Block oreRedstone = (new RedstoneOreBlock(73, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setCreativeTab(CreativeTabs.tabBlock);
	public static Block oreRedstoneGlowing = (new RedstoneOreBlock(74, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setCreativeTab(null);
	public static Block torchRedstoneIdle = (new BlockRedstoneTorch( 75, false )).setUnlocalizedName("notGate");
	public static Block torchRedstoneActive = (new BlockRedstoneTorch( 76, true )).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone);
	public static Block stoneButton = (new ButtonBlockStone(77)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("button");
	public static Block snow = new SnowCoverBlock( 78 );
	public static Block ice = (new IceBlock(79)).setHardness(0.5F).setBuoyant().setLightOpacity(3).setStepSound(soundGlassFootstep).setUnlocalizedName("ice");
	public static Block blockSnow = new LegacySnowBlock( 80 );
	public static Block cactus = new CactusBlock( 81 );
	public static Block blockClay = (new ClayBlock(82)).setHardness(0.6F).setUnlocalizedName("fcBlockClay");
	public static Block reed = (new LegacySugarCaneBlock(83)).setHardness(0.0F).setBuoyant().setStepSound(soundGrassFootstep).setUnlocalizedName("reeds").disableStats();
	public static Block jukebox = new JukeboxBlock( 84 );
	public static Block fence = new FenceBlockWood( 85 );
	public static Block pumpkin = new CarvedPumpkinBlock( 86 );
	public static Block netherrack = new NetherrackBlock( 87 );
	public static Block slowSand = (new SoulSandBlock(88)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("hellsand");
	public static Block glowStone = new GlowStoneBlock( 89 );
	public static BlockPortal portal = new PortalBlock( 90 );
	public static Block pumpkinLantern = new JackOLanternBlock( 91 );
	public static Block cake = (new CakeBlock(92)).setHardness(0.5F).setStepSound(soundClothFootstep).setUnlocalizedName("cake").disableStats();
	public static BlockRedstoneRepeater redstoneRepeaterIdle = new RedstoneRepeaterBlock( 93, false );
	public static BlockRedstoneRepeater redstoneRepeaterActive = new RedstoneRepeaterBlock( 94, true );

	/**
	 * April fools secret locked chest, only spawns on new chunks on 1st April.
	 */
	public static Block lockedChest = (new BlockLockedChest(95)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("lockedchest").setTickRandomly(true);
	public static Block trapdoor = new TrapDoorBlock( 96 );
	public static Block silverfish = new LegacySilverfishBlock( 97 );
	public static Block stoneBrick = new StoneBrickBlock( 98 );
	public static Block mushroomCapBrown = new LegacyMushroomCapBlock( 99, 0 );
	public static Block mushroomCapRed = new LegacyMushroomCapBlock( 100, 1 );
	public static Block fenceIron = new IronBarsBlock( 101 );
	public static Block thinGlass = (new PaneBlock(102, "glass", "thinglass_top", Material.glass, false)).setHardness(0.3F).setPicksEffectiveOn().setStepSound(soundGlassFootstep).setUnlocalizedName("thinGlass");
	public static Block melon = (new MelonBlock(103)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("melon");
	public static Block pumpkinStem = new StemBlock( 104, pumpkin );
	public static Block melonStem = new StemBlock( 105, melon );
	public static Block vine = new VineBlock( 106 );
	public static Block fenceGate = new FenceGateBlock( 107 );
	public static Block stairsBrick = (new BrickStairsBlock(108)).setUnlocalizedName("stairsBrick");
	public static Block stairsStoneBrick = new StoneBrickStairsBlock( 109, 0);
	public static BlockMycelium mycelium = new MyceliumBlock( 110 );
	public static Block waterlily = (new LilyPadBlock(111)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("waterlily");
	public static Block netherBrick = new NetherBrickBlock( 112 );
	public static Block netherFence = (new FenceBlock( 113, "netherBrick", BTWBlocks.netherRockMaterial)).setHardness(2F).setResistance(10F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherFence");
	public static Block stairsNetherBrick = new NetherBrickStairsBlock( 114 );
	public static Block netherStalk = (new NetherWartBlock(115)).setUnlocalizedName("netherStalk");
	public static Block enchantmentTable = (new EnchantingTableBlock(116)).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable");
	public static Block brewingStand = (new BrewingStandBlock(117)).setHardness(0.5F).setLightValue(0.125F).setUnlocalizedName("brewingStand");
	public static BlockCauldron cauldron = (BlockCauldron)(new CisternBlock(118)).setHardness(2.0F).setUnlocalizedName("cauldron");
	public static Block endPortal = (new EndPortalBlock(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F);
	public static Block endPortalFrame = (new EndPortalFrameBlock(120)).setStepSound(soundGlassFootstep).setLightValue(0.125F).setHardness(-1.0F).setUnlocalizedName("endPortalFrame").setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabDecorations);
	public static Block whiteStone = (new EndStoneBlock(121, Material.rock)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("whiteStone").setCreativeTab(CreativeTabs.tabBlock);
	public static Block dragonEgg = (new DragonEggBlock(122)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(0.125F).setUnlocalizedName("dragonEgg");
	public static Block redstoneLampIdle = (new RedstoneLampBlock(123, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight").setCreativeTab(CreativeTabs.tabRedstone);
	public static Block redstoneLampActive = (new RedstoneLampBlock(124, true)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight");
	public static BlockHalfSlab woodDoubleSlab = new WoodSlabBlock( 125, true );
	public static BlockHalfSlab woodSingleSlab = new WoodSlabBlock( 126, false );
	public static Block cocoaPlant = (new CocoaPlantBlock(127)).setHardness(0.2F).setResistance(5.0F).setBuoyant().setStepSound(soundWoodFootstep).setUnlocalizedName("cocoa");
	public static Block stairsSandStone = (new SandstoneStairsBlock(128)).setUnlocalizedName("stairsSandStone");
	public static Block oreEmerald = (new EmeraldOreBlock(129)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreEmerald");
	public static Block enderChest = (new EnderChestBlock(130)).setHardness(22.5F).setResistance(1000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("enderChest").setLightValue(0.5F);
	public static BlockTripWireSource tripWireSource = new TripWireHookBlock( 131 );
	public static Block tripWire = (new TripWireBlock(132)).setUnlocalizedName("tripWire");
	public static Block blockEmerald = (new OreStorageBlock(133)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockEmerald");
	public static Block stairsWoodSpruce = ( new WoodStairsBlock( 134, planks, 1 ) ).setUnlocalizedName( "stairsWoodSpruce" );
	public static Block stairsWoodBirch = ( new WoodStairsBlock( 135, planks, 2 ) ).setUnlocalizedName( "stairsWoodBirch" );
	public static Block stairsWoodJungle = ( new WoodStairsBlock( 136, planks, 3 ) ).setUnlocalizedName( "stairsWoodJungle" );
	public static Block commandBlock = (new BlockCommandBlock(137)).setUnlocalizedName("commandBlock");
	public static BlockBeacon beacon = (BlockBeacon)(new BeaconBlock(138)).setUnlocalizedName("beacon").setLightValue(1.0F);
	public static Block cobblestoneWall = (new WallBlock(139, cobblestone)).setUnlocalizedName("cobbleWall");
	public static Block flowerPot = (new FlowerPotBlock(140)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("flowerPot");
	public static Block carrot = (new LegacyCarrotBlock(141)).setUnlocalizedName("carrots");
	public static Block potato = (new PotatoBlock(142)).setUnlocalizedName("potatoes");
	public static Block woodenButton = (new ButtonBlockWood(143)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("button");
	public static Block skull = new SkullBlock( 144 );
	public static Block anvil = new AnvilBlock( 145 );
	public static Block chestTrapped = new StubBlock( 146 ).setUnlocalizedName( "chestTrap" );
	public static Block pressurePlateGold = new StubBlock( 147 ).setUnlocalizedName( "weightedPlate_light" );
	public static Block pressurePlateIron = new StubBlock( 148 ).setUnlocalizedName( "weightedPlate_heavy" );
	public static BlockComparator redstoneComparatorIdle = (BlockComparator)(new ComparatorBlock(149, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
	public static BlockComparator redstoneComparatorActive = (BlockComparator)(new ComparatorBlock(150, true)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
	public static BlockDaylightDetector daylightSensor = (BlockDaylightDetector)(new DaylightDetectorBlock(151)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("daylightDetector");
	public static Block blockRedstone = (new BlockPoweredOre(152)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockRedstone").setLightValue(0.75F);
	public static Block oreNetherQuartz = new NetherQuartzOreBlock( 153 );
	public static BlockHopper hopperBlock = new VanillaHopperBlock( 154 );
	public static Block blockNetherQuartz = new BlackStoneBlock( 155 );
	public static Block stairsNetherQuartz = new BlackStoneStairsBlock( 156 );
	public static Block railActivator = new StubBlock( 157 ).setUnlocalizedName( "activatorRail" );
	public static Block dropper = new StubBlock( 158 ).setUnlocalizedName( "dropper" );

	/** ID of the block. */
	public final int blockID;

	/** Indicates how many hits it takes to break a block. */
	public float blockHardness;

	/** Indicates the blocks resistance to explosions. */
	public float blockResistance;

	/**
	 * set to true when Block's constructor is called through the chain of super()'s. Note: Never used
	 */
	protected boolean blockConstructorCalled = true;

	/**
	 * If this field is true, the block is counted for statistics (mined or placed)
	 */
	protected boolean enableStats = true;

	/**
	 * Flags whether or not this block is of a type that needs random ticking. Ref-counted by ExtendedBlockStorage in
	 * order to broadly cull a chunk from the random chunk update list for efficiency's sake.
	 */
	protected boolean needsRandomTick;

	/** true if the Block contains a Tile Entity */
	protected boolean isBlockContainer;

	/** FCNOTE: DEPRECATED */
	protected double minX = 0D;
	/** FCNOTE: DEPRECATED */
	protected double minY = 0D;
	/** FCNOTE: DEPRECATED */
	protected double minZ = 0D;
	/** FCNOTE: DEPRECATED */
	protected double maxX = 1D;
	/** FCNOTE: DEPRECATED */
	protected double maxY = 1D;
	/** FCNOTE: DEPRECATED */
	protected double maxZ = 1D;

	/** Sound of stepping on the block */
	public StepSound stepSound;
	public float blockParticleGravity;

	/** Block material definition. */
	public Material blockMaterial;

	/**
	 * Determines how much velocity is maintained while moving on top of this block
	 */
	public float slipperiness;

	/** The unlocalized name of this block. */
	private String unlocalizedName;
	public Icon blockIcon;

	public static final boolean[] blockReplaced = new boolean[4096];
	public static final String[] blockReplacedBy = new String[4096];

	private int idDroppedOnStonecut = -1;
	private int countDroppedOnStonecut = 0;
	private int metaDroppedOnStonecut = 0;
	private MapColor[] mapColorsForMetadata;

	protected Block(int par1, Material par2Material)
	{
		this.stepSound = soundPowderFootstep;
		this.blockParticleGravity = 1.0F;
		this.slipperiness = 0.6F;

		if (blocksList[par1] != null)
		{
			throw new IllegalArgumentException("Slot " + par1 + " is already occupied by " + blocksList[par1] + " when adding " + this);
		}
		else
		{
			this.blockMaterial = par2Material;
			blocksList[par1] = this;
			this.blockID = par1;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			opaqueCubeLookup[par1] = this.isOpaqueCube();
			lightOpacity[par1] = this.isOpaqueCube() ? 255 : 0;
			canBlockGrass[par1] = !par2Material.getCanBlockGrass();
			// FCMOD: Added
			useNeighborBrightness[par1] = false;
			setFilterableProperties(Item.FILTERABLE_SOLID_BLOCK);
			// END FCMOD
		}
	}

	/**
	 * This method is called on a block after all other blocks gets already created. You can use it to reference and
	 * configure something on the block that needs the others ones.
	 */
	protected void initializeBlock() {}

	/**
	 * Sets the footstep sound for the block. Returns the object for convenience in constructing.
	 */
	public Block setStepSound(StepSound par1StepSound)
	{
		this.stepSound = par1StepSound;
		return this;
	}

	/**
	 * Sets how much light is blocked going through this block. Returns the object for convenience in constructing.
	 */
	protected Block setLightOpacity(int par1)
	{
		lightOpacity[this.blockID] = par1;
		return this;
	}

	/**
	 * Sets the amount of light emitted by a block from 0.0f to 1.0f (converts internally to 0-15). Returns the object
	 * for convenience in constructing.
	 */
	public Block setLightValue(float par1)
	{
		lightValue[this.blockID] = (int)(15.0F * par1);
		return this;
	}

	/**
	 * Sets the the blocks resistance to explosions. Returns the object for convenience in constructing.
	 */
	public Block setResistance(float par1)
	{
		this.blockResistance = par1 * 3.0F;
		return this;
	}

	public static boolean isNormalCube(int par0)
	{
		Block var1 = blocksList[par0];
		return var1 == null ? false : var1.blockMaterial.isOpaque() && var1.renderAsNormalBlock();
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	public boolean renderAsNormalBlock()
	{
		return true;
	}

	/**
	 * FCNOTE: This is misnamed and returns true if the block *doesn't* block movement
	 */	
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return !this.blockMaterial.blocksMovement();
	}

	/**
	 * The type of render function that is called for this block
	 */
	public int getRenderType()
	{
		return 0;
	}

	/**
	 * Sets how many hits it takes to break a block.
	 */
	public Block setHardness(float par1)
	{
		this.blockHardness = par1;

		if (this.blockResistance < par1 * 5.0F)
		{
			this.blockResistance = par1 * 5.0F;
		}

		return this;
	}

	/**
	 * This method will make the hardness of the block equals to -1, and the block is indestructible.
	 */
	protected Block setBlockUnbreakable()
	{
		this.setHardness(-1.0F);
		return this;
	}

	/**
	 * Returns the block hardness at a location. Args: world, x, y, z
	 */
	public float getBlockHardness(World par1World, int par2, int par3, int par4)
	{
		return this.blockHardness;
	}

	/**
	 * Sets whether this block type will receive random update ticks
	 */
	protected Block setTickRandomly(boolean par1)
	{
		this.needsRandomTick = par1;
		return this;
	}

	/**
	 * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
	 * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
	 */
	public boolean getTickRandomly()
	{
		return this.needsRandomTick;
	}

	public boolean hasTileEntity()
	{
		return this.isBlockContainer;
	}

	@Deprecated
	protected final void setBlockBounds(float par1, float par2, float par3, float par4, float par5, float par6) {}

	/**
	 * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
	 */
    @Environment(EnvType.CLIENT)
    public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return par1IBlockAccess.getBrightness(par2, par3, par4, getLightValueForBlock(par1IBlockAccess, par2, par3, par4, blocksList[par1IBlockAccess.getBlockId(par2, par3, par4)]));
	}

	/**
	 * Goes straight to getLightBrightnessForSkyBlocks for Blocks, does some fancy computing for Fluids
	 */
    @Environment(EnvType.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, getLightValueForBlock(par1IBlockAccess, par2, par3, par4, blocksList[par1IBlockAccess.getBlockId(par2, par3, par4)]));
	}

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return par1IBlockAccess.getBlockMaterial(par2, par3, par4).isSolid();
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}

	/**
	 * Returns the block texture based on the side being looked at.  Args: side
	 */
    @Environment(EnvType.CLIENT)
    public final Icon getBlockTextureFromSide(int par1)
	{
		return this.getIcon(par1, 0);
	}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
	 * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 * FCNOTE: The "mask" referred to above is just the bounding box to check for intersection with
	 */
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
	{
		AxisAlignedBB var8 = this.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);

		if (var8 != null && par5AxisAlignedBB.intersectsWith(var8))
		{
			par6List.add(var8);
		}
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	public boolean isOpaqueCube()
	{
		return true;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
	 */
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return this.isCollidable();
	}

	/**
	 * Returns if this block is collidable (only used by Fire). Args: x, y, z
	 */
	public boolean isCollidable()
	{
		return true;
	}

	/**
	 * Ticks the block if it's been scheduled
	 * FCNOTE: Called on server only
	 */
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 * FCNOTE: Called AFTER block is set to air, not before
	 */
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 * FCNOTE: Only called on server
	 */
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World par1World)
	{
		return 10;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 * FCNOTE: Called on server only
	 */
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {}

	/**
	 * ejects contained items into the world, and notifies neighbours of an update, as appropriate
	 * FCNOTE: Called on server only.  Called AFTER block is set, so it is no longer valid at pos.
	 */
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random)
	{
		return 1;
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return this.blockID;
	}

	/**
	 * Drops the specified block items
	 */
	public final void dropBlockAsItem(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		this.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, 1.0F, par6);
	}

	/**
	 * Drops the block items with a specified chance of dropping the specified items
	 */
	public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
	{
		if (!par1World.isRemote)
		{
			int var8 = this.quantityDroppedWithBonus(par7, par1World.rand);

			for (int var9 = 0; var9 < var8; ++var9)
			{
				if (par1World.rand.nextFloat() <= par6)
				{
					int var10 = this.idDropped(par5, par1World.rand, par7);

					if (var10 > 0)
					{
						this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(var10, 1, this.damageDropped(par5)));
					}
				}
			}
		}
	}

	/**
	 * Spawns EntityItem in the world for the given ItemStack if the world is not remote.
	 */
	protected void dropBlockAsItem_do(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
	{
		if (!par1World.isRemote && par1World.getGameRules().getGameRuleBooleanValue("doTileDrops"))
		{
			float var6 = 0.7F;
			double var7 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			double var9 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			double var11 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			EntityItem var13 = (EntityItem) EntityList.createEntityOfType(EntityItem.class, par1World, (double)par2 + var7, (double)par3 + var9, (double)par4 + var11, par5ItemStack);
			var13.delayBeforeCanPickup = 10;
			par1World.spawnEntityInWorld(var13);
		}
	}

	/**
	 * called by spawner, ore, redstoneOre blocks
	 */
	protected void dropXpOnBlockBreak(World par1World, int par2, int par3, int par4, int par5)
	{

	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	public int damageDropped(int par1)
	{
		return 0;
	}

	/**
	 * Returns how much this block can resist explosions from the passed in entity.
	 */
	public float getExplosionResistance(Entity par1Entity)
	{
		return this.blockResistance / 5.0F;
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {}

	public void postBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {}

	public void onBlockDestroyedByMiningCharge(World world, int x, int y, int z) {
		this.onBlockDestroyedByExplosion(world, x, y, z, null);
	}

	public void postBlockDestroyedByMiningCharge(World world, int x, int y, int z) {
		this.postBlockDestroyedByExplosion(world, x, y, z, null);
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
    @Environment(EnvType.CLIENT)
    public int getRenderBlockPass()
	{
		return 0;
	}

	public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5, ItemStack par6ItemStack)
	{
		return this.canPlaceBlockOnSide(par1World, par2, par3, par4, par5);
	}

	/**
	 * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
	 */
	public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
	{
		return this.canPlaceBlockAt(par1World, par2, par3, par4);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	{
		int var5 = par1World.getBlockId(par2, par3, par4);
		return var5 == 0 || blocksList[var5].blockMaterial.isReplaceable();
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		return false;
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {}
	
	/**
	 * Called whenever an entity is walking through this block. only works for
	 * entities that canTriggerWalking.
	 */
	public void onEntityStepsIn(World world, int x, int y, int z, Entity entity) {}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
	{
		return par9;
	}

	/**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {}

	/**
	 * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
	 */
	public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3 par6Vec3) {}

	/** FCNOTE: DEPRECATED */
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {}

	/**
	 * returns the block bounderies minX value
	 */
	public final double getBlockBoundsMinX()
	{
		return this.minX;
	}

	/**
	 * returns the block bounderies maxX value
	 */
	public final double getBlockBoundsMaxX()
	{
		return this.maxX;
	}

	/**
	 * returns the block bounderies minY value
	 */
	public final double getBlockBoundsMinY()
	{
		return this.minY;
	}

	/**
	 * returns the block bounderies maxY value
	 */
	public final double getBlockBoundsMaxY()
	{
		return this.maxY;
	}

	/**
	 * returns the block bounderies minZ value
	 */
	public final double getBlockBoundsMinZ()
	{
		return this.minZ;
	}

	/**
	 * returns the block bounderies maxZ value
	 */
	public final double getBlockBoundsMaxZ()
	{
		return this.maxZ;
	}

    @Environment(EnvType.CLIENT)
    public int getBlockColor()
	{
		return ColorizeBlock.colorizeBlock(this) ? ColorizeBlock.blockColor : 16777215;
	}

	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
    @Environment(EnvType.CLIENT)
    public int getRenderColor(int par1)
	{
		return ColorizeBlock.colorizeBlock(this, par1) ? ColorizeBlock.blockColor : 16777215;
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
	 * when first determining what to render.
	 */
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return ColorizeBlock.colorizeBlock(this, par1IBlockAccess, par2, par3, par4) ? ColorizeBlock.blockColor : 16777215;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	public boolean canProvidePower()
	{
		return false;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return 0;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender() {}

	/**
	 * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
	 * block and l is the block's subtype/damage.
	 * FCNOTE: Only called on server
	 */
	public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
	{
		par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);

		par2EntityPlayer.addHarvestBlockExhaustion(blockID, par3, par4, par5, par6);

		if ( this.canSilkHarvest( par6 ) && EnchantmentHelper.getSilkTouchModifier(par2EntityPlayer))
		{
			ItemStack var8 = this.createStackedBlock(par6);

			if (var8 != null)
			{
				this.dropBlockAsItem_do(par1World, par3, par4, par5, var8);
			}
		}
		else
		{
			int var7 = EnchantmentHelper.getFortuneModifier(par2EntityPlayer);
			this.dropBlockAsItem(par1World, par3, par4, par5, par6, var7);
		}
	}

	/**
	 * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
	 */
	protected boolean canSilkHarvest()
	{
		return this.renderAsNormalBlock() && !this.isBlockContainer;
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 * FCNOTE: This is the function used to create the silk-touch drop
	 */
	protected ItemStack createStackedBlock(int par1)
	{
		int var2 = 0;

		if (this.blockID >= 0 && this.blockID < Item.itemsList.length && Item.itemsList[this.blockID].getHasSubtypes())
		{
			var2 = par1;
		}

		return new ItemStack(this.blockID, 1, var2);
	}

	/**
	 * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
	 */
	public int quantityDroppedWithBonus(int par1, Random par2Random)
	{
		return this.quantityDropped(par2Random);
	}

	/**
	 * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
	 */
	public boolean canBlockStay(World par1World, int par2, int par3, int par4)
	{
		return true;
	}

	/**
	 * Called when the block is placed in the world.
	 * FCNOTE: Called AFTER the block is placed, unlike onBlockPlaced()
	 */
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {}

	/**
	 * Called after a block is placed
	 */
	public void onPostBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {}

	public Block setUnlocalizedName(String par1Str)
	{
		this.unlocalizedName = par1Str;
		return this;
	}

	/**
	 * Gets the localized name of this block. Used for the statistics page.
	 */
	public String getLocalizedName()
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
	}

	/**
	 * Returns the unlocalized name of this block.
	 */
	public String getUnlocalizedName()
	{
		return "tile." + this.unlocalizedName;
	}

	/**
	 * Returns the unlocalized name without the tile. prefix. Caution: client-only.
	 */
    @Environment(EnvType.CLIENT)
    public String getUnlocalizedName2()
	{
		return this.unlocalizedName;
	}

	/**
	 * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
	 * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
	 */
	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		return false;
	}

	/**
	 * Return the state of blocks statistics flags - if the block is counted for mined and placed.
	 */
	public boolean getEnableStats()
	{
		return this.enableStats;
	}

	/**
	 * Disable statistics for the block, the block will no count for mined or placed.
	 */
	public Block disableStats()
	{
		this.enableStats = false;
		return this;
	}

	/**
	 * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
	 * and stop pistons
	 * FCMOD: 3 = can be piston shoveled, but free otherwise 
	 */
	public int getMobilityFlag()
	{
		return this.blockMaterial.getMaterialMobility();
	}

	/**
	 * Returns the default ambient occlusion value based on block opacity
	 */
    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return par1IBlockAccess.isBlockNormalCube(par2, par3, par4) ? 0.2F : 1.0F;
	}

	/**
	 * Block's chance to react to an entity falling on it.
	 */
	public void onFallenUpon(World par1World, int par2, int par3, int par4, Entity par5Entity, float par6) {}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 * FCNOTE: Client only
	 */
    @Environment(EnvType.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return this.blockID;
	}

	/**
	 * Get the block's damage value (for use with pick block).
	 */
	public int getDamageValue(World par1World, int par2, int par3, int par4)
	{
		return this.damageDropped(par1World.getBlockMetadata(par2, par3, par4));
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
	}

	/**
	 * Returns the CreativeTab to display the given block on.
	 */
	public CreativeTabs getCreativeTabToDisplayOn()
	{
		return this.displayOnCreativeTab;
	}

	/**
	 * Sets the CreativeTab to display this block on.
	 */
	public Block setCreativeTab(CreativeTabs par1CreativeTabs)
	{
		this.displayOnCreativeTab = par1CreativeTabs;
		return this;
	}

	/**
	 * Called when the block is attempted to be harvested
	 */
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {}

	/**
	 * Called when this block is set (with meta data).
	 */
	public void onSetBlockIDWithMetaData(World par1World, int par2, int par3, int par4, int par5) {}

	/**
	 * currently only used by BlockCauldron to incrament meta-data during rain
	 */
	public void fillWithRain(World par1World, int par2, int par3, int par4) {}

	/**
	 * Returns true only if block is flowerPot
	 */
    @Environment(EnvType.CLIENT)
    public boolean isFlowerPot()
	{
		return false;
	}

	public boolean func_82506_l()
	{
		return true;
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return true;
	}

	/**
	 * Returns true if the given block ID is equivalent to this one. Example: redstoneTorchOn matches itself and
	 * redstoneTorchOff, and vice versa. Most blocks only match themselves.
	 */
	public boolean isAssociatedBlockID(int par1)
	{
		return this.blockID == par1;
	}

	/**
	 * Static version of isAssociatedBlockID.
	 */
	public static boolean isAssociatedBlockID(int par0, int par1)
	{
		return par0 == par1 ? true : (par0 != 0 && par1 != 0 && blocksList[par0] != null && blocksList[par1] != null ? blocksList[par0].isAssociatedBlockID(par1) : false);
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from
	 * getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	public boolean hasComparatorInputOverride()
	{
		return false;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
	 * strength when this block inputs to a comparator.
	 */
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
	{
		return 0;
	}

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(this.unlocalizedName);
	}

	/**
	 * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
	 */
    @Environment(EnvType.CLIENT)
    public String getItemIconName()
	{
		return null;
	}

	static
	{
		Item.itemsList[cloth.blockID] = (new ItemCloth(cloth.blockID - 256)).setUnlocalizedName("cloth");
		Item.itemsList[wood.blockID] = (new ItemMultiTextureTile(wood.blockID - 256, wood, BlockLog.woodType)).setUnlocalizedName("log");
		Item.itemsList[planks.blockID] = (new ItemMultiTextureTile(planks.blockID - 256, planks, BlockWood.woodType)).setUnlocalizedName("wood");
		Item.itemsList[silverfish.blockID] = (new ItemMultiTextureTile(silverfish.blockID - 256, silverfish, BlockSilverfish.silverfishStoneTypes)).setUnlocalizedName("monsterStoneEgg");
		Item.itemsList[stoneBrick.blockID] = (new ItemMultiTextureTile(stoneBrick.blockID - 256, stoneBrick, StoneBrickBlock.stoneBrickTypesStratified)).setUnlocalizedName("stonebricksmooth");
		Item.itemsList[sandStone.blockID] = (new ItemMultiTextureTile(sandStone.blockID - 256, sandStone, BlockSandStone.SAND_STONE_TYPES)).setUnlocalizedName("sandStone");
		Item.itemsList[blockNetherQuartz.blockID] = (new ItemMultiTextureTile(blockNetherQuartz.blockID - 256, blockNetherQuartz, BlockQuartz.quartzBlockTypes)).setUnlocalizedName("quartzBlock");
		Item.itemsList[stoneSingleSlab.blockID] = (new ItemSlab(stoneSingleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, false)).setUnlocalizedName("stoneSlab");
		Item.itemsList[stoneDoubleSlab.blockID] = (new ItemSlab(stoneDoubleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, true)).setUnlocalizedName("stoneSlab");
		Item.itemsList[woodSingleSlab.blockID] = (new ItemSlab(woodSingleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, false)).setUnlocalizedName("woodSlab");
		Item.itemsList[woodDoubleSlab.blockID] = (new ItemSlab(woodDoubleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, true)).setUnlocalizedName("woodSlab");
		Item.itemsList[sapling.blockID] = (new LegacySaplingBlockItem(sapling.blockID - 256).setUnlocalizedName("sapling"));
		Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setUnlocalizedName("leaves");
		Item.itemsList[vine.blockID] = new ItemColored(vine.blockID - 256, false);
		Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass.blockID - 256, true)).setBlockNames(new String[] {"shrub", "grass", "fern"});
		Item.itemsList[snow.blockID] = new ItemSnow(snow.blockID - 256, snow);
		Item.itemsList[waterlily.blockID] = new ItemLilyPad(waterlily.blockID - 256);
		Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase.blockID - 256);
		Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase.blockID - 256);
		Item.itemsList[cobblestoneWall.blockID] = (new ItemMultiTextureTile(cobblestoneWall.blockID - 256, cobblestoneWall, BlockWall.types)).setUnlocalizedName("cobbleWall");
		Item.itemsList[anvil.blockID] = (new ItemAnvilBlock(anvil)).setUnlocalizedName("anvil");

		for (int var0 = 0; var0 < 256; ++var0)
		{
			if (blocksList[var0] != null)
			{
				if (Item.itemsList[var0] == null)
				{
					Item.itemsList[var0] = new ItemBlock(var0 - 256);
					blocksList[var0].initializeBlock();
				}

				if ( canBlockGrass[var0] || lightOpacity[var0] == 0 )
				{
					useNeighborBrightness[var0] = true;
				}
			}
		}

		canBlockGrass[0] = true;
		StatList.initBreakableStats();
	}

	// FCMOD: Added New
	private static final int[] rotatedFacingsAroundJClockwise =
			new int[] { 0, 1, 4, 5, 3, 2 };

	private static final int[] rotatedFacingsAroundJCounterclockwise =
			new int[] { 0, 1, 5, 4, 2, 3 };

	private static final int[] cycledFacings =
			new int[] { 4, 0, 1, 5, 3, 2 };

	private static final int[] cycledFacingsReversed =
			new int[] { 1, 2, 5, 4, 0, 3 };

	public boolean isNormalCube(IBlockAccess blockAccess, int i, int j, int k)
	{
		return blockMaterial.isOpaque() && renderAsNormalBlock();
	}

	/**
	 * Similar to onBlockPlacedBy() but called before the block is placed rather than after
	 */
	public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata, EntityLiving entityBy)
	{
		return iMetadata;    	
	}

	public void setBlockMaterial(Material material)
	{
		blockMaterial = material;

		canBlockGrass[blockID] = !material.getCanBlockGrass();    	
	}

	/**
	 * Called on server only
	 */
	public void randomUpdateTick(World world, int i, int j, int k, Random rand)
	{
		updateTick( world, i, j, k, rand );
	}

	public void clientNotificationOfMetadataChange(World world, int i, int j, int k, int iOldMetadata, int iNewMetadata)
	{
	}

	public void onArrowImpact(World world, int i, int j, int k, EntityArrow arrow)
	{
	}

	public void onArrowCollide(World world, int i, int j, int k, EntityArrow arrow)
	{
	}

	public float getMovementModifier(World world, int i, int j, int k)
	{
		float fModifier = 1.0F;

		if ( blockMaterial != Material.ground && blockMaterial != Material.grass )
		{
			fModifier *= 1.2F;
		}

		return fModifier;
	}

	public void onPlayerWalksOnBlock(World world, int i, int j, int k, EntityPlayer player)
	{    	
		// Disabled Hardcore sinkholes
		/*
    	if ( IsFallingBlock( world, i, j, k ) )
    	{
    		CheckForUnstableGround( world, i, j, k );
    	}
		 */
	}

	/**
	 * Applies to Hopper ejecting items into the world, not inserting into the block itself
	 */
	public boolean doesBlockHopperEject(World world, int i, int j, int k)
	{
		return blockMaterial.isSolid();
	}

	/**
	 * Applies to Hopper inserting items directly into the block's inventory.  This
	 * does not ensure the block has a valid inventory, it's just a first-pass chance
	 * to block such behavior.
	 */
	public boolean doesBlockHopperInsert(World world, int i, int j, int k)
	{
		return false;
	}

	/**
	 * Returns true if the block is warm enough to melt nearby snow or ice
	 */
	public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public StepSound getStepSound(World world, int i, int j, int k)
	{
		return stepSound;
	}

	public void clientBreakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata)
	{
	}

	public void clientBlockAdded(World world, int i, int j, int k)
	{
	}

	public boolean hasStrata()
	{
		return false;
	}

	public int getMetadataConversionForStrataLevel(int iLevel, int iMetadata)
	{
		return iMetadata;
	}

	public float getExplosionResistance( Entity entity, World world, int i, int j, int k )
	{
		return getExplosionResistance( entity );
	}

	public boolean canBlockStayDuringGenerate(World world, int i, int j, int k)
	{
		// breaking this off into a separate function so that we can prevent certain blocks (like mushrooms) spawning under certain conditions without messing up worldgen or
		// its usual growth conditions

		// NOTE: This function isn't called for all types of WorldGen to avoid excessive base class changes where not needed.  If you want to override it, make sure it is called
		// appropriately for the block in question first.

		return canBlockStay( world, i, j, k );
	}

	/**
	 * Used to determine if this is a stair block for purposes of connecting visually to others
	 */
	public boolean isStairBlock()
	{
		return false;
	}

	public boolean shouldDeleteTileEntityOnBlockChange(int iNewBlockID)
	{
		return true;
	}

	/** 
	 * Determines whether other stone will "connect" to this block for purposes of determing whether
	 * a stone block can be individually harvested without breaking apart
	 */
	public boolean isNaturalStone(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	static public AxisAlignedBB getFullBlockBoundingBoxFromPool(World world, int i, int j, int k)
	{
		return AxisAlignedBB.getAABBPool().getAABB(
				(float)i, (float)j, (float)k, 
				(float)i + 1.0F, (float)j + 1.0F, (float)k + 1.0F );	    	
	}

	public boolean canSpitWebReplaceBlock(World world, int i, int j, int k)
	{
		return isGroundCover() || isAirBlock();
	}

	public boolean isAirBlock()
	{
		return false;
	}

	public boolean isReplaceableVegetation(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean hasWaterToSidesOrTop(World world, int i, int j, int k)
	{
		for ( int iFacing = 1; iFacing <= 5; iFacing++ )
		{		
			BlockPos tempPos = new BlockPos( i, j, k, iFacing );

			int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
			Block tempBlock = Block.blocksList[iTempBlockID];

			if ( tempBlock != null && tempBlock.blockMaterial == Material.water )
			{
				return true;
			}			
		}

		return false;
	}

	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
		return blockMaterial == Material.portal ? true : blockMaterial.blocksMovement();
	}

	public void onFluidFlowIntoBlock(World world, int i, int j, int k, BlockFluid fluidBlock)
	{
		dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
	}

	public boolean isBlockClimbable(World world, int i, int j, int k)
	{
		return false;
	}

	/** 
	 * Whether or not the block sets off Buddy Blocks.  Set to false for stuff like redstone blocks
	 * that can cause feedback loops.
	 */
	public boolean triggersBuddy()
	{
		return true;
	}

	//------------ Addon interfacing related functionality ----------//
	
	/**
	 * Replaces a reference to an existing block (vanilla or btw)
	 * @param id The block id to be replaced
	 * @param newClass The class of the new block
	 * @param parameters Optional additional parameters to pass to the block, not including the id.
	 */
	@Deprecated
	public static Block replaceBlock(int id, Class newClass, BTWAddon addonPerformingReplacement, Object ... parameters) {
		return replaceBlock(id, newClass, new String[] {}, addonPerformingReplacement, parameters);
	}

	/**
	 * Replaces a reference to an existing block (vanilla or btw), with a list of valid addons for which to be able to overwrite their replacement
	 * @param id The block id to be replaced
	 * @param newClass The class of the new block
	 * @param validAddonNamesForOverwrite An array of addon names which should be ignored when handling conflicts
	 * @param parameters Optional additional parameters to pass to the block, not including the id.
	 */
	@Deprecated
	public static Block replaceBlock(int id, Class newClass, String[] validAddonNamesForOverwrite, BTWAddon addonPerformingReplacement, Object ... parameters) {
		if (blockReplaced[id]) {
			String replacedBy = blockReplacedBy[id];
			boolean isValidOverwrite = false;
			
			for (String addonName : validAddonNamesForOverwrite) {
				if (replacedBy.equals(addonName)) {
					isValidOverwrite = true;
				}
			}
			
			if (!isValidOverwrite) {
				throw new RuntimeException("Multiple addons attempting to replace block " + blocksList[id]);
			}
		}

		Block newBlock = null;

		Class[] parameterTypes = new Class[parameters.length + 1];
		Object[] parameterValues = new Object[parameters.length + 1];

		parameterTypes[0] = Integer.TYPE;
		parameterValues[0] = id;

		Block original = blocksList[id];
		blocksList[id] = null;

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
			String message = "No appropriate constructor found for " + blocksList[id] + ": ";

			for (Class<?> paramType : parameterTypes) {
				message += paramType.getSimpleName() + ", ";
			}

			throw new RuntimeException(message);
		}

		try {
			constructorToUse.setAccessible(true);
			newBlock = (Block) constructorToUse.newInstance(parameterValues);
		} catch (InstantiationException e) {
			throw new RuntimeException("A problem has occured attempting to instantiate replacement for " + blocksList[id]);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Incompatible types passed to specified constructor for " + blocksList[id]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		blockReplaced[id] = true;
		
		if (addonPerformingReplacement != null) {
			blockReplacedBy[id] = addonPerformingReplacement.getName();
		}
		else {
			AddonHandler.logWarning("Deprecated block replacement call for block " + blocksList[id] + ". Please attach addon when handling replacement for mutual replacement handling.");
			blockReplacedBy[id] = "";
		}

		newBlock.setHardness(original.blockHardness).setResistance(original.blockResistance).setStepSound(original.stepSound).setUnlocalizedName(original.unlocalizedName).setCreativeTab(original.getCreativeTabToDisplayOn());
		if (!original.enableStats)
			newBlock.disableStats();

		blocksList[id] = newBlock;

		return newBlock;
	}

	//------------ Harvesting related functionality ----------//

	protected boolean canSilkHarvest( int iMetadata )
	{
		return canSilkHarvest();
	}

	/**
	 * Called on server only, after the block is removed from the world
	 */
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
	{
		world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, i, j, k, blockID + ( iMetadata << 12 ) );

		dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, 1F);
	}

	protected void dropItemsIndividually(World world, int i, int j, int k, int iIDDropped, int iPileCount, int iDamageDropped, float fChanceOfPileDrop)
	{
		for ( int iTempCount = 0; iTempCount < iPileCount; iTempCount++ )
		{
			if ( world.rand.nextFloat() <= fChanceOfPileDrop )
			{
				ItemStack stack = new ItemStack( iIDDropped, 1, iDamageDropped );

				dropBlockAsItem_do( world, i, j, k, stack );
			}
		}
	}

	/**
	 * Called by explosions and improper tool use.  
	 * Should return true if the block processes its own drops through this method, false otherwise
	 * Note that the block may no longer be at the specified position when this is called
	 */
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		return false;
	}

	/**
	 * Explosion may be null if this is called by a mining charge
	 */
	public void dropItemsOnDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion)
	{
		if ( !world.isRemote && canDropFromExplosion( explosion ) )
		{
			float fChance = 1F;

			if ( explosion != null )
			{
				fChance /= explosion.explosionSize;
			}

			int iMetadata = world.getBlockMetadata( i, j, k );

			if ( !dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, fChance) )
			{
				dropBlockAsItemWithChance( world, i, j, k, iMetadata, fChance, 0 );
			}
		}
	}
	
	public void dropItemsOnDestroyedByMiningCharge(World world, int x, int y, int z, int metadata) {
		if (!world.isRemote) {
			dropBlockAsItem(world, x, y, z, metadata, 0);
		}
	}

	/**
	 * Notifies neighbors of dirt blocks that they should be loosened
	 */
	protected void onDirtDugWithImproperTool(World world, int i, int j, int k)
	{
		for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
		{
			notifyNeighborDirtDugWithImproperTool(world, i, j, k, iTempFacing);
		}
	}

	protected void onDirtSlabDugWithImproperTool(World world, int i, int j, int k,
												 boolean bUpsideDown)
	{
		for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
		{
			if ( !( bUpsideDown && iTempFacing == 0 ) && !( !bUpsideDown && iTempFacing == 1 ) )
			{
				notifyNeighborDirtDugWithImproperTool(world, i, j, k, iTempFacing);
			}
		}
	}

	protected void notifyNeighborDirtDugWithImproperTool(World world, int i, int j, int k,
														 int iToFacing)
	{
		BlockPos neighborPos = new BlockPos( i, j, k, iToFacing );

		int iTargetBlockID = world.getBlockId(neighborPos.x, neighborPos.y, neighborPos.z);

		Block targetBlock = blocksList[iTargetBlockID];

		if ( targetBlock != null )
		{    	
			targetBlock.onNeighborDirtDugWithImproperTool(world,
														  neighborPos.x, neighborPos.y, neighborPos.z, getOppositeFacing(iToFacing));
		}    		
	}

	protected void onNeighborDirtDugWithImproperTool(World world, int i, int j, int k,
													 int iToFacing)
	{
	}

	//------------ Block placing functionality ------------//

	public boolean canBlocksBePlacedAgainstThisBlock(World world, int x, int y, int z) {
		return true;
	}

	//------------ Hard Point related functionality ----------//

	/**
	 * small attachment surfaces, like those required for the bottom of a torch (approx 1/8 block width)
	 */
	public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return hasCenterHardPointToFacing( blockAccess, i, j, k, iFacing, bIgnoreTransparency );
	}

	public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing )
	{
		return hasSmallCenterHardPointToFacing( blockAccess, i, j, k, iFacing, false );
	}

	/**
	 * medium sized attachment points like the top of fence posts (approx 1/4 block width)
	 */
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return hasLargeCenterHardPointToFacing( blockAccess, i, j, k, iFacing, bIgnoreTransparency );
	}

	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing )
	{
		return hasCenterHardPointToFacing( blockAccess, i, j, k, iFacing, false );
	}

	/**
	 * large attachment points that can support a full block width
	 */	
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return blockAccess.isBlockNormalCube( i, j, k );
	}

	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing )
	{
		return hasLargeCenterHardPointToFacing( blockAccess, i, j, k, iFacing, false );
	}

	/**
	 * returns true if the block is sitting on the one below, like a torch resting on the ground
	 */
	public boolean isBlockRestingOnThatBelow(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	/**
	 * returns true if block is attached to a block in a particular direction.  Example: pumpkins attached to stems
	 */
	public boolean isBlockAttachedToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return false;
	}

	public void attachToFacing(World world, int i, int j, int k, int iFacing)
	{
	}

	public boolean hasContactPointToFullFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return blockAccess.isBlockNormalCube( i, j, k );
	}

	public boolean hasContactPointToSlabSideFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIsSlabUpsideDown)
	{
		return hasContactPointToFullFace(blockAccess, i, j, k, iFacing);
	}

	/**
	 * This method refers to the 'L' shaped sides of stair blocks.  Other stair facings will refernce either the full face, stair top,
	 * or slab methods, depending on their shape
	 */
	public boolean hasContactPointToStairShapedFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return hasContactPointToFullFace(blockAccess, i, j, k, iFacing);
	}

	/**
	 * This method refers to the half-block shaped top or bottom of stair blocks.
	 */
	public boolean hasContactPointToStairNarrowVerticalFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, int iStairFacing)
	{
		return hasContactPointToFullFace(blockAccess, i, j, k, iFacing);
	}

	/**
	 * Should return true if mortar has been successfully applied to block.
	 */
	public boolean onMortarApplied(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean hasNeighborWithMortarInContact(World world, int i, int j, int k)
	{
		for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
		{
			if ( WorldUtils.hasNeighborWithMortarInFullFaceContactToFacing(world, i, j, k, iTempFacing) )
			{
				return true;
			}
		}

		return false;
	}

	public boolean isStickyToSnow(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean hasStickySnowNeighborInContact(World world, int i, int j, int k)
	{
		for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
		{
			if ( WorldUtils.hasStickySnowNeighborInFullFaceContactToFacing(world, i, j, k, iTempFacing) )
			{
				return true;
			}
		}

		return false;
	}

	//--------------- Fire related functionality -------------//

	private int defaultFurnaceBurnTime = 0;

	public int getFurnaceBurnTime(int iItemDamage)
	{
		return defaultFurnaceBurnTime;
	}

	public void setFurnaceBurnTime(int iBurnTime)
	{
		defaultFurnaceBurnTime = iBurnTime;
	}

	public void setFurnaceBurnTime(FurnaceBurnTime burnTime)
	{
		setFurnaceBurnTime(burnTime.burnTime);
	}

	public boolean doesInfiniteBurnToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return false;
	}

	public boolean doesExtinguishFireAbove(World world, int i, int j, int k)
	{
		return false;
	}

	public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread)
	{
		if ( bForcedFireSpread || ( world.rand.nextInt( iFireAge + 10 ) < 5 && 
				!world.isRainingAtPos(i, j, k) ) )
		{
			int iNewFireMetadata = iFireAge + world.rand.nextInt( 5 ) / 4;

			if ( iNewFireMetadata > 15 )
			{
				iNewFireMetadata = 15;
			}

			world.setBlockAndMetadataWithNotify( i, j, k, Block.fire.blockID, iNewFireMetadata );
		}
		else
		{
			world.setBlockWithNotify( i, j, k, 0 );
		}
	}    

	public Block setFireProperties(int iChanceToEncourageFire, int iAbilityToCatchFire)
	{
		BlockFire.chanceToEncourageFire[blockID] = iChanceToEncourageFire;
		BlockFire.abilityToCatchFire[blockID] = iAbilityToCatchFire;

		return this;
	}

	public Block setFireProperties(Flammability flammability)
	{
		return setFireProperties(flammability.chanceToEncourageFire,
								 flammability.abilityToCatchFire);
	}

	/**
	 * Whether the block itself can be set on fire, rather than a neighboring block being set to a fire block
	 */
	public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean getCanBeSetOnFireDirectlyByItem(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getCanBeSetOnFireDirectly(blockAccess, i, j, k);
	}

	public boolean setOnFireDirectly(World world, int i, int j, int k)
	{
		return false;
	}

	public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 0;
	}

	public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean getDoesFireDamageToEntities(World world, int i, int j, int k, Entity entity)
	{
		return getDoesFireDamageToEntities(world, i, j, k);
	}

	public boolean getDoesFireDamageToEntities(World world, int i, int j, int k)
	{
		return false;
	}

	/**
	 * Used by Hibachi to determine if it can remove the block above it when lit
	 */
	public boolean getCanBlockBeIncinerated(World world, int i, int j, int k)
	{
		return Block.fire.canBlockCatchFire( world, i, j, k ) || !blockMaterial.blocksMovement();
	}

	/** 
	 * Whether a fire block can be directly placed over this one, without first burning or catching fire, as if it were air.
	 */
	public boolean getCanBlockBeReplacedByFire(World world, int i, int j, int k)
	{
		return isAirBlock();
	}

	public boolean isIncineratedInCrucible()
	{
		return FireBlock.canBlockBeDestroyedByFire(blockID);
	}

	//------------- Pathing related functionality ------------//

	public boolean canPathThroughBlock(IBlockAccess blockAccess, int i, int j, int k,
									   Entity entity, PathFinder pathFinder)
	{
		// note: getBlocksMovement() is misnamed and returns if the block *doesn't* block movement

		return getBlocksMovement( blockAccess, i, j, k );
	}

	/**
	 * Used to determine if entities who start their pathing from within this block
	 * should instead start pathing from a neighbor block instead, to prevent getting stuck
	 * in this one.  Mostly applies to stuff like chickens getting stuck in fences.
	 */
	public boolean shouldOffsetPositionIfPathingOutOf(IBlockAccess blockAccess,
													  int i, int j, int k, Entity entity, PathFinder pathFinder)
	{
		return !canPathThroughBlock(blockAccess, i, j, k, entity, pathFinder);
	}

	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 0;
	}    

	public int adjustPathWeightOnNotBlocked(int iPreviousWeight)
	{
		return iPreviousWeight;
	}

	public boolean isBreakableBarricade(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	//------------- Kiln related functionality ------------//

	public final boolean getCanBeCookedByKiLn(IBlockAccess blockAccess, int i, int j, int k)
	{
		int metadata = blockAccess.getBlockMetadata(i, j, k);

		return KilnCraftingManager.instance.getRecipeResult(this, metadata) != null;
	}    

	public final int getCookTimeMultiplierInKiLn(IBlockAccess blockAccess, int i, int j, int k)
	{
		int metadata = blockAccess.getBlockMetadata(i, j, k);

		KilnRecipe recipe = KilnCraftingManager.instance.getRecipe(this, metadata);

		return recipe != null ? recipe.getCookTimeMultiplier() : 1;
	}

	public final ItemStack[] getOutputsWhenCookedByKiln(IBlockAccess blockAccess, int i, int j, int k)
	{
		int metadata = blockAccess.getBlockMetadata(i, j, k);

		return KilnCraftingManager.instance.getRecipeResult(this, metadata);
	}

	public void onCookedByKiLn(World world, int i, int j, int k)
	{
		ItemStack[] outputs = getOutputsWhenCookedByKiln(world, i, j, k);

		if ( outputs != null )
		{
			world.setBlockToAir( i, j, k );

			for (ItemStack stack : outputs) {
				ItemUtils.ejectStackWithRandomOffset(world, i, j, k, stack.copy());
			}
		}
	}

	//------------- Saw related functionality ------------//

	public boolean doesBlockBreakSaw(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		SawRecipe recipe = SawCraftingManager.instance.getRecipe(this, metadata);
		
		if (blockMaterial.isSolid() && blockMaterial.breaksSaw() && recipe == null) {
			return true;
		}

		return false;
	}

	/*
	 * returns true if the block has been sawed, false otherwise
	 */
	public boolean onBlockSawed(World world, int i, int j, int k, int iSawPosI, int iSawPosJ, int iSawPosK)
	{
		return onBlockSawed(world, i, j, k);
	}

	/*
	 * returns true if the block has been sawed, false otherwise
	 */
	public boolean onBlockSawed(World world, int i, int j, int k)
	{
		int metadata = world.getBlockMetadata(i, j, k);
		SawRecipe recipe = SawCraftingManager.instance.getRecipe(this, metadata);

		if (recipe != null)
		{
			for (ItemStack stack : recipe.getOutput()) {
				ItemUtils.ejectStackWithRandomOffset(world, i, j, k, stack.copy());
			}
		}
		else
		{
			if ( !doesBlockDropAsItemOnSaw(world, i, j, k) )
			{
				return false;
			}

			dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
		}

		world.setBlockToAir( i, j, k );

		return true;
	}

	public boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k)
	{
		return blockMaterial.isSolid();
	}

	//------------- silverfish related functionality ------------//
	
	public int getBlockIDOnInfest(EntityLiving entity, int metadata)
	{
		return BTWBlocks.infestedStone.blockID;
	}
	
	/**
	 * handles all things that happen on infestation, including what should happen to the infector
	 */
	public void onInfested(World world, EntityLiving entity, int x, int y, int z, int metadata)
	{
		infestBlock(world, entity, x, y, z, metadata);
		entity.spawnExplosionParticle();
		entity.setDead();
	}
	
	/**
	 * Basically set block but uses entity and metadata to pick blockID
	 */
	public void infestBlock(World world, EntityLiving entity, int x, int y, int z, int metadata)
	{
		world.setBlock(x, y, z, getBlockIDOnInfest(entity, metadata), 0, 3);
	}
	
	/**
	 * If this block is infested by linked entity, used by silverfish
	 */
	public boolean isBlockInfestedBy(EntityLiving entity)
	{
		return false;
	}
	
	/**
	 *
	 * @param entity for having hook for other infesting mobs
	 * @param metadata for having single blockID be infestable depending on metadata
	 * @return
	 */
	public boolean isBlockInfestable(EntityLiving entity, int metadata)
	{
		return false;
	}

	//------------- Mechanical power related functionality ------------//

	public int getMechanicalPowerLevelProvidedToAxleAtFacing(World world, int i, int j, int k, int iFacing)
	{
		return 0;
	}

	//------------- Tool effectiveness functionality ------------//

	private boolean shovelsEffectiveOn = false;
	private boolean picksEffectiveOn = false;
	private boolean axesEffectiveOn = false;
	private boolean hoesEffectiveOn = false;

	private boolean chiselsEffectiveOn = false;
	private boolean chiselsCanHarvest = false;

	public boolean areShovelsEffectiveOn()
	{
		return shovelsEffectiveOn;
	}

	public boolean arePicksEffectiveOn()
	{
		return picksEffectiveOn;
	}

	public boolean areAxesEffectiveOn()
	{
		return axesEffectiveOn;
	}

	public boolean areHoesEffectiveOn()
	{
		return hoesEffectiveOn;
	}

	public boolean arechiselseffectiveon()
	{
		return chiselsEffectiveOn;
	}

	public boolean arechiselseffectiveon(World world, int i, int j, int k)
	{
		return arechiselseffectiveon();
	}

	public boolean canChiselsHarvest()
	{
		return chiselsCanHarvest;
	}

	public Block setShovelsEffectiveOn() { return setShovelsEffectiveOn(true); }
	public Block setShovelsEffectiveOn(boolean bEffective)
	{
		shovelsEffectiveOn = bEffective;

		return this;
	}

	public Block setPicksEffectiveOn() { return setPicksEffectiveOn(true); }
	public Block setPicksEffectiveOn(boolean bEffective)
	{
		picksEffectiveOn = bEffective;

		return this;
	}

	public Block setAxesEffectiveOn() { return setAxesEffectiveOn(true); }
	public Block setAxesEffectiveOn(boolean bEffective)
	{
		axesEffectiveOn = bEffective;

		return this;
	}

	public Block setHoesEffectiveOn() { return setHoesEffectiveOn(true); }
	public Block setHoesEffectiveOn(boolean bEffective)
	{
		hoesEffectiveOn = bEffective;

		return this;
	}

	public Block setChiselsEffectiveOn() { return setChiselsEffectiveOn(true); }
	public Block setChiselsEffectiveOn(boolean bEffective)
	{
		chiselsEffectiveOn = bEffective;

		return this;
	}

	public Block setChiselsCanHarvest() { return setChiselsCanHarvest(true); }
	public Block setChiselsCanHarvest(boolean bCanHarvest)
	{
		chiselsCanHarvest = bCanHarvest;

		return this;
	}

	public float getPlayerRelativeBlockHardness( EntityPlayer player, World world, int i, int j, int k )
	{
		float fBlockHardness = getBlockHardness( world, i, j, k );

		if ( fBlockHardness >= 0F )
		{
			float fRelativeHardness = player.getCurrentPlayerStrVsBlock( this, i, j, k ) / fBlockHardness;

			if ( player.isCurrentToolEffectiveOnBlock(this, i, j, k) )
			{
				return fRelativeHardness / 30F;
			}
			else {
				return fRelativeHardness / (200F * world.getDifficulty().getNoToolBlockHardnessMultiplier());
			}
		}
		else
		{
			return 0F; 
		}
	}

	public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
	{
		return false;
	}

	/**
	 * Returns false if the block has not been replaced with another, and should be removed
	 */
	public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
	{
		return false;
	}

	public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 0;
	}

	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getEfficientToolLevel(blockAccess, i, j, k);
	}

	/**
	 * The following is for stumps and such, which are a pain to remove regardless of whether their overall block has
	 * relevant tool effeciencies
	 */
	public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	public boolean canToolsStickInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	public boolean canToolStickInBlockSpecialCase(World world, int x, int y, int z, Item toolOrSword) {
		return false;
	}

	//------------- Buoyancy related functionality ------------//

	private float buoyancy = -1.0F;

	public Block setBuoyancy(float fBuoyancy)
	{
		buoyancy = fBuoyancy;

		return this;
	}

	public Block setBuoyant() { return setBuoyancy(1F); }
	public Block setNonBuoyant() { return setBuoyancy(-1F); }
	public Block setNeutralBuoyant() { return setBuoyancy(0F); }

	public float getBuoyancy(int iMetadata)
	{
		return buoyancy;
	}

	//------------- Ground cover related functionality ------------//

	public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
	{
		if ( world.doesBlockHaveSolidTopSurface( i, j, k ) )
		{
			return true;
		}

		return false;
	}

	public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 0F;
	}

	public boolean isGroundCover()
	{
		return false;
	}

	public boolean getCanGrassSpreadToBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean spreadGrassToBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean getCanGrassGrowUnderBlock(World world, int i, int j, int k, boolean bGrassOnHalfSlab)
	{
		if ( !bGrassOnHalfSlab )
		{
			return !hasLargeCenterHardPointToFacing( world, i, j, k, 0 );
		}

		return true;
	}

	public boolean getCanMyceliumSpreadToBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean spreadMyceliumToBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean getCanBlightSpreadToBlock(World world, int i, int j, int k, int iBlightLevel)
	{
		return false;
	}

	/**
	 * Used by blocks like grass and mycellium to determine if they should use a snow side
	 * texture.  Note that this refers to the top visible surface, not just the top facing,
	 * which means that stuff like half-slabs should only return true if they have ground cover
	 * actually on the top surface halfway up the block vertically.
	 */ 
	public boolean isSnowCoveringTopSurface(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iBlockAboveID = blockAccess.getBlockId( i, j + 1, k );

		if ( iBlockAboveID != 0 )
		{
			Block blockAbove = blocksList[iBlockAboveID];

			Material aboveMaterial = blockAbove.blockMaterial;

			if ( aboveMaterial == Material.snow || aboveMaterial == Material.craftedSnow && 
					blockAbove.hasCenterHardPointToFacing( blockAccess, i, j + 1, k, 0 ) )
			{
				return true;
			}
			else if (
					blockAbove.groundCoverRestingOnVisualOffset(blockAccess, i, j + 1, k) < -0.99F &&
					blockAccess.getBlockId( i, j + 2, k ) == snow.blockID )
			{
				// consider snow resting on tall grass and such

				return true;
			}
		}

		return false;
	}

	//---------- Piston Related Functionality ---------//

	/**
	 * Returns the metadata that will be placed
	 */
	public int onPreBlockPlacedByPiston(World world, int i, int j, int k, int iMetadata, int iDirectionMoved)
	{
		return iMetadata;
	}

	public boolean canBlockBePulledByPiston(World world, int i, int j, int k, int iToFacing)
	{
		if ( getMobilityFlag() != 1 ) // blocks destroyed on push can not be pulled
		{
			return canBlockBePushedByPiston(world, i, j, k, iToFacing);
		}

		return false;    	                         
	}

	public boolean canBlockBePushedByPiston(World world, int i, int j, int k, int iToFacing)
	{
		int iMobility = getMobilityFlag();

		return iMobility != 2;
	}

	public boolean canBePistonShoveled(World world, int i, int j, int k)
	{
		return areShovelsEffectiveOn();
	}

	/**
	 * returns the direction the shoveled block will go in if this block is moving towards iToFacing.  
	 * return -1 if it's no shoveling is taking place.
	 */
	public int getPistonShovelEjectDirection(World world, int i, int j, int k, int iToFacing)
	{
		return -1;
	}

	public AxisAlignedBB getAsPistonMovingBoundingBox(World world, int i, int j, int k)
	{
		return getCollisionBoundingBoxFromPool( world, i, j, k );
	}

	public int adjustMetadataForPistonMove(int iMetadata)
	{
		return iMetadata;
	}

	public boolean canContainPistonPackingToFacing(World world, int i, int j, int k, int iFacing)
	{
		return hasLargeCenterHardPointToFacing( world, i, j, k, iFacing, true );
	}

	public void onBrokenByPistonPush(World world, int i, int j, int k, int iMetadata)
	{
		dropBlockAsItem( world, i, j, k, iMetadata, 0 );
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

	public void setFilterableProperties(int iProperties)
	{
		filterablePropertiesBitfield = iProperties;
	}

	//---------- Falling Block Functionality ----------//

	private static final int loadedRangeToCheckFalling = 32;

	public boolean isFallingBlock()
	{
		return false;
	}

	protected boolean checkForFall(World world, int i, int j, int k)
	{
		if (canFallIntoBlockAtPos(world, i, j - 1, k) && j >= 0 )
		{
			if ( !BlockSand.fallInstantly && world.checkChunksExist(
					i - loadedRangeToCheckFalling, j - loadedRangeToCheckFalling, k - loadedRangeToCheckFalling,
					i + loadedRangeToCheckFalling, j + loadedRangeToCheckFalling, k + loadedRangeToCheckFalling) )
			{
				if ( !world.isRemote )
				{
					FallingBlockEntity fallingEntity = (FallingBlockEntity) EntityList.createEntityOfType(FallingBlockEntity.class, world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D,
							blockID, world.getBlockMetadata( i, j, k ) );

					onStartFalling( fallingEntity );

					world.spawnEntityInWorld( fallingEntity );
				}

				return true;
			}
			else
			{
				world.setBlockToAir( i, j, k );

				while (canFallIntoBlockAtPos(world, i, j - 1, k) && j > 0 )
				{
					j--;
				}

				if ( j > 0 )
				{
					world.setBlock( i, j, k, blockID );
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Only called on server
	 */
	protected void onStartFalling( EntityFallingSand entity ) {}

	/**
	 * This is actually called when a block lands safely.  Do not rename as BlockSand has a child method off of this
	 */
	public void onFinishFalling( World world, int i, int j, int k, int iMetadata )
	{
		notifyNearbyAnimalsFinishedFalling(world, i, j, k);
	}

	public void onFallingUpdate(FallingBlockEntity entity) {}

	public void notifyNearbyAnimalsFinishedFalling(World world, int i, int j, int k)
	{
		if ( !world.isRemote )
		{
			EntityPlayer entityPlayer = world.getClosestPlayer((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, 64D );

			if ( entityPlayer != null )
			{
				world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(entityPlayer, this, i, j, k);
			}
		}
	}

	/** 
	 * returns true if the block still exists
	 */
	public boolean onFinishedFalling(EntityFallingSand entity, float fFallDistance)
	{
		return true;
	}

	/**
	 * returns true if the block has combined with the entity
	 */
	public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		return false;
	}

	public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		return false;
	}

	public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
	}

	public boolean canFallIntoBlockAtPos(World world, int i, int j, int k)
	{
		Block targetBlock = Block.blocksList[world.getBlockId( i, j, k )];

		return targetBlock == null || !targetBlock.canSupportFallingBlocks(world, i, j, k);
	}

	public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k)
	{
		return hasCenterHardPointToFacing( blockAccess, i, j, k, 1, true );
	}

	protected void checkForUnstableGround(World world, int i, int j, int k)
	{
		for ( int iJOffset = 1; iJOffset <= 16; iJOffset++ )
		{
			int iTempJ = j - iJOffset;

			if ( iTempJ <= 0 )
			{
				break;
			}
			else
			{
				if( world.isAirBlock( i, iTempJ, k ) )
				{
					world.notifyBlockOfNeighborChange( i, iTempJ + 1, k, 0 );

					break;
				}
				else
				{    	        	
					int iTempBlockID = world.getBlockId( i, iTempJ, k );

					if ( iTempBlockID == Block.fire.blockID )
					{
						world.notifyBlockOfNeighborChange( i, iTempJ + 1, k, 0 );

						break;
					}
					else
					{
						Block tempBlock = Block.blocksList[iTempBlockID];

						if ( tempBlock.blockMaterial == Material.water || tempBlock.blockMaterial == Material.lava )
						{
							world.notifyBlockOfNeighborChange( i, iTempJ + 1, k, 0 );

							break;
						}
						else if ( !tempBlock.isFallingBlock() )
						{
							break;
						}
					}
				}    			
			}
		}
	}

	public void scheduleCheckForFall(World world, int i, int j, int k)
	{
		world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	}

	/**
	 * Called on server only
	 */
	public void onBlockDestroyedLandingFromFall(World world, int i, int j, int k, int iMetadata)
	{
		onBlockDestroyedWithImproperTool(world, null, i, j, k, iMetadata);
	}

	public boolean hasFallingBlockRestingOn(IBlockAccess blockAccess, int i, int j, int k)
	{
		Block blockAbove = Block.blocksList[blockAccess.getBlockId( i, j + 1, k )];

		return blockAbove != null && blockAbove.isFallingBlock() &&
			   blockAbove.hasCenterHardPointToFacing( blockAccess, i, j + 1, k, 0 );
	}

	//----------- Block Facing Functionality ----------//

	public int getFacing(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getFacing(blockAccess.getBlockMetadata(i, j, k));
	}

	public int getFacing(int iMetadata)
	{
		return 0;
	}

	public void setFacing(World world, int i, int j, int k, int iFacing)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );

		int iNewMetadata = setFacing(iMetadata, iFacing);

		if ( iNewMetadata != iMetadata )
		{
			world.setBlockMetadataWithNotify( i, j, k, iNewMetadata );
		}
	}

	public int setFacing(int iMetadata, int iFacing)
	{
		return iMetadata;
	}

	/**
	 * Cycle through all the possible facings for a block 
	 * returns true if the facing has actually changed as a result of this call
	 */
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{
		return rotateAroundJAxis(world, i, j, k, bReverse);
	}

	public int convertFacingToTopTextureRotation(int iFacing)
	{
		if ( iFacing >= 2 )
		{
			if ( iFacing <= 3 )
			{
				if ( iFacing == 3 )
				{
					return 3;
				}
			}
			else
			{
				if ( iFacing == 4 )
				{
					return 2;
				}
				else // iFacing == 5
				{
					return 1;
				}
			}
		}

		return 0;
	}

	public int convertFacingToBottomTextureRotation(int iFacing)
	{
		if ( iFacing >= 2 )
		{
			if ( iFacing <= 3 )
			{
				if ( iFacing == 3 )
				{
					return 3;
				}
			}
			else
			{
				if ( iFacing == 4 )
				{
					return 1;
				}
				else // iFacing == 5
				{
					return 2;
				}
			}
		}

		return 0;
	}

	static public int getOppositeFacing(int iFacing)
	{
		return iFacing ^ 1;
	}

	static public int rotateFacingAroundY(int iFacing, boolean bReverse)
	{
		if ( bReverse )
		{
			return rotatedFacingsAroundJCounterclockwise[iFacing];
		}

		return rotatedFacingsAroundJClockwise[iFacing];
	}

	static public int cycleFacing(int iFacing, boolean bReverse)
	{
		if ( bReverse )
		{
			return cycledFacingsReversed[iFacing];
		}

		return cycledFacings[iFacing];
	}

	//-------- Turntable Related Functionality --------//

	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return blockAccess.isBlockNormalCube( i, j, k );
	}	

	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return blockAccess.isBlockNormalCube( i, j, k );
	}

	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return blockAccess.isBlockNormalCube( i, j, k );
	}

	/**
	 * Returns the new crafting counter after rotation.  It is unmodified if no crafting has taken place, 
	 * incremented or reset on completion if it has.
	 */
	public int rotateOnTurntable(World world, int x, int y, int z, boolean reverse, int craftingCounter) {
		onRotatedOnTurntable(world, x, y, z);

		if (!rotateAroundJAxis(world, x, y, z, reverse)) {
			// notify the surrounding blocks of a change if no facing change actually takes place, so that buddy can pick up on it
			// this is because solid blocks still "rotate" even if their facing doesn't change
			world.notifyBlocksOfNeighborChange( x, y, z, blockID );
		}
		
		int metadata = world.getBlockMetadata(x, y, z);
		
		TurntableRecipe recipe = TurntableCraftingManager.instance.getRecipe(this, metadata);
		
		if (recipe != null) {
			craftingCounter = this.turntableCraftingRotation(world, x, y, z, reverse, craftingCounter);
		}

		return craftingCounter;
	}

	/*
	 * Intended to play block specific FX and such
	 */
	protected void onRotatedOnTurntable(World world, int x, int y, int z) {}

	protected int turntableCraftingRotation(World world, int x, int y, int z, boolean reverse, int craftingCounter) {
		craftingCounter++;
		
		int metadata = world.getBlockMetadata(x, y, z);
		
		TurntableRecipe recipe = TurntableCraftingManager.instance.getRecipe(this, metadata);

		if (recipe != null) {
			if (craftingCounter >= recipe.getRotationsToCraft()) {
				this.onCraftedOnTurntable(world, x, y, z);
	
				Block output = recipe.getOutputBlock();
				int outputBlockID;
				
				if (output != null) {
					outputBlockID = output.blockID;
				}
				else {
					outputBlockID = 0;
				}
				
				world.setBlockAndMetadataWithNotify(x, y, z, outputBlockID, recipe.getOutputMetadata());
				
				recipe.playCompletionEffect(world, x, y, z);
				
				if (recipe.getItemsEjected() != null) {
					for (ItemStack stack : recipe.getItemsEjected()) {
						for (int i = 0; i < stack.stackSize; i++) {
							ItemUtils.ejectSingleItemWithRandomOffset(world, x, y, z, stack.itemID, stack.getItemDamage());
						}
					}
				}
				
				craftingCounter = 0;
			}
			else {
				recipe.playEffect(world, x, y, z);
			}
		}

		return craftingCounter;
	}

	public void onCraftedOnTurntable(World world, int x, int y, int z) {
		world.playAuxSFX(BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
				x, y, z, world.getBlockId(x, y, z) + (world.getBlockMetadata(x, y, z) << 12));
	}

	/**
	 * Returns true if the facing has actually changed as a result of this call
	 */
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );

		int iNewMetadata = rotateMetadataAroundJAxis(iMetadata, bReverse);

		if ( iNewMetadata != iMetadata )
		{
			world.setBlockMetadataWithNotify( i, j, k, iNewMetadata );

			return true;
		}

		return false;
	}

	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iFacing = getFacing(iMetadata);

		int iNewFacing = rotateFacingAroundY(iFacing, bReverse);

		return setFacing(iMetadata, iNewFacing);
	}

	public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
	{
		return false;
	}

	/**
	 * Returns false if the block was destroyed and should not be rotated
	 */
	public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
	{
		return true;
	}

	public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iInitialFacing, int iRotatedFacing)
	{
		return 0;
	}

	//----- Block Dispenser Related Functionality -----//

	/**
	 * If the stack returned is null, the block will not be retrieved
	 */
	public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );

		if ( canSilkHarvest( iMetadata ) )
		{
			return createStackedBlock( iMetadata );
		}

		int iIdDropped = idDropped( iMetadata, world.rand, 0 );

		if ( iIdDropped > 0 )
		{
			return new ItemStack( iIdDropped, 1, damageDropped( iMetadata ) );
		}

		return null;
	}

	/**
	 * Whether a block is destroyed by the dispenser, even if no item is collected
	 */
	public boolean isBlockDestroyedByBlockDispenser(int iMetadata)
	{
		return false;
	}

	public void onRemovedByBlockDispenser(World world, int i, int j, int k)
	{
		world.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
				i, j, k, blockID + ( world.getBlockMetadata( i, j, k ) << 12 ) );

		world.setBlockWithNotify( i, j, k, 0 );
	}

	//---------- Weather Related Functionality --------//

	/**
	 * Called on server only
	 */
	public void onStruckByLightning(World world, int i, int j, int k)
	{
	}

	//------- Mob Spawning Related Functionality ------//

	/**
	 * This is only a first-pass indicator as to whether ANY mobs can spawn on top of the block,
	 * so stuff like leaves where only Jungle Spiders can spawn on them, should still return true.
	 */
	public boolean canMobsSpawnOn(World world, int i, int j, int k)
	{
		return blockMaterial.getMobsCanSpawnOn(world.provider.dimensionId) &&
				getCollisionBoundingBoxFromPool( world, i, j, k ) != null;
	}

	public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
	{
		return 0F;
	}

	//-------- Collision Handling Functionality -------//

	/**
	 * This should never be modified after a block is initialized to avoid multithreading issues
	 * that occurred with the old min/max bounds variables.
	 */
	private AxisAlignedBB fixedBlockBounds = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, 1D );
	private boolean fixedBlockBoundsSet = false;

	/**
	 * Should only ever be called once for a block.  Repeated calls will silently fail without
	 * changing the bounds.
	 */
	protected void initBlockBounds(double dMinX, double dMinY, double dMinZ,
								   double dMaxX, double dMaxY, double dMaxZ)
	{
		if ( !fixedBlockBoundsSet)
		{
			// only allow the bounds to be set before they're ever accessed to 
			// discourage the kind of errors that necessitated it in the first 
			// place: client and server threads modifying the min/max values 
			// resulting in race conditions.

			fixedBlockBounds.setBounds(dMinX, dMinY, dMinZ, dMaxX, dMaxY, dMaxZ);
		}
	}

	protected void initBlockBounds(AxisAlignedBB bounds)
	{
		if ( !fixedBlockBoundsSet)
		{
			fixedBlockBounds.setBB(bounds);
		}
	}

	protected AxisAlignedBB getFixedBlockBoundsFromPool()
	{
		fixedBlockBoundsSet = true;

		return fixedBlockBounds.makeTemporaryCopy();
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
	}

	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
			IBlockAccess blockAccess, int i, int j, int k)
	{
		return getFixedBlockBoundsFromPool();
	}

	public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, 
			Vec3 startRay, Vec3 endRay )
	{
		return collisionRayTraceVsBlockBounds(world, i, j, k, startRay, endRay);
	}

	public MovingObjectPosition mouseOverRayTrace(World world, int i, int j, int k,
												  Vec3 startRay, Vec3 endRay)
	{
		return collisionRayTrace( world, i, j, k, startRay, endRay );
	}

	public MovingObjectPosition collisionRayTraceVsBlockBounds(World world, int i, int j, int k,
															   Vec3 startRay, Vec3 endRay)
	{
		AxisAlignedBB collisionBox = getBlockBoundsFromPoolBasedOnState(
				world, i, j, k).offset( i, j, k );

		MovingObjectPosition collisionPoint = collisionBox.calculateIntercept( startRay, endRay );

		if ( collisionPoint != null )
		{
			collisionPoint.blockX = i;
			collisionPoint.blockY = j;
			collisionPoint.blockZ = k;
		}

		return collisionPoint;
	}

	//------------- Grazing Functionality -------------//

	private int herbivoreItemFoodValue = 0;
	private int birdItemFoodValue = 0;
	private int pigItemFoodValue = 0;

	public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
								 EntityAnimal byAnimal)
	{
		return false;
	}

	public void onGrazed(World world, int i, int j, int k, EntityAnimal animal)
	{
		world.setBlockToAir( i, j, k );

		Block blockBelow = blocksList[world.getBlockId( i, j - 1, k )];

		if ( blockBelow != null )
		{
			blockBelow.onVegetationAboveGrazed(world, i, j - 1, k, animal);
		}
	}

	public void onVegetationAboveGrazed(World world, int i, int j, int k, EntityAnimal animal)
	{
	}

	/** 
	 * Used when pigs dig up dirt to let any attached neighbors know that they should break loose
	 */
	public void notifyNeighborsBlockDisrupted(World world, int i, int j, int k)
	{
		BlockPos pos = new BlockPos( i, j, k );
		BlockPos tempPos = new BlockPos();

		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
		{
			tempPos.set(pos);
			tempPos.addFacingAsOffset(iTempFacing);

			Block tempBlock = Block.blocksList[world.getBlockId(tempPos.x, tempPos.y, tempPos.z)];

			if ( tempBlock != null )
			{
				tempBlock.onNeighborDisrupted(world, tempPos.x, tempPos.y, tempPos.z,
											  getOppositeFacing(iTempFacing));
			}
		}
	}

	public void onNeighborDisrupted(World world, int i, int j, int k, int iToFacing)
	{
	}

	public int getHerbivoreItemFoodValue(int iItemDamage)
	{
		return herbivoreItemFoodValue;
	}

	public void setHerbivoreItemFoodValue(int iFoodValue)
	{
		herbivoreItemFoodValue = iFoodValue;
	}

	public int getChickenItemFoodValue(int iItemDamage)
	{
		return birdItemFoodValue;
	}

	public void setChickenItemFoodValue(int iFoodValue)
	{
		birdItemFoodValue = iFoodValue;
	}

	public int getPigItemFoodValue(int iItemDamage)
	{
		return pigItemFoodValue;
	}

	public void setPigItemFoodValue(int iFoodValue)
	{
		pigItemFoodValue = iFoodValue;
	}

	//------ Misc Animal Functionality ------//
	
	private boolean alwaysStartlesAnimals;

	public Block setAlwaysStartlesAnimals() {
		this.alwaysStartlesAnimals = true;
		return this;
	}
	
	public boolean startlesAnimalsWhenPlaced(World world, int x, int y, int z) {
		return this.alwaysStartlesAnimals || this.blockMaterial.blocksMovement();
	}
	
	//----------- Plant Growth Functionality ----------//

	public boolean canDomesticatedCropsGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean canReedsGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean canSaplingsGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	/**
	 * Covers stuff like flowers and tall grass
	 */
	public boolean canWildVegetationGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean canNetherWartGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean canCactusGrowOnBlock(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean isBlockHydratedForPlantGrowthOn(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean isConsideredNeighbouringWaterForReedGrowthOn(World world, int i, int j, int k)
	{
		for ( int iTempI = i - 1; iTempI <= i + 1; iTempI++ )
		{		
			for ( int iTempK = k - 1; iTempK <= k + 1; iTempK++ )
			{				
				if ( world.getBlockMaterial( iTempI, j, iTempK ) == Material.water )
				{
					return true;
				}
			}
		}

		return false;
	}

	/** 
	 * This is used by old style non-daily plant growth
	 */
	public float getPlantGrowthOnMultiplier(World world, int i, int j, int k, Block plantBlock)
	{
		return 1F;
	}

	public boolean getIsFertilizedForPlantGrowth(World world, int i, int j, int k)
	{
		return false;
	}

	/** 
	 * Called when a plant hits a full growth stage, like wheat fully grown, 
	 * or each full block of Hemp.  Used to clear fertilizer.
	 */
	public void notifyOfFullStagePlantGrowthOn(World world, int i, int j, int k, Block plantBlock)
	{
	}

	/**
	 * Called server only.  Called AFTER the plant is removed, so it's no longer valid.  
	 */
	public void notifyOfPlantAboveRemoved(World world, int i, int j, int k, Block plantBlock)
	{
	}

	/**
	 * This determines whether weeds can share space with crop blocks, or grow
	 * within their own independent weed blocks
	 */
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}

	/**
	 * The growth level of weeds growing out of this block.  Range of 0 to 7 
	 */
	public int getWeedsGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 0;
	}

	public void removeWeeds(World world, int i, int j, int k)
	{
	}

	public boolean attemptToApplyFertilizerTo(World world, int i, int j, int k)
	{
		return false;
	}

	public boolean getConvertsLegacySoil(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;		
	}

	//----------------- Map related functionality ----------------//

	/**
	 * Gets the color used in map rendering for this block with the specified metadata
	 * @param meta
	 * @return
	 */
	public MapColor getMapColor(int meta) {
		if (mapColorsForMetadata == null) {
			return this.blockMaterial.materialMapColor;
		}
		else {
			try {
				return mapColorsForMetadata[meta];
			}
			catch (Exception e) {
				AddonHandler.logMessage("Map color not found for metadata " + meta + " of block " + this);
				return this.blockMaterial.materialMapColor;
			}
		}
	}

	/**
	 * Set the array of map colors to use per metadata for this block. Make sure you include ALL possible metadata when using this method!
	 * @param mapColors Array of mapcolor objects which is referenced when rendering maps
	 * @return
	 */
	public Block setMapColorsForMetadata(MapColor[] mapColors) {
		this.mapColorsForMetadata = mapColors;
		return this;
	}

	//------ Light Functionality ------//
	
	public static int getLightValueForBlock(IBlockAccess blockAccess, int x, int y, int z, Block block) {
		if (block != null) {
			return block.getLightValue(blockAccess, x, y, z);
		}
		else {
			return 0;
		}
	}
    
    public int getLightValue(IBlockAccess blockAccess, int x, int y, int z) {
    	return lightValue[this.blockID];
    }
    
    //------ Mob spawner conversion functionality ------//
    
    public boolean canBeConvertedByMobSpawner(World world, int x, int y, int z) {
    	return false;
    }
    
    public void convertBlockFromMobSpawner(World world, int x, int y, int z) {}
    
    //------ Fence and wall connection functionality ------//
	
	public boolean shouldWallConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return this.isNormalCube(blockAccess, x, y, z) || this.isWall(blockAccess.getBlockMetadata(x, y, z)) || this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
	}
	
	public boolean shouldFenceConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return this.isNormalCube(blockAccess, x, y, z) || this.isFence(blockAccess.getBlockMetadata(x, y, z)) || this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
	}
	
	public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
		return this.isNormalCube(blockAccess, x, y, z) || this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
	}
	
	public boolean isWall(int metadata) {
		return false;
	}
	
	public boolean isFence(int metadata) {
		return false;
	}
	
	public boolean isBenchOrTable(int metadata) {
		return false;
	}
	
	public boolean shouldWallFormPostBelowThisBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, 0) ||
				this.isBlockRestingOnThatBelow(blockAccess, x, y, z) ||
				this.isFence(blockAccess.getBlockMetadata(x, y, z)) ||
				this.isWall(blockAccess.getBlockMetadata(x, y, z));
	}
	
	//------ Log and Leaf functionality ------//
	
	public boolean isLog(IBlockAccess blockAccess, int x, int y, int z) {
		return false;
	}
	
	public boolean canSupportLeaves(IBlockAccess blockAccess, int x, int y, int z) {
		return this.isLog(blockAccess, x, y, z);
	}
	
	public boolean isLeafBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return false;
	}
	
	//----------------- Integrity Test ----------------//

	static public boolean installationIntegrityTest()
	{
		return true;
	}
	
	//----------- Soul Functionality -----------//
	
	public boolean attemptToAffectBlockWithSoul(World world, int x, int y, int z) {
		return false;
	}
	
	//----------- Client Side Functionality -----------//

	/**
	 * Mainly used by shouldSideBeRendered() so that it can access the current render bounds.
	 * NOTE: Does not apply to item rendering unless specifically set within RenderBlockAsItem()
	 */
    @Environment(EnvType.CLIENT)
    public RenderBlocks currentBlockRenderer = null;

	public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
			int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
	{
		Block neighborBlock = Block.blocksList[blockAccess.getBlockId( iNeighborI, iNeighborJ, iNeighborK )];

		if ( neighborBlock != null )
		{
			return neighborBlock.shouldRenderNeighborFullFaceSide(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
		}

		return true;
	}

    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
	{
		return !isOpaqueCube();
	}

    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
	{
		return !isOpaqueCube();
	}

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{
		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
				renderer.blockAccess, i, j, k) );

		return renderer.renderStandardBlock( this, i, j, k );
	}

	/** 
	 * If the block has a second pass, like a kiln cooking overlay texture, it should Override this method.  This method does not call the overlay
	 * by default to cut down on rendering time, since this function is called by every single loaded block.
	 * Note that this function is necessary to prevent potential recursion within RenderBlock, if it were to call its own overlays
	 * directly, and then potentially get called with a texture overlay itself through RenderBlockWithTexture.
	 */
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
	{    
	}

    @Environment(EnvType.CLIENT)
    public boolean renderBlockWithTexture(RenderBlocks renderBlocks, int i, int j, int k, Icon texture)
	{
		boolean bReturnValue;

		renderBlocks.setOverrideBlockTexture( texture );

		// this test is necessary due to optimizations in RenderStandardFullBlock() that 
		// assumes the texture isn't overriden 
		if ( !renderAsNormalBlock() )
		{
			bReturnValue = renderBlock(renderBlocks, i, j, k);

		}
		else
		{
			renderBlocks.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
					renderBlocks.blockAccess, i, j, k) );

			bReturnValue = renderBlocks.renderStandardBlock( this, i, j, k );            
		}

		renderBlocks.clearOverrideBlockTexture();

		return bReturnValue;
	}

    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int iItemDamage)
	{
		return getFixedBlockBoundsFromPool();
	}

    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
	{
		renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );
	}

    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
	{
		return RenderBlocks.doesRenderIDRenderItemIn3D(getRenderType());
	}

    @Environment(EnvType.CLIENT)
    public void renderCookingByKiLnOverlay(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
	{
		if ( bFirstPassResult && hasKilnRecipe[this.blockID])
		{
			IBlockAccess blockAccess = renderBlocks.blockAccess;

			// check texture override to prevent recursion
			if (!renderBlocks.hasOverrideBlockTexture() && getCanBeCookedByKiLn(blockAccess, i, j, k) )
			{
				int iBlockBelowID = blockAccess.getBlockId( i, j - 1, k );

				if ( iBlockBelowID == BTWBlocks.kiln.blockID )
				{
					Icon overlayTexture = BTWBlocks.kiln.getCookTextureForCurrentState(blockAccess, i, j - 1, k);

					if ( overlayTexture != null )
					{
						renderBlockWithTexture(renderBlocks, i, j, k, overlayTexture);
					}
				} 
			}
		}
	}

    @Environment(EnvType.CLIENT)
    public boolean shouldRenderWhileFalling(World world, EntityFallingSand entity)
	{
		int iCurrentBlockI = MathHelper.floor_double( entity.posX );
		int iCurrentBlockJ = MathHelper.floor_double( entity.posY );
		int iCurrentBlockK = MathHelper.floor_double( entity.posZ );

		int iBlockIDAtLocation = world.getBlockId( iCurrentBlockI, iCurrentBlockJ, iCurrentBlockK );

		return iBlockIDAtLocation != entity.blockID;
	}

	/**
	 * Applies both to falling blocks, and those pushed by pistons
	 */
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
	{
		renderBlocks.setRenderBounds(getFixedBlockBoundsFromPool());

		renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
	}

    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRenderedOnFallingBlock(int iSide, int iMetadata)
	{
		// called within renderBlocks.renderStandardMovingBlock() instead of the usual
		// shouldSideBeRendered() since neighboring blocks aren't relevant while moving

		return true;
	}

    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
	{
		renderBlocks.renderBlockAllFaces( this, i, j, k );
	}

    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
	}

	/** 
	 * Replaces vanilla call in RenderGlobal to provide ray trace info so specific portions of the block can be highlighted as selected
	 */
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, MovingObjectPosition rayTraceHit )
	{
		return getSelectedBoundingBoxFromPool( world, rayTraceHit.blockX, rayTraceHit.blockY, rayTraceHit.blockZ );
	}

	/**
	 * Called by geometric primitives that FCModelBlock uses, to reference textures not associated with a specific block
	 * side.
	 */
    @Environment(EnvType.CLIENT)
    public Icon getIconByIndex(int iIndex)
	{
		return blockIcon;
	}

    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
	{
		return null;
	}

    @Environment(EnvType.CLIENT)
	public void renderCrossHatch(RenderBlocks renderer, int i, int j, int k, Icon icon,
								 double dBorderWidth, double dVerticalOffset)
	{
		Tessellator tessellator = Tessellator.instance;

		double dX = i; 
		double dY = j + dVerticalOffset;
		double dZ = k;

		double dMinU = icon.getMinU();
		double dMinV = icon.getMinV();
		double dMaxU = icon.getMaxU();
		double dMaxV = icon.getMaxV();

		double dX1 = dX + 1D - dBorderWidth;
		double dX2 = dX + dBorderWidth;

		double dZ1 = dZ;
		double dZ2 = dZ + 1D;

		tessellator.addVertexWithUV( dX1, dY + 1D, dZ1, dMinU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ1, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ2, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ2, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ2, dMinU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ2, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ1, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ1, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ2, dMinU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ2, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ1, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ1, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ1, dMinU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ1, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ2, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ2, dMaxU, dMinV );

		dX1 = dX;
		dX2 = dX + 1D;

		dZ1 = dZ + dBorderWidth;
		dZ2 = dZ + 1D - dBorderWidth;

		tessellator.addVertexWithUV( dX1, dY + 1D, dZ1, dMinU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ1, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ1, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ1, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ1, dMinU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ1, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ1, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ1, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ2, dMinU, dMinV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ2, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ2, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ2, dMaxU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 1D, dZ2, dMinU, dMinV );
		tessellator.addVertexWithUV( dX1, dY + 0D, dZ2, dMinU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 0D, dZ2, dMaxU, dMaxV );
		tessellator.addVertexWithUV( dX2, dY + 1D, dZ2, dMaxU, dMinV );
	}
	// END FCMOD
}
