// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.block.blocks.SlabBlock;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class LooseDirtSlabBlockItem extends SlabBlockItem {
    public LooseDirtSlabBlockItem(int itemID) {
        super(itemID);
    }
    
    @Override
    public boolean canCombineWithBlock(World world, int x, int y, int z, int itemDamage) {
        int blockID = world.getBlockId(x, y, z);
        
        if (blockID == BTWBlocks.dirtSlab.blockID) {
            int metadata = world.getBlockMetadata(x, y, z);
            int subtype = BTWBlocks.dirtSlab.getSubtype(metadata);
            
            if (subtype != DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
            	if (!BTWBlocks.dirtSlab.getIsUpsideDown(metadata)) {
            		return true;
            	}
            }
            
            return false;
        }
        else if (blockID == BTWBlocks.myceliumSlab.blockID || blockID == BTWBlocks.grassSlab.blockID) {
    		return true;
        }
        
    	return super.canCombineWithBlock(world, x, y, z, itemDamage);
    }

    @Override
    public boolean convertToFullBlock(World world, int x, int y, int z) {
    	// force target to convert to loose dirt, to handle stuff like combining with grass slabs
    	int newBlockID = ((SlabBlock) Block.blocksList[getBlockID()]).getCombinedBlockID(0);
    	
    	return world.setBlockWithNotify(x, y, z, newBlockID);
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
} 
