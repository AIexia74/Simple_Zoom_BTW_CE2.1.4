package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.util.RandomSelector;
import btw.util.ReflectionUtils;
import btw.world.feature.trees.grower.AbstractTreeGrower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class SaplingBlock extends DailyGrowthCropsBlock {
	private static final double WIDTH = 0.8D;
	private static final double HALF_WIDTH = (WIDTH / 2D);
	
	private String textureBase;
	
	public SaplingBlock(int id, String name, String textureBase) {
		super(id);
		
		setFurnaceBurnTime(FurnaceBurnTime.KINDLING);
		setFilterableProperties(Item.FILTERABLE_NO_PROPERTIES);
		
		initBlockBounds(0.5D - HALF_WIDTH, 0D, 0.5D - HALF_WIDTH, 0.5D + HALF_WIDTH, HALF_WIDTH * 2D, 0.5D + HALF_WIDTH);
		
		this.setUnlocalizedName(name);
		this.textureBase = textureBase;
		
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (updateIfBlockStays(world, x, y, z)) {
			// no plants can grow in the end
			if (world.provider.dimensionId != 1) {
				attemptToGrow(world, x, y, z, rand);
			}
		}
	}
	
	@Override
	protected void attemptToGrow(World world, int x, int y, int z, Random rand) {
		int timeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L);
		
		if (timeOfDay > 14000 && timeOfDay < 22000) {
			// night
			if (getHasGrownToday(world, x, y, z)) {
				setHasGrownToday(world, x, y, z, false);
			}
		}
		else {
			if (!getHasGrownToday(world, x, y, z) && canGrowAtCurrentLightLevel(world, x, y, z)) {
				Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
				
				if (blockBelow != null) {
					float growthChance = getBaseGrowthChance(world, x, y, z);
					
					if (blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) {
						growthChance *= 2F;
					}
					
					if (rand.nextFloat() <= growthChance) {
						incrementGrowthLevel(world, x, y, z);
						
						// Don't touch metadata if sapling turned into a tree
						if (world.getBlockId(x, y, z) == this.blockID) {
							updateFlagForGrownToday(world, x, y, z);
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void incrementGrowthLevel(World world, int x, int y, int z) {
		if (isFullyGrown(world, x, y, z)) {
			this.attemptToGrowTree(world, x, y, z);
		}
		else {
			int newGrowthLevel = getGrowthLevel(world, x, y, z) + 1;
			setGrowthLevel(world, x, y, z, newGrowthLevel);
			
			if (isFullyGrown(world, x, y, z)) {
				Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
				
				if (blockBelow != null) {
					blockBelow.notifyOfFullStagePlantGrowthOn(world, x, y - 1, z, this);
				}
			}
		}
	}
	
	@Override
	protected boolean requiresNaturalLight() {
		return false;
	}
	
	@Override
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k) {
		return false;
	}
	
	@Override
	protected boolean canGrowOnBlock(World world, int x, int y, int z) {
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		return block != null && block.canSaplingsGrowOnBlock(world, x, y, z);
	}
	
	@Override
	public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		return true;
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		return getFixedBlockBoundsFromPool();
	}
	
	@Override
	public int idDropped(int metadata, Random rand, int fortuneModifier) {
		return this.blockID;
	}
	
	@Override
	public int damageDropped(int metadata) {
		if (this.isFullyGrown(metadata)) {
			// Filter out has grown flag
			return this.getGrowthLevel(metadata);
		}
		else {
			return 0;
		}
	}
	
	@Override
	protected int getCropItemID() {
		return 0;
	}
	
	@Override
	protected int getSeedItemID() {
		return 0;
	}
	
	@Override
	protected boolean onlyDropWhenFullyGrown() {
		return false;
	}
	
	//------------- Class Specific Methods ------------//
	
	private static final int NOT_2X2 = 0;
	private static final int VALID_2X2_NOT_MATURE = 1;
	private static final int VALID_2X2 = 2;
	
	private final Map<AbstractTreeGrower, Integer> treeGrowers = new HashMap<>();
	private final Map<AbstractTreeGrower, Integer> treeGrowers2x2 = new HashMap<>();
	
	protected void attemptToGrowTree(World world, int x, int y, int z) {
		if (!checkFor2x2Tree(world, x, y, z)) {
			AbstractTreeGrower treeGrower = getTreeGrower(world.rand);
			
			int metadata = world.getBlockMetadata(x, y, z);
			world.setBlock(x, y, z, 0);
			
			if (!treeGrower.growTree(world, world.rand, x, y, z, false)) {
				world.setBlockAndMetadata(x, y, z, this.blockID, metadata);
			}
		}
	}
	
	protected boolean checkFor2x2Tree(World world, int x, int y, int z) {
		if (treeGrowers2x2.isEmpty()) {
			return false;
		}
		
		boolean has2x2 = false;
		
		if (canGrow2x2Tree(world, x, y, z) != NOT_2X2) {
			has2x2 = true;
			
			if (canGrow2x2Tree(world, x, y, z) == VALID_2X2) {
				attemptToGrow2x2Tree(world, x, y, z);
			}
		}
		
		if (canGrow2x2Tree(world, x - 1, y, z) != NOT_2X2) {
			has2x2 = true;
			
			if (canGrow2x2Tree(world, x - 1, y, z) == VALID_2X2) {
				attemptToGrow2x2Tree(world, x - 1, y, z);
			}
		}
		
		if (canGrow2x2Tree(world, x, y, z - 1) != NOT_2X2) {
			has2x2 = true;
			
			if (canGrow2x2Tree(world, x, y, z - 1) == VALID_2X2) {
				attemptToGrow2x2Tree(world, x, y, z - 1);
			}
		}
		
		if (canGrow2x2Tree(world, x - 1, y, z - 1) != NOT_2X2) {
			has2x2 = true;
			
			if (canGrow2x2Tree(world, x - 1, y, z - 1) == VALID_2X2) {
				attemptToGrow2x2Tree(world, x - 1, y, z - 1);
			}
		}
		
		return has2x2;
	}
	
	protected void attemptToGrow2x2Tree(World world, int x, int y, int z) {
		AbstractTreeGrower treeGrower = getTreeGrower2x2(world.rand);
		
		int metadata = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, 0);
		world.setBlock(x + 1, y, z, 0);
		world.setBlock(x, y, z + 1, 0);
		world.setBlock(x + 1, y, z + 1, 0);
		
		if (!treeGrower.growTree(world, world.rand, x, y, z, false)) {
			world.setBlockAndMetadata(x, y, z, this.blockID, metadata);
			world.setBlockAndMetadata(x + 1, y, z, this.blockID, metadata);
			world.setBlockAndMetadata(x, y, z + 1, this.blockID, metadata);
			world.setBlockAndMetadata(x + 1, y, z + 1, this.blockID, metadata);
		}
	}
	
	protected int canGrow2x2Tree(World world, int x, int y, int z) {
		if (world.getBlockId(x + 1, y, z) == this.blockID &&
				world.getBlockId(x, y, z + 1) == this.blockID &&
				world.getBlockId(x + 1, y, z + 1) == this.blockID)
		{
			if (this.isFullyGrown(world, x, y, z) &&
					this.isFullyGrown(world, x + 1, y, z) &&
					this.isFullyGrown(world, x, y, z + 1) &&
					this.isFullyGrown(world, x + 1, y, z + 1))
			{
				return VALID_2X2;
			}
			else {
				return VALID_2X2_NOT_MATURE;
			}
		}
		else {
			return NOT_2X2;
		}
	}
	
	public AbstractTreeGrower getTreeGrower(Random rand) {
		ToDoubleFunction<AbstractTreeGrower> weighter = treeGrowers::get;
		RandomSelector<AbstractTreeGrower> selector = RandomSelector.weighted(treeGrowers.keySet(), weighter);
		
		return selector.next(rand);
	}
	
	public AbstractTreeGrower getTreeGrower2x2(Random rand) {
		ToDoubleFunction<AbstractTreeGrower> weighter = treeGrowers2x2::get;
		RandomSelector<AbstractTreeGrower> selector = RandomSelector.weighted(treeGrowers2x2.keySet(), weighter);
		
		return selector.next(rand);
	}
	
	public SaplingBlock addTreeGrower(AbstractTreeGrower grower, int weight) {
		treeGrowers.put(grower, weight);
		return this;
	}
	
	public SaplingBlock add2x2TreeGrower(AbstractTreeGrower grower, int weight) {
		treeGrowers2x2.put(grower, weight);
		return this;
	}
	
	public boolean removeTreeGrower(String name) {
		for (AbstractTreeGrower treeGrower : this.treeGrowers.keySet()) {
			if (treeGrower.name.equals(name)) {
				this.treeGrowers.remove(treeGrower);
				return true;
			}
		}
		
		for (AbstractTreeGrower treeGrower : this.treeGrowers2x2.keySet()) {
			if (treeGrower.name.equals(name)) {
				this.treeGrowers2x2.remove(treeGrower);
				return true;
			}
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private Icon[] icons = new Icon[4];
	
	@Environment(EnvType.CLIENT)
	private final int[] iconIndexByGrowthStage = {0, 0, 0, 1, 1, 2, 2, 3};
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		for (int i = 0; i < 4; i++) {
			icons[i] = register.registerIcon(this.textureBase + i);
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int side, int metadata) {
		return icons[iconIndexByGrowthStage[this.getGrowthLevel(metadata)]];
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void getSubBlocks(int metadata, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(this.blockID, 1, 0));
		list.add(new ItemStack(this.blockID, 1, 7));
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
		renderer.renderCrossedSquares(this, x, y, z);
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getRenderType() {
		return 1;
	}
}