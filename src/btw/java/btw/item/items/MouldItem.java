// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class MouldItem extends Item
{
    public MouldItem(int iItemID)
    {
    	super( iItemID );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public boolean isConsumedInCrafting()
    {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
