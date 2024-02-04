package btw.block.blocks;

import java.util.List;
import java.util.Random;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class GrassSlabBlock extends AttachedSlabBlock {
	public GrassSlabBlock(int blockID) {
		super(blockID, Material.ground);

		this.setHardness(0.5F);
		this.setShovelsEffectiveOn(true);

		this.setStepSound(Block.soundGrassFootstep);

		this.setUnlocalizedName("fcBlockSlabDirt");

		this.setTickRandomly(true);

		this.setCreativeTab(CreativeTabs.tabBlock);

		this.setUnlocalizedName("fcBlockGrassSlab");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!GrassBlock.canGrassSurviveAtLocation(world, x, y, z)) {
			this.revertToDirt(world, x, y, z);
		}
		else if (GrassBlock.canGrassSpreadFromLocation(world, x, y, z)) {
			if (rand.nextFloat() <= GrassBlock.GROWTH_CHANCE) {
				GrassBlock.checkForGrassSpreadFromLocation(world, x, y, z);
			}

			if (isSparse(world, x, y, z) && rand.nextInt(GrassBlock.SELF_GROWTH_CHANCE) == 0) {
				this.setFullyGrown(world, x, y, z);
			}
		}
	}
    
    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        return BTWBlocks.looseDirtSlab.blockID;
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int iMetadata, float fChanceOfDrop) {
		dropItemsIndividually(world, x, y, z, BTWItems.dirtPile.itemID, 3, 0, fChanceOfDrop);
		return true;
	}

	@Override
	protected void onAnchorBlockLost(World world, int i, int j, int k) {
		world.setBlock(i, j, k, BTWBlocks.looseDirtSlab.blockID, world.getBlockMetadata(i, j, k) & 3, 2);
	}

	@Override
	public int getCombinedBlockID(int iMetadata) {
		return Block.grass.blockID;
	}
	
	@Override
    public boolean attemptToCombineWithFallingEntity(World world, int x, int y, int z, EntityFallingSand entity) {
		if (entity.blockID == BTWBlocks.looseDirtSlab.blockID) {
			if (!getIsUpsideDown(world, x, y, z)) {
				world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
						
				return true;
			}
		}
		
		return super.attemptToCombineWithFallingEntity(world, x, y, z, entity);
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
				world.setBlockWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID);
			}
			else {
				setSparse(world, x, y, z);
			}
		}
		else {
			world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirtSlab.blockID);
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
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
    	super.onBlockDestroyedWithImproperTool(world, player, i, j, k, iMetadata);
    	
		onDirtSlabDugWithImproperTool(world, i, j, k, getIsUpsideDown(iMetadata));
    }
    
	@Override
    public void onBlockDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
		super.onBlockDestroyedByExplosion(world, i, j, k, explosion);
    	
		onDirtSlabDugWithImproperTool(world, i, j, k, getIsUpsideDown(world, i, j, k));
    }

	//------ Class specific methods ------//

	public void revertToDirt(World world, int x, int y, int z) {
		boolean isUpsideDown = this.getIsUpsideDown(world, x, y, z);

		world.setBlockWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID);
		BTWBlocks.dirtSlab.setSubtype(world, x, y, z, DirtSlabBlock.SUBTYPE_DIRT);
		BTWBlocks.dirtSlab.setIsUpsideDown(world, x, y, z, isUpsideDown);
	}

	public boolean isSparse(IBlockAccess blockAccess, int x, int y, int z) {
		return isSparse(blockAccess.getBlockMetadata(x, y, z));
	}

	public boolean isSparse(int metadata) {
		return (metadata & -1) == 2;
	}

	public void setSparse(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, metadata | 2);
	}

	public void setFullyGrown(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, metadata & 1);
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private boolean hasSnowOnTop; // temporary variable used by rendering
    @Environment(EnvType.CLIENT)
    public static boolean secondPass;

    @Environment(EnvType.CLIENT)
    private Icon iconGrassTop;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTopSparse;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTopSparseDirt;

    @Environment(EnvType.CLIENT)
    private Icon iconSnowSide;
    @Environment(EnvType.CLIENT)
    private Icon iconSnowSideHalf;

    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideOverlay;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideOverlayHalf;

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
		this.blockIcon = Block.dirt.blockIcon;

		this.iconGrassTop = register.registerIcon("grass_top");
		this.iconGrassTopSparse = register.registerIcon("fcBlockGrassSparse");
		this.iconGrassTopSparseDirt = register.registerIcon("fcBlockGrassSparseDirt");

		this.iconSnowSide = register.registerIcon("snow_side");
		this.iconSnowSideHalf = register.registerIcon("FCBlockSlabDirt_grass_snow_side");

		this.iconGrassSideOverlay = register.registerIcon("grass_side_overlay");
		this.iconGrassSideOverlayHalf = register.registerIcon("FCBlockSlabDirt_grass_side_overlay");
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		if (hasSnowOnTop || !secondPass) {
			return 16777215;
		}
		else {
			if (ColorizeBlock.colorizeBlock(this, blockAccess, x, y, z)) {
				return ColorizeBlock.blockColor;
			}
			else {
				int red = 0;
				int green = 0;
				int blue = 0;

				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						int iBiomeGrassColor = blockAccess.getBiomeGenForCoords(x + i, z + k).getBiomeGrassColor();

						red += (iBiomeGrassColor & 0xff0000) >> 16;
					green += (iBiomeGrassColor & 0xff00) >> 8;
					blue += iBiomeGrassColor & 0xff;
					}
				}

				return (red / 9 & 0xff) << 16 | (green / 9 & 0xff) << 8 | blue / 9 & 0xff;
			}
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
					if (this.getIsUpsideDown(blockAccess, x, y, z)) {
						return iconSnowSide;
					}
					else {
						return iconSnowSideHalf;
					}
				}
			}
			else {
				return Block.dirt.getBlockTextureFromSide(side);
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
			if (this.getIsUpsideDown(blockAccess, x, y, z)) {
				return this.iconGrassSideOverlay;
			}
			else {
				return this.iconGrassSideOverlayHalf;
			}
		}

		return null;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		hasSnowOnTop = isSnowCoveringTopSurface(render.blockAccess, x, y, z);
		AxisAlignedBB bounds = this.getBlockBoundsFromPoolBasedOnState(render.blockAccess, x, y, z);
		render.setRenderBounds(bounds);
		return render.renderStandardBlock(this, x, y, z);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks render, int x, int y, int z, boolean firstPassResult) {
		secondPass = true;
		AxisAlignedBB bounds = this.getBlockBoundsFromPoolBasedOnState(render.blockAccess, x, y, z);
		render.setRenderBounds(bounds);
		render.renderStandardBlock(this, x, y, z);
		secondPass = false;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int itemDamage, float brightness) {
		renderBlocks.setRenderBounds(0, 0, 0, 1, 0.5F, 1);
		itemDamage = DirtSlabBlock.SUBTYPE_GRASS;
		RenderUtils.renderInvBlockWithMetadata(renderBlocks, BTWBlocks.dirtSlab, -0.5F, -0.5F, -0.5F, itemDamage << 1);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(blockID, 1, 0));
	}
}
