// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class AestheticOpaqueBlock extends Block {
    public final static int SUBTYPE_WICKER = 0; // deprecated
    public final static int SUBTYPE_DUNG = 1; // deprecated
    public final static int SUBTYPE_STEEL = 2;
    public final static int SUBTYPE_HELLFIRE = 3;
    public final static int SUBTYPE_PADDING = 4;
    public final static int SUBTYPE_SOAP = 5;
    public final static int SUBTYPE_ROPE = 6;
    public final static int SUBTYPE_FLINT = 7;
    public final static int SUBTYPE_NETHERRACK_WITH_GROWTH = 8;
    public final static int SUBTYPE_WHITE_STONE = 9;
    public final static int SUBTYPE_WHITE_COBBLE = 10;
    public final static int SUBTYPE_BARREL = 11;
    public final static int SUBTYPE_CHOPPING_BLOCK_DIRTY = 12;
    public final static int SUBTYPE_CHOPPING_BLOCK_CLEAN = 13;
    public final static int SUBTYPE_ENDER_BLOCK = 14;
    public final static int SUBTYPE_BONE = 15;

    public final static int NUM_SUBTYPES = 16;

    private final static float DEFAULT_HARDNESS = 2F;

    public AestheticOpaqueBlock(int blockID) {
        super(blockID, BTWBlocks.miscMaterial);

        setHardness(DEFAULT_HARDNESS);
        setAxesEffectiveOn(true);
        setPicksEffectiveOn(true);

        setStepSound(soundStoneFootstep);

        setCreativeTab(CreativeTabs.tabBlock);

        setUnlocalizedName("fcBlockAestheticOpaque");
    }

    @Override
    public int damageDropped(int metadata) {
        if (metadata == SUBTYPE_WICKER) {
            return 0;
        }
        else if (metadata == SUBTYPE_STEEL) {
            // the new block type should not have a metadata value

            return 0;
        }
        else if (metadata == SUBTYPE_FLINT) {
            return 0;
        }
        else if (metadata == SUBTYPE_NETHERRACK_WITH_GROWTH) {
            return 0;
        }
        else if (metadata == SUBTYPE_WHITE_STONE) {
            return SUBTYPE_WHITE_COBBLE;
        }

        return metadata;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        if (metadata == SUBTYPE_WICKER) {
            return BTWBlocks.wickerBlock.blockID;
        }
        else if (metadata == SUBTYPE_STEEL) {
            // convert to the new steel block

            return BTWBlocks.soulforgedSteelBlock.blockID;
        }
        else if (metadata == SUBTYPE_FLINT) {
            return Item.flint.itemID;
        }
        else if (metadata == SUBTYPE_NETHERRACK_WITH_GROWTH) {
            return Block.netherrack.blockID;
        }

        return blockID;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int metadata, float chance, int fortuneModifier) {
        if (metadata == SUBTYPE_FLINT) {
            if (world.isRemote) {
                return;
            }

            int iNumDropped = 9;

            for (int k1 = 0; k1 < iNumDropped; k1++) {
                int iItemID = idDropped(metadata, world.rand, fortuneModifier);

                if (iItemID > 0) {
                    dropBlockAsItem_do(world, i, j, k, new ItemStack(iItemID, 1, damageDropped(metadata)));
                }
            }
        }
        else {
            super.dropBlockAsItemWithChance(world, i, j, k, metadata, chance, fortuneModifier);
        }
    }

    @Override
    protected boolean canSilkHarvest(int metadata) {

        if (metadata == SUBTYPE_NETHERRACK_WITH_GROWTH || metadata == SUBTYPE_STEEL) {
            return false;
        }

        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int changedBlockID) {
        int iSubType = world.getBlockMetadata(i, j, k);

        if (iSubType == SUBTYPE_NETHERRACK_WITH_GROWTH) {
            int iBlockAboveID = world.getBlockId(i, j + 1, k);

            if (iBlockAboveID != BTWBlocks.netherGroth.blockID) {
                // convert back to regular netherrack if we don't have a growth above us

                world.setBlock(i, j, k, Block.netherrack.blockID);
            }
        }
    }

    @Override
    public boolean doesInfiniteBurnToFacing(IBlockAccess blockAccess, int i, int j, int k, int facing) {
        int iSubType = blockAccess.getBlockMetadata(i, j, k);

        return iSubType == SUBTYPE_HELLFIRE;
    }

    @Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_WICKER || iSubtype == SUBTYPE_DUNG || iSubtype == SUBTYPE_PADDING || iSubtype == SUBTYPE_SOAP || iSubtype == SUBTYPE_ROPE ||
            iSubtype == SUBTYPE_BARREL || iSubtype == SUBTYPE_CHOPPING_BLOCK_DIRTY || iSubtype == SUBTYPE_CHOPPING_BLOCK_CLEAN || iSubtype == SUBTYPE_BONE)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean onBlockSawed(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_CHOPPING_BLOCK_DIRTY || iSubtype == SUBTYPE_CHOPPING_BLOCK_CLEAN) {
            return false;
        }

        return super.onBlockSawed(world, i, j, k);
    }

    @Override
    public float getMovementModifier(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_DUNG) {
            return 1F;
        }

        return 1.2F;
    }

    @Override
    public StepSound getStepSound(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_DUNG) {
            return BTWBlocks.stepSoundSquish;
        }
        else if (iSubtype == SUBTYPE_BONE) {
            return soundGravelFootstep;
        }

        return stepSound;
    }

    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        return iSubtype == SUBTYPE_DUNG || iSubtype == SUBTYPE_BONE || iSubtype == SUBTYPE_SOAP;
    }

    @Override
    public boolean canToolsStickInBlock(IBlockAccess blockAccess, int i, int j, int k) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        return iSubtype != SUBTYPE_WICKER;
    }

    //------------- Class Specific Methods ------------//

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconWicker;
    @Environment(EnvType.CLIENT)
    private Icon iconDung;
    @Environment(EnvType.CLIENT)
    private Icon iconSteel;
    @Environment(EnvType.CLIENT)
    private Icon iconHellfire;
    @Environment(EnvType.CLIENT)
    private Icon iconPadding;
    @Environment(EnvType.CLIENT)
    private Icon iconSoap;
    @Environment(EnvType.CLIENT)
    private Icon iconSoapTop;
    @Environment(EnvType.CLIENT)
    private Icon iconRopeSide;
    @Environment(EnvType.CLIENT)
    private Icon iconRopeTop;
    @Environment(EnvType.CLIENT)
    private Icon iconFlint;
    @Environment(EnvType.CLIENT)
    private Icon iconNetherrackWithGrothSide;
    @Environment(EnvType.CLIENT)
    private Icon iconNetherrackWithGrothTop;
    @Environment(EnvType.CLIENT)
    private Icon iconNetherrackWithGrothBottom;
    @Environment(EnvType.CLIENT)
    private Icon iconWhiteStone;
    @Environment(EnvType.CLIENT)
    private Icon iconWhiteCobble;
    @Environment(EnvType.CLIENT)
    private Icon iconBarrelTop;
    @Environment(EnvType.CLIENT)
    private Icon iconBarrelSide;
    @Environment(EnvType.CLIENT)
    private Icon iconChoppingBlock;
    @Environment(EnvType.CLIENT)
    private Icon iconChoppingBlockDirty;
    @Environment(EnvType.CLIENT)
    private Icon iconEnderBlock;
    @Environment(EnvType.CLIENT)
    private Icon iconBoneSide;
    @Environment(EnvType.CLIENT)
    private Icon iconBoneTop;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon("stone"); // for hit effects

        iconWicker = register.registerIcon("fcBlockWicker");
        iconDung = register.registerIcon("fcBlockDung");
        iconSteel = register.registerIcon("fcBlockSoulforgedSteel");
        iconHellfire = register.registerIcon("fcBlockConcentratedHellfire");
        iconPadding = register.registerIcon("fcBlockPadding");
        iconSoap = register.registerIcon("fcBlockSoap");
        iconSoapTop = register.registerIcon("fcBlockSoap_top");
        iconRopeSide = register.registerIcon("fcBlockRope_side");
        iconRopeTop = register.registerIcon("fcBlockRope_top");
        iconFlint = register.registerIcon("bedrock");
        iconNetherrackWithGrothSide = register.registerIcon("fcBlockNetherrackGrothed_side");
        iconNetherrackWithGrothTop = register.registerIcon("fcBlockNetherrackGrothed_top");
        iconNetherrackWithGrothBottom = register.registerIcon("fcBlockNetherrackGrothed_bottom");
        iconWhiteStone = register.registerIcon("fcBlockWhiteStone");
        iconWhiteCobble = register.registerIcon("fcBlockWhiteCobble");
        iconBarrelTop = register.registerIcon("fcBlockBarrel_top");
        iconBarrelSide = register.registerIcon("fcBlockBarrel_side");
        iconChoppingBlock = register.registerIcon("fcBlockChoppingBlock");
        iconChoppingBlockDirty = register.registerIcon("fcBlockChoppingBlock_dirty");
        iconEnderBlock = register.registerIcon("fcBlockEnder");
        iconBoneSide = register.registerIcon("fcBlockBone_side");
        iconBoneTop = register.registerIcon("fcBlockBone_top");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metaData) {
        int iSubType = metaData;

        switch (iSubType) {
            case SUBTYPE_WICKER:

                return iconWicker;

            case SUBTYPE_DUNG:

                return iconDung;

            case SUBTYPE_STEEL:

                return iconSteel;

            case SUBTYPE_HELLFIRE:

                return iconHellfire;

            case SUBTYPE_PADDING:

                return iconPadding;

            case SUBTYPE_SOAP:

                if (side == 1) {
                    return iconSoapTop;
                }
                else {
                    return iconSoap;
                }

            case SUBTYPE_ROPE:

                if (side < 2) {
                    return iconRopeTop;
                }
                else {
                    return iconRopeSide;
                }

            case SUBTYPE_FLINT:

                return iconFlint;

            case SUBTYPE_NETHERRACK_WITH_GROWTH:

                if (side == 0) {
                    return iconNetherrackWithGrothBottom;
                }
                else if (side == 1) {
                    return iconNetherrackWithGrothTop;
                }
                else {
                    return iconNetherrackWithGrothSide;
                }

            case SUBTYPE_WHITE_STONE:

                return iconWhiteStone;

            case SUBTYPE_WHITE_COBBLE:

                return iconWhiteCobble;

            case SUBTYPE_BARREL:

                if (side < 2) {
                    return iconBarrelTop;
                }
                else {
                    return iconBarrelSide;
                }

            case SUBTYPE_CHOPPING_BLOCK_CLEAN:

                return iconChoppingBlock;

            case SUBTYPE_CHOPPING_BLOCK_DIRTY:

                return iconChoppingBlockDirty;

            case SUBTYPE_ENDER_BLOCK:

                return iconEnderBlock;

            case SUBTYPE_BONE:

                if (side < 2) {
                    return iconBoneTop;
                }
                else {
                    return iconBoneSide;
                }

            default:

                return blockIcon;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, SUBTYPE_HELLFIRE));
        list.add(new ItemStack(blockID, 1, SUBTYPE_PADDING));
        list.add(new ItemStack(blockID, 1, SUBTYPE_SOAP));
        list.add(new ItemStack(blockID, 1, SUBTYPE_ROPE));
        list.add(new ItemStack(blockID, 1, SUBTYPE_FLINT));
        list.add(new ItemStack(blockID, 1, SUBTYPE_WHITE_STONE));
        list.add(new ItemStack(blockID, 1, SUBTYPE_WHITE_COBBLE));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BARREL));
        list.add(new ItemStack(blockID, 1, SUBTYPE_CHOPPING_BLOCK_DIRTY));
        list.add(new ItemStack(blockID, 1, SUBTYPE_CHOPPING_BLOCK_CLEAN));
        list.add(new ItemStack(blockID, 1, SUBTYPE_ENDER_BLOCK));
        list.add(new ItemStack(blockID, 1, SUBTYPE_BONE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);

        if (metadata == SUBTYPE_FLINT) {
            return blockID;
        }

        return idDropped(metadata, world.rand, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue(World world, int x, int y, int z) {
        // used only by pick block
        int metadata = world.getBlockMetadata(x, y, z);

        if (metadata == SUBTYPE_FLINT) {
            return SUBTYPE_FLINT;
        }
        else if (metadata == SUBTYPE_WHITE_STONE) {
            return SUBTYPE_WHITE_STONE;
        }

        return super.getDamageValue(world, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int j, int k, int side) {
        if (side == 1 && blockAccess.getBlockId(i, j, k) == BTWBlocks.netherGroth.blockID) {
            // this is specifically for netherrack with groth

            return false;
        }

        return !blockAccess.isBlockOpaqueCube(i, j, k);
    }
}