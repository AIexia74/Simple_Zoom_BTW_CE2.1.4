package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.items.HoeItem;
import btw.item.util.ItemUtils;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LooseSparseGrassBlock extends FallingFullBlock {
	public LooseSparseGrassBlock(int blockID) {
		super(blockID, Material.grass);
		
		setHardness(0.5F);
		setShovelsEffectiveOn();
		setHoesEffectiveOn();
		
		setStepSound(Block.soundGravelFootstep);
		
		setUnlocalizedName("fcBlockGrassSparseLoose");
		
		setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		
		if (!GrassBlock.canGrassSurviveAtLocation(world, x, y, z)) {
			// convert back to dirt in low light
			world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
		}
		else if (GrassBlock.canGrassSpreadFromLocation(world, x, y, z)) {
			if (rand.nextFloat() <= GrassBlock.GROWTH_CHANCE) {
				GrassBlock.checkForGrassSpreadFromLocation(world, x, y, z);
			}
			
			if (rand.nextInt(GrassBlock.SELF_GROWTH_CHANCE) == 0) {
				world.setBlockWithNotify(x, y, z, Block.grass.blockID);
			}
		}
	}
	
	@Override
	protected ItemStack createStackedBlock(int metadata) {
		return new ItemStack(BTWBlocks.looseDirt);
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
	public boolean canBePistonShoveled(World world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public boolean canBeGrazedOn(IBlockAccess blockAccess, int x, int y, int z, EntityAnimal animal) {
		return animal.isStarving();
	}
	
	@Override
	public void onGrazed(World world, int x, int y, int z, EntityAnimal animal) {
		world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
		notifyNeighborsBlockDisrupted(world, x, y, z);
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
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private boolean hasSnowOnTop; // temporary variable used by rendering
	@Environment(EnvType.CLIENT)
	public static boolean secondPass;
	
	// duplicate variables to parent class to avoid base class modification
	
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
		this.blockIcon = register.registerIcon("fcBlockGrassSparseLoose_side");
		
		this.iconSnowSide = register.registerIcon("snow_side");
		this.iconGrassSideOverlay = register.registerIcon("grass_side_overlay");
		
		iconGrassTopSparse = register.registerIcon("fcBlockGrassSparseLoose");
		iconGrassTopSparseDirt = register.registerIcon("fcBlockGrassSparseDirtLoose");
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		return Block.grass.colorMultiplier(blockAccess, x, y, z);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getBlockColor() {
		return Block.grass.getBlockColor();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getRenderColor(int par1) {
		return Block.grass.getRenderColor(par1);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
		if (secondPass) {
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
			if (side == 1) {
				return this.iconGrassTopSparseDirt;
			}
			else if (side > 1) {
				Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(this, blockAccess, x, y, z, side, iconGrassTopSparse);
				
				if (betterGrassIcon != null && betterGrassIcon != iconGrassTopSparse) {
					return betterGrassIcon;
				}
				else {
					if (this.hasSnowOnTop) {
						return iconSnowSide;
					}
					else {
						return this.blockIcon;
					}
				}
			}
			else {
				return BTWBlocks.looseDirt.blockIcon;
			}
		}
		else {
			return getBlockTextureSecondPass(blockAccess, x, y, z, side);
		}
	}
	
	@Environment(EnvType.CLIENT)
	public Icon getBlockTextureSecondPass(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Icon topIcon;
		
		topIcon = iconGrassTopSparse;
		
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
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int itemDamage, float brightness) {
		renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);
	}
}
