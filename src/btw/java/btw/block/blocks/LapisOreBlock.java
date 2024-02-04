// FCMOD

package btw.block.blocks;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;

import java.util.Random;

public class LapisOreBlock extends OreBlockStaged
{
    public LapisOreBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return Item.dyePowder.itemID ;
    }
    
    @Override
    public int quantityDropped( Random rand )
    {
        return 4 + rand.nextInt( 5 );
    }
    
    @Override
    public int damageDropped( int iMetadata )
    {
        return 4; // blue dye
    }
    
    @Override
    public int idDroppedOnConversion(int iMetadata)
    {
        return Item.dyePowder.itemID ;
    }
    
    @Override
    public int quantityDroppedOnConversion(Random rand)
    {
        return 4 + rand.nextInt( 5 );
    }
    
    @Override
    public int damageDroppedOnConversion(int iMetadata)
    {
        return 4; // blue dye
    }
    
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 1;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}

