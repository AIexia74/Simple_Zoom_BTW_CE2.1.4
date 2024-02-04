// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class SandPileItem extends Item
{
    public SandPileItem(int iItemID )
    {
        super( iItemID );
        
        setBellowsBlowDistance(2);
		setFilterableProperties(FILTERABLE_FINE);
        
        setUnlocalizedName( "fcItemPileSand" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
