// FCMOD

package btw;

import btw.block.BTWBlocks;
import btw.block.tileentity.beacon.BeaconTileEntity;
import btw.block.tileentity.dispenser.*;
import btw.client.fx.BTWEffectManager;
import btw.client.render.BTWRenderMapper;
import btw.crafting.recipe.RecipeManager;
import btw.entity.mob.villager.TradeList;
import btw.network.packet.BTWPacketManager;
import btw.entity.*;
import btw.item.BTWItems;
import btw.network.packet.HardcoreSpawnPacket;
import btw.util.ColorUtils;
import btw.util.ReflectionUtils;
import btw.world.util.WorldData;
import btw.world.util.WorldUtils;
import btw.world.util.BTWWorldData;

import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Map;

public class BTWMod extends BTWAddon {
    public static BTWMod instance = new BTWMod();

    private Map<String, String> propertyValues;

    // configuration settings
    public static boolean disableMinecartChanges = false;
    public static boolean localEnableHardcoreBuoy = true;
    public static boolean serverEnableHardcoreBuoy = true;
    public static int localHardcorePlayerNamesLevel = 1;
    public static int serverHardcorePlayerNamesLevel = 1;
    public static boolean disableEndText = true;
    public static boolean disableGearBoxPowerDrain = false;
    public static Float nauseaStrength = 5.0F;
    public static boolean increaseGhastSpawns = false;
    public static boolean alwaysSpawnTogether = false;
    public static boolean increaseLargeBiomeHCS = true;
    public static boolean enableSnowRework = false;
    public static boolean allowHardcore = false;
    public static boolean useHardcoreHearts = true;

    //Config setting not exposed, but present to allow addons to use
    public static boolean allowPlaceWhileJumping = false;

    public static String playerSkinURL = "http://crafatar.com/skins/";
    public static String playerCloakURL = "http://crafatar.com/capes/";

    // temporary state variables
    public static boolean isLensBeamBeingRemoved = false;

    // new potion effects
    public static final Potion potionFortune = (new Potion(31, false, 14270531)).setPotionName("potion.fcFortune").setIconIndex(2, 0);
    public static final Potion potionLooting = (new Potion(30, false, 9643043)).setPotionName("potion.fcLooting").setIconIndex(4, 0);
    public static final Potion potionTrueSight = (new Potion(29, false, 14270531)).setPotionName("potion.fcTrueSight").setIconIndex(4, 1);

    public BTWMod() {
        super("Better Than Wolves CE", "2.1.4", "BTW");
    }

    @Override
    public void preInitialize() {
        registerConfigProperties();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage("Better Than Wolves Community Edition Version " + getVersionString() + " Initializing...");
        modInstallationIntegrityTest();

        BTWBlocks.instantiateModBlocks();
        BTWBlocks.createModTileEntityMappings();
        BTWBlocks.initBlocksPotentialFluidSources();

        BTWItems.instantiateModItems();

        BTWDispenserBehaviorManager.initDispenserBehaviors();
        BeaconTileEntity.initializeEffectsByBlockID();

        BTWEntityMapper.createModEntityMappings();

        BTWPacketManager.initPacketInfo();
    
        RecipeManager.addAllModRecipes();
    
        TradeList.addVillagerTrades();

        if (!ReflectionUtils.isObfuscated()) {
            initDebug();
        }

        // client only
        if (!MinecraftServer.getIsServer()) {
            postInitClient();
        }

        AddonHandler.logMessage("Better Than Wolves Initialization Complete.");
    }

    @Override
    public WorldData createWorldData() {
        return new BTWWorldData();
    }

    //------------- Class Specific Methods ------------//

    public void modInstallationIntegrityTest() {
        try {
            World.installationIntegrityTest();
            Block.installationIntegrityTest();
            EntityLiving.installationIntegrityTest();
            EntityPlayer.installationIntegrityTestPlayer();
            EntityItem.installationIntegrityTestEntityItem();
            // Client only
            if (!MinecraftServer.getIsServer()) {
                GuiIngame.installationIntegrityTest();
                GuiContainer.installationIntegrityTest();
                EntityRenderer.installationIntegrityTest();
            }
        }
        catch (Exception e) {
            String errorString = "***Better Than Wolves has not been properly installed.  Please consult the readme.txt file for installation instructions***";

            AddonHandler.logMessage(errorString);

            throw new RuntimeException(e);
        }
    }

    public static boolean isHardcoreBuoyEnabled(World world) {
        if (!world.isRemote) {
            return localEnableHardcoreBuoy;
        }
        else {
            return serverEnableHardcoreBuoy;
        }
    }

    public static boolean isHardcorePlayerNamesEnabled(World world) {
        if (!world.isRemote) {
            return localHardcorePlayerNamesLevel == 1;
        }
        else {
            return serverHardcorePlayerNamesLevel == 1;
        }
    }

    public static boolean areHardcorePlayerNamesObstructed(World world) {
        if (!world.isRemote) {
            return localHardcorePlayerNamesLevel == 2;
        }
        else {
            return serverHardcorePlayerNamesLevel == 2;
        }
    }

