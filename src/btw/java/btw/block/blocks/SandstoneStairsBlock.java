// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class SandstoneStairsBlock extends StairsBlock
{
    public SandstoneStairsBlock(int iBlockID)
    {
        super( iBlockID, Block.sandStone, 0 );
        
        setPicksEffectiveOn();
    }
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 3; // diamonds or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sandPile.itemID, 12, 0, fChanceOfDrop);
		
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}