// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Item;

public class KnittingNeedlesItem extends Item
{
    public KnittingNeedlesItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT);
    	setFilterableProperties(Item.FILTERABLE_NARROW);
    	
    	setUnlocalizedName( "fcItemKnittingNeedles" );
    	
    	setCreativeTab( CreativeTabs.tabTools );
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
    	return false;
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage)
    {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
