// FCMOD

package btw.item.items;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;

public class RedstoneItem extends PlaceAsBlockItem
{
    public RedstoneItem(int iItemID )
    {
    	super( iItemID, Block.redstoneWire.blockID );
    	
    	setBellowsBlowDistance(3);
    	setFilterableProperties(FILTERABLE_FINE);
    	
    	setUnlocalizedName( "redstone" );
    	
    	setCreativeTab( CreativeTabs.tabRedstone );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
