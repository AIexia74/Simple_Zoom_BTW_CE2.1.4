// FCMOD

package btw.item.items;

import net.minecraft.src.Item;
import net.minecraft.src.PotionHelper;

public class MysteriousGlandItem extends Item
{
    public MysteriousGlandItem(int iItemID )
    {
    	super( iItemID );

    	setBuoyant();
		setBellowsBlowDistance(2); // same as dye powder, and thus the ink sack
		setFilterableProperties(FILTERABLE_SMALL);
    	
    	setPotionEffect( PotionHelper.speckledMelonEffect );
    	
    	setUnlocalizedName( "fcItemMysteriousGland" );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
