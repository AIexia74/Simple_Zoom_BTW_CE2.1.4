// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.IBlockAccess;

import java.util.Random;

public class IronOreBlock extends OreBlockStaged
{
    public IronOreBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return BTWItems.ironOreChunk.itemID;
    }
    
    @Override
    public int idDroppedOnConversion(int iMetadata)
    {
        return BTWItems.ironOrePile.itemID;
    }
    
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 1;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
