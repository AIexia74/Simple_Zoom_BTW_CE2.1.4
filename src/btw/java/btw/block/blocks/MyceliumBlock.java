// FCMOD

package btw.block.blocks;

import java.util.Random;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.HoeItem;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class MyceliumBlock extends BlockMycelium {
    public MyceliumBlock(int blockID) {
        super(blockID);
        
        setHardness(0.6F);
        setShovelsEffectiveOn();
    	setHoesEffectiveOn();
        
        setStepSound(soundGrassFootstep);
        
        setUnlocalizedName("mycel");
    }

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!canMyceliumSurviveAtLocation(world, x, y, z)) {
			// convert back to dirt in low light
			world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
		}
		else {
			checkForMyceliumSpreadFromLocation(world, x, y, z);

			if (isSparse(world, x, y, z) && rand.nextInt(4) == 0) {
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
    public boolean canMobsSpawnOn(World world, int x, int y, int z) {
    	return false;
    }
    
	@Override
	public boolean canBeGrazedOn(IBlockAccess blockAccess, int x, int y, int z, EntityAnimal animal) {
		if (!isSparse(blockAccess, x, y, z) || animal.isStarving() || animal.getDisruptsEarthOnGraze()) {
			return animal.canGrazeMycelium();
		}
		
		return false;
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
    public boolean getCanBlightSpreadToBlock(World world, int x, int y, int z, int blightLevel) {
    	return blightLevel >= 2;
    }
    
	@Override
    public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) {
    	return stack != null && stack.getItem() instanceof HoeItem;
    }
	
    @Override
    public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide) {
    	world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
    	return true;
    }

	//------------- Class Specific Methods ------------//

	public static boolean canMyceliumSurviveAtLocation(World world, int x, int y, int z) {
		int blockAboveID = world.getBlockId(x, y + 1, z);
		Block blockAbove = Block.blocksList[blockAboveID];
		
		if (Block.lightOpacity[blockAboveID] > 2 || (blockAbove != null && !blockAbove.getCanGrassGrowUnderBlock(world, x, y + 1, z, false))) {
			return false;
		}

		return true;
	}

	public static void checkForMyceliumSpreadFromLocation(World world, int x, int y, int z) {
		if (world.provider.dimensionId != 1 && !GroundCoverBlock.isGroundCoverRestingOnBlock(world, x, y, z)) {
			// check for grass spread

			int i = x + world.rand.nextInt(3) - 1;
			int j = y + world.rand.nextInt(4) - 2;
			int k = z + world.rand.nextInt(3) - 1;

			Block targetBlock = Block.blocksList[world.getBlockId(i, j, k)];

			if (targetBlock != null) {
				attempToSpreadMyceliumToLocation(world, i, j, k);
			}
		}
	}

	public static boolean attempToSpreadMyceliumToLocation(World world, int x, int y, int z) {
		int targetBlockID = world.getBlockId(x, y, z);
		Block targetBlock = Block.blocksList[targetBlockID];

		if (targetBlock.getCanMyceliumSpreadToBlock(world, x, y, z) &&
				Block.lightOpacity[world.getBlockId(x, y + 1, z)] <= 2 &&
			!GroundCoverBlock.isGroundCoverRestingOnBlock(world, x, y, z))
		{
			return targetBlock.spreadMyceliumToBlock(world, x, y, z);
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
    
    // duplicate variables to parent class to avoid base class modification

    @Environment(EnvType.CLIENT)
    private Icon iconTop; // field_94422_a in parent
    @Environment(EnvType.CLIENT)
    private Icon iconTopSparse;
    @Environment(EnvType.CLIENT)
    private Icon iconSnowSide; // field_94421_b in parent

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
    	super.registerIcons(register);
    	
        iconTop = register.registerIcon("mycel_top");
        iconTopSparse = register.registerIcon("fcBlockMyceliumSparse");
        iconSnowSide = register.registerIcon("snow_side");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
    	Icon topIcon;
    	
    	if (isSparse(blockAccess, x, y, z)) {
    		topIcon = this.iconTopSparse;
    	}
    	else {
    		topIcon = this.iconTop;
    	}
    	
    	Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(this, blockAccess, x, y, z, side, topIcon);

        if (betterGrassIcon != null) {
            return betterGrassIcon;
        }
        else if (side == 1) {
            return topIcon;
        }
        else if (side == 0) {
            return Block.dirt.getBlockTextureFromSide(side);
        }
        else if (hasSnowOnTop) {
    		return iconSnowSide;
    	}
        
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
        hasSnowOnTop = isSnowCoveringTopSurface(blockAccess, x, y, z);
        
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
    	return renderer.renderStandardBlock(this, x, y, z);
    }
}