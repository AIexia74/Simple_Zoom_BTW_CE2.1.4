// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class SlimeballItem extends MortarItem
{
    public SlimeballItem(int iItemID )
    {
    	super( iItemID );
    	
    	setNeutralBuoyant();
		setFilterableProperties(Item.FILTERABLE_SMALL);
    	
    	setUnlocalizedName( "slimeball" );
    	
    	setCreativeTab( CreativeTabs.tabMisc );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}