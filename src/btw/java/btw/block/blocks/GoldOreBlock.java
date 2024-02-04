// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.IBlockAccess;

import java.util.Random;

public class GoldOreBlock extends OreBlockStaged
{
    public GoldOreBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return BTWItems.goldOreChunk.itemID;
    }
    
    @Override
    public int idDroppedOnConversion(int iMetadata)
    {
        return BTWItems.goldOrePile.itemID;
    }
    
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
