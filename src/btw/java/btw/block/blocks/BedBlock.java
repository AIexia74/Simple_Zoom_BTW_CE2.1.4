// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BedBlock extends BedBlockBase
{
    public BedBlock(int blockID) {
		super(blockID);
    	
		this.setStepSound(Block.soundClothFootstep);
		this.setBlockMaterial(BTWBlocks.plankMaterial);
		
    	initBlockBounds(0D, 0D, 0D, 1D, 0.5625D, 1D);
	}
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int iMetadata, float chanceOfDrop) {
		dropItemsIndividually(world, x, y, z, BTWItems.sawDust.itemID, 3, 0, chanceOfDrop);
		dropItemsIndividually(world, x, y, z, Item.stick.itemID, 1, 0, chanceOfDrop);
		dropItemsIndividually(world, x, y, z, BTWItems.padding.itemID, 2, 0, chanceOfDrop);
		
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockBed(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }
	
	@Override
    public int idPicked(World world, int x, int y, int z) {
        return Item.bed.itemID;
    }
}