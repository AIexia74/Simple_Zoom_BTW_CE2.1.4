// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class LooseCobblestoneStairsBlock extends MortarReceiverStairsBlock {
	private int strata;
	
	public LooseCobblestoneStairsBlock(int iBlockID, int strata) {
        super( iBlockID, BTWBlocks.looseCobblestone, strata << 2 );
        
        this.strata = strata; 
        
        setPicksEffectiveOn();
        setChiselsEffectiveOn();
        
        setUnlocalizedName( "fcBlockCobblestoneLooseStairs" );        
    }
	
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k) {
    	int blockID = Block.stairsCobblestone.blockID;
    	
    	if (strata != 0) {
    		if (strata == 1) {
    			blockID = BTWBlocks.midStrataCobblestoneStairs.blockID;
    		}
    		else {
    			blockID = BTWBlocks.deepStrataCobblestoneStairs.blockID;
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