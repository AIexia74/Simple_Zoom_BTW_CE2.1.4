// FCMOD

package btw.block.blocks;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;

import java.util.Random;

public class DiamondOreBlock extends OreBlockStaged
{
    public DiamondOreBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return Item.diamond.itemID;
    }
    
    @Override
    public int idDroppedOnConversion(int iMetadata)
    {
        return Item.diamond.itemID;
    }
    
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}

