// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class FlintItem extends Item
{
    public FlintItem(int iItemID )
    {
        super( iItemID );
        
        setFilterableProperties(FILTERABLE_SMALL);

        setUnlocalizedName( "flint" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
}
