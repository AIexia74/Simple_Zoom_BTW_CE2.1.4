package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class SugarCaneBlockBase extends Block {
    private static final double WIDTH = 0.75D;
    private static final double HALF_WIDTH = WIDTH / 2;
    
	public SugarCaneBlockBase(int id) {
		super(id, Material.plants);
		
		this.setHardness(0.0F);
		this.setBuoyant();
		this.setStepSound(soundGrassFootstep);
		this.disableStats();
		
		this.setTickRandomly(true);
		initBlockBounds(0.5D - HALF_WIDTH, 0F, 0.5D - HALF_WIDTH, 0.5D + HALF_WIDTH, 1F, 0.5D + HALF_WIDTH);
	}
	
	@Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
    	// prevent growth in the end dimension
    	if (world.provider.dimensionId != 1 && this.canBlockStay(world, x, y, z)) {
            if (world.isAirBlock(x, y + 1, z)) {
                int reedHeight = 1;

                while (Block.blocksList[world.getBlockId(x, y - reedHeight, z)] instanceof SugarCaneBlockBase) {
                	reedHeight++;
                }

                if (reedHeight < 3) {
                    int metadata = world.getBlockMetadata(x, y, z);

                    if (metadata == 15) {
                        world.setBlock(x, y + 1, z, BTWBlocks.sugarCane.blockID);
                        
                        world.SetBlockMetadataWithNotify(x, y, z, 0, 4);
                    }
                    else {
                        world.SetBlockMetadataWithNotify(x, y, z, metadata + 1, 4);
                    }
                }
            }
    	}
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
	
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
    	if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return this.canPlaceBlockAt(world, x, y, z);
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
    	if (world.getBlockMaterial(x, y, z) == Material.water)
    		return false;
    	
        int blockBelowID = world.getBlockId(x, y - 1, z);
        Block blockBelow = Block.blocksList[blockBelowID];

    	return blockBelowID == blockID || (blockBelow != null &&
                                           blockBelow.canReedsGrowOnBlock(world, x, y - 1, z) &&
                                           blockBelow.isConsideredNeighbouringWaterForReedGrowthOn(world, x, y - 1, z));
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	if (entity.isAffectedByMovementModifiers() && entity.onGround) {
	        entity.motionX *= 0.8D;
	        entity.motionZ *= 0.8D;
    	}
    }
    
	@Override
    public boolean doesBlockDropAsItemOnSaw(World world, int z, int y, int x) {
		return true;
    }

    @Override
	public boolean getPreventsFluidFlow(World world, int x, int y, int z, Block fluidBlock) {
    	return false;
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        
    	return renderer.renderCrossedSquares(this, x, y, z);
    }
}
