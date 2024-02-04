// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;

public class NetherSludgeItem extends MortarItem
{
    public NetherSludgeItem(int iItemID )
    {
    	super( iItemID );
    	
    	setNeutralBuoyant();
    	
    	setUnlocalizedName( "fcItemNetherSludge" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