    private void registerConfigProperties() {
        //Gameplay config
        this.registerProperty("EnableHardcoreBuoy", "True", "Set the following to false to disable hardcore buoy, which causes some items to float in water");
        this.registerProperty("DisableEndText", "True", "Set the following to false to re-enable the end game text when leaving the end dimension. You'll regret it though.");
        this.registerProperty("DisableGearBoxPowerDrain", "False", "Set the following to true to stop gearboxes from shortening the length of mechanical power pulses through time.");
        this.registerProperty("NauseaStrength", "3", "Determines the strength of the Nausea visual effect, ranging from 0 - 4. 0 disables the effect while 3 is the default strength");
        this.registerProperty("IncreaseGhastSpawns", "False", "Restores the increased ghast spawn rates from before v1.1.0.");
        this.registerProperty("IncreaseLargeBiomeHCS", "True", "Whether to increase Hardcore Spawn related distances in large biomes world type.");
        this.registerProperty("EnableSnowRework", "False", "Enables experimental snow piling mechanics.");
        this.registerProperty("AllowHardcoreMode", "False", "BTW is not designed for vanilla's hardcore mode, and it interferes many of its design features (such as Hardcore Spawn) and its design philosophy.\n" +
                "# Thus, it has been disabled by default, as many players turn on hardcore mode not understanding the implications this has on the game as a whole.\n" +
                "# You REALLY shouldn't be playing hardcore unless you know exactly what you are doing and have an intimate understanding of BTW and how hardcore mode affects the game.");

        //Client only config
        if (!MinecraftServer.getIsServer()) {
            this.registerProperty("UseHardcoreHeartsInSurvival", "True", "Sets the in game health display to always use the hearts from vanilla's hardcore mode");
            
            this.registerProperty("UseBetterGrass", "False", "***Client Settings (do not apply to servers)***\n\n" +
                    "# Set to true to enable MCPatcher's Better Grass, which renders the grass top texture on the sides of grass blocks on 1 block tall slopes");
        }

        //Server only config
        this.registerProperty("EnableHardcorePlayerNames", "2", "***Server and LAN Settings (do not apply to singleplayer)***\n\n"
                + "# Set to 0 to allow players to always see the names of other players\n"
                + "# Set to 1 for hardcore, which displays no player names at all\n"
                + "# Set to 2 for obstructed, which displays names, but not if they are behind other objects like blocks.");
        this.registerProperty("AlwaysSpawnTogether", "False", "Set the following to true to make players always spawn together regardless of progression (default behavior stops spawning together after the nether)");

        //ID config
        registerConfigIDs();
    }

