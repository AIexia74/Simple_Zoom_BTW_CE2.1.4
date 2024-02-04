// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemBook;

public class BookItem extends ItemBook
{
    public BookItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
        setIncineratedInCrucible();
        
        setUnlocalizedName( "book" );
        
        setCreativeTab( CreativeTabs.tabMisc );
    }

    @Override
    public int getItemEnchantability()
    {
    	// override to remove being able to enchant books into ancient manuscripts
    	
        return 0;
    }
}
