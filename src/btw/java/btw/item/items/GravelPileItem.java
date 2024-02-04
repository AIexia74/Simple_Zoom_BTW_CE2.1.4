// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class GravelPileItem extends Item
{
    public GravelPileItem(int iItemID )
    {
        super( iItemID );
        
        setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_SMALL);
        
        setUnlocalizedName( "fcItemPileGravel" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
