// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Item;

public class WickerPieceItem extends Item
{
    public WickerPieceItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
        setBellowsBlowDistance(2);
    	setIncineratedInCrucible();
    	setfurnaceburntime(FurnaceBurnTime.WICKER_PIECE);
    	setFilterableProperties(Item.FILTERABLE_THIN);
    	
    	setUnlocalizedName( "fcItemWickerPiece" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
    	// so that it doesn't accidentally go into a fire after basket weaving
    	
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
