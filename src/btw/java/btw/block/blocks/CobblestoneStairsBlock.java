// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class CobblestoneStairsBlock extends StairsBlock {
	private int strata;
	
    public CobblestoneStairsBlock(int iBlockID, int strata) {
        super( iBlockID, Block.cobblestone, strata );
        
        this.strata = strata;
        
        setPicksEffectiveOn();
        
        setUnlocalizedName( "stairsStone" );
    }
	
    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier ) {   	
    	int blockID = BTWBlocks.looseCobblestoneStairs.blockID;
    	
    	if (strata != 0) {
    		if (strata == 1) {
    			blockID = BTWBlocks.looseMidStrataCobblestoneStairs.blockID;
    		}
    		else {
    			blockID = BTWBlocks.looseDeepStrataCobblestoneStairs.blockID;
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