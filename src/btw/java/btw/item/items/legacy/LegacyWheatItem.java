// FCMOD

package btw.item.items.legacy;

import net.minecraft.src.Item;

public class LegacyWheatItem extends Item
{
    public LegacyWheatItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
        setIncineratedInCrucible();
		setBellowsBlowDistance(1);
    	setFilterableProperties(FILTERABLE_NARROW);
        
    	setAsBasicHerbivoreFood();
    	setAsBasicPigFood();
    	
    	setUnlocalizedName( "wheat" );
	}
    
    //------------- Class Specific Methods ------------//	
    
	//----------- Client Side Functionality -----------//
}
