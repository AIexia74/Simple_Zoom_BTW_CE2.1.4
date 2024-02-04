// FCMOD

package btw.block.blocks;

import java.util.Random;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.HoeItem;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class GrassBlock extends BlockGrass {
	public static final int SPREAD_LIGHT_LEVEL = 9; // 7 previously, 4 vanilla
	public static final int SURVIVE_LIGHT_LEVEL = 9; // 4 previously

	public static final float GROWTH_CHANCE = 0.8F;
	public static final int SELF_GROWTH_CHANCE = 12;

	public GrassBlock(int blockID) {
		super(blockID);

		setHardness(0.6F);
		setShovelsEffectiveOn();
		setHoesEffectiveOn();

		setStepSound(soundGrassFootstep);

		setUnlocalizedName("grass");    	
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!canGrassSurviveAtLocation(world, x, y, z)) {
			// convert back to dirt in low light
			world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
		}
		else if (canGrassSpreadFromLocation(world, x, y, z)) {
			if (rand.nextFloat() <= GROWTH_CHANCE) {
				checkForGrassSpreadFromLocation(world, x, y, z);
			}

			if (isSparse(world, x, y, z) && rand.nextInt(SELF_GROWTH_CHANCE) == 0) {
				this.setFullyGrown(world, x, y, z);
			}
		}
	}
	
	@Override
	protected ItemStack createStackedBlock(int metadata) {
		if (this.isSparse(metadata)) {
			return new ItemStack(Block.dirt);
		}
		else {
			return new ItemStack(this);
		}
	}

	@Override
	public int idDropped(int metadata, Random rand, int fortuneModifier) {
		return BTWBlocks.looseDirt.blockID;
	}

	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int metadata, float chanceOfDrop) {
		dropItemsIndividually(world, x, y, z, BTWItems.dirtPile.itemID, 6, 0, chanceOfDrop);

		return true;
	}

	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);

		onDirtDugWithImproperTool(world, x, y, z);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		super.onBlockDestroyedByExplosion(world, x, y, z, explosion);

		onDirtDugWithImproperTool(world, x, y, z);
	}

	@Override
	protected void onNeighborDirtDugWithImproperTool(World world, int x, int y, int z, int toFacing) {
		// only disrupt grass/mycelium when block below is dug out

		if (toFacing == 0) {
			world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
		}    		
	}

	@Override
	public boolean canBePistonShoveled(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canBeGrazedOn(IBlockAccess blockAccess, int x, int y, int z, EntityAnimal animal) {
		return !isSparse(blockAccess, x, y, z) || animal.isStarving() || animal.getDisruptsEarthOnGraze();
	}

	@Override
	public void onGrazed(World world, int x, int y, int z, EntityAnimal animal) {
		if (!animal.getDisruptsEarthOnGraze()) {
			if (isSparse(world, x, y, z)) {
				world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
			}
			else {
				setSparse(world, x, y, z);
			}
		}
		else {
			//world.setBlockWithNotify(x, y, z, BTWBlocks.looseSparseGrass.blockID);
			world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
			notifyNeighborsBlockDisrupted(world, x, y, z);
		}
	}

	@Override
	public void onVegetationAboveGrazed(World world, int x, int y, int z, EntityAnimal animal) {
		if (animal.getDisruptsEarthOnGraze()) {
			world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
			notifyNeighborsBlockDisrupted(world, x, y, z);
		}
	}

	@Override
	public boolean canReedsGrowOnBlock(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canSaplingsGrowOnBlock(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canWildVegetationGrowOnBlock(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean getCanBlightSpreadToBlock(World world, int x, int y, int z, int blightLevel) {
		return true;
	}

	@Override
	public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) {
		return stack != null && stack.getItem() instanceof HoeItem;
	}

	@Override
	public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide) {
		world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);

		if (!world.isRemote) {
			if (world.rand.nextInt(25) == 0) {
				ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), fromSide);
			}
		}

		return true;
	}

	@Override
	public boolean getCanGrassSpreadToBlock(World world, int x, int y, int z) {
		return isSparse(world, x, y, z);
	}

	@Override
	public boolean spreadGrassToBlock(World world, int x, int y, int z) {
		if (isSparse(world, x, y, z)) {
			setFullyGrown(world, x, y, z);
			return true;
		}
		else {
			return false;
		}
	}

	//------------- Class Specific Methods ------------//

	public static boolean canGrassSurviveAtLocation(World world, int x, int y, int z) {
		int blockAboveID = world.getBlockId(x, y + 1, z);
		Block blockAbove = Block.blocksList[blockAboveID];

		int blockAboveMaxNaturalLight = world.getBlockNaturalLightValueMaximum(x, y + 1, z);

		int blockAboveCurrentNaturalLight = blockAboveMaxNaturalLight - world.skylightSubtracted;

		if (blockAboveMaxNaturalLight < SURVIVE_LIGHT_LEVEL || Block.lightOpacity[blockAboveID] > 2 ||
				(blockAbove != null && !blockAbove.getCanGrassGrowUnderBlock(world, x, y + 1, z, false)))
		{
			return false;
		}

		return true;
	}

	public static boolean canGrassSpreadFromLocation(World world, int x, int y, int z) {
		int blockAboveID = world.getBlockId(x, y + 1, z);
		Block blockAbove = Block.blocksList[blockAboveID];

		int blockAboveMaxNaturalLight = world.getBlockNaturalLightValueMaximum(x, y + 1, z);

		int blockAboveCurrentNaturalLight = blockAboveMaxNaturalLight - world.skylightSubtracted;
		int blockAboveCurrentBlockLight = world.getBlockLightValue(x, y + 1, z);

		int currentLight = Math.max(blockAboveCurrentNaturalLight, blockAboveCurrentBlockLight);

		return currentLight >= SPREAD_LIGHT_LEVEL;
	}

	public static void checkForGrassSpreadFromLocation(World world, int x, int y, int z) {
		if (world.provider.dimensionId != 1 && !GroundCoverBlock.isGroundCoverRestingOnBlock(world, x, y, z)) {
			// check for grass spread

			int i = x + world.rand.nextInt(3) - 1;
			int j = y + world.rand.nextInt(4) - 2;
			int k = z + world.rand.nextInt(3) - 1;

			Block targetBlock = Block.blocksList[world.getBlockId(i, j, k)];

			if (targetBlock != null) {
				attempToSpreadGrassToLocation(world, i, j, k);
			}
		}
	}

	public static boolean attempToSpreadGrassToLocation(World world, int x, int y, int z) {
		int targetBlockID = world.getBlockId(x, y, z);
		Block targetBlock = Block.blocksList[targetBlockID];

		if (canGrassSurviveAtLocation(world, x, y, z)) {
			if (targetBlock.getCanGrassSpreadToBlock(world, x, y, z) &&
					Block.lightOpacity[world.getBlockId(x, y + 1, z)] <= 2 &&
				!GroundCoverBlock.isGroundCoverRestingOnBlock(world, x, y, z))
			{
				return targetBlock.spreadGrassToBlock(world, x, y, z);
			}
		}

		return false;
	}

	public boolean isSparse(IBlockAccess blockAccess, int x, int y, int z) {
		return isSparse(blockAccess.getBlockMetadata(x, y, z));
	}

	public boolean isSparse(int metadata) {
		return metadata == 1;
	}

	public void setSparse(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 1);
	}

	public void setFullyGrown(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 0);
	}

	//----------- Client Side Functionality -----------//    

    @Environment(EnvType.CLIENT)
    private boolean hasSnowOnTop; // temporary variable used by rendering
    @Environment(EnvType.CLIENT)
    public static boolean secondPass;

	// duplicate variables to parent class to avoid base class modification

    @Environment(EnvType.CLIENT)
    private Icon iconGrassTop;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTopSparse;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTopSparseDirt;
    @Environment(EnvType.CLIENT)
    private Icon iconSnowSide;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideOverlay;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
		super.registerIcons(register);

		this.iconGrassTop = register.registerIcon("grass_top");
		this.iconSnowSide = register.registerIcon("snow_side");
		this.iconGrassSideOverlay = register.registerIcon("grass_side_overlay");

		iconGrassTopSparse = register.registerIcon("fcBlockGrassSparse");
		iconGrassTopSparseDirt = register.registerIcon("fcBlockGrassSparseDirt");
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		if (hasSnowOnTop || !secondPass) {
			return 16777215;
		}
		else {
			return super.colorMultiplier(blockAccess, x, y, z);
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
		BlockPos pos = new BlockPos(neighborX, neighborY, neighborZ, Facing.oppositeSide[side]);

		if (!secondPass ) {
			//Don't render dirt under normal grass
			if (side == 1 && !isSparse(blockAccess, pos.x, pos.y, pos.z) && !hasSnowOnTop) {
				//return false;
			}
		}
		else {
			//Bottom never has a second pass texture
			if (side == 0) {
				return false;
			}
			//Snow has its own texture and should not render the second pass
			else if (side >= 2 && hasSnowOnTop) {
				return false;
			}
		}

		return super.shouldSideBeRendered(blockAccess, neighborX, neighborY, neighborZ, side);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		if (!secondPass) {
			if (side == 1 && this.isSparse(blockAccess, x, y, z)) {
				return this.iconGrassTopSparseDirt;
			}
			else if (side > 1 && hasSnowOnTop) {
				Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(this, blockAccess, x, y, z, side, iconGrassTop);

				if (betterGrassIcon != null && betterGrassIcon != iconGrassTop && betterGrassIcon != iconGrassTopSparse) {
					return betterGrassIcon;
				}
				else {
					return iconSnowSide;
				}
			}
			else {
				return Block.grass.getBlockTextureFromSide(side);
			}
		}
		else {
			return getBlockTextureSecondPass(blockAccess, x, y, z, side);
		}
	}

    @Environment(EnvType.CLIENT)
    public Icon getBlockTextureSecondPass(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Icon topIcon;

		if (isSparse(blockAccess, x, y, z)) {
			topIcon = iconGrassTopSparse;
		}
		else {
			topIcon = iconGrassTop;
		}

		Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(this, blockAccess, x, y, z, side, topIcon);

		if (betterGrassIcon != null) {
			return betterGrassIcon;
		}
		else if (side == 1) {
			return topIcon;
		}
		else if (side > 1) {
			return this.iconGrassSideOverlay;
		}

		return null;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		hasSnowOnTop = isSnowCoveringTopSurface(render.blockAccess, x, y, z);
		render.setRenderBounds(0, 0, 0, 1, 1, 1);
		return render.renderStandardBlock(this, x, y, z);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks render, int x, int y, int z, boolean firstPassResult) {
		secondPass = true;
		render.setRenderBounds(0, 0, 0, 1, 1, 1);
		render.renderStandardBlock(this, x, y, z);
		secondPass = false;
	}
}