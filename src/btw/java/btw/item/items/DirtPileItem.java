// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class DirtPileItem extends Item
{
    public DirtPileItem(int iItemID )
    {
        super( iItemID );
        
        setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_FINE);
        
        setUnlocalizedName( "fcItemPileDirt" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
