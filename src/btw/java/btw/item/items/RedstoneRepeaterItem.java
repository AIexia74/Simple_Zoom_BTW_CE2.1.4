// FCMOD

package btw.item.items;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;

public class RedstoneRepeaterItem extends PlaceAsBlockItem
{
    public RedstoneRepeaterItem(int iItemID )
    {
    	super( iItemID, Block.redstoneRepeaterIdle.blockID );
    	
    	setUnlocalizedName( "diode" );
    	
    	setCreativeTab( CreativeTabs.tabRedstone );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
