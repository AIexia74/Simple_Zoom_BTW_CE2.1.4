// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.MushroomCapBlock;
import btw.block.util.Flammability;
import net.minecraft.src.World;

public class LegacyMushroomCapBlock extends MushroomCapBlock
{
    public LegacyMushroomCapBlock(int iBlockID, int iMushroomType )
    {
        super( iBlockID, iMushroomType );
        
		setFireProperties(Flammability.NONE);
    }
    
	@Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );

        // convert vanilla blocks to new flammable ones on add so that we don't have to modify
        // WorldGenBigMushroom

        int iNewBlockID = BTWBlocks.brownMushroomCap.blockID;
        
        if (mushroomType != 0 )
        {
        	iNewBlockID = BTWBlocks.redMushroomCap.blockID;
        }
        
    	// "2" in last param to not trigger another neighbor block notify
    	
        world.setBlock( i, j, k, iNewBlockID, world.getBlockMetadata( i, j, k ), 2 );
    }
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
