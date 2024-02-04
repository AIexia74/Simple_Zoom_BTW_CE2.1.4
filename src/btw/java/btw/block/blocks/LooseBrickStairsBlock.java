// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class LooseBrickStairsBlock extends MortarReceiverStairsBlock
{
    public LooseBrickStairsBlock(int iBlockID)
    {
        super( iBlockID, BTWBlocks.looseBrick, 0 );
        
        setPicksEffectiveOn();
        
        setUnlocalizedName( "fcBlockBrickLooseStairs" );        
    }
	
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		world.setBlockAndMetadataWithNotify( i, j, k, Block.stairsBrick.blockID,
			world.getBlockMetadata( i, j, k ) );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}