// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class WheatItem extends Item
{
    public WheatItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
        setIncineratedInCrucible();
		setBellowsBlowDistance(1);
    	setFilterableProperties(FILTERABLE_FINE);
        
    	setAsBasicHerbivoreFood();
    	setAsBasicPigFood();
    	
    	setUnlocalizedName( "fcItemWheat" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
	}
    
    //------------- Class Specific Methods ------------//	
    
	//----------- Client Side Functionality -----------//
}
