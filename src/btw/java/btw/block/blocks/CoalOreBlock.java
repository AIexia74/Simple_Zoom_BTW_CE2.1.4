// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.Item;

import java.util.Random;

public class CoalOreBlock extends OreBlockStaged
{
    public CoalOreBlock(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
        return Item.coal.itemID;
    }
    
    @Override
    public int idDroppedOnConversion(int iMetadata)
    {
        return BTWItems.coalDust.itemID;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}