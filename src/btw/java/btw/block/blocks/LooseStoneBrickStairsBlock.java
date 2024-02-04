// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class LooseStoneBrickStairsBlock extends MortarReceiverStairsBlock {
	private int strata;
	
    public LooseStoneBrickStairsBlock(int iBlockID, int strata) {
        super( iBlockID, BTWBlocks.looseStoneBrick, strata << 2 );
        
        this.strata = strata;
        
        setPicksEffectiveOn();
        
        setUnlocalizedName( "fcBlockStoneBrickLooseStairs" );        
    }
	
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k) {
    	int blockID = Block.stairsStoneBrick.blockID;
    	
    	if (strata != 0) {
    		if (strata == 1) {
    			blockID = BTWBlocks.midStrataStoneBrickStairs.blockID;
    		}
    		else {
    			blockID = BTWBlocks.deepStrataStoneBrickStairs.blockID;
    		}
    	}
    	
		world.setBlockAndMetadataWithNotify( i, j, k, blockID, 
			world.getBlockMetadata( i, j, k ) );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
    
    public int getStrata() {
		return strata;
	}
    
	//----------- Client Side Functionality -----------//
}