// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.MiningChargeBlock;
import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class MiningChargeBlockItem extends ItemBlock
{
    public MiningChargeBlockItem(int iItemID )
    {
    	super( iItemID );
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
											  int i, int j, int k, int iFacing)
	{
    	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    	
		MiningChargeBlock.createPrimedEntity(world,
											 targetPos.x, targetPos.y, targetPos.z, iFacing);
		
        world.playAuxSFX( BTWEffectManager.BLOCK_PLACE_EFFECT_ID, i, j, k,
        	BTWBlocks.miningCharge.blockID );
        
		return true;
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
