// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Item;

public class StrawItem extends Item
{
    public StrawItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
        setIncineratedInCrucible();
		setBellowsBlowDistance(2);
    	setfurnaceburntime(FurnaceBurnTime.KINDLING);
    	setFilterableProperties(FILTERABLE_NARROW);
        
    	setHerbivoreFoodValue(BASE_HERBIVORE_ITEM_FOOD_VALUE / 4);
    	
    	setUnlocalizedName( "fcItemStraw" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
