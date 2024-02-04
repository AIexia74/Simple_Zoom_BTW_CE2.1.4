// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.Random;

public class BedrollBlock extends BedBlockBase {
	private static final double BEDROLL_HEIGHT = 0.125D;
	
    public BedrollBlock(int blockID) {
		super(blockID);

		this.setStepSound(Block.soundClothFootstep);
    	
    	initBlockBounds(0D, 0D, 0D, 1D, BEDROLL_HEIGHT, 1D);
	}
	
    @Override
	public boolean blocksHealing() {
		return true;
	}

    @Override
    public int idDropped(int meta, Random rand, int par3) {
        return isBlockHeadOfBed(meta) ? 0 : BTWItems.bedroll.itemID;
    }
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
    
    @Override
    public Icon getIcon(int side, int meta) {
    	return this.blockIcon;
    }
    
    @Override
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        renderer.renderStandardBlock(this, x, y, z);
        
        int meta = renderer.blockAccess.getBlockMetadata(x, y, z);
        
        if (this.isBlockHeadOfBed(meta)) {
        	int direction = this.getDirection(meta);
        	
        	double distOuter = 0.125;
        	double distY = 0.0625;
        	double distInner = 0.5;
        	
        	switch (direction) {
        	case 0:
                renderer.setRenderBounds(
                		distOuter, BEDROLL_HEIGHT, 1 - distInner, 
                		1 - distOuter, BEDROLL_HEIGHT + distY, 1 - distOuter);
        		break;
        	case 1:
                renderer.setRenderBounds(
                		distOuter, BEDROLL_HEIGHT, distOuter, 
                		1 - distInner, BEDROLL_HEIGHT + distY, 1 - distOuter);
        		break;
        	case 2:
                renderer.setRenderBounds(
                		distOuter, BEDROLL_HEIGHT, distOuter, 
                		1 - distOuter, BEDROLL_HEIGHT + distY, 1 - distInner);
        		break;
        	case 3:
                renderer.setRenderBounds(
                		distInner, BEDROLL_HEIGHT, distOuter, 
                		1 - distOuter, BEDROLL_HEIGHT + distY, 1 - distOuter);
        		break;
        	}
        	
            renderer.renderStandardBlock(this, x, y, z);
        }
        
        return true;
    }
    
    @Override
    public void registerIcons(IconRegister register) {
    	this.blockIcon = register.registerIcon("cloth_0");
    }
    
    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
    	if (side == 0) {
    		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
    	}
    	else {
    		return true;
    	}
    }
	
	@Override
    public int idPicked(World world, int x, int y, int z) {
        return BTWItems.bedroll.itemID;
    }
}