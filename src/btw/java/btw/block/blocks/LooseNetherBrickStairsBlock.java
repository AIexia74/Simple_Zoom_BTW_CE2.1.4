// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class LooseNetherBrickStairsBlock extends MortarReceiverStairsBlock
{
    public LooseNetherBrickStairsBlock(int iBlockID)
    {
        super( iBlockID, BTWBlocks.looseNetherBrick, 0 );
        
        setPicksEffectiveOn();
        
        setUnlocalizedName( "fcBlockNetherBrickLooseStairs" );        
    }
	
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		world.setBlockAndMetadataWithNotify( i, j, k, Block.stairsNetherBrick.blockID,
			world.getBlockMetadata( i, j, k ) );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}