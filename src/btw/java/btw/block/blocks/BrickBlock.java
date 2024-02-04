// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class BrickBlock extends Block
{
    public BrickBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setPicksEffectiveOn();
    }
	
    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.looseBrick.blockID;
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