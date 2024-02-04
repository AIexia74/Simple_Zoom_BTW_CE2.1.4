// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;

public class InfiniteUnlitTorchBlock extends TorchBlockUnlitBase
{
    public InfiniteUnlitTorchBlock(int iBlockID)
    {
    	super( iBlockID );    	
    	
    	setUnlocalizedName( "fcBlockTorchIdle" );
    }
    
	@Override
	protected int getLitBlockID()
	{
		return BTWBlocks.infiniteBurningTorch.blockID;
	}
	
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//
}
    
