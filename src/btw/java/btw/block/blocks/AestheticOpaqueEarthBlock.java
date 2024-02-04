// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.HoeItem;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class AestheticOpaqueEarthBlock extends Block {
    public final static int SUBTYPE_BLIGHT_LEVEL_0 = 0;
    public final static int SUBTYPE_BLIGHT_LEVEL_1 = 1;
    public final static int SUBTYPE_BLIGHT_LEVEL_2 = 2;
    public final static int SUBTYPE_BLIGHT_ROOTS_LEVEL_2 = 3;
    public final static int SUBTYPE_BLIGHT_LEVEL_3 = 4;
    public final static int SUBTYPE_BLIGHT_ROOTS_LEVEL_3 = 5;
    public final static int SUBTYPE_PACKED_EARTH = 6;
    public final static int SUBTYPE_DUNG = 7;

    public static final int[] subtypeToBlightLevel = new int[]{0, 1, 2, 2, 3, 3, -1, -1};

    public static final int[] blightLevelToSubtype =
            new int[]{SUBTYPE_BLIGHT_LEVEL_0, SUBTYPE_BLIGHT_LEVEL_1, SUBTYPE_BLIGHT_LEVEL_2, SUBTYPE_BLIGHT_LEVEL_3};

    public final static int M_I_NUM_SUBTYPES = 8;

    public AestheticOpaqueEarthBlock(int iBlockID) {
        super(iBlockID, Material.ground);

        setHardness(0.6F);
        setShovelsEffectiveOn(true);
        setHoesEffectiveOn();

        setTickRandomly(true);

        setStepSound(soundGravelFootstep);

        setCreativeTab(CreativeTabs.tabBlock);

        setUnlocalizedName("fcBlockAestheticOpaqueEarth");
    }

    @Override
    public int damageDropped(int iMetadata) {
        if (isBlightFromMetadata(iMetadata)) {
            return 0;
        }

        return iMetadata;
    }

    @Override
    public int idDropped(int iMetadata, Random random, int iFortuneModifier) {
        if (isBlightFromMetadata(iMetadata)) {
            return BTWBlocks.looseDirt.blockID;
        }

        return blockID;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
        if (isBlightFromMetadata(iMetadata)) {
            if (!world.isRemote) {
                int iNumDropped = 8;

                for (int iTempCount = 0; iTempCount < iNumDropped; iTempCount++) {
                    dropBlockAsItem_do(world, i, j, k, new ItemStack(BTWItems.dirtPile));
                }
            }
        }
        else {
            super.dropBlockAsItemWithChance(world, i, j, k, iMetadata, fChance, iFortuneModifier);
        }
    }

    @Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (isBlightFromMetadata(iSubtype)) {
            blightRandomUpdateTick(world, i, j, k, rand);
        }
    }

    @Override
    protected ItemStack createStackedBlock(int iMetadata) {
        // overriden to provide custom silk-touch drops

        int iItemDamage = iMetadata;

        if (iMetadata == SUBTYPE_BLIGHT_LEVEL_1 || iMetadata == SUBTYPE_BLIGHT_LEVEL_2 || iMetadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_2) {
            iItemDamage = SUBTYPE_BLIGHT_LEVEL_0;
        }
        else if (iMetadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_3) {
            iItemDamage = SUBTYPE_BLIGHT_LEVEL_3;
        }

        return new ItemStack(blockID, 1, iItemDamage);
    }

    @Override
    public float getMovementModifier(World world, int i, int j, int k) {
        float fModifier = 1F;

        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_PACKED_EARTH) {
            fModifier = 1.2F;
        }
        else if (iSubtype == SUBTYPE_DUNG) {
            fModifier = 0.8F;
        }

        return fModifier;
    }

    @Override
    public StepSound getStepSound(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_DUNG) {
            return BTWBlocks.stepSoundSquish;
        }

        return stepSound;
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        dropAsPiles(world, i, j, k, iMetadata, 1F);

        if (iMetadata != SUBTYPE_DUNG && iMetadata != SUBTYPE_PACKED_EARTH) {
            onDirtDugWithImproperTool(world, i, j, k);
        }
    }

    private void dropAsPiles(World world, int i, int j, int k, int iMetadata, float fChanceOfPileDrop) {
        Item itemToDrop = BTWItems.dirtPile;
        int iCountToDrop = 6;
        int iSubtype = iMetadata;

        if (iSubtype == SUBTYPE_PACKED_EARTH) {
            iCountToDrop = 12;
        }
        else if (iSubtype == SUBTYPE_DUNG) {
            itemToDrop = BTWItems.dung;
            iCountToDrop = 8;
        }

        for (int iTempCount = 0; iTempCount < iCountToDrop; iTempCount++) {
            if (world.rand.nextFloat() <= fChanceOfPileDrop) {
                ItemStack tempStack = new ItemStack(itemToDrop);

                dropBlockAsItem_do(world, i, j, k, tempStack);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion par1Explosion) {
        return false;
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
        int iMetadata = world.getBlockMetadata(i, j, k);

        float fChanceOfPileDrop = 1F;

        if (explosion != null) {
            fChanceOfPileDrop = 1F / explosion.explosionSize;
        }

        dropAsPiles(world, i, j, k, iMetadata, fChanceOfPileDrop);

        if (iMetadata != SUBTYPE_DUNG && iMetadata != SUBTYPE_PACKED_EARTH) {
            onDirtDugWithImproperTool(world, i, j, k);
        }
    }

    @Override
    public boolean canSaplingsGrowOnBlock(World world, int i, int j, int k) {
        return isBlightFromMetadata(world.getBlockMetadata(i, j, k));
    }

    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k) {
        return true;
    }

    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
        return stack != null && stack.getItem() instanceof HoeItem;
    }

    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
        if (isBlightFromMetadata(world.getBlockMetadata(i, j, k))) {
            world.setBlockWithNotify(i, j, k, BTWBlocks.looseDirt.blockID);

            if (!world.isRemote) {
                world.playAuxSFX(2001, i, j, k, blockID); // block break FX
            }

            return true;
        }

        return false;
    }

    //------------- Class Specific Methods ------------//

    public boolean isSurfaceBlightFromMetadata(int iMetadata) {
        return iMetadata >= SUBTYPE_BLIGHT_LEVEL_0 && iMetadata <= SUBTYPE_BLIGHT_LEVEL_3 && iMetadata != SUBTYPE_BLIGHT_ROOTS_LEVEL_2;
    }

    public boolean isBlightFromMetadata(int iMetadata) {
        return iMetadata >= SUBTYPE_BLIGHT_LEVEL_0 && iMetadata <= SUBTYPE_BLIGHT_ROOTS_LEVEL_3;
    }

    private int getRootsSubtypeForLevel(int iLevel) {
        if (iLevel >= 3) {
            return SUBTYPE_BLIGHT_ROOTS_LEVEL_3;
        }
        else {
            return SUBTYPE_BLIGHT_ROOTS_LEVEL_2;
        }
    }

    private void blightRandomUpdateTick(World world, int i, int j, int k, Random rand) {
        if (!checkForBlightSurfaceConversions(world, i, j, k)) {
            int iBlockSubtype = world.getBlockMetadata(i, j, k);

            if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_1) {
                blightKillLeaves(world, i, j, k, rand);
            }
            else if (iBlockSubtype >= SUBTYPE_BLIGHT_LEVEL_2) {
                blightKillVinesAndLeaves(world, i, j, k, rand);
            }

            if (world.provider.dimensionId != 1) {
                checkForBlightSpread(world, i, j, k, rand);

                checkForBlightEvolution(world, i, j, k, rand);
            }
        }
    }

    private void checkForSpreadToLocation(World world, int i, int j, int k, int iBlightSubtype) {
        int iTargetBlockID = world.getBlockId(i, j, k);

        if (iTargetBlockID > 0) {
            int iBlightLevel = subtypeToBlightLevel[iBlightSubtype];

            if (iTargetBlockID == blockID) {
                // evolve lower level blight

                int iTargetMetadata = world.getBlockMetadata(i, j, k);
                int iTargetBlightLevel = subtypeToBlightLevel[iTargetMetadata];

                if (iTargetBlightLevel < iBlightLevel && iTargetBlightLevel >= 0) {
                    if (isSurfaceBlightFromMetadata(iTargetMetadata)) {
                        if (iBlightLevel == 3) {
                            iTargetBlightLevel = 3;
                        }
                        else {
                            iTargetBlightLevel++;
                        }

                        world.setBlockMetadataWithNotify(i, j, k, blightLevelToSubtype[iTargetBlightLevel]);
                    }
                    else {
                        world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_BLIGHT_ROOTS_LEVEL_3);
                    }
                }
            }
            else {
                Block targetBlock = Block.blocksList[iTargetBlockID];

                if (targetBlock.getCanBlightSpreadToBlock(world, i, j, k, iBlightLevel)) {
                    if (iBlightLevel < 3) {
                        if (Block.lightOpacity[world.getBlockId(i, j + 1, k)] <= 2) {
                            world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_LEVEL_0);
                        }
                    }
                    else {
                        if (Block.lightOpacity[world.getBlockId(i, j + 1, k)] <= 2) {
                            world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_LEVEL_3);
                        }
                        else {
                            world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_ROOTS_LEVEL_3);
                        }
                    }
                }
            }
        }
    }

    private void checkForBlightSpread(World world, int i, int j, int k, Random rand) {
        int iBlockSubtype = world.getBlockMetadata(i, j, k);

        if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_0) {
            int iRandI = i + rand.nextInt(3) - 1;
            int iRandJ = j + rand.nextInt(3) - 1;
            int iRandK = k + rand.nextInt(3) - 1;

            checkForSpreadToLocation(world, iRandI, iRandJ, iRandK, iBlockSubtype);
        }
        else if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_1) {
            // check for spread

            for (int iTempCount = 0; iTempCount < 2; iTempCount++) {
                int iRandI = i + rand.nextInt(3) - 1;
                int iRandJ = j + rand.nextInt(4) - 1;
                int iRandK = k + rand.nextInt(3) - 1;

                checkForSpreadToLocation(world, iRandI, iRandJ, iRandK, iBlockSubtype);
            }
        }
        else // levels 2 & 3
        {
            // check for spread

            for (int iTempCount = 0; iTempCount < 4; iTempCount++) {
                int iRandI = i + rand.nextInt(3) - 1;
                int iRandJ = j + rand.nextInt(5) - 2;
                int iRandK = k + rand.nextInt(3) - 1;

                checkForSpreadToLocation(world, iRandI, iRandJ, iRandK, iBlockSubtype);
            }

            // grow roots

            int iRootsSubtype = getRootsSubtypeForLevel(subtypeToBlightLevel[iBlockSubtype]);

            if (world.getBlockId(i, j - 1, k) == Block.dirt.blockID) {
                world.setBlockAndMetadataWithNotify(i, j - 1, k, blockID, iRootsSubtype);
            }

            if (world.getBlockId(i, j + 1, k) == Block.dirt.blockID) {
                world.setBlockAndMetadataWithNotify(i, j + 1, k, blockID, iRootsSubtype);
            }
        }
    }

    private void checkForBlightEvolution(World world, int i, int j, int k, Random rand) {
        int iBlockSubtype = world.getBlockMetadata(i, j, k);

        if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_0) {
            // check for evolution

            int iRandomFacing = rand.nextInt(6);

            BlockPos targetPos = new BlockPos(i, j, k, iRandomFacing);

            if (world.getBlockMaterial(targetPos.x, targetPos.y, targetPos.z) == Material.water) {
                world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_BLIGHT_LEVEL_1);
            }
        }
        else if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_1) {
            // check for evolution

            int iRandomFacing = rand.nextInt(6);

            BlockPos targetPos = new BlockPos(i, j, k, iRandomFacing);

            if (world.getBlockMaterial(targetPos.x, targetPos.y, targetPos.z) == Material.lava) {
                world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_BLIGHT_LEVEL_2);
            }
        }
        else if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_2 || iBlockSubtype == SUBTYPE_BLIGHT_ROOTS_LEVEL_2) {
            // check for evolution

            int iRandI = i + rand.nextInt(7) - 3;
            int iRandJ = j + rand.nextInt(7) - 3;
            int iRandK = k + rand.nextInt(7) - 3;

            int iTargetBlockID = world.getBlockId(iRandI, iRandJ, iRandK);

            if (iTargetBlockID == Block.portal.blockID) {
                if (iBlockSubtype == SUBTYPE_BLIGHT_LEVEL_2) {
                    world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_BLIGHT_LEVEL_3);
                }
                else {
                    world.setBlockMetadataWithNotify(i, j, k, SUBTYPE_BLIGHT_ROOTS_LEVEL_3);
                }
            }
        }
    }

    /**
     * Returns true if the blight has changed forms
     */
    private boolean checkForBlightSurfaceConversions(World world, int i, int j, int k) {
        int iBlightSubtype = world.getBlockMetadata(i, j, k);
        int iBlockAboveID = world.getBlockId(i, j + 1, k);

        if (Block.lightOpacity[iBlockAboveID] > 2) {
            // below surface

            if (iBlightSubtype == SUBTYPE_BLIGHT_LEVEL_0) {
                world.setBlockWithNotify(i, j, k, Block.dirt.blockID);

                return true;
            }
            else if (iBlightSubtype == SUBTYPE_BLIGHT_LEVEL_2) {
                world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_ROOTS_LEVEL_2);

                return true;
            }
            else if (iBlightSubtype == SUBTYPE_BLIGHT_LEVEL_3) {
                world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_ROOTS_LEVEL_3);

                return true;
            }
        }
        else {
            // on surface

            if (iBlightSubtype == SUBTYPE_BLIGHT_ROOTS_LEVEL_2) {
                world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_LEVEL_2);

                return true;
            }
            if (iBlightSubtype == SUBTYPE_BLIGHT_ROOTS_LEVEL_3) {
                world.setBlockAndMetadataWithNotify(i, j, k, blockID, SUBTYPE_BLIGHT_LEVEL_3);

                return true;
            }
        }

        return false;
    }

    private void blightKillLeaves(World world, int i, int j, int k, Random rand) {
        for (int iTempCount = 0; iTempCount < 4; ++iTempCount) {
            int iRandI = i + rand.nextInt(3) - 1;
            int iRandJ = j + rand.nextInt(9);
            int iRandK = k + rand.nextInt(3) - 1;

            int iTargetBlockID = world.getBlockId(iRandI, iRandJ, iRandK);

            if (iTargetBlockID == Block.leaves.blockID) {
                world.setBlockWithNotify(iRandI, iRandJ, iRandK, 0);
            }
        }
    }

    private void blightKillVinesAndLeaves(World world, int i, int j, int k, Random rand) {
        for (int iTempCount = 0; iTempCount < 4; ++iTempCount) {
            int iRandI = i + rand.nextInt(3) - 1;
            int iRandJ = j + rand.nextInt(9);
            int iRandK = k + rand.nextInt(3) - 1;

            int iTargetBlockID = world.getBlockId(iRandI, iRandJ, iRandK);

            if (iTargetBlockID == Block.leaves.blockID || iTargetBlockID == Block.vine.blockID) {
                world.setBlockWithNotify(iRandI, iRandJ, iRandK, 0);
            }
        }
    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconDung;
    @Environment(EnvType.CLIENT)
    private Icon iconPackedEarth;

    @Environment(EnvType.CLIENT)
    private Icon[] iconBlightLevel0SideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon[] iconBlightLevel1SideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon[] iconBlightLevel2SideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon[] iconBlightLevel3SideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon[] iconBlightRootsLevel2SideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon iconBlightRootsLevel3;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon("dirt"); // for hit effects

        iconDung = register.registerIcon("fcBlockDung");
        iconPackedEarth = register.registerIcon("FCBlockPackedEarth");

        iconBlightLevel0SideArray[0] = register.registerIcon("FCBlockBlightL0_bottom");
        iconBlightLevel0SideArray[1] = register.registerIcon("FCBlockBlightL0_top");

        Icon sideIcon = register.registerIcon("FCBlockBlightL0_side");

        iconBlightLevel0SideArray[2] = sideIcon;
        iconBlightLevel0SideArray[3] = sideIcon;
        iconBlightLevel0SideArray[4] = sideIcon;
        iconBlightLevel0SideArray[5] = sideIcon;

        iconBlightLevel1SideArray[0] = register.registerIcon("FCBlockBlightL1_bottom");
        iconBlightLevel1SideArray[1] = register.registerIcon("FCBlockBlightL1_top");

        sideIcon = register.registerIcon("FCBlockBlightL1_side");

        iconBlightLevel1SideArray[2] = sideIcon;
        iconBlightLevel1SideArray[3] = sideIcon;
        iconBlightLevel1SideArray[4] = sideIcon;
        iconBlightLevel1SideArray[5] = sideIcon;

        iconBlightLevel2SideArray[0] = register.registerIcon("FCBlockBlightL2_bottom");
        iconBlightLevel2SideArray[1] = register.registerIcon("FCBlockBlightL2_top");

        sideIcon = register.registerIcon("FCBlockBlightL2_side");

        iconBlightLevel2SideArray[2] = sideIcon;
        iconBlightLevel2SideArray[3] = sideIcon;
        iconBlightLevel2SideArray[4] = sideIcon;
        iconBlightLevel2SideArray[5] = sideIcon;

        iconBlightLevel3SideArray[0] = register.registerIcon("FCBlockBlightL3_roots");
        iconBlightLevel3SideArray[1] = register.registerIcon("FCBlockBlightL3_top");

        sideIcon = register.registerIcon("FCBlockBlightL3_side");

        iconBlightLevel3SideArray[2] = sideIcon;
        iconBlightLevel3SideArray[3] = sideIcon;
        iconBlightLevel3SideArray[4] = sideIcon;
        iconBlightLevel3SideArray[5] = sideIcon;

        iconBlightRootsLevel2SideArray[0] = register.registerIcon("FCBlockBlightL2_bottom");

        sideIcon = register.registerIcon("FCBlockBlightL2_roots");

        iconBlightRootsLevel2SideArray[1] = sideIcon;
        iconBlightRootsLevel2SideArray[2] = sideIcon;
        iconBlightRootsLevel2SideArray[3] = sideIcon;
        iconBlightRootsLevel2SideArray[4] = sideIcon;
        iconBlightRootsLevel2SideArray[5] = sideIcon;

        iconBlightRootsLevel3 = register.registerIcon("FCBlockBlightL3_roots");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        if (iMetadata == SUBTYPE_BLIGHT_LEVEL_0) {
            return iconBlightLevel0SideArray[iSide];
        }
        else if (iMetadata == SUBTYPE_BLIGHT_LEVEL_1) {
            return iconBlightLevel1SideArray[iSide];
        }
        else if (iMetadata == SUBTYPE_BLIGHT_LEVEL_2) {
            return iconBlightLevel2SideArray[iSide];
        }
        else if (iMetadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_2) {
            return iconBlightRootsLevel2SideArray[iSide];
        }
        else if (iMetadata == SUBTYPE_BLIGHT_LEVEL_3) {
            return iconBlightLevel3SideArray[iSide];
        }
        else if (iMetadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_3) {
            return iconBlightRootsLevel3;
        }
        else if (iMetadata == SUBTYPE_PACKED_EARTH) {
            return iconPackedEarth;
        }
        else if (iMetadata == SUBTYPE_DUNG) {
            return iconDung;
        }

        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_LEVEL_0));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_LEVEL_1));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_LEVEL_2));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_LEVEL_3));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_ROOTS_LEVEL_2));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BLIGHT_ROOTS_LEVEL_3));
        list.add(new ItemStack(blockID, 1, SUBTYPE_PACKED_EARTH));
        list.add(new ItemStack(blockID, 1, SUBTYPE_DUNG));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue(World world, int x, int y, int z) {
        // used only by pick block
        int metadata = world.getBlockMetadata(x, y, z);

        if (metadata == SUBTYPE_BLIGHT_LEVEL_0 || metadata == SUBTYPE_BLIGHT_LEVEL_1 || metadata == SUBTYPE_BLIGHT_LEVEL_2 ||
            metadata == SUBTYPE_BLIGHT_LEVEL_3 || metadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_2 || metadata == SUBTYPE_BLIGHT_ROOTS_LEVEL_3) {
            return metadata;
        }

        return super.getDamageValue(world, x, y, z);
    }
}
