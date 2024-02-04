// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.CreativeTabs;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.ItemReed;

public class ShaftItem extends ItemReed
{
    public ShaftItem(int iItemID )
    {
    	// the shaft supplies its own blockID through method override to avoid initialization order
    	// problems
    	
    	super( iItemID, 0 );
    	
    	setFull3D();
    	
    	setBuoyant();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT);
    	setIncineratedInCrucible();
    	setFilterableProperties(FILTERABLE_NARROW);
    	
    	setUnlocalizedName( "stick" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }

    @Override
    public int getBlockID()
    {
        return BTWBlocks.placedShaft.blockID;
    }

    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
