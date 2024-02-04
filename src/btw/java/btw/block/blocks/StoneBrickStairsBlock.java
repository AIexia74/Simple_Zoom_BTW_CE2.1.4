// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class StoneBrickStairsBlock extends StairsBlock {
	private int strata;
	
    public StoneBrickStairsBlock(int iBlockID, int strata) {
        super( iBlockID, Block.stoneBrick, strata << 2 );
        
        this.strata = strata;
        
        setPicksEffectiveOn();
        
        setUnlocalizedName( "stairsStoneBrickSmooth" );
    }
	
    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier ) {
    	int blockID = BTWBlocks.looseStoneBrickStairs.blockID;
    	
    	if (strata != 0) {
    		if (strata == 1) {
    			blockID = BTWBlocks.looseMidStrataStoneBrickStairs.blockID;
    		}
    		else {
    			blockID = BTWBlocks.looseDeepStrataStoneBrickStairs.blockID;
    		}
    	}
    	
        return blockID;
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k) {
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
    public int getStrata() {
		return strata;
	}
    
	//----------- Client Side Functionality -----------//
}