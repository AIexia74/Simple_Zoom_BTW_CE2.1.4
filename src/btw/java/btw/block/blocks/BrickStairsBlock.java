// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class BrickStairsBlock extends StairsBlock
{
    public BrickStairsBlock(int iBlockID)
    {
        super( iBlockID, Block.brick, 0 );
        
        setPicksEffectiveOn();
    }
	
    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.looseBrickStairs.blockID;
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}