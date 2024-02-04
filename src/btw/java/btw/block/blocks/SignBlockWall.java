// FCMOD

package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.World;

public class SignBlockWall extends SignBlock
{
    public SignBlockWall(int iBlockID)
    {
    	super( iBlockID, false );    	
    }
    
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		return iFacing == Block.getOppositeFacing(world.getBlockMetadata(i, j, k));
    }
	
	@Override
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
		
		world.setBlockToAir( i, j, k );
		
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
