// FCMOD

package btw.item.items;

import net.minecraft.src.Item;

public class SoapItem extends Item
{
    public SoapItem(int iItemID )
    {
        super(iItemID);
        
        setBuoyant();
        
        setUnlocalizedName( "fcItemSoap" );        
    }
}