    private void registerConfigIDs() {
        //Block ids
        this.registerProperty("fcBlockSlabFallingID", "175", "***Block IDs***\n\n");
        this.registerProperty("fcBlockArcaneVesselID", "176");
        this.registerProperty("fcBlockAxlePowerSourceID", "177");
        this.registerProperty("fcBlockSidingAndCornerBlackStoneID", "178");
        this.registerProperty("fcBlockMouldingAndDecorativeBlackStoneID", "179");
        this.registerProperty("fcBlockAestheticOpaqueEarthID", "180");
        this.registerProperty("fcBlockCandleID", "181");
        this.registerProperty("fcBlockSandstoneSidingAndCornerID", "182");
        this.registerProperty("fcBlockSandstoneMouldingAndDecorativeID", "183");
        this.registerProperty("fcBlockWoodOakSidingAndCornerID", "184");
        this.registerProperty("fcBlockSmoothStoneSidingAndCornerID", "185");
        this.registerProperty("fcBlockBrickSidingAndCornerID", "186");
        this.registerProperty("fcBlockBrickMouldingAndDecorativeID", "187");
        this.registerProperty("fcBlockNetherBrickSidingAndCornerID", "188");
        this.registerProperty("fcBlockNetherBrickMouldingAndDecorativeID", "189");
        this.registerProperty("fcBlockWhiteStoneStairsID", "190");
        this.registerProperty("fcBlockWhiteStoneSidingAndCornerID", "191");
        this.registerProperty("fcBlockWhiteStoneMouldingAndDecorativeID", "192");
        this.registerProperty("fcBlockStakeStringID", "193");
        this.registerProperty("fcBlockStakeID", "194");
        this.registerProperty("fcBlockScrewPumpID", "195");
        this.registerProperty("fcBlockWoodSpruceSidingAndCornerID", "196");
        this.registerProperty("fcBlockWoodSpruceMouldingID", "197");
        this.registerProperty("fcBlockWoodBirchSidingAndCornerID", "198");
        this.registerProperty("fcBlockWoodBirchMouldingID", "199");
        this.registerProperty("fcBlockWoodJungleSidingAndCornerID", "200");
        this.registerProperty("fcBlockWoodJungleMouldingID", "201");
        this.registerProperty("fcBlockStoneBrickSidingAndCornerID", "202");
        this.registerProperty("fcBlockStoneBrickMouldingID", "203");
        this.registerProperty("fcBlockFarmlandFertilizedID", "204");
        this.registerProperty("fcBlockWoolSlabTopID", "205");
        this.registerProperty("fcBlockDirtSlabID", "206");
        this.registerProperty("fcBlockNetherGrowthID", "207");
        this.registerProperty("fcInfernalEnchanterID", "208");
        this.registerProperty("fcSoulforgedSteelBlockID", "209");
        this.registerProperty("fcBlockDetectorGlowingLogicID", "210");
        this.registerProperty("fcLeavesID", "211");
        this.registerProperty("fcBloodWoodID", "212");
        this.registerProperty("fcAestheticVegetationID", "213");
        this.registerProperty("fcStoneMouldingID", "214");
        this.registerProperty("fcAestheticOpaqueID", "215");
        this.registerProperty("fcAestheticNonOpaqueID", "216");
        this.registerProperty("fcMiningChargeID", "217");
        this.registerProperty("fcBuddyBlockID", "218");
        this.registerProperty("fcKilnID", "219");
        this.registerProperty("fcWoolSlabID", "220");
        this.registerProperty("fcAnvilID", "221");
        this.registerProperty("fcLightBulbOffID", "222");
        this.registerProperty("fcLightBulbOnID", "223");
        this.registerProperty("fcBBQID", "224");
        this.registerProperty("fcHopperID", "225");
        this.registerProperty("fcSawID", "226");
        this.registerProperty("fcPlatformID", "227");
        this.registerProperty("fcCementID", "228");
        this.registerProperty("fcPulleyID", "229");
        this.registerProperty("fcPressurePlateObsidianID", "230");
        this.registerProperty("fcMouldingID", "231");
        this.registerProperty("fcCornerID", "232");
        this.registerProperty("fcBlockDispenserID", "233");
        this.registerProperty("fcCauldronID", "234");
        this.registerProperty("fcDetectorRailWoodID", "235");
        this.registerProperty("fcDetectorRailObsidianID", "236");
        this.registerProperty("fcCompanionCubeID", "237");
        this.registerProperty("fcBlockDetectorID", "238");
        this.registerProperty("fcBlockDetectorLogicID", "239");
        this.registerProperty("fcBlockLensID", "240");
        this.registerProperty("fcHempCropID", "241");
        this.registerProperty("fcHandCrankID", "242");
        this.registerProperty("fcMillStoneID", "243");
        this.registerProperty("fcAnchorID", "244");
        this.registerProperty("fcRopeBlockID", "245");
        this.registerProperty("fcOmniSlabID", "246");
        this.registerProperty("fcAxleBlockID", "247");
        this.registerProperty("fcGearBoxID", "248");
        this.registerProperty("fcTurntableID", "249");
        this.registerProperty("fcBellowsID", "250");
        this.registerProperty("fcStokedFireID", "251");
        this.registerProperty("fcUnfiredPotteryID", "252");
        this.registerProperty("fcCrucibleID", "253");
        this.registerProperty("fcPlanterID", "254");
        this.registerProperty("fcVaseID", "255");

        this.registerProperty("fcBlockRottenFleshID", "1000");
        this.registerProperty("fcBlockShaftID", "1001");
        this.registerProperty("fcBlockSoulforgeDormantID", "1002");
        this.registerProperty("fcBlockSmoothstoneStairsID", "1003");
        this.registerProperty("fcBlockRottenFleshSlabID", "1004");
        this.registerProperty("fcBlockBoneSlabID", "1005");
        this.registerProperty("fcBlockPumpkinFreshID", "1006");
        this.registerProperty("fcBlockWoodBloodSidingAndCornerID", "1007");
        this.registerProperty("fcBlockWoodBloodMouldingAndDecorativeID", "1008");
        this.registerProperty("fcBlockWoodBloodStairsID", "1009");
        this.registerProperty("fcBlockLogDamagedID", "1010");
        this.registerProperty("fcBlockDirtLooseID", "1011");
        this.registerProperty("fcBlockDirtLooseSlabID", "1012");
        this.registerProperty("fcBlockCampfireUnlitID", "1013");
        this.registerProperty("fcBlockCampfireSmallID", "1014");
        this.registerProperty("fcBlockCampfireMediumID", "1015");
        this.registerProperty("fcBlockCampfireLargeID", "1016");
        this.registerProperty("fcBlockUnfiredBrickID", "1017");
        this.registerProperty("fcBlockCookedBrickID", "1018");
        this.registerProperty("fcBlockBrickLooseID", "1019");
        this.registerProperty("fcBlockBrickLooseSlabID", "1020");
        this.registerProperty("fcBlockCobblestoneLooseID", "1021");
        this.registerProperty("fcBlockCobblestoneLooseSlabID", "1022");
        this.registerProperty("fcBlockFurnaceBrickIdleID", "1023");
        this.registerProperty("fcBlockFurnaceBrickBurningID", "1024");
        this.registerProperty("fcBlockTorchFiniteIdleID", "1025");
        this.registerProperty("fcBlockTorchFiniteBurningID", "1026");
        this.registerProperty("fcBlockStoneRoughID", "1027");
        this.registerProperty("fcBlockStoneRoughMidStrataID", "1028");
        this.registerProperty("fcBlockStoneRoughDeepStrataID", "1029");
        this.registerProperty("fcBlockWorkStumpID", "1030");
        this.registerProperty("fcBlockBasketWickerID", "1031");
        this.registerProperty("fcBlockLogSpikeID", "1032");
        this.registerProperty("fcBlockTorchIdleID", "1033");
        this.registerProperty("fcBlockWorkbenchID", "1034");
        this.registerProperty("fcBlockChestID", "1035");
        this.registerProperty("fcBlockDoorWoodID", "1036");
        this.registerProperty("fcBlockWebID", "1037");
        this.registerProperty("fcBlockUnfiredClayID", "1038");
        this.registerProperty("fcBlockMyceliumSlabID", "1039");
        this.registerProperty("fcBlockToolPlacedID", "1040");
        this.registerProperty("fcBlockBrickLooseStairsID", "1041");
        this.registerProperty("fcBlockCobblestoneLooseStairsID", "1042");
        this.registerProperty("fcBlockLogSmoulderingID", "1043");
        this.registerProperty("fcBlockWoodCindersID", "1044");
        this.registerProperty("fcBlockStumpCharredID", "1045");
        this.registerProperty("fcBlockAshGroundCoverID", "1046");
        this.registerProperty("fcBlockSnowLooseID", "1047");
        this.registerProperty("fcBlockSnowLooseSlabID", "1048");
        this.registerProperty("fcBlockSnowSolidID", "1049");
        this.registerProperty("fcBlockSnowSolidSlabID", "1050");
        this.registerProperty("fcBlockLadderID", "1051");
        this.registerProperty("fcBlockLadderOnFireID", "1052");
        this.registerProperty("fcBlockShovelID", "1053");
        this.registerProperty("fcBlockHamperID", "1054");
        this.registerProperty("fcBlockCreeperOystersID", "1055");
        this.registerProperty("fcBlockCreeperOystersSlabID", "1056");
        this.registerProperty("fcBlockTorchNetherBurningID", "1057");
        this.registerProperty("fcBlockBucketEmptyID", "1058");
        this.registerProperty("fcBlockBucketWaterID", "1059");
        this.registerProperty("fcBlockBucketCementID", "1060");
        this.registerProperty("fcBlockBucketMilkID", "1061");
        this.registerProperty("fcBlockBucketMilkChocolateID", "1062");
        this.registerProperty("fcBlockMilkID", "1063");
        this.registerProperty("fcBlockMilkChocolateID", "1064");
        this.registerProperty("fcBlockGearBoxID", "1065");
        this.registerProperty("fcBlockSpikeIronID", "1066");
        this.registerProperty("fcBlockLightningRodID", "1067");
        this.registerProperty("fcBlockChunkOreIronID", "1068");
        this.registerProperty("fcBlockChunkOreGoldID", "1069");
        this.registerProperty("fcBlockStoneBrickLooseID", "1070");
        this.registerProperty("fcBlockStoneBrickLooseSlabID", "1071");
        this.registerProperty("fcBlockStoneBrickLooseStairsID", "1072");
        this.registerProperty("fcBlockNetherBrickLooseID", "1073");
        this.registerProperty("fcBlockNetherBrickLooseSlabID", "1074");
        this.registerProperty("fcBlockNetherBrickLooseStairsID", "1075");
        this.registerProperty("fcBlockNetherrackFallingID", "1076");
        this.registerProperty("fcBlockLavaPillowID", "1077");
        this.registerProperty("fcBlockMushroomCapBrownID", "1078");
        this.registerProperty("fcBlockMushroomCapRedID", "1079");
        this.registerProperty("fcBlockChunkOreStorageIronID", "1080");
        this.registerProperty("fcBlockChunkOreStorageGoldID", "1081");
        this.registerProperty("fcBlockWickerID", "1082");
        this.registerProperty("fcBlockWickerSlabID", "1083");
        this.registerProperty("fcBlockWickerPaneID", "1084");
        this.registerProperty("fcBlockGrateID", "1085");
        this.registerProperty("fcBlockSlatsID", "1086");
        this.registerProperty("fcBlockFarmlandNewID", "1087");
        this.registerProperty("fcBlockFarmlandFertilizedNewID", "1088");
        this.registerProperty("fcBlockWheatCropID", "1089");
        this.registerProperty("fcBlockWheatCropTopID", "1090");
        this.registerProperty("fcBlockWeedsID", "1091");
        this.registerProperty("fcBlockPlanterSoilID", "1092");
        this.registerProperty("fcBlockReedsID", "1093");
        this.registerProperty("fcBlockReedRootsID", "1094");
        this.registerProperty("fcBlockCarrotID", "1095");
        this.registerProperty("fcBlockCarrotFlowersID", "1096");
        this.registerProperty("fcBlockCandlePlainID", "1097");
        for (int i = 0; i < 16; i++) {
            int id = 1098 + i;
            String idProperty = "fcBlockCandle" + ColorUtils.colorOrderCapital[i] + "ID";

            this.registerProperty(idProperty, Integer.toString(id));
        }
        this.registerProperty("fcBlockSilverfishStoneID", "1114");
        this.registerProperty("fcBlockSilverfishStoneSecondStrataID", "1115");
        this.registerProperty("fcBlockSilverfishStoneThirdStrataID", "1116");
        this.registerProperty("fcBlockSilverfishCobblestoneID", "1117");
        this.registerProperty("fcBlockSilverfishStoneBricksID", "1118");
        this.registerProperty("fcBlockSilverfishStoneBricksMossyID", "1119");
        this.registerProperty("fcBlockSilverfishStoneBricksCrackedID", "1120");
        this.registerProperty("fcBlockSilverfishStoneBricksChiseledID", "1121");
        this.registerProperty("fcBlockGrassSlabID", "1122");
        this.registerProperty("fcBlockCobblestoneSlabSingleID", "1123");
        this.registerProperty("fcBlockCobblestoneSlabDoubleID", "1124");
        this.registerProperty("fcBlockStoneBrickSlabSingleID", "1125");
        this.registerProperty("fcBlockStoneBrickSlabDoubleID", "1126");
        this.registerProperty("fcBlockSmoothStoneSlabSingleID", "1127");
        this.registerProperty("fcBlockSmoothStoneSlabDoubleID", "1128");
        this.registerProperty("fcBlockCobblestoneStairsMidStrataID", "1129");
        this.registerProperty("fcBlockCobblestoneStairsDeepStrataID", "1130");
        this.registerProperty("fcBlockCobblestoneLooseStairsMidStrataID", "1131");
        this.registerProperty("fcBlockCobblestoneLooseStairsDeepStrataID", "1132");
        this.registerProperty("fcBlockStoneBrickStairsMidStrataID", "1133");
        this.registerProperty("fcBlockStoneBrickStairsDeepStrataID", "1134");
        this.registerProperty("fcBlockStoneBrickLooseStairsMidStrataID", "1135");
        this.registerProperty("fcBlockStoneBrickLooseStairsDeepStrataID", "1136");
        this.registerProperty("fcBlockSmoothstoneStairsMidStrataID", "1137");
        this.registerProperty("fcBlockSmoothstoneStairsDeepStrataID", "1138");
        this.registerProperty("fcBlockStoneBrickSidingAndCornerMidStrataID", "1139");
        this.registerProperty("fcBlockStoneBrickMouldingMidStrataID", "1140");
        this.registerProperty("fcBlockSmoothStoneSidingAndCornerMidStrataID", "1141");
        this.registerProperty("fcBlockSmoothStoneMouldingMidStrataID", "1142");
        this.registerProperty("fcBlockStoneBrickSidingAndCornerDeepStrataID", "1143");
        this.registerProperty("fcBlockStoneBrickMouldingDeepStrataID", "1144");
        this.registerProperty("fcBlockSmoothStoneSidingAndCornerDeepStrataID", "1145");
        this.registerProperty("fcBlockSmoothStoneMouldingDeepStrataID", "1146");
        this.registerProperty("fcBlockSilverfishCobblestoneMidStrataID", "1147");
        this.registerProperty("fcBlockSilverfishCobblestoneDeepStrataID", "1148");
        this.registerProperty("fcBlockSilverfishStoneBricksMidStrataID", "1149");
        this.registerProperty("fcBlockSilverfishStoneBricksMossyMidStrataID", "1150");
        this.registerProperty("fcBlockSilverfishStoneBricksCrackedMidStrataID", "1151");
        this.registerProperty("fcBlockSilverfishStoneBricksChiseledMidStrataID", "1152");
        this.registerProperty("fcBlockSilverfishStoneBricksDeepStrataID", "1153");
        this.registerProperty("fcBlockSilverfishStoneBricksMossyDeepStrataID", "1154");
        this.registerProperty("fcBlockSilverfishStoneBricksCrackedDeepStrataID", "1155");
        this.registerProperty("fcBlockSilverfishStoneBricksChiseledDeepStrataID", "1156");
        this.registerProperty("fcBlockBedrollID", "1157");
        this.registerProperty("fcBlockSaplingOakID", "1158");
        this.registerProperty("fcBlockSaplingSpruceID", "1159");
        this.registerProperty("fcBlockSaplingBirchID", "1160");
        this.registerProperty("fcBlockSaplingJungleID", "1161");
        this.registerProperty("fcBlockLoomID", "1162");
        this.registerProperty("fcBlockGrassSparseLooseID", "1163");
        this.registerProperty("fcBlockGrassSparseLooseSlabID", "1164");
        
        //Block IDs borrowed from deco
        this.registerProperty("fcBlockLogSpikeSpruceID", "3031");
        this.registerProperty("fcBlockLogSpikeBirchID", "3032");
        this.registerProperty("fcBlockLogSpikeJungleID", "3033");
        this.registerProperty("fcBlockLogDamagedSpruceID", "3206");
        this.registerProperty("fcBlockLogDamagedBirchID", "3207");
        this.registerProperty("fcBlockLogDamagedJungleID", "3208");
        this.registerProperty("fcBlockSpiderEyeID", "3307");
        this.registerProperty("fcBlockSpiderEyeSlabID", "3308");

        //Item ids
        this.registerProperty("fcBucketCementID", "222", "***Item IDs***\n\n");
        this.registerProperty("fcWolfRawID", "223");
        this.registerProperty("fcWolfCookedID", "224");
        this.registerProperty("fcNethercoalID", "225");
        this.registerProperty("fcHempSeedsID", "226");
        this.registerProperty("fcHempID", "227");
        this.registerProperty("fcGearID", "228");
        this.registerProperty("fcFlourID", "229");
        this.registerProperty("fcHempFibersID", "230");
        this.registerProperty("fcScouredLeatherID", "231");
        this.registerProperty("fcDonutID", "232");
        this.registerProperty("fcRopeItemID", "233");
        this.registerProperty("fcRollersItemID", "234");
        this.registerProperty("fcDungID", "235");
        this.registerProperty("fcWaterWheelItemID", "236");
        this.registerProperty("fcWindMillBladeItemID", "237");
        this.registerProperty("fcWindMillItemID", "238");
        this.registerProperty("fcHempClothID", "239");
        this.registerProperty("fcGrateID", "240");
        this.registerProperty("fcWickerID", "241");
        this.registerProperty("fcTannedLeatherID", "242");
        this.registerProperty("fcStrapID", "243");
        this.registerProperty("fcBeltID", "244");
        this.registerProperty("fcFoulFoodID", "245");
        this.registerProperty("fcWoodBladeID", "246");
        this.registerProperty("fcGlueID", "247");
        this.registerProperty("fcTallowID", "248");
        this.registerProperty("fcHaftID", "249");
        this.registerProperty("fcSteelID", "250");
        this.registerProperty("fcRefinedPickAxeID", "251");
        this.registerProperty("fcRefinedShovelID", "252");
        this.registerProperty("fcRefinedHoeID", "253");
        this.registerProperty("fcBattleAxeID", "254");
        this.registerProperty("fcRefinedSwordID", "255");
        this.registerProperty("fcGroundNetherrackID", "256");
        this.registerProperty("fcHellfireDustID", "257");
        this.registerProperty("fcConcentratedHellfireID", "258");
        this.registerProperty("fcArmorPlateID", "259");
        this.registerProperty("fcPlateHelmID", "260");
        this.registerProperty("fcPlateBreastPlateID", "261");
        this.registerProperty("fcPlateLeggingsID", "262");
        this.registerProperty("fcPlateBootsID", "263");
        this.registerProperty("fcCompositeBowID", "264");
        this.registerProperty("fcBroadheadArrowheadID", "265");
        this.registerProperty("fcBroadheadArrowID", "266");
        this.registerProperty("fcCoalDustID", "267");
        this.registerProperty("fcPaddingID", "268");
        this.registerProperty("fcFilamentID", "269");
        this.registerProperty("fcPolishedLapisID", "270");
        this.registerProperty("fcUrnID", "271");
        this.registerProperty("fcSoulUrnID", "272");
        this.registerProperty("fcHardBoiledEggID", "273");
        this.registerProperty("fcPotashID", "274");
        this.registerProperty("fcSoapID", "275");
        this.registerProperty("fcSawDustID", "276");
        this.registerProperty("fcTannedLeatherHelmID", "277");
        this.registerProperty("fcTannedLeatherChestID", "278");
        this.registerProperty("fcTannedLeatherLeggingsID", "279");
        this.registerProperty("fcTannedLeatherBootsID", "280");
        this.registerProperty("fcDynamiteID", "281");
        this.registerProperty("fcBreedingHarnessID", "282");
        this.registerProperty("fcSoulDustID", "283");
        this.registerProperty("fcMattockID", "284");
        this.registerProperty("fcRefinedAxeID", "285");
        this.registerProperty("fcNetherSludgeID", "286");
        this.registerProperty("fcNetherBrickID", "287");

        this.registerProperty("fcItemTuningForkID", "22222");
        this.registerProperty("fcItemArcaneScrollID", "22223");
        this.registerProperty("fcItemCandleID", "22224");
        this.registerProperty("fcItemNetherGrowthSporesID", "22225");
        this.registerProperty("fcItemMouldID", "22226");
        this.registerProperty("fcItemCanvasID", "22227");
        this.registerProperty("fcItemDogFoodID", "22228");
        this.registerProperty("fcItemRawEggID", "22229");
        this.registerProperty("fcItemFriedEggID", "22230");
        this.registerProperty("fcItemScrewID", "22231");
        this.registerProperty("fcItemRottenArrowID", "22232");
        this.registerProperty("fcItemOcularOfEnderID", "22233");
        this.registerProperty("fcItemEnderSpectaclesID", "22234");
        this.registerProperty("fcItemStakeID", "22235");
        this.registerProperty("fcItemBrimstoneID", "22236");
        this.registerProperty("fcItemNitreID", "22237");
        this.registerProperty("fcItemElementID", "22238");
        this.registerProperty("fcItemFuseID", "22239");
        this.registerProperty("fcItemBlastingOilID", "22240");
        this.registerProperty("fcItemWindMillVerticalID", "22241");
        this.registerProperty("fcItemBoiledPotatoID", "22242");
        this.registerProperty("fcItemMuttonRawID", "22243");
        this.registerProperty("fcItemMuttonCookedID", "22244");
        this.registerProperty("fcItemWitchWartID", "22245");
        this.registerProperty("fcItemCookedCarrotID", "22246");
        this.registerProperty("fcItemTastySandwichID", "22247");
        this.registerProperty("fcItemSteakAndPotatoesID", "22248");
        this.registerProperty("fcItemHamAndEggsID", "22249");
        this.registerProperty("fcItemSteakDinnerID", "22250");
        this.registerProperty("fcItemPorkDinnerID", "22251");
        this.registerProperty("fcItemWolfDinnerID", "22252");
        this.registerProperty("fcItemRawKebabID", "22253");
        this.registerProperty("fcItemCookedKebabID", "22254");
        this.registerProperty("fcItemChickenSoupID", "22255");
        this.registerProperty("fcItemFishSoupID", "22256");
        this.registerProperty("fcItemHeartyStewID", "22257");
        this.registerProperty("fcItemMushroomRedID", "22258");
        this.registerProperty("fcItemMushroomBrownID", "22259");
        this.registerProperty("fcItemNuggetIronID", "22260");
        this.registerProperty("fcItemMailID", "22261");
        this.registerProperty("fcItemRawMysteryMeatID", "22262");
        this.registerProperty("fcItemCookedMysteryMeatID", "22263");
        this.registerProperty("fcItemRawMushroomOmeletID", "22264");
        this.registerProperty("fcItemCookedMushroomOmeletID", "22265");
        this.registerProperty("fcItemRawScrambledEggsID", "22266");
        this.registerProperty("fcItemCookedScrambledEggsID", "22267");
        this.registerProperty("fcItemCreeperOystersID", "22268");
        this.registerProperty("fcItemArmorWoolHelmID", "22269");
        this.registerProperty("fcItemArmorWoolChestID", "22270");
        this.registerProperty("fcItemArmorWoolLeggingsID", "22271");
        this.registerProperty("fcItemArmorWoolBootsID", "22272");
        this.registerProperty("fcItemArmorPaddedHelmID", "22273");
        this.registerProperty("fcItemArmorPaddedChestID", "22274");
        this.registerProperty("fcItemArmorPaddedLeggingsID", "22275");
        this.registerProperty("fcItemArmorPaddedBootsID", "22276");
        this.registerProperty("fcItemArmorTannedHelmID", "22277");
        this.registerProperty("fcItemArmorTannedChestID", "22278");
        this.registerProperty("fcItemArmorTannedLeggingsID", "22279");
        this.registerProperty("fcItemArmorTannedBootsID", "22280");
        this.registerProperty("fcItemIngotDiamondID", "22281");
        this.registerProperty("fcItemLeatherCutID", "22282");
        this.registerProperty("fcItemTannedLeatherCutID", "22283");
        this.registerProperty("fcItemScouredLeatherCutID", "22284");
        this.registerProperty("fcItemFishingRodBaitedID", "22285");
        this.registerProperty("fcItemPileDirtID", "22286");
        this.registerProperty("fcItemPileSandID", "22287");
        this.registerProperty("fcItemPileGravelID", "22288");
        this.registerProperty("fcItemBatWingID", "22289");
        this.registerProperty("fcItemGoldenDungID", "22290");
        this.registerProperty("fcItemBarkID", "22291");
        this.registerProperty("fcItemPileSoulSandID", "22292");
        this.registerProperty("fcItemRedstoneLatchID", "22293");
        this.registerProperty("fcItemNuggetSteelID", "22294");

        this.registerProperty("fcItemWoolID", "2295");
        this.registerProperty("fcItemCocoaBeansID", "2296");
        this.registerProperty("fcItemChocolateID", "2297");
        this.registerProperty("fcItemBucketChocolateMilkID", "2298");
        this.registerProperty("fcItemSoulFluxID", "2299");
        this.registerProperty("fcItemEnderSlagID", "2300");
        this.registerProperty("fcItemPastryUncookedCakeID", "2301");
        this.registerProperty("fcItemPastryUncookedCookiesID", "2302");
        this.registerProperty("fcItemPastryUncookedPumpkinPieID", "2303");
        this.registerProperty("fcItemMysteriousGlandID", "2304");
        this.registerProperty("fcItemBeastLiverRawID", "2305");
        this.registerProperty("fcItemBeastLiverCookedID", "2306");
        this.registerProperty("fcItemAncientProphecyID", "2307");
        this.registerProperty("fcItemStumpRemoverID", "2308");

        this.registerProperty("fcItemChiselWoodID", "22309");
        this.registerProperty("fcItemStoneID", "22310");
        this.registerProperty("fcItemChiselStoneID", "22311");
        this.registerProperty("fcItemClubID", "22312");
        this.registerProperty("fcItemFireStarterSticksID", "22313");
        this.registerProperty("fcItemFireStarterBowID", "22314");
        this.registerProperty("fcItemChunkIronOreID", "22315");
        this.registerProperty("fcItemPileIronOreID", "22316");
        this.registerProperty("fcItemChiselIronID", "22317");
        this.registerProperty("fcItemChunkGoldOreID", "22318");
        this.registerProperty("fcItemPileGoldOreID", "22319");
        this.registerProperty("fcItemWickerPieceID", "22320");
        this.registerProperty("fcItemKnittingNeedlesID", "22321");
        this.registerProperty("fcItemKnittingID", "22322");
        this.registerProperty("fcItemWoolKnitID", "22323");
        this.registerProperty("fcItemClubBoneID", "22324");
        this.registerProperty("fcItemMeatCuredID", "22325");
        this.registerProperty("fcItemMetalFragmentID", "22326");
        this.registerProperty("fcItemPileClayID", "22327");
        this.registerProperty("fcItemMeatBurnedID", "22328");
        this.registerProperty("fcItemChickenFeedID", "22329");
        this.registerProperty("fcItemFishHookBoneID", "22330");
        this.registerProperty("fcItemCarvingBoneID", "22331");
        this.registerProperty("fcItemBrickStoneID", "22332");
        this.registerProperty("fcItemWickerWeavingID", "22333");
        this.registerProperty("fcItemWheatID", "22334");
        this.registerProperty("fcItemWheatSeedsID", "22335");
        this.registerProperty("fcItemBreadDoughID", "22336");
        this.registerProperty("fcItemStrawID", "22337");
        this.registerProperty("fcItemBrickUnfiredID", "22338");
        this.registerProperty("fcItemNetherBrickUnfiredID", "22339");
        this.registerProperty("fcItemReedRootsID", "22340");
        this.registerProperty("fcItemCarrotID", "22341");
        this.registerProperty("fcItemCarrotSeedsID", "22342");
        this.registerProperty("fcItemDiamondPlateID", "22343");
        this.registerProperty("fcItemMelonMashedID", "22344");
        this.registerProperty("fcItemBedrollID", "22345");
        this.registerProperty("fcItemCandleNewID", "22346");
        this.registerProperty("fcItemCorpseEyeID", "22347");
        this.registerProperty("fcItemTangledWebID", "22348");
        this.registerProperty("fcItemWebUntanglingID", "22349");
        this.registerProperty("fcItemSinewID", "22350");
        this.registerProperty("fcItemSinewExtractingBeefID", "22351");
        this.registerProperty("fcItemSinewExtractingWolfID", "22352");

        //Item IDs reused from deco
        this.registerProperty("fcItemNameTagID", "30005");
        this.registerProperty("fcItemShearsDiamondID", "30006");
        this.registerProperty("fcItemChiselDiamondID", "30050");

        //Entity ids
        this.registerProperty("fcWaterWheelEntityID", "222", "***Entity IDs***\n\n");
        this.registerProperty("fcWindMillEntityID", "223");
        this.registerProperty("fcMovingAnchorEntityID", "224");
        this.registerProperty("fcMovingPlatformEntityID", "225");
        this.registerProperty("fcBlockLiftedByPlatformEntityID", "226");
        this.registerProperty("fcBroadheadArrowEntityID", "227");
        this.registerProperty("fcUrnEntityID", "228");
        this.registerProperty("fcDynamiteEntityID", "229");
        this.registerProperty("fcMiningChargeEntityID", "230");
        this.registerProperty("fcInfiniteArrowEntityID", "231");
        this.registerProperty("fcItemFloatingEntityID", "232");
        this.registerProperty("fcItemBloodWoodSaplingEntityID", "233");
        this.registerProperty("fcCanvasEntityID", "234");
        this.registerProperty("fcRottenArrowEntityID", "235");
        this.registerProperty("fcEntityWindMillVerticalID", "236");
        this.registerProperty("fcEntitySpiderWebID", "237");
        this.registerProperty("fcEntityDireWolfID", "238");
        this.registerProperty("fcEntitySoulSandID", "239");
        this.registerProperty("fcEntityJungleSpiderID", "240");
        this.registerProperty("fcWitherPersistentID", "241");
        this.registerProperty("fcEntityCorpseEyeID", "242");
        //Villager ids ported from extended addon API
        this.registerProperty("addonVillagerFarmer", "600");
        this.registerProperty("addonVillagerLibrarian", "601");
        this.registerProperty("addonVillagerPriest", "602");
        this.registerProperty("addonVillagerBlacksmith", "603");
        this.registerProperty("addonVillagerButcher", "604");

        //Container ids
        this.registerProperty("fcMillStoneContainerID", "222", "***Container IDs***\n\n");
        this.registerProperty("fcCauldronContainerID", "223");
        this.registerProperty("fcHopperContainerID", "224");
        this.registerProperty("fcCrucibleContainerID", "225");
        this.registerProperty("fcAnvilContainerID", "226");
        this.registerProperty("fcBlockDispenserContainerID", "227");
        this.registerProperty("fcPulleyContainerID", "228");
        this.registerProperty("fcInfernalEnchanterContainerID", "229");
        this.registerProperty("fcFurnaceBrickContainerID", "230");
        this.registerProperty("fcHamperContainerID", "231");
        this.registerProperty("fcVanillaAnvilContainerID", "232");
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;

        localEnableHardcoreBuoy = Boolean.parseBoolean(this.propertyValues.get("EnableHardcoreBuoy"));
        disableEndText = Boolean.parseBoolean(this.propertyValues.get("DisableEndText"));
        disableGearBoxPowerDrain = Boolean.parseBoolean(this.propertyValues.get("DisableGearBoxPowerDrain"));

        setNauseaStrength(Integer.parseInt(this.propertyValues.get("NauseaStrength")));
        increaseGhastSpawns = Boolean.parseBoolean(this.propertyValues.get("IncreaseGhastSpawns"));
        increaseLargeBiomeHCS = Boolean.parseBoolean(this.propertyValues.get("IncreaseLargeBiomeHCS"));
        enableSnowRework = Boolean.parseBoolean(this.propertyValues.get("EnableSnowRework"));
        allowHardcore = Boolean.parseBoolean(this.propertyValues.get("AllowHardcoreMode"));

        localHardcorePlayerNamesLevel = Integer.parseInt(this.propertyValues.get("EnableHardcorePlayerNames"));
        alwaysSpawnTogether = Boolean.parseBoolean(this.propertyValues.get("AlwaysSpawnTogether"));

        if (!MinecraftServer.getIsServer()) {
            useHardcoreHearts = Boolean.parseBoolean(this.propertyValues.get("UseHardcoreHeartsInSurvival"));
            RenderBlocksUtils.enableBetterGrass = Boolean.parseBoolean(this.propertyValues.get("UseBetterGrass"));
        }
    }
    
