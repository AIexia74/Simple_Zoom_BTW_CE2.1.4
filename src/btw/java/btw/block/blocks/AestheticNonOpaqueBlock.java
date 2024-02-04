// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.blockitems.WoodMouldingDecorativeStubBlockItem;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class AestheticNonOpaqueBlock extends Block {
    public final static int SUBTYPE_URN = 0;
    public final static int SUBTYPE_COLUMN = 1; // deprecated
    public final static int SUBTYPE_PEDESTAL_UP = 2;  // deprecated
    public final static int SUBTYPE_PEDESTAL_DOWN = 3; // deprecated
    public final static int SUBTYPE_TABLE = 4; // deprecated
    public final static int SUBTYPE_WICKER_SLAB = 5; // deprecated
    public final static int SUBTYPE_GRATE = 6; // deprecated
    public final static int SUBTYPE_WICKER = 7; // deprecated
    public final static int SUBTYPE_SLATS = 8; // deprecated
    public final static int SUBTYPE_WICKER_SLAB_UPSIDE_DOWN = 9; // deprecated
    public final static int SUBTYPE_WHITE_COBBLE_SLAB = 10;
    public final static int SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN = 11;
    public final static int SUBTYPE_LIGHTNING_ROD = 12; // deprecated

    public final static int NUM_SUBTYPES = 13;

    private final static float DEFAULT_HARDNESS = 2F;

    private final static float COLUM_WIDTH = (10F / 16F);
    private final static float COLUM_HALF_WIDTH = (COLUM_WIDTH / 2F);

    private final static float PEDESTAL_BASE_HEIGHT = (12F / 16F);
    private final static float PEDESTAL_MIDDLE_HEIGHT = (2F / 16F);
    private final static float PEDESTAL_MIDDLE_WIDTH = (14F / 16F);
    private final static float PEDESTAL_MIDDLE_HALF_WIDTH = (PEDESTAL_MIDDLE_WIDTH / 2F);
    private final static float PEDESTAL_TOP_HEIGHT = (2F / 16F);
    private final static float PEDESTAL_TOP_WIDTH = (12F / 16F);
    private final static float PEDESTAL_TOP_HALF_WIDTH = (PEDESTAL_TOP_WIDTH / 2F);

    private final static float TABLE_TOP_HEIGHT = (2F / 16F);
    private final static float TABLE_LEG_HEIGHT = (1F - TABLE_TOP_HEIGHT);
    private final static float TABLE_LEG_WIDTH = (4F / 16F);
    private final static float TABLE_LEG_HALF_WIDTH = (TABLE_LEG_WIDTH / 2F);

    private final static float LIGHTNING_ROD_SHAFT_WIDTH = (1F / 16F);
    private final static float LIGHTNING_ROD_SHAFT_HALF_WIDTH = (LIGHTNING_ROD_SHAFT_WIDTH / 2F);
    private final static float LIGHTNING_ROD_BASE_WIDTH = (4F / 16F);
    private final static float LIGHTNING_ROD_BASE_HALF_WIDTH = (LIGHTNING_ROD_BASE_WIDTH / 2F);
    private final static float LIGHTNING_ROD_BASE_HEIGHT = (2F / 16F);
    private final static float LIGHTNING_ROD_BASE_HALF_HEIGHT = (LIGHTNING_ROD_BASE_HEIGHT / 2F);
    private final static float LIGHTNING_ROD_BALL_WIDTH = (3F / 16F);
    private final static float LIGHTNING_ROD_BALL_HALF_WIDTH = (LIGHTNING_ROD_BALL_WIDTH / 2F);
    private final static float LIGHTNING_ROD_BALL_VERTICAL_OFFSET = (10F / 16F);
    private final static float LIGHTNING_ROD_CANDLE_HOLDER_WIDTH = (4F / 16F);
    private final static float LIGHTNING_ROD_CANDLE_HOLDER_HALF_WIDTH = (LIGHTNING_ROD_CANDLE_HOLDER_WIDTH / 2F);
    private final static float LIGHTNING_ROD_CANDLE_HOLDER_HEIGHT = (1F / 16F);
    private final static float LIGHTNING_ROD_CANDLE_HOLDER_HALF_HEIGHT = (LIGHTNING_ROD_CANDLE_HOLDER_HEIGHT / 2F);
    private final static float LIGHTNING_ROD_CANDLE_HOLDER_VERTICAL_OFFSET = ((16F - LIGHTNING_ROD_CANDLE_HOLDER_HEIGHT) / 16F);

    public AestheticNonOpaqueBlock(int blockID) {
        super(blockID, BTWBlocks.miscMaterial);

        setHardness(DEFAULT_HARDNESS);
        setAxesEffectiveOn(true);
        setPicksEffectiveOn(true);

        setStepSound(soundStoneFootstep);

        setUnlocalizedName("fcBlockAestheticNonOpaque");

        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        if (metadata == SUBTYPE_URN) {
            return BTWItems.urn.itemID;
        }
        else if (metadata == SUBTYPE_WICKER_SLAB || metadata == SUBTYPE_WICKER_SLAB_UPSIDE_DOWN) {
            return BTWBlocks.wickerSlab.blockID;
        }
        else if (metadata == SUBTYPE_GRATE) {
            return BTWBlocks.gratePane.blockID;
        }
        else if (metadata == SUBTYPE_WICKER) {
            return BTWBlocks.wickerPane.blockID;
        }
        else if (metadata == SUBTYPE_SLATS) {
            return BTWBlocks.slatsPane.blockID;
        }
        else if (metadata == SUBTYPE_PEDESTAL_DOWN || metadata == SUBTYPE_PEDESTAL_UP || metadata == SUBTYPE_COLUMN) {
            return BTWBlocks.stoneMouldingAndDecorative.blockID;
        }
        else if (metadata == SUBTYPE_TABLE) {
            return BTWItems.woodMouldingDecorativeStubID;
        }
        else {
            return blockID;
        }
    }

    @Override
    public int damageDropped(int metadata) {
        if (metadata == SUBTYPE_PEDESTAL_DOWN || metadata == SUBTYPE_PEDESTAL_UP) {
            metadata = MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_UP;
        }
        else if (metadata == SUBTYPE_COLUMN) {
            metadata = MouldingAndDecorativeBlock.SUBTYPE_COLUMN;
        }
        else if (metadata == SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN) {
            metadata = SUBTYPE_WHITE_COBBLE_SLAB;
        }
        else if (metadata == SUBTYPE_TABLE) {
            return WoodMouldingDecorativeStubBlockItem.TYPE_TABLE << 2;
        }
        else if (metadata == SUBTYPE_URN || metadata == SUBTYPE_WICKER_SLAB || metadata == SUBTYPE_WICKER_SLAB_UPSIDE_DOWN || metadata == SUBTYPE_GRATE ||
                 metadata == SUBTYPE_WICKER || metadata == SUBTYPE_SLATS)
        {
            metadata = 0;
        }

        return metadata;
    }

    @Override
    public int onBlockPlaced(World world, int i, int j, int k, int facing, float clickX, float clickY, float clickZ, int metadata) {

        if (metadata == SUBTYPE_PEDESTAL_UP) {
            if (facing == 0 || facing != 1 && (double) clickY > 0.5D) {
                return SUBTYPE_PEDESTAL_DOWN;
            }
        }
        else if (metadata == SUBTYPE_WICKER_SLAB) {
            if (facing == 0 || facing != 1 && (double) clickY > 0.5D) {
                return SUBTYPE_WICKER_SLAB_UPSIDE_DOWN;
            }
        }
        else if (metadata == SUBTYPE_WHITE_COBBLE_SLAB) {
            if (facing == 0 || facing != 1 && (double) clickY > 0.5D) {
                return SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN;
            }
        }

        return metadata;
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
        int iSubType = blockAccess.getBlockMetadata(i, j, k);

        switch (iSubType) {
            case SUBTYPE_TABLE:

                if (!isTableOnCorner(blockAccess, i, j, k)) {
                    return AxisAlignedBB.getAABBPool().getAABB(0D, 1D - TABLE_TOP_HEIGHT, 0D, 1D, 1D, 1D);
                }
                else {
                    return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 1D, 1D, 1D);
                }

            case SUBTYPE_GRATE:
            case SUBTYPE_WICKER:
            case SUBTYPE_SLATS:

                return getBlockBoundsFromPoolForPane(blockAccess, i, j, k, iSubType);

            case SUBTYPE_URN:

                AxisAlignedBB urnBox = AxisAlignedBB.getAABBPool().getAABB((0.5D - UnfiredPotteryBlock.UNFIRED_POTTERY_URN_BODY_HALF_WIDTH), 0D,
                                                                           (0.5D - UnfiredPotteryBlock.UNFIRED_POTTERY_URN_BODY_HALF_WIDTH),
                                                                           (0.5D + UnfiredPotteryBlock.UNFIRED_POTTERY_URN_BODY_HALF_WIDTH),
                                                                           UnfiredPotteryBlock.UNFIRED_POTTERY_URN_HEIGHT,
                                                                           (0.5D + UnfiredPotteryBlock.UNFIRED_POTTERY_URN_BODY_HALF_WIDTH));

                if (blockAccess.getBlockId(i, j + 1, k) == BTWBlocks.hopper.blockID) {
                    urnBox.offset(0D, 1D - UnfiredPotteryBlock.UNFIRED_POTTERY_URN_HEIGHT, 0D);
                }

                return urnBox;

            case SUBTYPE_COLUMN:

                return AxisAlignedBB.getAABBPool().getAABB((0.5F - COLUM_HALF_WIDTH), 0.0F, (0.5F - COLUM_HALF_WIDTH), (0.5F + COLUM_HALF_WIDTH), 1.0F,
                                                           (0.5F + COLUM_HALF_WIDTH));

            case SUBTYPE_WICKER_SLAB:
            case SUBTYPE_WHITE_COBBLE_SLAB:

                return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);

            case SUBTYPE_WICKER_SLAB_UPSIDE_DOWN:
            case SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN:

                return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);

            case SUBTYPE_LIGHTNING_ROD:

                return AxisAlignedBB.getAABBPool().getAABB(0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH, 0F, 0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH,
                                                           0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH, 1F, 0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH);
        }

        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
    }

    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB intersectingBox, List list, Entity entity) {
        int iSubType = world.getBlockMetadata(i, j, k);

        if (iSubType == SUBTYPE_GRATE || iSubType == SUBTYPE_WICKER || iSubType == SUBTYPE_SLATS) {
            boolean bKNeg = shouldPaneConnectToBlock(world, i, j, k - 1, iSubType);
            boolean bKPos = shouldPaneConnectToBlock(world, i, j, k + 1, iSubType);

            boolean bINeg = shouldPaneConnectToBlock(world, i - 1, j, k, iSubType);
            boolean bIPos = shouldPaneConnectToBlock(world, i + 1, j, k, iSubType);

            if ((!bINeg || !bIPos) && (bINeg || bIPos || bKNeg || bKPos)) {
                if (bINeg && !bIPos) {
                    AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0F, 0F, 0.4375F, 0.5F, 1F, 0.5625F).offset(i, j, k);

                    tempBox.addToListIfIntersects(intersectingBox, list);
                }
                else if (!bINeg && bIPos) {
                    AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F).offset(i, j, k);

                    tempBox.addToListIfIntersects(intersectingBox, list);
                }
            }
            else {
                AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F).offset(i, j, k);

                tempBox.addToListIfIntersects(intersectingBox, list);
            }

            if ((!bKNeg || !bKPos) && (bINeg || bIPos || bKNeg || bKPos)) {
                if (bKNeg && !bKPos) {
                    AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F).offset(i, j, k);

                    tempBox.addToListIfIntersects(intersectingBox, list);
                }
                else if (!bKNeg && bKPos) {
                    AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F).offset(i, j, k);

                    tempBox.addToListIfIntersects(intersectingBox, list);
                }
            }
            else {
                AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F).offset(i, j, k);

                tempBox.addToListIfIntersects(intersectingBox, list);
            }
        }
        else {
            super.addCollisionBoxesToList(world, i, j, k, intersectingBox, list, entity);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int changedBlockID) {
        super.onNeighborBlockChange(world, i, j, k, changedBlockID);

        int iSubtype = getSubtype(world, i, j, k);

        if (iSubtype == SUBTYPE_LIGHTNING_ROD) {
            if (!canLightningRodStay(world, i, j, k)) {
                if (world.getBlockId(i, j, k) == blockID) {
                    dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
                    world.setBlockWithNotify(i, j, k, 0);
                }
            }
            else {
                world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
            }
        }
    }

    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int facing, boolean ignoreTransparency) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        switch (iSubtype) {
            case SUBTYPE_COLUMN:

                return facing == 0 || facing == 1;

            case SUBTYPE_PEDESTAL_UP:
            case SUBTYPE_PEDESTAL_DOWN:

                return true;
        }

        return super.hasCenterHardPointToFacing(blockAccess, i, j, k, facing, ignoreTransparency);
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int facing, boolean ignoreTransparency) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        switch (iSubtype) {
            case SUBTYPE_PEDESTAL_UP:

                return facing == 0;

            case SUBTYPE_PEDESTAL_DOWN:

                return facing == 1;

            case SUBTYPE_WICKER_SLAB:
            case SUBTYPE_WHITE_COBBLE_SLAB:

                return facing == 0;

            case SUBTYPE_WICKER_SLAB_UPSIDE_DOWN:
            case SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN:

                return facing == 1;

            case SUBTYPE_TABLE:

                return facing == 1;
        }

        return super.hasLargeCenterHardPointToFacing(blockAccess, i, j, k, facing, ignoreTransparency);
    }

    @Override
    public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int facing, boolean ignoreTransparency) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_LIGHTNING_ROD) {
            return facing == 1;
        }

        return super.hasSmallCenterHardPointToFacing(blockAccess, i, j, k, facing, ignoreTransparency);
    }

    @Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        return iSubtype != SUBTYPE_URN && iSubtype != SUBTYPE_TABLE && iSubtype != SUBTYPE_WICKER_SLAB && iSubtype != SUBTYPE_WICKER_SLAB_UPSIDE_DOWN &&
               iSubtype != SUBTYPE_GRATE && iSubtype != SUBTYPE_WICKER && iSubtype != SUBTYPE_SLATS;
    }

    @Override
    public float getMovementModifier(World world, int i, int j, int k) {
        return 1.2F;
    }

    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k) {
        int iSubtype = world.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_WICKER_SLAB || iSubtype == SUBTYPE_WHITE_COBBLE_SLAB) {
            return true;
        }
        else if (iSubtype == SUBTYPE_URN) {
            return world.doesBlockHaveSolidTopSurface(i, j - 1, k);
        }

        return super.canGroundCoverRestOnBlock(world, i, j, k);
    }

    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        if (iSubtype == SUBTYPE_WICKER_SLAB || iSubtype == SUBTYPE_WHITE_COBBLE_SLAB) {
            return -0.5F;
        }
        else if (iSubtype == SUBTYPE_URN) {
            return -1F;
        }

        return super.groundCoverRestingOnVisualOffset(blockAccess, i, j, k);
    }

    @Override
    public boolean canToolsStickInBlock(IBlockAccess blockAccess, int i, int j, int k) {
        int iSubtype = blockAccess.getBlockMetadata(i, j, k);

        return iSubtype != SUBTYPE_WICKER_SLAB;
    }

    //------------- Class Specific Methods ------------//

    public int getSubtype(IBlockAccess blockAccess, int i, int j, int k) {
        return blockAccess.getBlockMetadata(i, j, k);
    }

    public void setSubtype(World world, int i, int j, int k, int subtype) {
        world.setBlockMetadata(i, j, k, subtype);
    }

    public boolean isBlockTable(IBlockAccess blockAccess, int i, int j, int k) {
        if (blockAccess.getBlockId(i, j, k) == BTWBlocks.aestheticNonOpaque.blockID) {
            if (blockAccess.getBlockMetadata(i, j, k) == SUBTYPE_TABLE) {
                return true;
            }
        }

        return false;
    }

    public boolean isTableOnCorner(IBlockAccess blockAccess, int i, int j, int k) {
        boolean positiveITable = isBlockTable(blockAccess, i + 1, j, k);
        boolean negativeITable = isBlockTable(blockAccess, i - 1, j, k);
        boolean positiveKTable = isBlockTable(blockAccess, i, j, k + 1);
        boolean negativeKTable = isBlockTable(blockAccess, i, j, k - 1);

        return (!positiveITable && (!positiveKTable || !negativeKTable)) || (!negativeITable && (!positiveKTable || !negativeKTable));
    }

    private boolean shouldPaneConnectToBlock(IBlockAccess blockAccess, int i, int j, int k, int subType) {
        int iBlockID = blockAccess.getBlockId(i, j, k);

        if (Block.opaqueCubeLookup[iBlockID] || iBlockID == Block.glass.blockID) {
            return true;
        }
        else if (iBlockID == blockID) {
            int iTargetSubType = blockAccess.getBlockMetadata(i, j, k);

            return iTargetSubType == subType;
        }

        return false;
    }

    public AxisAlignedBB getBlockBoundsFromPoolForPane(IBlockAccess blockAccess, int i, int j, int k, int subType) {
        float fXMin = 0.4375F;
        float fXMax = 0.5625F;

        float fZMin = 0.4375F;
        float fZMax = 0.5625F;

        boolean bKNeg = shouldPaneConnectToBlock(blockAccess, i, j, k - 1, subType);
        boolean bKPos = shouldPaneConnectToBlock(blockAccess, i, j, k + 1, subType);
        boolean bINeg = shouldPaneConnectToBlock(blockAccess, i - 1, j, k, subType);
        boolean bIPos = shouldPaneConnectToBlock(blockAccess, i + 1, j, k, subType);

        if ((!bINeg || !bIPos) && (bINeg || bIPos || bKNeg || bKPos)) {
            if (bINeg && !bIPos) {
                fXMin = 0.0F;
            }
            else if (!bINeg && bIPos) {
                fXMax = 1.0F;
            }
        }
        else {
            fXMin = 0.0F;
            fXMax = 1.0F;
        }

        if ((!bKNeg || !bKPos) && (bINeg || bIPos || bKNeg || bKPos)) {
            if (bKNeg && !bKPos) {
                fZMin = 0.0F;
            }
            else if (!bKNeg && bKPos) {
                fZMax = 1.0F;
            }
        }
        else {
            fZMin = 0.0F;
            fZMax = 1.0F;
        }

        return AxisAlignedBB.getAABBPool().getAABB(fXMin, 0.0F, fZMin, fXMax, 1.0F, fZMax);
    }

    static public boolean canLightningRodStay(World world, int i, int j, int k) {
        int iBlockBelowID = world.getBlockId(i, j - 1, k);

        if (iBlockBelowID == BTWBlocks.aestheticNonOpaque.blockID && world.getBlockMetadata(i, j - 1, k) == SUBTYPE_LIGHTNING_ROD) {
            return true;
        }

        return WorldUtils.doesBlockHaveCenterHardpointToFacing(world, i, j - 1, k, 1, true);
    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconUrn;
    @Environment(EnvType.CLIENT)
    private Icon iconColumnStoneTop;
    @Environment(EnvType.CLIENT)
    private Icon iconColumnStoneSide;
    @Environment(EnvType.CLIENT)
    private Icon iconPedestalStoneTop;
    @Environment(EnvType.CLIENT)
    private Icon iconPedestalStoneSide;
    @Environment(EnvType.CLIENT)
    private Icon iconSlabWicker;
    @Environment(EnvType.CLIENT)
    private Icon iconGrate;
    @Environment(EnvType.CLIENT)
    private Icon iconWicker;
    @Environment(EnvType.CLIENT)
    private Icon iconSlats;
    @Environment(EnvType.CLIENT)
    private Icon iconSlatsSide;
    @Environment(EnvType.CLIENT)
    private Icon iconWhiteCobble;
    @Environment(EnvType.CLIENT)
    private Icon iconLightningRod;

    @Environment(EnvType.CLIENT)
    public Icon iconTableWoodOakTop;
    @Environment(EnvType.CLIENT)
    public Icon iconTableWoodOakLeg;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon("stone"); // hit effect

        iconUrn = register.registerIcon("fcBlockUrn");
        iconColumnStoneTop = register.registerIcon("fcBlockColumnStone_top");
        iconColumnStoneSide = register.registerIcon("fcBlockColumnStone_side");
        iconPedestalStoneTop = register.registerIcon("fcBlockPedestalStone_top");
        iconPedestalStoneSide = register.registerIcon("fcBlockPedestalStone_side");
        iconTableWoodOakTop = register.registerIcon("fcBlockTableWoodOak_top");
        iconTableWoodOakLeg = register.registerIcon("fcBlockTableWoodOak_leg");
        iconSlabWicker = register.registerIcon("fcBlockSlabWicker");
        iconGrate = register.registerIcon("fcBlockGrate");
        iconWicker = register.registerIcon("fcBlockWicker");
        iconSlats = register.registerIcon("fcBlockSlats");
        iconSlatsSide = register.registerIcon("fcBlockSlats_side");
        iconWhiteCobble = register.registerIcon("fcBlockWhiteCobble");
        iconLightningRod = register.registerIcon("fcBlockLightningRodOld");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        switch (metadata) {
            case SUBTYPE_COLUMN:

                if (side < 2) {
                    return iconColumnStoneTop;
                }
                else {
                    return iconColumnStoneSide;
                }

            case SUBTYPE_PEDESTAL_UP:
            case SUBTYPE_PEDESTAL_DOWN:

                if (side < 2) {
                    return iconPedestalStoneTop;
                }
                else {
                    return iconPedestalStoneSide;
                }

            case SUBTYPE_TABLE:

                return iconTableWoodOakTop;

            case SUBTYPE_WICKER_SLAB:
            case SUBTYPE_WICKER_SLAB_UPSIDE_DOWN:

                return iconSlabWicker;

            case SUBTYPE_GRATE:

                return iconGrate;

            case SUBTYPE_WICKER:

                return iconWicker;

            case SUBTYPE_SLATS:

                return iconSlats;

            case SUBTYPE_WHITE_COBBLE_SLAB:
            case SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN:

                return iconWhiteCobble;

            case SUBTYPE_LIGHTNING_ROD:

                return iconLightningRod;

            default:

                return blockIcon;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborI, int neighborJ, int neighborK, int side) {
        return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborI, neighborJ, neighborK, side);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        // none of the sub-blocks are in the creative inventory
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int i, int j, int k) {
        return idDropped(world.getBlockMetadata(i, j, k), world.rand, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        IBlockAccess blockAccess = renderer.blockAccess;

        int iSubType = blockAccess.getBlockMetadata(i, j, k);

        switch (iSubType) {
            case SUBTYPE_URN:

                float fVerticalOffset = 0.0F;

                if (blockAccess.getBlockId(i, j + 1, k) == BTWBlocks.hopper.blockID) {
                    fVerticalOffset = 1.0F - UnfiredPotteryBlock.UNFIRED_POTTERY_URN_HEIGHT;
                }

                return UnfiredPotteryBlock.renderUnfiredUrn(renderer, blockAccess, i, j, k, this, iconUrn, fVerticalOffset);

            case SUBTYPE_PEDESTAL_UP:

                return renderPedestalUp(renderer, blockAccess, i, j, k, this);

            case SUBTYPE_PEDESTAL_DOWN:

                return renderPedestalDown(renderer, blockAccess, i, j, k, this);

            case SUBTYPE_TABLE:

                return renderTable(renderer, blockAccess, i, j, k, this);

            case SUBTYPE_GRATE:
            case SUBTYPE_WICKER:
            case SUBTYPE_SLATS:

                return renderPane(renderer, blockAccess, i, j, k, this, iSubType);

            case SUBTYPE_LIGHTNING_ROD:

                return renderLightningRod(renderer, blockAccess, i, j, k, this);

            default:

                renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));

                return renderer.renderStandardBlock(this, i, j, k);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int itemDamage, float brightness) {

        switch (itemDamage) {
            case SUBTYPE_URN:

                UnfiredPotteryBlock.renderUnfiredUrnInvBlock(renderBlocks, this, itemDamage, iconUrn);

                return;

            case SUBTYPE_PEDESTAL_UP:

                renderPedestalUpInvBlock(renderBlocks, this);

                return;

            case SUBTYPE_PEDESTAL_DOWN:

                renderPedestalDownInvBlock(renderBlocks, this);

                return;

            case SUBTYPE_TABLE:

                renderTableInvBlock(renderBlocks, this);

                return;

            case SUBTYPE_LIGHTNING_ROD:

                renderLightningRodInvBlock(renderBlocks, this);

                return;

            case SUBTYPE_COLUMN:

                renderBlocks.setRenderBounds((0.5F - COLUM_HALF_WIDTH), 0.0F, (0.5F - COLUM_HALF_WIDTH), (0.5F + COLUM_HALF_WIDTH), 1.0F,
                                             (0.5F + COLUM_HALF_WIDTH));

                break;

            case SUBTYPE_WICKER_SLAB:
            case SUBTYPE_WHITE_COBBLE_SLAB:

                renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);

                break;

            case SUBTYPE_WICKER_SLAB_UPSIDE_DOWN:
            case SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN:

                renderBlocks.setRenderBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);

                break;

            default:

                renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

                break;
        }

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, itemDamage);
    }

    //------------- Pedestal Renderers ------------//

    @Environment(EnvType.CLIENT)
    public boolean renderPedestalUp(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block) {
        // render base

        renderBlocks.setRenderBounds(0F, 0F, 0F, 1.0F, PEDESTAL_BASE_HEIGHT, 1.0F);

        renderBlocks.renderStandardBlock(block, i, j, k);

        // render middle

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
                                     0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT + PEDESTAL_MIDDLE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);

        renderBlocks.renderStandardBlock(block, i, j, k);

        // render top

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 1.0F - PEDESTAL_TOP_HEIGHT, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH,
                                     1.0F, 0.5F + PEDESTAL_TOP_HALF_WIDTH);

        renderBlocks.renderStandardBlock(block, i, j, k);

        return true;
    }

    @Environment(EnvType.CLIENT)
    public void renderPedestalUpInvBlock(RenderBlocks renderBlocks, Block block) {
        // render base

        renderBlocks.setRenderBounds(0F, 0F, 0F, 1.0F, PEDESTAL_BASE_HEIGHT, 1.0F);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);

        // render middle

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
                                     0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, PEDESTAL_BASE_HEIGHT + PEDESTAL_MIDDLE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);

        // render top

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 1.0F - PEDESTAL_TOP_HEIGHT, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH,
                                     1.0F, 0.5F + PEDESTAL_TOP_HALF_WIDTH);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_UP);
    }

    @Environment(EnvType.CLIENT)
    public boolean renderPedestalDown(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block) {
        // render base

        renderBlocks.setRenderBounds(0F, 1.0F - PEDESTAL_BASE_HEIGHT, 0F, 1.0F, 1.0F, 1.0F);

        renderBlocks.renderStandardBlock(block, i, j, k);

        // render middle

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT - PEDESTAL_MIDDLE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
                                     0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);

        renderBlocks.renderStandardBlock(block, i, j, k);

        // render top

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 00F, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH, PEDESTAL_TOP_HEIGHT,
                                     0.5F + PEDESTAL_TOP_HALF_WIDTH);

        renderBlocks.renderStandardBlock(block, i, j, k);

        return true;
    }

    @Environment(EnvType.CLIENT)
    public void renderPedestalDownInvBlock(RenderBlocks renderBlocks, Block block) {
        // render base

        renderBlocks.setRenderBounds(0F, 1.0F - PEDESTAL_BASE_HEIGHT, 0F, 1.0F, 1.0F, 1.0F);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);

        // render middle

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT - PEDESTAL_MIDDLE_HEIGHT, 0.5F - PEDESTAL_MIDDLE_HALF_WIDTH,
                                     0.5F + PEDESTAL_MIDDLE_HALF_WIDTH, 1.0F - PEDESTAL_BASE_HEIGHT, 0.5F + PEDESTAL_MIDDLE_HALF_WIDTH);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);

        // render top

        renderBlocks.setRenderBounds(0.5F - PEDESTAL_TOP_HALF_WIDTH, 00F, 0.5F - PEDESTAL_TOP_HALF_WIDTH, 0.5F + PEDESTAL_TOP_HALF_WIDTH, PEDESTAL_TOP_HEIGHT,
                                     0.5F + PEDESTAL_TOP_HALF_WIDTH);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, SUBTYPE_PEDESTAL_DOWN);
    }

    //------------- Table Renderers ------------//

    @Environment(EnvType.CLIENT)
    public boolean renderTable(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block) {
        // render top

        renderBlocks.setRenderBounds(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);

        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, iconTableWoodOakTop);

        if (isTableOnCorner(blockAccess, i, j, k)) {
            // render leg

            renderBlocks.setRenderBounds(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH, TABLE_LEG_HEIGHT,
                                         0.5F + TABLE_LEG_HALF_WIDTH);

            RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, iconTableWoodOakLeg);
        }

        return true;
    }

    @Environment(EnvType.CLIENT)
    public void renderTableInvBlock(RenderBlocks renderBlocks, Block block) {
        // render top

        renderBlocks.setRenderBounds(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);

        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, iconTableWoodOakTop);

        // render leg

        renderBlocks.setRenderBounds(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH, TABLE_LEG_HEIGHT,
                                     0.5F + TABLE_LEG_HALF_WIDTH);

        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, iconTableWoodOakLeg);
    }

    //------------- Pane Renderers ------------//

    @Environment(EnvType.CLIENT)
    public boolean renderPane(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block, int subType) {
        // copied over from RenderBlocks BlockPane renderer with minor modifications

        int iWorldHeight = 256; //blockAccess.getWorldHeight();

        Tessellator tessellator = Tessellator.instance;

        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i, j, k));

        int iColor = block.colorMultiplier(blockAccess, i, j, k);

        float iColorRed = (float) (iColor >> 16 & 0xff) / 255F;
        float iColorGreen = (float) (iColor >> 8 & 0xff) / 255F;
        float iColorBlue = (float) (iColor & 0xff) / 255F;

        if (EntityRenderer.anaglyphEnable) {
            iColorRed = (iColorRed * 30F + iColorGreen * 59F + iColorBlue * 11F) / 100F;
            iColorGreen = (iColorRed * 30F + iColorGreen * 70F) / 100F;
            iColorBlue = (iColorRed * 30F + iColorBlue * 70F) / 100F;
        }

        tessellator.setColorOpaque_F(iColorRed, iColorGreen, iColorBlue);

        Icon paneTexture;
        Icon sideTexture;

        if (renderBlocks.hasOverrideBlockTexture()) {
            paneTexture = renderBlocks.getOverrideTexture();
            sideTexture = renderBlocks.getOverrideTexture();
        }
        else {
            int iMetadata = blockAccess.getBlockMetadata(i, j, k);

            paneTexture = block.getIcon(0, iMetadata);
            sideTexture = block.getIcon(0, iMetadata);

            if (subType == SUBTYPE_SLATS) {
                sideTexture = this.iconSlatsSide;
            }
        }

        int iPaneTextureOriginX = paneTexture.getOriginX();
        int iPaneTextureOriginY = paneTexture.getOriginY();

        double dPaneTextureMinU = (double) paneTexture.getMinU();
        double dPaneTextureInterpolatedMidU = (double) paneTexture.getInterpolatedU(8.0D);
        double dPaneTextureMaxU = (double) paneTexture.getMaxU();

        double dPaneTextureMinV = (double) paneTexture.getMinV();
        double dPaneTextureMaxV = (double) paneTexture.getMaxV();

        int iSideTextureOriginX = sideTexture.getOriginX();
        int iSideTextureOriginY = sideTexture.getOriginY();

        double dSideTextureInterpolatedMinU = (double) sideTexture.getInterpolatedU(7.0D);
        double dSideTextureInterpolatedMaxU = (double) sideTexture.getInterpolatedU(9.0D);

        double dSideTextureMinV = (double) sideTexture.getMinV();
        double dSideTextureInterpolatedMidV = (double) sideTexture.getInterpolatedV(8.0D);
        double dSideTextureMaxV = (double) sideTexture.getMaxV();

        double dPosXMin = (double) i;
        double dPosXMid = (double) i + 0.5D;
        double dPosXMax = (double) (i + 1);

        double dPosZMin = (double) k;
        double dPosZMid = (double) k + 0.5D;
        double dPosZMax = (double) (k + 1);

        double var50 = (double) i + 0.5D - 0.0625D;
        double var52 = (double) i + 0.5D + 0.0625D;

        double var54 = (double) k + 0.5D - 0.0625D;
        double var56 = (double) k + 0.5D + 0.0625D;

        boolean var58 = shouldPaneConnectToBlock(blockAccess, i, j, k - 1, subType);
        boolean var59 = shouldPaneConnectToBlock(blockAccess, i, j, k + 1, subType);
        boolean var60 = shouldPaneConnectToBlock(blockAccess, i - 1, j, k, subType);
        boolean var61 = shouldPaneConnectToBlock(blockAccess, i + 1, j, k, subType);

        boolean var62 = !shouldPaneConnectToBlock(blockAccess, i, j + 1, k, subType);
        boolean var63 = !shouldPaneConnectToBlock(blockAccess, i, j - 1, k, subType);

        if ((!var60 || !var61) && (var60 || var61 || var58 || var59)) {
            if (var60 && !var61) {
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1), dPosZMid, dPaneTextureMinU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 0), dPosZMid, dPaneTextureMinU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureMinU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureMinU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);

                if (!var59 && !var58) {
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var56, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var56, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var54, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var54, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                }

                if (var62 || j < iWorldHeight - 1 && blockAccess.isAirBlock(i - 1, j + 1, k)) {
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                }

                if (var63 || j > 1 && blockAccess.isAirBlock(i - 1, j - 1, k)) {
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                }
            }
            else if (!var60 && var61) {
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 0), dPosZMid, dPaneTextureMaxU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1), dPosZMid, dPaneTextureMaxU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureMaxU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureMaxU, dPaneTextureMinV);

                if (!var59 && !var58) {
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var56, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var56, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), var54, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), var54, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                }

                if (var62 || j < iWorldHeight - 1 && blockAccess.isAirBlock(i + 1, j + 1, k)) {
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }

                if (var63 || j > 1 && blockAccess.isAirBlock(i + 1, j - 1, k)) {
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }
            }
        }
        else {
            tessellator.addVertexWithUV(dPosXMin, (double) (j + 1), dPosZMid, dPaneTextureMinU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMin, (double) (j + 0), dPosZMid, dPaneTextureMinU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMax, (double) (j + 0), dPosZMid, dPaneTextureMaxU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMax, (double) (j + 1), dPosZMid, dPaneTextureMaxU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMax, (double) (j + 1), dPosZMid, dPaneTextureMinU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMax, (double) (j + 0), dPosZMid, dPaneTextureMinU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMin, (double) (j + 0), dPosZMid, dPaneTextureMaxU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMin, (double) (j + 1), dPosZMid, dPaneTextureMaxU, dPaneTextureMinV);

            if (var62) {
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
            }
            else {
                if (j < iWorldHeight - 1 && blockAccess.isAirBlock(i - 1, j + 1, k)) {
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                }

                if (j < iWorldHeight - 1 && blockAccess.isAirBlock(i + 1, j + 1, k)) {
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) (j + 1) + 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }
            }

            if (var63) {
                tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
            }
            else {
                if (j > 1 && blockAccess.isAirBlock(i - 1, j - 1, k)) {
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMin, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                }

                if (j > 1 && blockAccess.isAirBlock(i + 1, j - 1, k)) {
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var56, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMid, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(dPosXMax, (double) j - 0.01D, var54, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }
            }
        }

        if ((!var58 || !var59) && (var60 || var61 || var58 || var59)) {
            if (var58 && !var59) {
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMin, dPaneTextureMinU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMin, dPaneTextureMinU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureMinU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureMinU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMin, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMin, dPaneTextureInterpolatedMidU, dPaneTextureMinV);

                if (!var61 && !var60) {
                    tessellator.addVertexWithUV(var50, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var52, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var50, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                }

                if (var62 || j < iWorldHeight - 1 && blockAccess.isAirBlock(i, j + 1, k - 1)) {
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }

                if (var63 || j > 1 && blockAccess.isAirBlock(i, j - 1, k - 1)) {
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }
            }
            else if (!var58 && var59) {
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMax, dPaneTextureMaxU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMax, dPaneTextureMaxU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMax, dPaneTextureInterpolatedMidU, dPaneTextureMinV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMax, dPaneTextureInterpolatedMidU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMid, dPaneTextureMaxU, dPaneTextureMaxV);
                tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMid, dPaneTextureMaxU, dPaneTextureMinV);

                if (!var61 && !var60) {
                    tessellator.addVertexWithUV(var52, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var52, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var50, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 0), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1), dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                }

                if (var62 || j < iWorldHeight - 1 && blockAccess.isAirBlock(i, j + 1, k + 1)) {
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                }

                if (var63 || j > 1 && blockAccess.isAirBlock(i, j - 1, k + 1)) {
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                }
            }
        }
        else {
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMax, dPaneTextureMinU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMax, dPaneTextureMinU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMin, dPaneTextureMaxU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMin, dPaneTextureMaxU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMin, dPaneTextureMinU, dPaneTextureMinV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMin, dPaneTextureMinU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 0), dPosZMax, dPaneTextureMaxU, dPaneTextureMaxV);
            tessellator.addVertexWithUV(dPosXMid, (double) (j + 1), dPosZMax, dPaneTextureMaxU, dPaneTextureMinV);

            if (var62) {
                tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMaxV);
            }
            else {
                if (j < iWorldHeight - 1 && blockAccess.isAirBlock(i, j + 1, k - 1)) {
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }

                if (j < iWorldHeight - 1 && blockAccess.isAirBlock(i, j + 1, k + 1)) {
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) (j + 1) + 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                }
            }

            if (var63) {
                tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMinV);
                tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMaxV);
            }
            else {
                if (j > 1 && blockAccess.isAirBlock(i, j - 1, k - 1)) {
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMinV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMin, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMinV);
                }

                if (j > 1 && blockAccess.isAirBlock(i, j - 1, k + 1)) {
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMinU, dSideTextureInterpolatedMidV);
                    tessellator.addVertexWithUV(var50, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMinU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMid, dSideTextureInterpolatedMaxU, dSideTextureMaxV);
                    tessellator.addVertexWithUV(var52, (double) j - 0.005D, dPosZMax, dSideTextureInterpolatedMaxU, dSideTextureInterpolatedMidV);
                }
            }
        }

        return true;
    }

    //------------- Lightning Rod Renderers ------------//

    @Environment(EnvType.CLIENT)
    public boolean renderLightningRod(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block) {
        Icon texture = iconLightningRod;

        // render shaft

        renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_SHAFT_HALF_WIDTH, 0F, 0.5F - LIGHTNING_ROD_SHAFT_HALF_WIDTH, 0.5F + LIGHTNING_ROD_SHAFT_HALF_WIDTH,
                                     1.0F, 0.5F + LIGHTNING_ROD_SHAFT_HALF_WIDTH);

        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, iconLightningRod);

        // render base

        if (blockAccess.getBlockId(i, j - 1, k) != BTWBlocks.aestheticNonOpaque.blockID ||
            blockAccess.getBlockMetadata(i, j - 1, k) != AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD)
        {
            renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH, 0F, 0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH, 0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH,
                                         LIGHTNING_ROD_BASE_HEIGHT, 0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH);

            RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, iconLightningRod);
        }

        // render ball

        int iBlockAboveID = blockAccess.getBlockId(i, j + 1, k);
        int iBlockAboveMetadata = blockAccess.getBlockMetadata(i, j + 1, k);

        if (iBlockAboveID != BTWBlocks.aestheticNonOpaque.blockID || iBlockAboveMetadata != AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD) {
            if (iBlockAboveID == BTWBlocks.legacyCandle.blockID) {
                renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_CANDLE_HOLDER_HALF_WIDTH, LIGHTNING_ROD_CANDLE_HOLDER_VERTICAL_OFFSET,
                                             0.5F - LIGHTNING_ROD_CANDLE_HOLDER_HALF_WIDTH, 0.5F + LIGHTNING_ROD_CANDLE_HOLDER_HALF_WIDTH,
                                             LIGHTNING_ROD_CANDLE_HOLDER_VERTICAL_OFFSET + LIGHTNING_ROD_CANDLE_HOLDER_HEIGHT,
                                             0.5F + LIGHTNING_ROD_CANDLE_HOLDER_HALF_WIDTH);
            }
            else {
                renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_BALL_HALF_WIDTH, LIGHTNING_ROD_BALL_VERTICAL_OFFSET, 0.5F - LIGHTNING_ROD_BALL_HALF_WIDTH,
                                             0.5F + LIGHTNING_ROD_BALL_HALF_WIDTH, LIGHTNING_ROD_BALL_VERTICAL_OFFSET + LIGHTNING_ROD_BALL_WIDTH,
                                             0.5F + LIGHTNING_ROD_BALL_HALF_WIDTH);
            }

            RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, iconLightningRod);
        }

        return true;
    }

    @Environment(EnvType.CLIENT)
    public void renderLightningRodInvBlock(RenderBlocks renderBlocks, Block block) {
        Icon texture = iconLightningRod;

        // render shaft

        renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_SHAFT_HALF_WIDTH, 0F, 0.5F - LIGHTNING_ROD_SHAFT_HALF_WIDTH, 0.5F + LIGHTNING_ROD_SHAFT_HALF_WIDTH,
                                     1.0F, 0.5F + LIGHTNING_ROD_SHAFT_HALF_WIDTH);

        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);

        // render base

        renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH, 0F, 0.5F - LIGHTNING_ROD_BASE_HALF_WIDTH, 0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH,
                                     LIGHTNING_ROD_BASE_HEIGHT, 0.5F + LIGHTNING_ROD_BASE_HALF_WIDTH);

        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);

        // render ball

        renderBlocks.setRenderBounds(0.5F - LIGHTNING_ROD_BALL_HALF_WIDTH, LIGHTNING_ROD_BALL_VERTICAL_OFFSET, 0.5F - LIGHTNING_ROD_BALL_HALF_WIDTH,
                                     0.5F + LIGHTNING_ROD_BALL_HALF_WIDTH, LIGHTNING_ROD_BALL_VERTICAL_OFFSET + LIGHTNING_ROD_BALL_WIDTH,
                                     0.5F + LIGHTNING_ROD_BALL_HALF_WIDTH);

        RenderUtils.renderInvBlockWithTexture(renderBlocks, block, -0.5F, -0.5F, -0.5F, texture);
    }
}