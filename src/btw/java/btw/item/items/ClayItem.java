// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ClayItem extends MortarItem
{
    public ClayItem(int iItemID )
    {
    	super( iItemID );
    	
    	setFilterableProperties(Item.FILTERABLE_SMALL);
    	
    	setUnlocalizedName( "clay" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
