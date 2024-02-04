// FCMOD

package btw.item.items;

import net.minecraft.src.EnumToolMaterial;
import btw.crafting.util.FurnaceBurnTime;

public class ChiselItemWood extends ChiselItem
{
    public ChiselItemWood(int iItemID )
    {
        super( iItemID, EnumToolMaterial.WOOD, 2 );
        
        setBuoyant();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT.burnTime / 2);
		setFilterableProperties(FILTERABLE_NARROW);
        
        efficiencyOnProperMaterial /= 4;
        
        setUnlocalizedName( "fcItemChiselWood" );        
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
