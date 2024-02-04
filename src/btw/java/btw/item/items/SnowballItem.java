// FCMOD

package btw.item.items;

import net.minecraft.src.ItemSnowball;

public class SnowballItem extends ItemSnowball
{
    public SnowballItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
        setIncineratedInCrucible();
        setFilterableProperties(FILTERABLE_SMALL);
        
        setUnlocalizedName( "snowball" );
    }
}
