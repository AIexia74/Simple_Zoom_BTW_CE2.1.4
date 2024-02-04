// FCMOD

package btw.item.items;

import net.minecraft.src.ItemFood;

public class HardBoiledEggItem extends ItemFood
{
	static private final int HARD_BOILED_EGG_HEALTH_HEALED = 3;
	static private final float HARD_BOILED_EGG_SATURATION_MODIFIER = 0.25F;

    public HardBoiledEggItem(int iItemID )
    {
        super(iItemID, HARD_BOILED_EGG_HEALTH_HEALED, HARD_BOILED_EGG_SATURATION_MODIFIER, false);
        
        setNeutralBuoyant();
		setFilterableProperties(FILTERABLE_SMALL);
        
        setUnlocalizedName( "fcItemEggPoached" );    
    }    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
