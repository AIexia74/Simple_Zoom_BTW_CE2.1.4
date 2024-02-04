// FCMOD

package btw.block.blocks;

import java.util.Random;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class MyceliumSlabBlock extends AttachedSlabBlock
{
    public MyceliumSlabBlock(int iBlockID)
    {
        super(iBlockID, Material.grass);
        
        setHardness(0.6F);
        setShovelsEffectiveOn();
        
        setStepSound(Block.soundGrassFootstep);
        
        setUnlocalizedName("fcBlockMyceliumSlab");
        
        setTickRandomly(true);
        
        setCreativeTab(CreativeTabs.tabBlock);
    }

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!MyceliumBlock.canMyceliumSurviveAtLocation(world, x, y, z)) {
			this.revertToDirt(world, x, y, z);
		}
		else {
			MyceliumBlock.checkForMyceliumSpreadFromLocation(world, x, y, z);

			if (isSparse(world, x, y, z) && rand.nextInt(4) == 0) {
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
	protected void onAnchorBlockLost(World world, int x, int y, int z) {
		world.setBlock(x, y, z, BTWBlocks.looseDirtSlab.blockID, world.getBlockMetadata(x, y, z) & 3, 2);
	}
	
	@Override
	public int getCombinedBlockID(int metadata) {
		return Block.mycelium.blockID;
	}
	
    @Override
    public boolean canBePistonShoveled(World world, int x, int y, int z) {
    	return true;
    }
    
	@Override
    protected boolean canSilkHarvest() {
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
		world.setBlockMetadata(x, y, z, metadata | 2);
	}

	public void setFullyGrown(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadata(x, y, z, metadata & 1);
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconTopSparse;

    @Environment(EnvType.CLIENT)
    private Icon iconBottom;

    @Environment(EnvType.CLIENT)
    private Icon iconSide;
    @Environment(EnvType.CLIENT)
    private Icon iconSideHalf;

    @Environment(EnvType.CLIENT)
    private Icon iconSideSnow;
    @Environment(EnvType.CLIENT)
    private Icon iconSideHalfSnow;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register)
    {
        blockIcon = register.registerIcon("fcBlockMyceliumSlab_top");
        iconTopSparse = register.registerIcon("fcBlockMyceliumSparse");

		iconBottom = register.registerIcon("fcBlockMyceliumSlab_bottom");
		iconSide = register.registerIcon("fcBlockMyceliumSlab_side");
		iconSideHalf = register.registerIcon("fcBlockMyceliumSlab_side_half");

		iconSideSnow = register.registerIcon("snow_side");
		iconSideHalfSnow = register.registerIcon("FCBlockSlabDirt_grass_snow_side");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int metadata)
    {
		if (iSide < 2)
		{
			if (iSide == 0)
			{
				return iconBottom;
			}
			else // iSide == 1 
			{
				Icon topIcon;
		    	
		    	if (isSparse(metadata)) {
		    		topIcon = this.iconTopSparse;
		    	}
		    	else {
		    		topIcon = this.blockIcon;
		    	}
		    	
		    	return topIcon;
			}
		}
		
		if (getIsUpsideDown(metadata))
		{
			return iconSide;
		}
		
		return iconSideHalf;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		
		Icon topIcon;
    	
    	if (isSparse(blockAccess, x, y, z)) {
    		topIcon = this.iconTopSparse;
    	}
    	else {
    		topIcon = this.blockIcon;
    	}
		
		if (side > 1 && isSnowCoveringTopSurface(blockAccess, x, y, z))
		{
	    	Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(this, blockAccess, x, y, z, side, topIcon);

	        if (betterGrassIcon != null)
	        {
	            return betterGrassIcon;
	        }
	        else if (getIsUpsideDown(metadata))
			{
				return iconSideSnow;
			}
			else
			{				
				return iconSideHalfSnow;
			}
		}
		
		Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(Block.mycelium, blockAccess, x, y, z, side, topIcon);

        if (betterGrassIcon != null) {
            return betterGrassIcon;
        }
        else {
        	return getIcon(side, metadata);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int i, int j, int k, Random rand)
    {
        super.randomDisplayTick(world, i, j, k, rand);
        
        if (rand.nextInt(10) == 0)
        {
            double dYParticle = (double)j + 0.6D;
            
            if (getIsUpsideDown(world, i, j, k))
            {
            	dYParticle += 0.5D;
            }

            world.spawnParticle("townaura", (double)i + rand.nextDouble(), dYParticle, (double)k + rand.nextDouble(), 0D, 0D, 0D);
        }
    }
}