    /**
    logic handled here to reduce calculations at runtime
    int 0-4 turns to  F 320, 80, 20, 5, 1.25
     5F used to be vanilla's strength, 320F will make the method disable nausea wobble
    */
    private static void setNauseaStrength(int nauseaStrength) {
        BTWMod.nauseaStrength = 1.25F * (float) Math.pow(4, 4 - MathHelper.clamp_int(nauseaStrength, 0, 4));
    }
    
    public int parseID(String name) {
        try {
            return Integer.parseInt(this.propertyValues.get(name));
        }
        catch (NumberFormatException e) {
            if (this.propertyValues.get(name) == null) {
                throw new IllegalArgumentException("Unable to find property " + name + " in addon " + this.addonName);
            }
            else {
                throw new IllegalArgumentException("Invalid id value for property " + name + " in addon " + this.addonName + ". Check for stray whitespace");
            }
        }
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP player) {
        if (!MinecraftServer.getServer().isSinglePlayer()) {
            // send server connect message, which is displayed by all clients whether they have the mod installed or not
            WorldUtils.sendPacketToPlayer(serverHandler, new Packet3Chat("\247e" + // yellow text
                                                                         player.username + " connected to Better Than Wolves CE server V" + this.getVersionString()));
        }

        // setup the server options packet
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        byte hardcoreBuoy = 0;

        if (localEnableHardcoreBuoy) {
            hardcoreBuoy = 1;
        }

        byte hardcorePlayerNames = (byte) localHardcorePlayerNamesLevel;

        try {
            dataStream.writeByte(hardcoreBuoy);
            dataStream.writeByte(hardcorePlayerNames);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        // send packet
        Packet250CustomPayload optionsPacket = new Packet250CustomPayload(BTWPacketManager.BTW_OPTIONS_PACKET_CHANNEL, byteStream.toByteArray());
        WorldUtils.sendPacketToPlayer(serverHandler, optionsPacket);
        
        HardcoreSpawnPacket packet = new HardcoreSpawnPacket(player.getTimeOfLastSpawnAssignment());
        player.playerNetServerHandler.sendPacketToPlayer(packet);

        if (!MinecraftServer.getServer().isSinglePlayer()) {
            // send server options message
            String optionsString = "\247f" // white
                    + "Hardcore Modes: Buoy ";

            if (localEnableHardcoreBuoy) {
                optionsString += "(on)";
            }
            else {
                optionsString += "(off)";
            }

            optionsString += " Player Names ";

            if (localHardcorePlayerNamesLevel == 1) {
                optionsString += "(Hardcore)";
            }
            else if (localHardcorePlayerNamesLevel == 2) {
                optionsString += "(Obstructed)";
            }
            else {
                optionsString += "(Displayed)";
            }

            WorldUtils.sendPacketToPlayer(serverHandler, new Packet3Chat(optionsString));
        }
    }

    public static void serverOpenCustomInterface(EntityPlayerMP player, Container container, int containerID) {
        try {
            int windowID = player.incrementAndGetWindowID();

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);

            dataStream.writeInt(windowID);
            dataStream.writeInt(containerID);

            Packet250CustomPayload packet = new Packet250CustomPayload(BTWPacketManager.CUSTOM_INTERFACE_PACKET_CHANNEL, byteStream.toByteArray());

            WorldUtils.sendPacketToPlayer(player.playerNetServerHandler, packet);

            player.openContainer = container;
            player.openContainer.windowId = windowID;

            player.openContainer.addCraftingToCrafters(player);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isSinglePlayerNonLan() {
        if (MinecraftServer.getIsServer()) {
            return false;
        }
        else {
            return Minecraft.getMinecraft().isSingleplayer() && !Minecraft.getMinecraft().getIntegratedServer().getPublic();
        }
    }

    public static void debugChatOutput(String string) {
        if (MinecraftServer.getServer() != null) {
            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(
                    new Packet3Chat(string));

            AddonHandler.logMessage(string);
        }
    }

    public static void debugWarning(String string) {
        if (MinecraftServer.getServer() != null) {
            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(
                    new Packet3Chat("\247E" // yellow text
                            + "WARNING: " + string));

            AddonHandler.logWarning(string);
        }
    }

    private void initDebug() {

    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private void postInitClient() {
        BTWRenderMapper.initEntityRenderers();
        BTWRenderMapper.initTileEntityRenderers();
        BTWEffectManager.initEffects();
    }
}