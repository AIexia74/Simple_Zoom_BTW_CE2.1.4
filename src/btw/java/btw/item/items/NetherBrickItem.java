// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class NetherBrickItem extends Item
{
    public NetherBrickItem(int iItemID )
    {
        super( iItemID );
        
        setUnlocalizedName( "fcItemBrickNether" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
}
