// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class BoneItem extends Item
{
    public BoneItem(int iItemID )
    {
        super( iItemID );
        
        maxStackSize = 16;
        
        setBuoyant();
        setIncineratedInCrucible();
        setFilterableProperties(FILTERABLE_NARROW);

        setFull3D();
        
        setUnlocalizedName( "bone" );
        
        setCreativeTab( CreativeTabs.tabMisc );
    }
}
