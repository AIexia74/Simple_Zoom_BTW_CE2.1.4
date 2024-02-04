// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.PlanterBlockBase;
import btw.crafting.util.FurnaceBurnTime;
import btw.util.ReflectionUtils;
import btw.world.feature.trees.BigTreeGenerator;
import btw.world.feature.trees.JungleTreeGenerator;
import btw.world.feature.trees.TreeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class LegacySaplingBlock extends BlockSapling
{
	public static final String[] saplingTypes = new String[] {
			"oak", "spruce", "birch", "jungle",
			"oak", "spruce", "birch", "jungle",
			"oak", "spruce", "birch", "jungle",
			"oakMature", "spruceMature", "birchMature", "jungleMature",
	};

	private static final double WIDTH = 0.8D;
	private static final double HALF_WIDTH = (WIDTH / 2D);

	public LegacySaplingBlock(int iBlockID)
	{
		super(iBlockID);

		setFurnaceBurnTime(FurnaceBurnTime.KINDLING);
		setFilterableProperties(Item.FILTERABLE_NO_PROPERTIES);

		initBlockBounds(0.5D - HALF_WIDTH, 0D, 0.5D - HALF_WIDTH,
						0.5D + HALF_WIDTH, HALF_WIDTH * 2D, 0.5D + HALF_WIDTH);
		
		this.setCreativeTab(null);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random)
	{
		checkFlowerChange(world, i, j, k); // replaces call to the super method two levels up in the hierarchy

		if (world.provider.dimensionId != 1 && world.getBlockId(i, j, k) == blockID) // necessary because checkFlowerChange() may destroy the sapling
		{
			if (world.getBlockLightValue(i, j + 1, k) >= 9)
			{
				attemptToGrow(world, i, j, k, random);
			}
		}
	}

	@Override
	//Debug method
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!ReflectionUtils.isObfuscated()) {
			if (!world.isRemote) {
				attemptToGrow(world, x, y, z, world.rand);
			}

			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public void growTree(World world, int x, int y, int z, Random random) {
		int treeType = world.getBlockMetadata(x, y, z) & 3;
		boolean success = false;

		int xOffset = 0;
		int zOffset = 0;

		boolean generatedHuge = false;

		boolean planter = Block.blocksList[world.getBlockId(x, y - 1, z)] instanceof PlanterBlockBase;

		if (treeType != 3) {
			world.setBlock(x, y, z, 0);            
		}

		if (treeType == 1) {
			success = TreeUtils.generateTaiga2(world, random, x, y, z);
		} 
		else if (treeType == 2) {
			success = TreeUtils.generateForest(world, random, x, y, z);
		} 
		else if (treeType == 3) {
			do {
				if (xOffset < -1) {
					break;
				}

				zOffset = 0;

				do {
					if (zOffset < -1) {
						break;
					}

					if (isSameSapling(world, x + xOffset, y, z + zOffset, 3) && 
							isSameSapling(world, x + xOffset + 1, y, z + zOffset, 3) && 
							isSameSapling(world, x + xOffset, y, z + zOffset + 1, 3) && 
							isSameSapling(world, x + xOffset + 1, y, z + zOffset + 1, 3))
					{
						if (getSaplingGrowthStage(world, x + xOffset, y, z + zOffset) == 3 &&
							getSaplingGrowthStage(world, x + xOffset + 1, y, z + zOffset) == 3 &&
							getSaplingGrowthStage(world, x + xOffset, y, z + zOffset + 1) == 3 &&
							getSaplingGrowthStage(world, x + xOffset + 1, y, z + zOffset + 1) == 3)
						{
							// clear all 4 saplings that make up the huge tree

							world.setBlock(x + xOffset, y, z + zOffset, 0);
							world.setBlock(x + xOffset + 1, y, z + zOffset, 0);
							world.setBlock(x + xOffset, y, z + zOffset + 1, 0);
							world.setBlock(x + xOffset + 1, y, z + zOffset + 1, 0);

							JungleTreeGenerator hugeTree = new JungleTreeGenerator(true, 10 + random.nextInt(20), 3, 3);
							success = hugeTree.generate(world, random, x + xOffset, y, z + zOffset);

							generatedHuge = true;
							break;
						}
						else {
							// we have 4 saplings, but they aren't all fully grown.  Bomb out entirely
							return;
						}
					}

					zOffset--;
				}
				while (true);

				if (generatedHuge) {
					break;
				}

				xOffset--;
			}
			while (true);

			if (!generatedHuge) {
				xOffset = zOffset = 0;
				world.setBlock(x, y, z, 0);

				success = TreeUtils.generateTrees(world, random, x, y, z, 4 + random.nextInt(7), 3, 3, false);
			}
		}
		else {	// metadata is 0
			if (random.nextInt(10) == 0)
			{
				BigTreeGenerator bigTree = new BigTreeGenerator(true);

				success = bigTree.generate(world, random, x, y, z);
			}
			else {
				success = TreeUtils.generateTrees(world, random, x, y, z);
			}
		}

		if (!success) {
			// restore saplings at full growth

			int saplingMetadata = treeType + (3 << 2);

			if (generatedHuge) {
				// replace all the saplings if a huge tree was grown

				world.setBlockAndMetadata(x + xOffset, y, z + zOffset, blockID, saplingMetadata);
				world.setBlockAndMetadata(x + xOffset + 1, y, z + zOffset, blockID, saplingMetadata);
				world.setBlockAndMetadata(x + xOffset, y, z + zOffset + 1, blockID, saplingMetadata);
				world.setBlockAndMetadata(x + xOffset + 1, y, z + zOffset + 1, blockID, saplingMetadata);
			}
			else {
				world.setBlockAndMetadata(x, y, z, blockID, saplingMetadata);
			}
		}
		else if (planter) {
			world.setBlockMetadata(x, y, z, treeType);

			//Block break sfx
			world.playAuxSFX(2001, x, y - 1, z, BTWBlocks.planterWithSoil.blockID);

			world.setBlockAndMetadata(x, y - 1, z, Block.wood.blockID, treeType | 12);
		}
	}

	@Override
	public boolean onBlockSawed(World world, int i, int j, int k)
	{
		return false;
	}

	@Override
	protected boolean canGrowOnBlock(World world, int i, int j, int k)
	{
		Block blockOn = Block.blocksList[world.getBlockId(i, j, k)];

		return blockOn != null && blockOn.canSaplingsGrowOnBlock(world, i, j, k);
	}

	//------------- Class Specific Methods ------------//

	public int getSaplingGrowthStage(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata(i, j, k);

		int iGrowthStage = (iMetadata & (~3)) >> 2;

			return iGrowthStage;
	}

	public void attemptToGrow(World world, int x, int y, int z, Random rand) {
		int growthChance = 32;

		int blockIDBelow = world.getBlockId(x, y - 1, z);
		Block blockBelow = Block.blocksList[blockIDBelow];

		if (blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) {
			growthChance /= blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, this);
		}

		int metadata = world.getBlockMetadata(x, y, z);
		int growthStage = (metadata & (~3)) >> 2;

		if (growthStage == 3) {
			growthChance /= 2;
		}

		if (rand.nextInt(growthChance) == 0) {
			if (growthStage < 3) {
				growthStage++;
				metadata = (metadata & 3) | (growthStage << 2);

				world.setBlockMetadataWithNotify(x, y, z, metadata);

				if (growthStage == 3) {
					blockBelow.notifyOfFullStagePlantGrowthOn(world, x, y - 1, z, this);
				}
			}
			else {
				if (!(blockBelow instanceof PlanterBlockBase) || blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) {
					growTree(world, x, y, z, rand);
				}
			}
		}
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[][] iconArray = new Icon[4][4];
    @Environment(EnvType.CLIENT)
    public static final String[] baseTextureNames = new String[] {"fcBlockSaplingOak_0", "fcBlockSaplingSpruce_0", "fcBlockSaplingBirch_0", "fcBlockSaplingJungle_0" };

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register)
	{
		for (int iTempSaplingType = 0; iTempSaplingType < 4; iTempSaplingType++)
		{
			for (int iTempGrowthStage = 0; iTempGrowthStage < 4; iTempGrowthStage++)
			{
				iconArray[iTempSaplingType][iTempGrowthStage] = register.registerIcon(baseTextureNames[iTempSaplingType] + iTempGrowthStage);
			}
		}
	}

    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata)
	{
		int iSaplingType = iMetadata & 3;
		int iGrowthStage = (iMetadata & (~3)) >> 2;

			return iconArray[iSaplingType][iGrowthStage];
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));

		par3List.add(new ItemStack(par1, 1, 12));
		par3List.add(new ItemStack(par1, 1, 13));
		par3List.add(new ItemStack(par1, 1, 14));
		par3List.add(new ItemStack(par1, 1, 15));
	}
}
