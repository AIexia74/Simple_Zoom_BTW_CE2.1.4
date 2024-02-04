// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

import java.util.Random;

public class NetherBrickStairsBlock extends StairsBlock
{	
    public NetherBrickStairsBlock(int iBlockID)
    {
    	super( iBlockID, netherBrick, 0 );
    	
    	setUnlocalizedName( "stairsNetherBrick" );
    }
	
    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.looseNEtherBrickStairs.blockID;
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